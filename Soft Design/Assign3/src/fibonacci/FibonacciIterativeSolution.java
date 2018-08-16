package fibonacci;

import java.math.BigInteger;

public class FibonacciIterativeSolution implements FibonacciSolution {

    public BigInteger compute(int position) {
        BigInteger next = BigInteger.valueOf(1);
        BigInteger current = BigInteger.valueOf(1);

        for(int i = 3; i <= position + 1; i++) {
            BigInteger temp = next;
            next = next.add(current);
            current = temp;
        }

        return next;
    }
}
