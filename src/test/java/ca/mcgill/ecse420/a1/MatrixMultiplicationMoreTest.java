package ca.mcgill.ecse420.a1;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

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

    @Test
    void testVaryingBySize() {
        int[] sizes = {100, 200, 500, 1000, 2000, 3000, 4000};
        int nThreads = 11;

        StringBuilder sb = new StringBuilder("size,sequential_ns,parallel_ns\n");
        for (int size : sizes) {
            double[][] a = randomMatrix(size, size);
            double[][] b = randomMatrix(size, size);

            double[][][] seq = new double[1][][];
            double[][][] par = new double[1][][];
            long seqTime = timeNanos(() -> seq[0] = MatrixMultiplication.sequentialMultiplyMatrix(a, b));
            long parTime = timeNanos(() -> par[0] = MatrixMultiplication.parallelMultiplyMatrix(a, b, nThreads));
            assertMatrixEquals(seq[0], par[0]);

            sb.append(size).append(',').append(seqTime).append(',').append(parTime).append('\n');
            System.out.println(size + ", seq=" + seqTime + "ns, par=" + parTime + "ns");
        }

        File file = new File("times.csv");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void testVaryingByThreadCount() {
        int size = 4000;
        int maxThreads = 16;
        double[][] a = randomMatrix(size, size);
        double[][] b = randomMatrix(size, size);

        StringBuilder sb = new StringBuilder("threads,parallel_ns\n");
        for (int nThreads = 1; nThreads <= maxThreads; nThreads++) {
            int threads = nThreads;
            long parTime = timeNanos(() -> MatrixMultiplication.parallelMultiplyMatrix(a, b, threads));

            sb.append(threads).append(',').append(parTime).append('\n');
            System.out.println(threads + " threads, par=" + parTime + "ns");
        }

        File file = new File("threads.csv");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static long timeNanos(Runnable r) {
        long startTime = System.nanoTime();
        r.run();
        return System.nanoTime() - startTime;
    }

    @Test 
    void testVaryingSize(){
        HashMap<Integer, Long> times = new HashMap<>();
        for (int t = 1; t < 100; t++) {
            int size = 1000;
            double[][] a = randomMatrix(size, size);
            double[][] b = randomMatrix(size, size);

            double[][] seq = MatrixMultiplication.sequentialMultiplyMatrix(a, b);
            long startTime = System.nanoTime();
            double[][] par = MatrixMultiplication.parallelMultiplyMatrix(a, b);
            long endTime = System.nanoTime();
            times.put(t, endTime - startTime);
            
        }
        for (int t = 1; t < 100; t++) {
            System.out.println(t + ", " + times.get(t));
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
