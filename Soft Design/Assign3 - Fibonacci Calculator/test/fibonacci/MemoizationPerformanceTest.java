package fibonacci;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MemoizationPerformanceTest {

    long computeTime(FibonacciSolution fibonacciSolution) {
        long start = System.currentTimeMillis();
        fibonacciSolution.compute(30);
        return System.currentTimeMillis() - start;
    }

    @Test
    void testMemoizedPerformanceIsBetterThanRecursive() {
        long recursiveTime = computeTime(new FibonacciRecursiveSolution());
        long memoizedTime = computeTime(new FibonacciMemoizedSolution());

        assertTrue(memoizedTime < (recursiveTime / 10));
    }
}
