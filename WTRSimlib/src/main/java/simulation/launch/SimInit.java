package simulation.launch;

/**
 * Run SimInit as a thread to launch and intialize simulator.
 * 
 * If using a custom unity project path, pass the path as a string
 * ex: "new SimInit(stringPathName).start();""
 */
public class SimInit extends Thread{
    InitializationProcess launchUnity;

    /**
     * Default Constructor
     */
    public SimInit () {
        launchUnity = new UnityLauncher();
    } 
    
    /**
     *  Construct with custom project path.
     *  ex: "C:\\WTRSim\\WTRSimUnity"
     * @param unityProjectPath Must be path to Unity folder
     */
    public SimInit(String unityProjectPath) {
        launchUnity = new UnityLauncher(unityProjectPath);
    }

    public void run() {
        launchUnity.initProcess();
    }

}