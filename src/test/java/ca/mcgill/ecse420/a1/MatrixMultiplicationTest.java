package ca.mcgill.ecse420.a1;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import mcgill.ecse420.a1.MatrixMultiplication;

import org.junit.jupiter.api.Test;

class MatrixMultiplicationTest {

    private static final double DELTA = 1e-9;

    @Test
    void sequentialMultiplicationWorks() {
        double[][] a = {
                {1, 2, 3},
                {4, 5, 6}
        };
        double[][] b = {
                {7, 8},
                {9, 10},
                {11, 12}
        };
        double[][] expected = {
                {58, 64},
                {139, 154}
        };

        double[][] actual = MatrixMultiplication.sequentialMultiplyMatrix(a, b);
        assertMatrixEquals(expected, actual);
    }

    @Test
    void sequentialThrowsOnInvalidDimensions() {
        double[][] a = {
                {1, 2, 3},
                {4, 5, 6}
        };
        double[][] b = {
                {7, 8},
                {9, 10}
        };

        assertThrows(IllegalArgumentException.class, () ->
                MatrixMultiplication.sequentialMultiplyMatrix(a, b));
    }

    @Test
    void parallelMatchesSequential() {
        double[][] a = {
                {2, 0, 1},
                {3, 4, 5},
                {1, 1, 1}
        };
        double[][] b = {
                {1, 2},
                {3, 4},
                {5, 6}
        };

        double[][] sequential = MatrixMultiplication.sequentialMultiplyMatrix(a, b);
        double[][] parallel = MatrixMultiplication.parallelMultiplyMatrix(a, b);

        assertMatrixEquals(sequential, parallel);
    }

    private static void assertMatrixEquals(double[][] expected, double[][] actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected.length, actual.length, "row count");
        for (int row = 0; row < expected.length; row++) {
            assertArrayEquals(expected[row], actual[row], DELTA, "row " + row);
        }
    }
}
