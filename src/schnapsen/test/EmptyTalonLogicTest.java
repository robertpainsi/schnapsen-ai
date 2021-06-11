package schnapsen.test;

import schnapsen.logic.EmptyTalonLogic;
import schnapsen.logic.EmptyTalonLogic.Scenario;
import schnapsen.models.Player;

import java.io.File;
import java.io.IOException;

import static schnapsen.logic.EmptyTalonLogic.testDataFolder;
import static schnapsen.utils.Utils.loadObjectFromJSON;

public class EmptyTalonLogicTest {

    public static void main(String[] args) throws IOException {
        Scenario scenario = loadObjectFromJSON(new File(testDataFolder, "4x5.json"), Scenario.class);

        EmptyTalonLogic emptyTalonLogic = new EmptyTalonLogic();
        Player winner = emptyTalonLogic.getWinner(scenario.game, scenario.firstPlayer, scenario.secondPlayer);
        System.out.println(winner.id.equals(scenario.firstPlayer.id) ? "First player wins" : "Second player wins");
    }
}
