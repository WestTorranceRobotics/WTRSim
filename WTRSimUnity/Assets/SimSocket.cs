using System;
using System.Net;
using System.Net.Sockets;
using UnityEngine;
using UnityEditor;
using UnityEditor.PackageManager;
using System.Collections;
using UnityEngine.Assertions;
using System.Threading;

//Singleton needed on game object

[InitializeOnLoad]
public class SimSocket: MonoBehaviour
{
    public  IPEndPoint serverAddress;
    public  Socket socket;
    [SerializeField] string temp = "wow!";
    bool clientMode = false;
    bool coroutineOn = false;

    GameObject socketCoroutine;

    SimSocket() {
        var ip = Dns.GetHostAddresses(Dns.GetHostName());
        string ipAddress = null;
        for ( int i = 0; i < Dns.GetHostAddresses(Dns.GetHostName()).Length; i++ )
        {
            Debug.Log(ip[ i ].MapToIPv4().ToString() + " " + ip[ i ].AddressFamily);
            if ( ip[ i ].AddressFamily == AddressFamily.InterNetwork )
            {
                ipAddress = ip[ i ].MapToIPv4().ToString();
            }
        }
        if ( ipAddress.Equals(null) )
        {
            Debug.LogError("Failed to find local host IP");
        }

        serverAddress = new IPEndPoint(IPAddress.Parse(ipAddress), 4512);

        socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        socket.Connect(serverAddress);

        //Thread ioThread = new Thread(IOThread);
        //ioThread.Start();
        EditorApplication.update += FixedUpdate;
    }

    private void Awake()
    {
     
    }

    private void Start()
    {
        
    }

   
    private void FixedUpdate()
    {
        try
        {
            string toSend = temp;

            //out
            int toSendLen = System.Text.Encoding.ASCII.GetByteCount(toSend);
            byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
            byte[] toSendLenBytes = System.BitConverter.GetBytes(toSendLen);
            socket.Send(toSendLenBytes);
            socket.Send(toSendBytes);

           /* byte[] byData = System.Text.Encoding.ASCII.GetBytes("oof");
            socket.Send(byData);*/


            //In
            byte[] rcvLenBytes = new byte[ 4 ];
            socket.Receive(rcvLenBytes);
            int rcvLen = System.BitConverter.ToInt32(rcvLenBytes, 0);
            byte[] rcvBytes = new byte[ rcvLen ];
            socket.Receive(rcvBytes);
            String rcv = System.Text.Encoding.ASCII.GetString(rcvBytes);
            Debug.Log(rcv);

        }
        catch ( SocketException e )
        {
            if ( clientMode == true )
            {
                System.Diagnostics.Process.Start("CMD.exe", "Taskkill /IM Unity.exe /F");
            }
        }
    }

    private void IOThread() {
        while ( true )
        {
          
        }
    }
}

    
