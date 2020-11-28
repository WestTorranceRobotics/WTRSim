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
public class SocketHandler implements Runnable  {

    Runnable unityVerifier;

    public int sendPort = 4513;
    public int receivePort = 4512;

    public DatagramSocket receiveSocket;
    public DatagramSocket sendSocket;
    InetAddress address;

    public volatile String outboundString = "";
    public volatile String inboundString = "";

    volatile boolean kill = false;
    int timeOutMili = 10000;

    public SocketHandler() {
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
                    Thread.sleep(20);
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
                    DatagramPacket inboundDp;
                    byte[] buffer = new byte[256];
                    inboundDp = new DatagramPacket(buffer, buffer.length);
                    receiveSocket.receive(inboundDp);
                    setInboundString(new String(inboundDp.getData()));
                    System.out.println("packet received: " + 
                        getLatestInboundString());
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();      
    }

    public synchronized void setInboundString(String string) {
        synchronized (inboundString) {
            inboundString = string;
        }
    }

    public synchronized String getLatestInboundString() {
        synchronized (inboundString) {
            return inboundString;
        }
    }

    public synchronized void setOutboundString(String string) {
        synchronized (outboundString) {
            outboundString = string;
        }
    }

    public synchronized String getLatestOutboundString() {
        synchronized (outboundString) {
            return outboundString;
        }
    }
    
}
