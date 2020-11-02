package simulation.launch;

/**
 * Run SimInit as a thread to launch and intialize simulator.
 * 
 * If using a custom unity project path, pass the path as a string
 * ex: "new SimInit(stringPathName).start();""
 */
public class SimStarter extends Thread{
    Runnable launchUnity;

    /**
     * Default Constructor
     */
    public SimStarter() {
        launchUnity = new UnityLauncher();
    } 
    
    /**
     *  Construct with custom project path.
     *  ex: "C:\\WTRSim\\WTRSimUnity"
     * @param unityProjectPath Must be path to Unity folder
     */
    public SimStarter(String unityProjectPath) {
        launchUnity = new UnityLauncher(unityProjectPath);
    }

    public void run() {
        launchUnity.run();
    }

}