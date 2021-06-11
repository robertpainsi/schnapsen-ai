package schnapsen.models;

import java.util.Arrays;
import java.util.List;

public class Trick {
    public final List<Card> cards;
    public final int value;
    private final Integer id;

    public Trick(Card card1, Card card2, Integer id) {
        cards = Arrays.asList(card1, card2);
        value = card1.value.value + card2.value.value;
        this.id = id;
    }

    public Trick(Card card1, Card card2) {
        this(card1, card2, null);
    }

    @Override
    public String toString() {
        return cards.toString() + "=" + value + ((id != null) ? "     " + id : "");
    }
}
