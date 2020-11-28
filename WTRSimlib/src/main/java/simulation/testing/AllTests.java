package simulation.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import simulation.testing.ConcurrentSafetyTests;

@RunWith(Suite.class)
@SuiteClasses({
        BasicTestClass.class,
        ConcurrentSafetyTests.class,
        //More tests go here
        })

public class AllTests {

}