package fibonacci;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FibonacciIterativeSolutionTest implements FibonacciTest {

    @Test
    void canary() {
        assertTrue(true);
    }

    public FibonacciSolution createInstance() {
        return new FibonacciIterativeSolution();
    }

    @Test
    void testFibIterativeForPosition50() {
        FibonacciIterativeSolution fibonacciIterativeSolution = new FibonacciIterativeSolution();

        assertEquals(20365011074L, fibonacciIterativeSolution.compute(50).longValue());
    }

    @Test
    void simpleIterationForPosition300(){
        FibonacciIterativeSolution fibonacciIterativeSolution = new FibonacciIterativeSolution();

        assertEquals("359579325206583560961765665172189099052367214309267232255589801",
                String.valueOf(fibonacciIterativeSolution.compute(300)));
    }
}
