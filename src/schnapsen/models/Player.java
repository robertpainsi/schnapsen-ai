package schnapsen.models;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final String id;
    public final List<Card> hand;
    public final List<Trick> tricks;
    public final List<Marriage> marriages;
    private int tricksValue;
    public int points;

    public Player() {
        id = null;
        hand = null;
        tricks = null;
        marriages = null;
    }

    public Player(String id, List<Card> hand) {
        this.id = id;
        this.hand = hand;
        this.tricks = new ArrayList<>();
        this.marriages = new ArrayList<>();
        this.tricksValue = 0;
        this.points = 0;
    }

    public Player(Player player) {
        id = player.id;
        hand = new ArrayList<>(player.hand);
        tricks = new ArrayList<>(player.tricks);
        marriages = new ArrayList<>(player.marriages);
        tricksValue = player.tricksValue;
        points = player.points;
    }

    public void setScenario(Player player) {
        this.hand.clear();
        this.tricks.clear();
        this.marriages.clear();

        this.hand.addAll(player.hand);
        this.tricks.addAll(player.tricks);
        this.marriages.addAll(player.marriages);
        this.tricksValue = player.tricksValue;
    }

    public Move getFirstMove(Game game) {
        throw new RuntimeException("Never instantiate abstractish methods");
    }

    public Move getSecondMove(Game game, Move opponentMove) {
        throw new RuntimeException("Never instantiate abstractish methods");
    }

    public void selfMove(Move selfMove) {
        throw new RuntimeException("Never instantiate abstractish methods");
    }

    public void opponentMove(Move opponentMove) {
        throw new RuntimeException("Never instantiate abstractish methods");
    }

    public void playCard(Card card) {
        hand.remove(card);
    }

    public void drawCard(Card card) {
        hand.add(card);
    }

    public void swapCard(Card removeCard, Card addCard) {
        hand.remove(removeCard);
        hand.add(addCard);
    }

    public void addTrick(Trick trick) {
        tricks.add(trick);
        tricksValue += trick.value;
    }

    public void addMarriage(Marriage marriage) {
        marriages.add(marriage);
        tricksValue += marriage.value;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public int getTricksValue() {
        return tricks.isEmpty() ? 0 : tricksValue;
    }

    @Override
    public String toString() {
        return "Player{" + id +
                ", hand=" + hand +
                ", tricks=" + tricks +
                ", marriages=" + marriages +
                ", trickValue=" + tricksValue +
                ", points=" + points +
                '}';
    }
}
