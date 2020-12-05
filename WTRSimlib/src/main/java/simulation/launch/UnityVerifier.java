package simulation.launch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UnityVerifier is responsible for verifying 
 * that Input/Output with Unity is working, 
 * and by extension confirms that Unity itself 
 * has launched and is sending/receiving packets.
*/
public class UnityVerifier implements Runnable {

    int verifTimeOutMili = 100000;
    int sendPort;
    DatagramSocket sendSocket;
    DatagramSocket receiveSocket;

    public boolean kill = false;

    /**
     * UnityVerifier verifies that Unity has launched properly.
     *
     * @param sendSocket Socket for sending datagrams.
     * @param receiveSocket Socket for receiving datagrams.
     * @param sendPort Port used for sending datagrams.
     */
    public UnityVerifier(DatagramSocket sendSocket, DatagramSocket receiveSocket, int sendPort) {
        this.sendSocket = sendSocket;
        this.receiveSocket = receiveSocket;
        this.sendPort = sendPort;
    }

    @Override
    public void run() {
        try {
            final InetAddress address = InetAddress.getLoopbackAddress();
            System.out.print("Waiting for response from Unity... ");
            receiveSocket.setSoTimeout(verifTimeOutMili);

            //Wait for Unity to assert it's existence
            DatagramPacket inboundDp;
            byte[] buffer = new byte[256];
            inboundDp = new DatagramPacket(buffer, buffer.length);
            receiveSocket.receive(inboundDp);

            System.out.println("Received! ");
            System.out.println("\nPacket: " + new String(inboundDp.getData()));
            System.out.print("Prompting Unity to enter playmode and awaiting confirmation... ");

            //Prompt for Unity to enter playmode
            buffer = new byte[256];
            buffer = "".getBytes();
            DatagramPacket outboundDp = new DatagramPacket(
                buffer, buffer.length, 
                address, sendPort
            );
            sendSocket.send(outboundDp);

            //Wait for Unity to assert that it is in playmode
            buffer = new byte[256];
            inboundDp = new DatagramPacket(buffer, buffer.length);
            receiveSocket.receive(inboundDp);

            System.out.println("Received! ");
            System.out.println("\nPacket: " + new String(inboundDp.getData()));

            System.out.println("Handshake Success!");

        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("\nFailed. Aborting... \n");
        }
    }
}