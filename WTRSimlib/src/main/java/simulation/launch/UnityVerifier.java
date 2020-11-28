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
    SocketHandler socketHandler;

    public boolean kill = false;

    /**
     * UnityVerifier verifies that Unity has launched properly.
     *
     * @param socketHandler is the class responsible 
     *      for standard communication. 
     */
    UnityVerifier(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
        this.sendSocket = socketHandler.sendSocket;
        this.receiveSocket = socketHandler.receiveSocket;
        this.sendPort = socketHandler.sendPort;
    }

    @Override
    public void run() {
        try {
            System.out.println("Verifying Unity Launch... ");
            InetAddress address = InetAddress.getLoopbackAddress();
            byte[] buffer = new byte[1];
            DatagramPacket outboundDp = new DatagramPacket(
                buffer, buffer.length, 
                address, sendPort
            );

            System.out.print("Sending Request... ");
            sendSocket.send(outboundDp);
            System.out.println("Sent! ");

            System.out.print("Receiving Response... ");
            receiveSocket.setSoTimeout(verifTimeOutMili);
            DatagramPacket inboundDp;
            buffer = new byte[1];
            inboundDp = new DatagramPacket(buffer, buffer.length);
            receiveSocket.receive(inboundDp);
            System.out.println("Received! ");

            System.out.println("Handshake Success!");
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("\nFailed. Aborting... \n");
            socketHandler.kill = true;
        }
    }
}