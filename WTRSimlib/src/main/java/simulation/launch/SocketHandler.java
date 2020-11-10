package simulation.launch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

// Credit to the University of Northampton, UK
// http://www.eng.northampton.ac.uk/~espen/CSY2026/JavaServerCSClient.htm
// CSY2026 Modern Networks

/**
 * Class that handles communication with Unity
 */
class SocketHandler implements Runnable {

    Runnable connectionStarter;

    int clientPort = 4513;
    int serverPort = 4512;

    DatagramSocket receiveSocket;
    DatagramSocket sendSocket;
    InetAddress address;

    public String outboundString = "hello Unity";
    public String received;

    volatile boolean kill = false;
    int lastHeartBeat = -1;
    int heartBeat = 0;
    Boolean heartStopped = false;

    SocketHandler() {
        try {
            address = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(serverPort);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    receiveSocket.close();
                    sendSocket.close();
                }
            });
        } catch (SocketException e) {
            e.printStackTrace();
        }

        connectionStarter = new ConnectionStarter(sendSocket, receiveSocket, this);
    }

    public void run() {
        connectionStarter.run();
   
        //Outbound
        new Thread(() -> { while ( !kill ) {
                try {
                    Thread.sleep(10);
                } 
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    byte[] buffer = new byte[256];
                    buffer = outboundString.getBytes();
                    DatagramPacket outboundDP = new DatagramPacket (buffer, buffer.length, address, clientPort);
    
                    sendSocket.send(outboundDP);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}).start();

            //Inbound
        new Thread(() -> { while ( !kill ) {
                    
            try {
                Thread.sleep(10);
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                lastHeartBeat = heartBeat;
                DatagramPacket inboundDP;
                byte[] buffer = new byte[256];
                inboundDP = new DatagramPacket (buffer, buffer.length);
                receiveSocket.receive (inboundDP);
                heartBeat++;
                received = new String(inboundDP.getData());
                System.out.println("packet received: " + received);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if ( heartStopped ) { 
                System.out.print("\nTimed out. Aborting... \n\n");
                kill = true;
            }

            heartBeat = (heartBeat >= 1000000) ? 0 : heartBeat;
            heartStopped = (heartBeat == lastHeartBeat) ? true : heartStopped;
        }}).start();         
    }
}
