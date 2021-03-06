package simulation.launch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import simulation.launch.UnityVerifier;

// Credit to the University of Northampton, UK
// http://www.eng.northampton.ac.uk/~espen/CSY2026/JavaServerCSClient.htm
// CSY2026 Modern Networks

/**
 * Class that handles communication with Unity.
 */
class SocketHandler implements Runnable  {

    Runnable unityVerifier;

    public int sendPort = 4513;
    public int receivePort = 4512;

    public DatagramSocket receiveSocket;
    public DatagramSocket sendSocket;
    InetAddress address;

    public String outboundString = "hello Unity";
    public String received;

    volatile boolean kill = false;
    int lastHeartBeat = -1;
    int heartBeat = 0;
    Boolean heartStopped = false;

    SocketHandler() {
        address = InetAddress.getLoopbackAddress();

        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(receivePort);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    receiveSocket.close();
                    sendSocket.close();
                }
            });
        } catch(SocketException e) {
            e.printStackTrace();
            kill = true;
        }

        unityVerifier = new UnityVerifier(this);
    }

    public void run() {
        unityVerifier.run();
   
        //Outbound
        new Thread(() -> { 
            while (!kill) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    byte[] buffer = new byte[256];
                    buffer = outboundString.getBytes();
                    DatagramPacket outboundDp = 
                        new DatagramPacket(
                        buffer, 
                        buffer.length, 
                        address, 
                        sendPort
                    );
    
                    sendSocket.send(outboundDp);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //Inbound
        new Thread(() -> { 
            while (!kill) { 
                try {
                    Thread.sleep(10);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    lastHeartBeat = heartBeat;
                    DatagramPacket inboundDp;
                    byte[] buffer = new byte[256];
                    inboundDp = new DatagramPacket(buffer, buffer.length);
                    receiveSocket.receive(inboundDp);
                    heartBeat++;
                    received = new String(inboundDp.getData());
                    System.out.println("packet received: " + received);
                } catch(IOException e) {
                    e.printStackTrace();
                }

                if (heartStopped) { 
                    System.out.print("\nTimed out. Aborting... \n\n");
                    kill = true;
                }

                heartBeat = (heartBeat >= 1000000) ? 0 : heartBeat;
                heartStopped = (heartBeat == lastHeartBeat) ? true 
                    : heartStopped;
            }
        }).start();         
    }
}
