package schnapsen.ai;

import schnapsen.models.*;
import schnapsen.models.Card.Suit;
import schnapsen.models.Move.SwapTrump;

import java.util.ArrayList;
import java.util.List;

import static schnapsen.models.Card.Value.*;
import static schnapsen.utils.SchnapsenUtils.*;

public class Computer extends Player {

    private static int UNIQUE_ID = 0;

    private final NeuralNetwork neuralNetwork;

    private final List<Card> opponentCards = new ArrayList<>();
    private final List<Card> playedCards = new ArrayList<>();

    public Computer(List<Card> hand, NeuralNetwork neuralNetwork) {
        super(String.valueOf(UNIQUE_ID++), hand);
        this.neuralNetwork = neuralNetwork;
    }

    @Override
    public Move getFirstMove(Game game) {
        SwapTrump swapTrump = null;
        Card trump = game.talon.trump;
        if (game.talon.canSwapTrump()) {
            for (Card card : hand) {
                if (card.equals(Card.getCard(trump.suit, JACK))) {
                    Card oldTrump = game.talon.swapTrump(card);
                    this.swapCard(card, oldTrump);
                    swapTrump = new SwapTrump(oldTrump, card);
                    break;
                }
            }
        }
        Move move = getBestMove(game, null);
        move.swapTrump = swapTrump;
        if (move.closeTalon) {
            // TODO: move.closeTalon currently always false
            game.talon.close();
        }
        if (hasMarriage(move.card, hand)) {
            // TODO: Emphasize in neural network
            Marriage marriage = new Marriage(move.card.suit, trump.suit == move.card.suit ? 40 : 20);
            move.marriage = marriage;
            addMarriage(marriage);
        }
        this.playCard(move.card);
        return move;
    }

    @Override
    public Move getSecondMove(Game game, Move opponentMove) {
        Move move = getBestMove(game, opponentMove);
        this.playCard(move.card);
        return move;
    }

    private Move getBestMove(Game game, Move opponentMove) {
        // TODO: If single played card ends game (marriage, trump, closed stack)
        List<Float> input = new ArrayList<>();

        // number-of-talon-cards [1]
        input.add((float) game.talon.cards.size());
        // number-of-remaining-trump-cards [1]
        input.add((float) numberOfRemainingTrumpCards(game.talon));
        // trump-suit [4]
        for (Suit suit : Card.Suit.values()) {
            input.add(game.talon.trump.suit == suit ? 1f : 0f);
        }
        // talon-closed [1]
        input.add(game.talon.isClosed ? 1f : 0f);

        Player opponent = getOpponent(game.players, this);
        // opponent-cards [20][probability owning cards]
        int unknownCards = game.talon.isEmpty()
                ? opponent.hand.size()
                : opponent.hand.size() + game.talon.cards.size() - 1;
        for (Card card : Card.cards) {
            if (playedCards.contains(card) || hand.contains(card)) {
                input.add(0f);
            } else if (opponentCards.contains(card)) {
                input.add(1f);
            } else {
                input.add(1f / unknownCards);
            }
        }
        // opponent-points [1]
        input.add((float) opponent.getTricksValue());

        // player-is-first [1]
        input.add(opponentMove == null ? 1f : 0f);
        // player-cards [20][owning cards]
        for (Card card : Card.cards) {
            input.add(hand.contains(card) ? 1f : 0f);
        }
        // player-cards [20][playable cards]
        List<Card> playableCards = getPlayableCards(hand, game.talon, opponentMove != null ? opponentMove.card : null);
        for (Card card : Card.cards) {
            input.add(playableCards.contains(card) ? 1f : 0f);
        }
        // player-points [1]
        input.add((float) getTricksValue());
        // player-trump-marriages [1]
        List<Suit> marriageSuits = getMarriageSuits(hand);
        boolean hasTrumpMarriage = marriageSuits.contains(game.talon.trump.suit);
        input.add(hasTrumpMarriage ? 1f : 0f);
        // player-marriages [1]
        input.add((float) marriageSuits.size() - (hasTrumpMarriage ? 1f : 0f));

        float[] target = new float[input.size()];
        for (int i = 0; i < input.size(); i++) {
            target[i] = input.get(i);
        }

        float[] output = neuralNetwork.output(target);

        Card bestCard = null;
        float bestCardProbability = 0f;
        for (int i = 0; i < output.length; i++) {
            Card card = Card.cards.get(i);
            if ((bestCard == null || output[i] > bestCardProbability) && playableCards.contains(card)) {
                bestCard = card;
                bestCardProbability = output[i];
            }
        }
        Move move = new Move(this);
        move.card = bestCard;
        return move;
    }

    private int numberOfRemainingTrumpCards(Talon talon) {
        int remaining = 5;
        for (Card card : playedCards) {
            if (card.suit == talon.trump.suit) {
                remaining--;
            }
        }
        for (Card card : hand) {
            if (card.suit == talon.trump.suit) {
                remaining--;
            }
        }
        return remaining;
    }

    @Override
    public void selfMove(Move selfMove) {
        playedCards.add(selfMove.card);
    }

    @Override
    public void opponentMove(Move opponentMove) {
        if (opponentMove.marriage != null) {
            if (opponentMove.card.value == KING) {
                opponentCards.add(Card.getCard(opponentMove.card.suit, QUEEN));
            } else {
                opponentCards.add(Card.getCard(opponentMove.card.suit, KING));
            }
        }
        if (opponentMove.swapTrump != null) {
            opponentCards.add(opponentMove.swapTrump.oldTrump);
            opponentCards.remove(opponentMove.swapTrump.newTrump);
        }
        playedCards.add(opponentMove.card);
        opponentCards.remove(opponentMove.card);
    }
}
