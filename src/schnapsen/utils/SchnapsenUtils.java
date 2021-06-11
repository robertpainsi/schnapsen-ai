package schnapsen.utils;

import schnapsen.models.Card;
import schnapsen.models.Card.Suit;
import schnapsen.models.Move;
import schnapsen.models.Player;
import schnapsen.models.Talon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static schnapsen.models.Card.Value.KING;
import static schnapsen.models.Card.Value.QUEEN;

public class SchnapsenUtils {
    private static List<List<Card>> marriages = new ArrayList<>();

    static {
        for (Suit suit : Suit.values()) {
            marriages.add(Arrays.asList(
                    Card.getCard(suit, QUEEN),
                    Card.getCard(suit, KING)
            ));
        }
    }

    public static Player getOpponent(List<Player> players, Player player) {
        return players.get(0) == player ? players.get(1) : players.get(0);
    }

    public static Player getOpponent(Player[] players, Player player) {
        return players[0] == player ? players[1] : players[0];
    }

    public static Player getTricker(Move firstMove, Move secondMove, Suit trumpSuit) {
        if (firstMove.card.suit == trumpSuit && secondMove.card.suit != trumpSuit) {
            return firstMove.player;
        } else if (firstMove.card.suit != trumpSuit && secondMove.card.suit == trumpSuit) {
            return secondMove.player;
        } else if (firstMove.card.suit != secondMove.card.suit) {
            return firstMove.player;
        } else if (firstMove.card.value.value > secondMove.card.value.value) {
            return firstMove.player;
        } else {
            return secondMove.player;
        }
    }

    public static int getPoints(int tricksValue) {
        if (tricksValue >= 33) {
            return 1;
        } else if (tricksValue > 0) {
            return 2;
        } else {
            return 3;
        }
    }

    public static List<Card> getPlayableCards(List<Card> hand, Talon talon, Card opponentCard) {
        if (opponentCard != null) {
            if (talon.isClosed || talon.isEmpty()) {
                List<Card> sameSuitBetterCards = new ArrayList<>();
                List<Card> sameSuitCards = new ArrayList<>();
                List<Card> trumpCards = new ArrayList<>();

                for (Card card : hand) {
                    if (card.suit == opponentCard.suit && card.value.value > opponentCard.value.value) {
                        sameSuitBetterCards.add(card);
                    }
                    if (card.suit == opponentCard.suit) {
                        sameSuitCards.add(card);
                    }
                    if (card.suit == talon.trump.suit) {
                        trumpCards.add(card);
                    }
                }
                if (!sameSuitBetterCards.isEmpty()) {
                    return sameSuitBetterCards;
                }
                if (!sameSuitCards.isEmpty()) {
                    return sameSuitCards;
                }
                if (!trumpCards.isEmpty()) {
                    return trumpCards;
                }
            }
        }
        return new ArrayList<>(hand);
    }

    public static List<Suit> getMarriageSuits(List<Card> hand) {
        List<Suit> suits = new ArrayList<>();
        for (List<Card> marriage : marriages) {
            if (hand.containsAll(marriage)) {
                suits.add(marriage.get(0).suit);
            }
        }
        return suits;
    }

    public static boolean hasMarriage(Card card, List<Card> hand) {
        return (card.value == QUEEN || card.value == KING) && getMarriageSuits(hand).contains(card.suit);
    }

    public static List<Card> sort(Suit trump, List<Card> cards) {
        List<Card> sorted = new ArrayList<>(cards);
        sorted.sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                if (o1.suit == o2.suit) {
                    return o1.value.value - o2.value.value;
                } else {
                    if (o1.suit == trump) {
                        return 1;
                    } else if (o2.suit == trump) {
                        return -1;
                    } else {
                        return o1.suit.ordinal() - o2.suit.ordinal();
                    }
                }
            }
        });
        return sorted;
    }
}
