package simulation.testing;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.DoubleFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;



@RunWith(Parameterized.class)
public class BasicTestClass {
    int input;
    int expected;
    

    public BasicTestClass(int input, int expected) {
        this.input = input;
        this.expected = expected;
    }

    @Parameters(name = "{index}: {0}={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                { 0, 0 }, { 1, 1 }, { 2, 2 }, 
                { 3, 3 }, { 4, 4 }, { 5, 5 }, 
                { 6, 6 }, {7, 7}, {8, 8}, {9, 9}  
            });
    }

    DoubleFunction<Double> returnInput = (input) -> { 
        return input;
    };


    @Test
    public void test_basic() {
        assertEquals(input + " yielded " + returnInput.apply(input), 
            expected, returnInput.apply(input), 0);
    }
}