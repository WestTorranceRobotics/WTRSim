using System;
using System.Net;
using System.Net.Sockets;
using UnityEngine;
using UnityEditor;
using UnityEditor.PackageManager;
using System.Collections;
using UnityEngine.Assertions;
using System.Threading;
using System.Linq;
using System.Text;

//Singleton needed on game object

// Credit to the University of Northampton, UK
// http://www.eng.northampton.ac.uk/~espen/CSY2026/JavaServerCSClient.htm
// CSY2026 Modern Networks

/**
 * Class that handles communication with Java
 */
[InitializeOnLoad]
public class SocketHandler: MonoBehaviour
{
    int clientPort = 4513;
    int serverPort = 4512;

    Socket sendSocket;
    UdpClient receiveSocket;
    IPEndPoint clientEP;
    IPAddress serverIPAddress;
    IPEndPoint serverEP;

    Boolean dependentMode; //If dependentMode is true, Unity is running dependently to WTRSimlib
    volatile Boolean kill = false;
	
	Boolean initialized = false;

    int lastHeartBeat = -1;
    int heartBeat = 0;
    Boolean heartStopped = false;

    private volatile String outboundString = "";
    private volatile String inboundString = "";

    SocketHandler()
    {
       
        sendSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
        receiveSocket = new UdpClient(clientPort);
        clientEP = new IPEndPoint(IPAddress.Any, clientPort);
        sendSocket.SendTimeout = 10000;
        receiveSocket.Client.ReceiveTimeout = 10000;

        try
        {
            serverIPAddress = IPAddress.Parse("127.0.0.1");
            serverEP = new IPEndPoint(serverIPAddress, serverPort);

            //Tell java that Unity has launched
            byte[] buffer = new byte[256];
            buffer = Encoding.ASCII.GetBytes("");
            sendSocket.SendTo(buffer, serverEP);

            //Verify that Java knows Unity is alive
            lastHeartBeat = heartBeat;
            byte[] inboundBytes = receiveSocket.Receive(ref clientEP);
            setInboundString(Encoding.ASCII.GetString(inboundBytes, 0, inboundBytes.Length));
            //heartBeat++;

            Debug.Log(inboundString);

            Debug.Log("Handshake Success!");
            dependentMode = true;
			EditorApplication.playModeStateChanged += playModeChanged;
        }
        catch ( SocketException e )
        {
            Debug.Log("Running Independent to RobotCode");
            dependentMode = false;
        }
		
       EditorApplication.update += Update;
    }

    private void playModeChanged(PlayModeStateChange state)
    {
        //Tell java that playmode has been reached
        byte[] buffer = new byte[ 256 ];
        buffer = Encoding.ASCII.GetBytes("");
        sendSocket.SendTo(buffer, serverEP);

        if(dependentMode)
        {
            //Inbound
            new Thread(() =>
            {
                while ( !kill )
                {
                    Thread.Sleep(5);
                    try
                    {
                        lastHeartBeat = heartBeat;
                        byte[] inboundBytes = receiveSocket.Receive(ref clientEP);
                        setInboundString(Encoding.ASCII.GetString(inboundBytes, 0, inboundBytes.Length));
                        heartBeat++;
                        Debug.Log("packet received: " + inboundString);
                    }
                    catch ( SocketException e )
                    {
                        Debug.LogError(e.Message + " " + e.StackTrace);
                        if ( heartStopped )
                        {
                            kill = true;
                        }
                    }

                    heartBeat = (heartBeat >= 1000000) ? 0 : heartBeat;
                    heartStopped = (heartBeat == lastHeartBeat) ? true : heartStopped;
                }
            }).Start();

            //Outbound
            new Thread(() =>
            {
                while ( !kill )
                {
                    Thread.Sleep(30);
                    try
                    {
                        byte[] message = Encoding.ASCII.GetBytes(outboundString);
                        sendSocket.SendTo(message, serverEP);
                    }
                    catch ( SocketException e )
                    {
                        Debug.LogError(e.Message + " " + e.StackTrace);
                    }

                }
            }).Start();
        }
    }

    private void Update()
    {
        if ( !initialized && dependentMode )
        {
            EditorApplication.EnterPlaymode();
            initialized = true;
        }

        if ( dependentMode && !EditorApplication.isPlaying )
        {
            receiveSocket.Close();
            sendSocket.Close();
        }

        if ( kill )
        {
            killProcesses();
        }
    }

    void OnApplicationQuit()
    {
        killProcesses();
    }

    void killProcesses()
    {
        AppDomain.CurrentDomain.ProcessExit += (obj, eArg) =>
        {
            kill = true;
        };

        receiveSocket.Close();
        sendSocket.Close();
        EditorApplication.Exit(0);
    }

    public void setInboundString(String inbString)
    {
        lock(inboundString) {
            inboundString = inbString;
        }
    }

    public void setOutboundString(String obString)
    {
        lock ( outboundString )
        {
            outboundString = obString;
        }
    }
    
}


