package mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = Runtime.getRuntime().availableProcessors();
	private static final int MATRIX_SIZE = 2000;

        public static void main(String[] args) {
		
		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		sequentialMultiplyMatrix(a, b);
		parallelMultiplyMatrix(a, b);	
	}
	
	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
		if (a == null || b == null) throw new IllegalArgumentException("Input matrices must not be null");
		int m = a.length;
		int p = a[0].length;
		int n = b[0].length;
		if (p != b.length) throw new IllegalArgumentException("Inner dimensions must match for multiplication");

		double[][] result = new double[m][n];
		for (int i = 0; i < m; i++){
			double[] rowA = a[i];
			double[] rowC = result[i];
			for (int z = 0; z < p; z++){
				double aik = rowA[z];
				double[] rowB = b[z];
				for (int j = 0; j < n; j++){
					rowC[j] += aik * rowB[j];
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * - added parameter for number of threads for testing purposes
	 * @return the result of the multiplication
	 * */
		public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
			if (a == null || b == null) throw new IllegalArgumentException("Input matrices must not be null");
			int m = a.length;
			int p = a[0].length;
			int n = b[0].length;
			if (p != b.length) throw new IllegalArgumentException("Inner dimensions must match for multiplication");

			double[][] result = new double[m][n];

			int numThreads = Math.max(1, NUMBER_THREADS);
			ExecutorService es = Executors.newFixedThreadPool(numThreads);

			// split rows into one contiguous chunk per thread
			int chunkSize = (m + numThreads - 1) / numThreads;
			for (int start = 0; start < m; start += chunkSize) {
				final int rowStart = start;
				final int rowEnd = Math.min(start + chunkSize, m);
				es.execute(() -> {
					for (int row = rowStart; row < rowEnd; row++) {
						double[] rowA = a[row];
						double[] rowC = result[row];
						for (int z = 0; z < p; z++){
							double aik = rowA[z];
							double[] rowB = b[z];
							for (int j = 0; j < n; j++){
								rowC[j] += aik * rowB[j];
							}
						}
					}
				});
			}

			es.shutdown();
			try {
				// wait up to 4 minute for all tasks to finish
				if (!es.awaitTermination(500, java.util.concurrent.TimeUnit.SECONDS)) {
					es.shutdownNow();
					throw new RuntimeException("Timed out waiting for parallel tasks to finish");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}

			return result;

		}
		/**
		 * Returns the result of a concurrent matrix multiplication
		 * The two matrices are randomly generated
		 * @param a is the first matrix
		 * @param b is the second matrix
		 * @param nThreads is the number of threads to use
		 * @return the result of the multiplication
		 * */
		public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b, int nThreads) {
			if (a == null || b == null) throw new IllegalArgumentException("Input matrices must not be null");
			int m = a.length;
			int p = a[0].length;
			int n = b[0].length;
			if (p != b.length) throw new IllegalArgumentException("Inner dimensions must match for multiplication");

			double[][] result = new double[m][n];

			int numThreads = Math.max(1, nThreads);
			ExecutorService es = Executors.newFixedThreadPool(numThreads);

			// split rows into one contiguous chunk per thread
			int chunkSize = (m + numThreads - 1) / numThreads;
			for (int start = 0; start < m; start += chunkSize) {
				final int rowStart = start;
				final int rowEnd = Math.min(start + chunkSize, m);
				es.execute(() -> {
					for (int row = rowStart; row < rowEnd; row++) {
						double[] rowA = a[row];
						double[] rowC = result[row];
						for (int z = 0; z < p; z++){
							double aik = rowA[z];
							double[] rowB = b[z];
							for (int j = 0; j < n; j++){
								rowC[j] += aik * rowB[j];
							}
						}
					}
				});
			}

			es.shutdown();
			try {
				// wait up to 500 seconds for all tasks to finish (single-threaded 4000x4000 needs well over a minute)
				if (!es.awaitTermination(500, java.util.concurrent.TimeUnit.SECONDS)) {
					es.shutdownNow();
					throw new RuntimeException("Timed out waiting for parallel tasks to finish");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}

			return result;

		}

		/**
         * Populates a matrix of given size with randomly generated integers between 0-10.
         * @param numRows number of rows
         * @param numCols number of cols
         * @return matrix
         */
        private static double[][] generateRandomMatrix (int numRows, int numCols) {
             double matrix[][] = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }
	
}
