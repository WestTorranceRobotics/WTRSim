package simulation.launch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicLong;
import simulation.launch.UnityVerifier;
import simulation.serialization.ByteAssembler;
import simulation.serialization.DatagramAssembler;

// Credit to the University of Northampton, UK
// http://www.eng.northampton.ac.uk/~espen/CSY2026/JavaServerCSClient.htm
// CSY2026 Modern Networks

/**
 * Class that handles communication with Unity.
 */
public class SocketManager implements Runnable  {

    Runnable unityVerifier;
    ByteAssembler datagramAssembler;

    int soTimeout = 2000;

    public int sendPort = 4513;
    public int receivePort = 4512;

    public DatagramSocket receiveSocket;
    public DatagramSocket sendSocket;
    InetAddress address;

    public volatile String outboundString = "";
    public volatile String inboundString = "";
    public volatile AtomicLong sent;
    public volatile AtomicLong received;
    public volatile Object[] objects;

    volatile boolean kill = false;

    /**
     * Constructor for SocketManager.
     */
    public SocketManager() {
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

        unityVerifier = new UnityVerifier(sendSocket, receiveSocket, sendPort);
        datagramAssembler = new DatagramAssembler();
        
        sent = new AtomicLong();
        received = new AtomicLong();
    }

    @Override
    public void run() {
        unityVerifier.run();  
  
        //Outbound
        new Thread(() -> { 
            try {
                sendSocket.setSoTimeout(soTimeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            
            while (!kill) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] outboundBytes = datagramAssembler.getBytes(objects, sent.addAndGet(0), 
                    received.addAndGet(0)
                );
                    
                try {
                    DatagramPacket outboundDp = new DatagramPacket(outboundBytes, 
                        outboundBytes.length, address, sendPort
                    );

                    sendSocket.send(outboundDp);
                    sent.addAndGet(1);
                } catch (IOException e) {
            
                }

                System.out.println(sent.addAndGet(0) + "    " + received.addAndGet(0));
            }
        }).start();

        //Inbound
        new Thread(() -> { 
            try {
                receiveSocket.setSoTimeout(soTimeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            while (!kill) { 
                try {
                    Thread.sleep(5);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    DatagramPacket inboundDp;
                    byte[] buffer = new byte[256];
                    inboundDp = new DatagramPacket(buffer, buffer.length);
                    receiveSocket.receive(inboundDp);
                    received.addAndGet(1);
                } catch(IOException e) {
                    System.out.println("Timed out. Aborting... ");
                    kill = true; 
                }
            }
        }).start();      
    }
}
