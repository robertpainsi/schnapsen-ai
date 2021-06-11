package schnapsen.models;

import java.util.ArrayList;
import java.util.List;

public class Talon {
    public final List<Card> cards;
    public final Card trump;
    public boolean isClosed;

    public Talon(List<Card> cards) {
        this(cards, null);
    }

    public Talon(List<Card> cards, Card trump) {
        this(cards, trump, false);
    }

    public Talon(List<Card> cards, boolean isClosed) {
        this(cards, null, false);
    }

    public Talon(List<Card> cards, Card trump, boolean isClosed) {
        this.cards = new ArrayList<>(cards);
        if (trump == null) {
            this.trump = cards.get(0);
        } else {
            this.trump = trump;
        }
        this.isClosed = isClosed;
    }

    public void close() {
        if (isClosed) {
            System.out.println("Trying to close already closed talon");
        }
        isClosed = true;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public boolean canDrawCards() {
        return !isClosed && !isEmpty();
    }

    public Card drawCard() {
        return drawCards(1).get(0);
    }

    public List<Card> drawCards(int count) {
        if (count <= 0) {
            throw new RuntimeException("Non-positive number of cards to draw");
        }
        if (!canDrawCards()) {
            throw new RuntimeException("Can't draw " + count + " cards from"
                    + (isClosed ? " closed" : "") + (isEmpty() ? " empty" : "")
                    + " talon");
        }

        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            drawnCards.add(cards.remove(cards.size() - 1));
        }
        return drawnCards;
    }

    public boolean canClose() {
        return !isClosed && cards.size() > 2;
    }

    public boolean canSwapTrump() {
        return !isClosed && cards.size() > 2;
    }

    public Card swapTrump(Card trumpJack) {
        if (trumpJack.value != Card.Value.JACK) {
            throw new RuntimeException("Only jack can swap trump " + trumpJack);
        }
        if (!canSwapTrump()) {
            throw new RuntimeException("Can't swap trump " + this);
        }

        Card previousTrump = cards.get(0);
        cards.set(0, trumpJack);
        return previousTrump;
    }

    @Override
    public String toString() {
        return "{" +
                "cards=" + cards +
                ", trump=" + trump +
                ", closed=" + isClosed +
                '}';
    }
}
