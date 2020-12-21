package simulation.testing;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import simulation.launch.SimStarter;


public class TestRunner {

    public static void main(String [] args) {  
        new SimStarter().start();   
        // Result result = JUnitCore.runClasses();
        // for (Failure failure : result.getFailures()) {
        //     System.out.println(failure.toString());
        // }
    }
}
