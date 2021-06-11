package schnapsen.models;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public final Talon talon;
    public final List<Player> players;

    public Game(Talon talon, List<Player> players) {
        this.talon = talon;
        this.players = new ArrayList<>(players);
    }
}
