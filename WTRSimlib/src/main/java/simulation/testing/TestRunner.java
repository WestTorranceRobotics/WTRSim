package simulation.testing;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import simulation.launch.SimStarter;

class TestRunner {
    public static void main(String [] args) throws InterruptedException {
        new SimStarter().start();
        // Result result = JUnitCore.runClasses(BasicTestClass.class);
        // for (Failure failure : result.getFailures()) {
        //     System.out.println(failure.toString());
        // } 
    }
}