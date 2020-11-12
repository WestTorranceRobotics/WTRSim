package simulation.launch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * IoVerifier is responsible for verifying 
 * that Input/Output with Unity is working, 
 * and by extension confirms that Unity itself 
 * has launched and is sending/receiving packets.
*/
public class UnityVerifier implements Runnable {

    int timeOutMili = 2000;
    int verifTimeOutMili = 60000;
    int clientPort = 4513;
    DatagramSocket sendSocket;
    DatagramSocket receiveSocket;
    SocketHandler socketHandler;

    public boolean kill = false;

    UnityVerifier(DatagramSocket sendSocket, DatagramSocket 
        receiveSocket, SocketHandler socketHandler) {
        this.sendSocket = sendSocket;
        this.receiveSocket = receiveSocket;
        this.socketHandler = socketHandler;
    }

    @Override
    public void run() {
        Thread connect = new Thread(() -> {
            try {
                System.out.print("Establishing Connection... ");
                InetAddress address = InetAddress.getByName("127.0.0.1");
                byte[] buffer = new byte[1];
                DatagramPacket outboundDp = new DatagramPacket(buffer, 
                    buffer.length, address, clientPort);
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

                System.out.println("Verified Input/Output Efficacy");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("\nFailed. Aborting... \n");
                socketHandler.kill = true;
            }
        });

        connect.start();

        while (connect.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            sendSocket.setSoTimeout(timeOutMili);
            receiveSocket.setSoTimeout(timeOutMili);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}