package simulation.launch;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;

class UnityLauncher implements Runnable {
    int port = 4512;
    String unityVersion = "2019.4.2f1";
    String unityProjectPath;
    String unityDefaultScenePath = "\\Assets\\DefaultScene.unity";
    InputStream inputStream;
    OutputStream outputStream;
    ServerSocket serverSocket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    volatile Socket socket;
    volatile boolean kill = false;


    UnityLauncher() {
        this.unityProjectPath = System.getenv("userprofile") + File.separator + "Documents" + "\\WTRSim\\WTRSimUnity";
    }

    UnityLauncher(String unityProjectPath) {
        this.unityProjectPath = unityProjectPath;
    }

    /**
     * Initializes socket connection and starts Unity
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            System.out.print("\n\n WAITING FOR CLIENT \n\n" );
            Thread socketAccept = new Thread(){public void run(){ try { socket = serverSocket.accept();
            System.out.print("\n\n CLIENT ACCEPTED \n\n" );} catch (IOException e) { System.out.println("Failed to connect to Unity client"); e.printStackTrace();} }};

            socketAccept.start();
            launchUnitySimulator(socketAccept);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);

            Runtime.getRuntime().addShutdownHook(new Thread() { public void run(){ try { 
                socket.close();
                serverSocket.close(); 
                dataInputStream.close();
                dataOutputStream.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }}});

        }
        catch (IOException e) {
            e.printStackTrace();
        }
            Runnable socketHandler = new SocketHandler(dataOutputStream, dataInputStream, socket, serverSocket, inputStream, outputStream);
            System.out.println("Starting communication!");

            socketHandler.run(); //start IO
    }

    /**
     * Launches unity simulator.
     * @param socketAccept The thread that attempts to accept unity client.
     */
    private void launchUnitySimulator(Thread socketAccept) {
        try {
            Runtime.getRuntime().exec("C:\\Program Files\\Unity\\Hub\\Editor\\" + 
                unityVersion + "\\Editor\\Unity.exe -projectPath " + unityProjectPath + " -openfile " + unityProjectPath + unityDefaultScenePath); // Launch unity

            Runtime.getRuntime().addShutdownHook(new Thread() { public void run(){ 
                try { Runtime.getRuntime().exec("Taskkill /IM Unity.exe /F"); } catch(IOException e) {} } } ); //Kill unity at shutdown
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        try{
            socketAccept.join();
       
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
    } 
}