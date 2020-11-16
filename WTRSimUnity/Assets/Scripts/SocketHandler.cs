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

public class SocketHandler: MonoBehaviour
{
    int clientPort = 4513;
    int serverPort = 4512;
    
    public string outboundString = "hello Java";
    public string received;

    Socket sendSocket;
    UdpClient receiveSocket;
    IPEndPoint clientEP;

    Boolean dependentMode; //If dependentMode is true, Unity is running dependently to WTRSimlib
    volatile Boolean kill = false;

    int lastHeartBeat = -1;
    int heartBeat = 0;
    Boolean heartStopped = false;

    SocketHandler()
    {
		
    }
	
	void Start() {
		sendSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
        receiveSocket = new UdpClient(clientPort);
        clientEP = new IPEndPoint(IPAddress.Any, clientPort);

        sendSocket.SendTimeout = 2000;
        receiveSocket.Client.ReceiveTimeout = 2000;

        try
        {
            IPAddress serverIPAddress = IPAddress.Parse("127.0.0.1");
            IPEndPoint serverEP = new IPEndPoint(serverIPAddress, serverPort);
            byte[] message = new byte[1];
            sendSocket.SendTo(message, serverEP);

            lastHeartBeat = heartBeat;
            receiveSocket.Receive(ref clientEP);
            heartBeat++;

            Debug.Log("Connected!");
            dependentMode = true;
        }
        catch ( SocketException e )
        {
            dependentMode = false;
        }
     

        if ( dependentMode )
        {
            //Inbound
            new Thread(() =>
            {
                while ( !kill )
                {
                    Thread.Sleep(10);
                    try
                    {
                        lastHeartBeat = heartBeat;
                        byte[] inboundBytes = receiveSocket.Receive(ref clientEP);
                        received = Encoding.ASCII.GetString(inboundBytes, 0, inboundBytes.Length);
                        heartBeat++;
                        Debug.Log("packet received: " + received);
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
                    Thread.Sleep(10);
                    try
                    {
                        IPAddress serverIPAddress = IPAddress.Parse("127.0.0.1");
                        IPEndPoint serverEP = new IPEndPoint(serverIPAddress, serverPort);
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
        EditorApplication.update += Update;
	}

    private void Update()
    {
        if ( kill )
        {
            killProcesses();
        }
    }

    void killProcesses() {
        AppDomain.CurrentDomain.ProcessExit += (obj, eArg) =>
        {
            kill = true;
        };

        EditorApplication.Exit(0);
        receiveSocket.Close();
        sendSocket.Close();
        EditorApplication.Exit(0);
    }
}
