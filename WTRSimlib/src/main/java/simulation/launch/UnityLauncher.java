package simulation.launch;

import java.io.IOException;

class UnityLauncher implements Runnable {

    String unityVersion = "2019.4.2f1";
    String unityProjectPath = "C:\\WTRSim\\WTRSimUnity";
    String unityDefaultScenePath = "\\Assets\\DefaultScene.unity";

    Runnable socketHandler;


    UnityLauncher() {
        socketHandler = new SocketHandler();
    }

    UnityLauncher(String unityProjectPath) {
        socketHandler = new SocketHandler();
        this.unityProjectPath = unityProjectPath;
    }

    /**
     * Initializes socket connection and starts Unity
     */
    @Override
    public void run() {
        System.out.println("Launching Unity Client");
        launchUnitySimulator();
        socketHandler.run(); 
    }

    /**
     * Launches unity simulator.
     * @param socketAccept The thread that attempts to accept unity client.
     */
    private void launchUnitySimulator() {
        try {
            Runtime.getRuntime().exec("C:\\Program Files\\Unity\\Hub\\Editor\\" + 
                unityVersion + "\\Editor\\Unity.exe -projectPath " + unityProjectPath + " -openfile " + unityProjectPath + unityDefaultScenePath); // Launch unity

            Runtime.getRuntime().addShutdownHook(new Thread() { public void run(){ 
                try { Runtime.getRuntime().exec("Taskkill /IM Unity.exe /F"); } catch(IOException e) {} } } ); //Kill unity at shutdown
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}