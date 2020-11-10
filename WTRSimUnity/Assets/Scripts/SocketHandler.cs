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
    Socket sendSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);

    public string outboundString = "hello Java";
    public string received;

    volatile Boolean kill = false;

    SocketHandler()
    {
        UdpClient receiveSocket = new UdpClient(clientPort);
        IPEndPoint clientEP = new IPEndPoint(IPAddress.Any, clientPort);

        //Inbound
        new Thread(() => {
            while ( true )
            {
                Thread.Sleep(10);
                try
                {
                    byte[] bytes = receiveSocket.Receive(ref clientEP);
                    received = Encoding.ASCII.GetString(bytes, 0, bytes.Length);
                    Debug.Log("packet received: " + received);
                }
                catch ( SocketException e )
                {
                    Debug.LogError(e.Message + " " + e.StackTrace);
                }
            }
        }).Start();

        //Outbound
        new Thread(() => {
            while ( true )
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
}


