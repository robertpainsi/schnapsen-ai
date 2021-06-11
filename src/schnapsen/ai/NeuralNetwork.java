package schnapsen.ai;

import java.io.Serializable;
import java.util.Arrays;

import static schnapsen.ai.Matrix.randomMatrix;

public class NeuralNetwork implements Serializable {
    private final int[] nodesInLayers;
    private final Matrix[] layers;

    public NeuralNetwork(int[] layers) {
        this.nodesInLayers = Arrays.copyOf(layers, layers.length);
        this.layers = new Matrix[layers.length];
        this.layers[0] = randomMatrix(layers[1], layers[0] + 1);
        for (int i = 1; i < layers.length - 1; i++) {
            int layer = layers[i];
            this.layers[i] = randomMatrix(layer, layer + 1);
        }
        this.layers[layers.length - 1] = randomMatrix(layers[layers.length - 1], layers[layers.length - 2] + 1);
    }

    public NeuralNetwork(NeuralNetwork neuralNetwork) {
        this.nodesInLayers = Arrays.copyOf(neuralNetwork.nodesInLayers, neuralNetwork.nodesInLayers.length);
        this.layers = new Matrix[neuralNetwork.layers.length];
        for (int i = 0; i < layers.length; i++) {
            this.layers[i] = new Matrix(neuralNetwork.layers[i]);
        }
    }

    public NeuralNetwork mutate(float mutationRate) {
        NeuralNetwork result = new NeuralNetwork(this);
        for (int i = 0; i < layers.length; i++) {
            result.layers[i] = result.layers[i].mutate(mutationRate);
        }
        return result;
    }

    public NeuralNetwork crossover(NeuralNetwork partner) {
        NeuralNetwork child = new NeuralNetwork(this);
        for (int i = 0; i < layers.length; i++) {
            child.layers[i] = layers[i].crossover(partner.layers[i]);
        }
        return child;
    }

    public float[] output(float[] input) {
        Matrix matrix = Matrix.singleColumnMatrixFromArray(input);
        matrix = matrix.addBias();
        for (int i = 0; i < layers.length; i++) {
            Matrix layer = layers[i];

            matrix = layer.dot(matrix);
            matrix = matrix.activate();
            if (i < layers.length - 1) {
                matrix = matrix.addBias();
            }
        }
        return matrix.toArray();
    }

    @Override
    public String toString() {
        return "NeuralNetwork{" +
                "layers=" + Arrays.toString(layers) +
                '}';
    }
}
