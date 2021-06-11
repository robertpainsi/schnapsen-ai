package schnapsen;

import schnapsen.ai.GeneticAlgorithm;
import schnapsen.ai.GeneticAlgorithm.Snapshot;

import java.io.IOException;
import java.util.Scanner;

import static schnapsen.ai.GeneticAlgorithm.Config;
import static schnapsen.ai.GeneticAlgorithm.snapshotFile;
import static schnapsen.utils.Utils.getTimestamp;
import static schnapsen.utils.Utils.loadObjectFromJSON;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        Scanner in = new Scanner(System.in);
        loadDataLoop:
        while (true) {
            System.out.println("Load population from latest folder?");
            switch (in.nextLine().toLowerCase()) {
                case "yes", "y" -> {
                    System.out.println(getTimestamp() + " Loading snapshot from " + snapshotFile);
                    Snapshot snapshot = loadObjectFromJSON(snapshotFile, Snapshot.class);
                    System.out.println(getTimestamp() + " Loading done");
                    geneticAlgorithm.train(snapshot);
                    break loadDataLoop;
                }
                case "no", "n" -> {
                    Config config = new Config();
                    geneticAlgorithm.train(config);
                    break loadDataLoop;
                }
            }
        }
    }
}
