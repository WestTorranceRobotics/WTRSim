package simulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Run SimInit as a thread to launch and intialize simulator.
 * 
 * If using a custom unity project path, pass the path as a string
 * ex: "new SimInit(stringPathName).start();""
 */
public class SimInit extends Thread{
    volatile boolean kill = false;
    int port = 4512;
    String unityVersion = "2019.4.2f1";
    String unityProjectPath = "C:\\WTRSim\\WTRSimUnity";
    String unityDefaultScenePath = "\\Assets\\DefaultScene.unity";
    InputStream inputStream;
    OutputStream outputStream;

    /**
     * Default Constructor
     */
    SimInit () {} 
    
    /**
     *  Construct with custom project path.
     *  ex: "C:\\WTRSim\\WTRSimUnity"
     * @param unityProjectPath Must be path to Unity folder
     */
    SimInit(String unityProjectPath) {
        this.unityProjectPath = unityProjectPath;
    }

    volatile Socket socket;
    ServerSocket serverSocket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public void run() {
        startSimulator();
    }

    private void startSimulator() {
       initSocket();
       verifyConnection();
    }

    /**
     * Initializes socket connection
     */
    private void initSocket() {
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
            SimSocket simsocket = new SimSocket(dataOutputStream, dataInputStream, socket, serverSocket, inputStream, outputStream);
            System.out.println("Starting communication!");
            simsocket.start(); //start IO
    }

    /**
     * Launches unity simulator.
     * @param socketAccept The thread that attempts to accept unity client.
     */
    private void launchUnitySimulator(Thread socketAccept) {
        try {
            Process launchUnity = Runtime.getRuntime().exec("C:\\Program Files\\Unity\\Hub\\Editor\\" + 
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

    // Send and recieve echo to verify connection to simulator
    private void verifyConnection() {

    }
}