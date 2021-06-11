package schnapsen.models;

import schnapsen.models.Card.Suit;

public class Marriage {
    public final Suit suit;
    public final int value;

    public Marriage(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    @Override
    public String toString() {
        return suit.toString() + value;
    }
}
