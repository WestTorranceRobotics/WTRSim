package simulation.launch;

import java.io.File;

/**
 * Run SimInit as a thread to launch and intialize simulator.
 * 
 * <p>If using a custom unity project path, pass the path as a string
 * ex: "new SimInit(stringPathName).start();""
 */
public class SimStarter extends Thread {
    String unityVersion = "2019.4.2f1";
    String unityProjectPath;
    String unityDefaultScenePath;

    Runnable socketManager;
    Runnable launchUnity;
    

    /**
     * Default Constructor.
     */
    public SimStarter() {
        this.unityProjectPath = 
            System.getenv("userprofile") + File.separator + 
            "Documents" + File.separator + 
            "WTRSim" + File.separator + 
            "WTRSimUnity"
        ;

        this.unityDefaultScenePath = File.separator + 
            "Assets" + File.separator + "Scenes" + File.separator + "DefaultScene.unity";

        launchUnity = new UnityLauncher(
            unityVersion, 
            unityProjectPath, 
            unityDefaultScenePath
        );

        socketManager = new SocketManager();
    } 
    
    /**
     *  Construct with custom project path.
     *  ex: "C:\\WTRSim\\WTRSimUnity"
     *
     * @param unityProjectPath Must be path to Unity project folder
     */
    public SimStarter(String unityProjectPath) {
        this.unityProjectPath = unityProjectPath;
        launchUnity = new UnityLauncher(
            unityVersion, 
            unityProjectPath, 
            unityDefaultScenePath
        );

        socketManager = new SocketManager();
    }

    public void run() {
        launchUnity.run();
        socketManager.run(); 
    }
}
