package schnapsen.models;

public class PlayerDummy extends Player {
    public PlayerDummy(Player player) {
        super(player);
    }

    public PlayerDummy() {
        super();
    }

    @Override
    public Move getFirstMove(Game game) {
        return null;
    }

    @Override
    public Move getSecondMove(Game game, Move opponentMove) {
        return null;
    }

    @Override
    public void selfMove(Move selfMove) {

    }

    @Override
    public void opponentMove(Move opponentMove) {

    }
}
