package ca.mcgill.ecse420.a1;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import mcgill.ecse420.a1.MatrixMultiplication;

import org.junit.jupiter.api.Test;

class MatrixMultiplicationMoreTest {

    private static final double DELTA = 1e-9;

    @Test
    void parallelThrowsOnInvalidDimensions() {
        double[][] a = {
                {1, 2, 3},
                {4, 5, 6}
        };
        double[][] b = {
                {7, 8},
                {9, 10}
        };

        assertThrows(IllegalArgumentException.class, () ->
                MatrixMultiplication.parallelMultiplyMatrix(a, b));
    }

    @Test
    void identityMatrixMultiplication() {
        double[][] a = {
                {2, 3, 4, 5},
                {1, 0, 2, 3}
        };
        double[][] id = identity(4);

        double[][] seq = MatrixMultiplication.sequentialMultiplyMatrix(a, id);
        double[][] par = MatrixMultiplication.parallelMultiplyMatrix(a, id);

        assertMatrixEquals(a, seq);
        assertMatrixEquals(a, par);
        assertMatrixEquals(seq, par);
    }

    @Test
    void randomSmallMatricesMatch() {
        for (int t = 0; t < 20; t++) {
            int m = 1 + (int) (Math.random() * 5);
            int p = 1 + (int) (Math.random() * 5);
            int n = 1 + (int) (Math.random() * 5);

            double[][] a = randomMatrix(m, p);
            double[][] b = randomMatrix(p, n);

            double[][] seq = MatrixMultiplication.sequentialMultiplyMatrix(a, b);
            double[][] par = MatrixMultiplication.parallelMultiplyMatrix(a, b);

            assertMatrixEquals(seq, par);
        }
    }

    private static void assertMatrixEquals(double[][] expected, double[][] actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected.length, actual.length, "row count");
        for (int row = 0; row < expected.length; row++) {
            assertArrayEquals(expected[row], actual[row], DELTA, "row " + row);
        }
    }

    private static double[][] identity(int size) {
        double[][] id = new double[size][size];
        for (int i = 0; i < size; i++) id[i][i] = 1.0;
        return id;
    }

    private static double[][] randomMatrix(int r, int c) {
        double[][] m = new double[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                m[i][j] = (double) ((int) (Math.random() * 10));
            }
        }
        return m;
    }
}
