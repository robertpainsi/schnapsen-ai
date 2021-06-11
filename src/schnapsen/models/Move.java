package schnapsen.models;

public class Move {
    public static class SwapTrump {
        public final Card oldTrump;
        public final Card newTrump;

        public SwapTrump(Card oldTrump, Card newTrump) {
            this.oldTrump = oldTrump;
            this.newTrump = newTrump;
        }
    }

    public final Player player;
    public SwapTrump swapTrump;
    public boolean closeTalon;
    public Marriage marriage;
    public Card card;

    public Move(Player player) {
        this.player = player;
    }
}
