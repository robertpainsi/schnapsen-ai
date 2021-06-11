package schnapsen.ai;

import java.io.Serializable;

public class TrainingNetwork implements Comparable<TrainingNetwork>, Serializable {
    private volatile int fitness = 0;
    public final NeuralNetwork neuralNetwork;

    public TrainingNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public int getFitness() {
        return fitness;
    }

    public void resetFitness() {
        fitness = 0;
    }

    public synchronized void increaseFitness(int by) {
        fitness += by;
    }

    @Override
    public int compareTo(TrainingNetwork other) {
        return Integer.compare(other.fitness, fitness);
    }

    @Override
    public String toString() {
        return "TrainingNetwork{" +
                "fitness=" + fitness +
                ", neuralNetwork=" + neuralNetwork +
                '}';
    }
}
