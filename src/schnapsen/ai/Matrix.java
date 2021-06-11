package schnapsen.ai;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class Matrix implements Serializable {
    private final static Random random = new Random();

    public final int rows;
    public final int cols;
    public final float[][] m;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.m = new float[rows][cols];
    }

    public Matrix(Matrix origin) {
        this(origin.rows, origin.cols);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(origin.m[i], 0, this.m[i], 0, cols);
        }
    }

    public static Matrix randomMatrix(int rows, int cols) {
        Matrix randomMatrix = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                randomMatrix.m[i][j] = random.nextFloat() * 2 - 1; // random [-1, 1]
            }
        }
        return randomMatrix;
    }

    public static Matrix singleColumnMatrixFromArray(float[] array) {
        Matrix result = new Matrix(array.length, 1);
        for (int i = 0; i < array.length; i++) {
            result.m[i][0] = array[i];
        }
        return result;
    }

    public Matrix dot(Matrix n) {
        if (cols != n.rows) {
            throw new RuntimeException(cols + " != " + n.rows);
        }

        Matrix result = new Matrix(rows, n.cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < n.cols; j++) {
                float sum = 0;
                for (int k = 0; k < cols; k++) {
                    sum += m[i][k] * n.m[k][j];
                }
                result.m[i][j] = sum;
            }
        }
        return result;
    }

    public Matrix addBias() {
        Matrix n = new Matrix(rows + 1, 1);
        for (int i = 0; i < rows; i++) {
            n.m[i][0] = m[i][0];
        }
        n.m[rows][0] = 1;
        return n;
    }

    public Matrix activate() {
        Matrix n = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                n.m[i][j] = sigmoid(m[i][j]);
            }
        }
        return n;
    }

    private float sigmoid(float x) {
        return (float) (1 / (1 + Math.pow(Math.E, -x)));
    }

    public Matrix mutate(float mutationRate) {
        Matrix result = new Matrix(this);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (Math.random() < mutationRate) {
                    result.m[i][j] += random.nextGaussian() / 5;

                    if (result.m[i][j] > 1) {
                        result.m[i][j] = 1;
                    } else if (m[i][j] < -1) {
                        result.m[i][j] = -1;
                    }
                }
            }
        }
        return result;
    }

    public Matrix crossover(Matrix partner) {
        Matrix child = new Matrix(rows, cols);

        int randR = random.nextInt(rows);
        int randC = random.nextInt(cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((i < randR) || (i == randR && j <= randC)) {
                    child.m[i][j] = m[i][j];
                } else {
                    child.m[i][j] = partner.m[i][j];
                }
            }
        }
        return child;
    }

    public float[] toArray() {
        float[] result = new float[rows * cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(m[i], 0, result, i * cols, cols);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "rows=" + rows +
                ", cols=" + cols +
                ", m=" + Arrays.deepToString(m) +
                '}';
    }
}
