package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

class MultiThreadedSumMatrix implements SumMatrix {
    private final int nthread;

    MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    @Override
    public double sum(final double[][] matrix) {
        int dim = 0;
        for (final double[] l : matrix) {
            dim += l.length;
        }

        final int size = dim % nthread + dim / nthread;

        final List<Worker> workers = new ArrayList<>();
        int row = 0, col = 0;
        for (int i = 0; i < dim && row < matrix.length; i += size) {
            workers.add(new Worker(matrix, row, col, size));
            col += size;
            while (row < matrix.length && col >= matrix[row].length) {
                col -= matrix[row].length;
                row++;
            }
        }
        for (final Worker w : workers) {
            w.start();
        }
        long sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getSum();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private int rstart;
        private int cstart;
        private final int steps;
        private long sum;

        Worker(final double[][] matrix, final int rstart, final int cstart, final int steps) {
            this.matrix = matrix; // NOPMD
            this.rstart = rstart;
            this.cstart = cstart;
            this.steps = steps;
        }

        @Override
        public void run() {
            int cursor = 0;
            while (cursor < steps && (rstart < matrix.length - 1 || cstart < matrix[matrix.length - 1].length)) {
                if (cstart >= matrix[rstart].length) {
                    rstart++;
                    cstart = 0;
                }
                sum += matrix[rstart][cstart];
                cstart++;
                cursor++;
            }
        }

        public long getSum() {
            return sum;
        }
    }
}
