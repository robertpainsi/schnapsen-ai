package schnapsen.ai;

import schnapsen.logic.GameLogic;
import schnapsen.models.Card;
import schnapsen.models.Game;
import schnapsen.models.Player;
import schnapsen.models.Talon;
import schnapsen.utils.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static schnapsen.utils.Utils.getTimestamp;
import static schnapsen.utils.Utils.saveObjectAsJSON;

public class GeneticAlgorithm {
    public static final File latestFolder = new File("./data/latest/");
    public static final File snapshotFile = new File(latestFolder, "snapshot.json");
    public static final File bestFile = new File(latestFolder, "best.json");

    public static class Snapshot {
        final Config config;
        final int generation;
        final List<TrainingNetwork> population;

        public Snapshot(Config config, List<TrainingNetwork> population, int generation) {
            this.config = config;
            this.population = population;
            this.generation = generation;
        }
    }

    public static class  Config {
        public int generations = 1000;
        public int gamesPerCombination = 42;
        public int populationSize = 450;
        public float mutationRate = 0.1f;
        public int[] layers = new int[]{72, 42, 42, 20};

        @Override
        public String toString() {
            return "Config{" +
                    "generations=" + generations +
                    ", gamesPerCombination=" + gamesPerCombination +
                    ", populationSize=" + populationSize +
                    ", mutationRate=" + mutationRate +
                    ", layers=" + Arrays.toString(layers) +
                    '}';
        }
    }

    public static class GameTask implements Runnable {
        private final TrainingNetwork a;
        private final TrainingNetwork b;
        private final List<Card> cards;

        public GameTask(List<Card> cards, TrainingNetwork a, TrainingNetwork b) {
            this.cards = cards;
            this.a = a;
            this.b = b;
        }

        @Override
        public void run() {
            Talon talon = new Talon(cards);
            Computer computerA = new Computer(talon.drawCards(5), a.neuralNetwork);
            Computer computerB = new Computer(talon.drawCards(5), b.neuralNetwork);
            Game game = new Game(talon, Arrays.asList(computerA, computerB));

            Player winner = GameLogic.playGame(game);
            if (winner == computerA) {
                a.increaseFitness(winner.points);
            } else {
                b.increaseFitness(winner.points);
            }
        }
    }

    public void train(Config config) throws InterruptedException, IOException {
        List<TrainingNetwork> population = new ArrayList<>();
        for (int i = 0; i < config.populationSize; i++) {
            population.add(new TrainingNetwork(new NeuralNetwork(config.layers)));
        }
        train(config, population, 0);
    }

    public void train(Snapshot snapshot) throws InterruptedException, IOException {
        for (TrainingNetwork trainingNetwork : snapshot.population) {
            trainingNetwork.resetFitness();
        }
        train(snapshot.config, snapshot.population, snapshot.generation);
    }

    public void train(Config config, List<TrainingNetwork> population, int startAtGeneration) throws InterruptedException, IOException {
        StopWatch totalTrainStopWatch = new StopWatch();
        System.out.println(getTimestamp() + " " + config);
        for (int generation = startAtGeneration; true; generation++) {
            StopWatch generationStopWatch = new StopWatch();
            System.out.println();
            System.out.println(getTimestamp() + " Training generation #" + generation);
            final ExecutorService executorService = Executors.newFixedThreadPool(
                    Math.max(1, Runtime.getRuntime().availableProcessors()));
            for (int gameCombination = 0; gameCombination < config.gamesPerCombination; gameCombination++) {
                List<Card> shuffledCards = new ArrayList<>(Card.cards);
                Collections.shuffle(shuffledCards);

                for (TrainingNetwork a : population) {
                    for (TrainingNetwork b : population) {
                        if (a == b) {
                            continue;
                        }
                        executorService.execute(new GameTask(shuffledCards, a, b));
                    }
                }
            }
            executorService.shutdown();
            if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Waiting for all games to finish timed out");
            }

            Collections.sort(population);
            TrainingNetwork bestTrainingNetwork = population.get(0);
            TrainingNetwork worstTrainingNetwork = population.get(population.size() - 1);
            System.out.println(getTimestamp() + " Best fitness:   " + bestTrainingNetwork.getFitness());
            System.out.println(getTimestamp() + " Middle fitness: " + population.get(population.size() / 2).getFitness());
            System.out.println(getTimestamp() + " Mean fitness:   " + ((float) totalFitness(population) / population.size()));
            System.out.println(getTimestamp() + " Worst fitness:  " + worstTrainingNetwork.getFitness());

            System.out.println(getTimestamp() + " Storing data for generation #" + generation);
            Thread.sleep(1000);
            saveObjectAsJSON(new File(latestFolder, "generation-" + generation + "-best.json"), bestTrainingNetwork);
            saveObjectAsJSON(new File(latestFolder, "generation-" + generation + "-fitness.json"),
                    population.stream().map(TrainingNetwork::getFitness).collect(Collectors.toList()));
            saveObjectAsJSON(bestFile, bestTrainingNetwork);
            saveObjectAsJSON(snapshotFile, new Snapshot(config, population, generation));

            if (generation >= config.generations - 1) {
                break;
            }

            List<TrainingNetwork> newPopulation = new ArrayList<>();
            for (int k = 0; k < population.size(); k++) {
                NeuralNetwork newNeuralNetwork;
                if (k == 0) {
                    // Elitism
                    newNeuralNetwork = population.get(0).neuralNetwork;
                } else if (k < population.size() / 2) {
                    newNeuralNetwork = new NeuralNetwork(selectRandomNeuralNetwork(population));
                } else {
                    newNeuralNetwork = new NeuralNetwork(
                            selectRandomNeuralNetwork(population).crossover(selectRandomNeuralNetwork(population)));
                }
                newNeuralNetwork.mutate(config.mutationRate);
                newPopulation.add(new TrainingNetwork(newNeuralNetwork));
            }
            population = newPopulation;
            System.out.println(getTimestamp() + " Generation runtime " + generationStopWatch.getElapsedFormatted());
        }
        System.out.println(getTimestamp() + " Total runtime " + totalTrainStopWatch.getElapsedFormatted());
    }

    private NeuralNetwork selectRandomNeuralNetwork(List<TrainingNetwork> networks) {
        int totalFitness = totalFitness(networks);
        int rand = new Random().nextInt(totalFitness);
        int runningSum = 0;

        for (TrainingNetwork network : networks) {
            runningSum += network.getFitness();
            if (runningSum > rand) {
                return network.neuralNetwork;
            }
        }

        return networks.get(0).neuralNetwork;
    }

    private int totalFitness(List<TrainingNetwork> networks) {
        int totalFitness = 0;
        for (TrainingNetwork network : networks) {
            totalFitness += network.getFitness();
        }
        return totalFitness;
    }
}
