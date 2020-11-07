package simulation.launch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;

/**
 * Socket to Simulator
 */
class SocketHandler implements Runnable 
{
    int i = 0;
    public boolean echoRecieved = false;
    Socket socket;
    ServerSocket serverSocket;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    InputStream inputStream;
    OutputStream outputStream;

    String temp = "hello unity";

    SocketHandler(DataOutputStream dataOutputStream, DataInputStream dataInputStream, Socket socket, ServerSocket serverSocket, InputStream inputStream, OutputStream outputStream) {
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void run() 
    {
        System.out.println("SocketHandler Thread Started");
        while (true) {
            try {
                Thread.sleep(10);
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            //System.out.println("Attempting to flush output and read input");
            try {
            //     System.out.println("Writing");
            //     dataOutputStream.writeUTF("okDude");
            //     System.out.println("Flushing");
            //     dataOutputStream.flush();
            //     System.out.println("Reading");
            //     if (dataInputStream.available() > 0) {
            //         String read = dataInputStream.readUTF();
            //         System.out.println(read);
            //     }
            //     System.out.println("IO Cycle " + i + " complete!");

             //In
             byte[] lenBytes = new byte[4];
             inputStream.read(lenBytes, 0, 4);
             int len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
                       ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
             byte[] receivedBytes = new byte[len];
             inputStream.read(receivedBytes, 0, len);
             String received = new String(receivedBytes, 0, len);
     
             System.out.print("\n\nEcho: " + received + "\n\n");
             
 
             // Out
             String toSend = temp;
             byte[] toSendBytes = toSend.getBytes();
             int toSendLen = toSendBytes.length;
             byte[] toSendLenBytes = new byte[4];
             toSendLenBytes[0] = (byte)(toSendLen & 0xff);
             toSendLenBytes[1] = (byte)((toSendLen >> 8) & 0xff);
             toSendLenBytes[2] = (byte)((toSendLen >> 16) & 0xff);
             toSendLenBytes[3] = (byte)((toSendLen >> 24) & 0xff);
             outputStream.write(toSendLenBytes);
             outputStream.write(toSendBytes);
             
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

           i++;
      }
    }

}

