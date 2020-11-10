package simulation.launch;

import java.io.File;

/**
 * Run SimInit as a thread to launch and intialize simulator.
 * 
 * If using a custom unity project path, pass the path as a string
 * ex: "new SimInit(stringPathName).start();""
 */
public class SimStarter extends Thread{
    String unityVersion = "2019.4.2f1";
    String unityProjectPath;
    String unityDefaultScenePath = "\\Assets\\DefaultScene.unity";

    Runnable socketHandler;
    Runnable launchUnity;
    

    /**
     * Default Constructor
     */
    public SimStarter() {
        this.unityProjectPath = System.getenv("userprofile") + File.separator + "Documents" + "\\WTRSim\\WTRSimUnity";
        launchUnity = new UnityLauncher(unityVersion, unityProjectPath, unityDefaultScenePath);
        socketHandler = new SocketHandler();
    } 
    
    /**
     *  Construct with custom project path.
     *  ex: "C:\\WTRSim\\WTRSimUnity"
     * @param unityProjectPath Must be path to Unity project folder
     */
    public SimStarter(String unityProjectPath) {
        this.unityProjectPath = unityProjectPath;
        launchUnity = new UnityLauncher(unityVersion, unityProjectPath, unityDefaultScenePath);
        socketHandler = new SocketHandler();
    }

    public void run() {
        launchUnity.run();
        socketHandler.run(); 
    }
}
