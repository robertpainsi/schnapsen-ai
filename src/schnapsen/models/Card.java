package schnapsen.models;

import java.util.ArrayList;
import java.util.List;

public class Card {
    public enum Suit {
        CLUBS("♣"), DIAMONDS("♦"), HEARTS("♥"), SPADES("♠");

        public String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    public enum Value {
        JACK(2, "J"), QUEEN(3, "Q"), KING(4, "K"),
        TEN(10, "10"), ACE(11, "A");

        public String symbol;
        public int value;

        Value(int value, String symbol) {
            this.value = value;
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    public static List<Card> cards = new ArrayList<>();

    static {
        for (Suit s : Suit.values()) {
            for (Value v : Value.values()) {
                cards.add(new Card(s, v));
            }
        }
    }

    public final Suit suit;
    public final Value value;

    public Card(Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
    }

    public static Card getCard(Suit suit, Value value) {
        for (Card card : cards) {
            if (card.suit == suit && card.value == value) {
                return card;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return suit == card.suit && value == card.value;
    }

    @Override
    public int hashCode() {
        return suit.ordinal() * 100 + value.value;
    }

    @Override
    public String toString() {
        return suit.toString() + value.toString();
    }
}
