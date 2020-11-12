package simulation.launch;

import java.io.IOException;

class UnityLauncher implements Runnable {

    String unityVersion;
    String unityProjectPath;
    String unityDefaultScenePath;

    UnityLauncher(String unityVersion, String unityProjectPath, 
        String unityDefaultScenePath) {
            
        this.unityVersion = unityVersion;
        this.unityProjectPath = unityProjectPath;
        this.unityDefaultScenePath = unityDefaultScenePath;
    }

    @Override
    public void run() {
        System.out.println("Launching Unity Client... ");
        try {
            Runtime.getRuntime().exec("C:\\Program Files\\Unity\\Hub\\Editor\\" 
                + unityVersion + "\\Editor\\Unity.exe -projectPath " 
                + unityProjectPath + " -openfile " + unityProjectPath 
                + unityDefaultScenePath); // Launch unity

            Runtime.getRuntime().addShutdownHook(new Thread() { 
                public void run() { 
                    try { 
                        Runtime.getRuntime().exec("Taskkill /IM Unity.exe /F"); 
                    } catch (IOException e) { 
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}