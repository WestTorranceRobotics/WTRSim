package simulation;

import simulation.launch.SimStarter;
import edu.wpi.first.hal.sim.*;
import edu.wpi.first.hal.*;

public class Testing{
    public static void main(String [] args) {
      //  new SimStarter().start();
        HAL.runMain();
        System.out.print(HAL.hasMain());
      
        
    }
}