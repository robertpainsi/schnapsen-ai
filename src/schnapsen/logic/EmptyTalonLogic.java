package schnapsen.logic;

import schnapsen.models.*;

import java.io.File;

import static schnapsen.utils.SchnapsenUtils.*;

public class EmptyTalonLogic {

    private int id = 0;

    private final static class Pair {
        private final PlayerDummy winner;
        private final PlayerDummy loser;
        private String trickerId;

        public Pair(PlayerDummy winner, PlayerDummy loser) {
            this.winner = winner;
            this.loser = loser;
        }
    }

    public static class Scenario {
        public final Game game;
        public final PlayerDummy firstPlayer;
        public final PlayerDummy secondPlayer;

        public Scenario() {
            this.game = null;
            this.firstPlayer = null;
            this.secondPlayer = null;
        }

        public Scenario(Game game, PlayerDummy firstPlayer, PlayerDummy secondPlayer) {
            this.game = game;
            this.firstPlayer = firstPlayer;
            this.secondPlayer = secondPlayer;
        }
    }

    public static final File testDataFolder = new File("./data/manual-tests/");

    public Player getWinner(Game game, Player firstPlayer, Player secondPlayer) {
//        String fileName = firstPlayer.id + "x" + secondPlayer.id + ".json";
//        System.out.println("Saving " + fileName);
//        try {
//            saveObjectAsJSON(new File(testDataFolder, fileName), new Scenario(game, new PlayerDummy(firstPlayer), new PlayerDummy(secondPlayer)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Pair result = getWinnerPair(game, new PlayerDummy(firstPlayer), new PlayerDummy(secondPlayer), 0);

        Player winner = result.winner.id.equals(firstPlayer.id) ? firstPlayer : secondPlayer;
        Player loser = winner == firstPlayer ? secondPlayer : firstPlayer;

//        System.out.println();
//        System.out.println();
//        System.out.println(game.talon.trump.suit);
//        System.out.println(firstPlayer.getTricksValue());
//        System.out.println(SchnapsenUtils.sort(game.talon.trump.suit, firstPlayer.hand).toString() + " " + firstPlayer.id);
//        System.out.println(SchnapsenUtils.sort(game.talon.trump.suit, secondPlayer.hand).toString() + " " + secondPlayer.id);
//        System.out.println(secondPlayer.getTricksValue());
//
//        System.out.println(result.winner.id.equals(firstPlayer.id) ? "First player wins" : "Second player wins");
//
//        List<Card> f = new ArrayList<>(firstPlayer.hand);
//        f.removeAll(result.winner.hand);
//        f.removeAll(result.loser.hand);
//        List<Card> s = new ArrayList<>(secondPlayer.hand);
//        s.removeAll(result.winner.hand);
//        s.removeAll(result.loser.hand);

        winner.setScenario(result.winner);
        loser.setScenario(result.loser);

//        System.out.println(firstPlayer.getTricksValue());
//        System.out.println(SchnapsenUtils.sort(game.talon.trump.suit, f).toString() + " " + firstPlayer.id);
//        System.out.println(SchnapsenUtils.sort(game.talon.trump.suit, s).toString() + " " + secondPlayer.id);
//        System.out.println(secondPlayer.getTricksValue());

        return winner;
    }

    // TODO: Add caching
    private Pair getWinnerPair(Game game, Player firstPlayer, Player secondPlayer, int level) {
        if (firstPlayer.hand.isEmpty() || firstPlayer.getTricksValue() >= 66) {
            return new Pair(new PlayerDummy(firstPlayer), new PlayerDummy(secondPlayer));
        }

        Card bestCard = null;
        Pair bestResult = null;
        for (Card firstPlayerCard : firstPlayer.hand) {
            boolean hasMarriage = hasMarriage(firstPlayerCard, firstPlayer.hand);
            Pair firstPlayerWinsBestResult = null;
            Pair firstPlayerLosesBestResult = null;
            for (Card secondPlayerCard : getPlayableCards(secondPlayer.hand, game.talon, firstPlayerCard)) {
                Player fp = new PlayerDummy(firstPlayer);
                Move fpMove = new Move(fp);
                fpMove.card = firstPlayerCard;
                if (hasMarriage) {
                    Marriage marriage = new Marriage(fpMove.card.suit, game.talon.trump.suit == fpMove.card.suit ? 40 : 20);
                    fpMove.marriage = marriage;
                    fp.addMarriage(marriage);
                }
                fp.playCard(fpMove.card);
                if (fp.getTricksValue() >= 66) {
                    // First player wins
                    Pair result = new Pair(new PlayerDummy(fp), new PlayerDummy(secondPlayer));
                    result.trickerId = firstPlayer.id;
                    if (firstPlayerWinsBestResult == null
                            || (result.loser.getTricksValue() > firstPlayerWinsBestResult.loser.getTricksValue()) // Maximize second player tricks
                            || (result.loser.getTricksValue() == firstPlayerWinsBestResult.loser.getTricksValue() && fp.getTricksValue() < firstPlayerWinsBestResult.winner.getTricksValue())  // Minimize first player tricks
                    ) {
                        firstPlayerWinsBestResult = result;
                    }
                    continue;
                }

                Player sp = new PlayerDummy(secondPlayer);
                Move spMove = new Move(sp);
                spMove.card = secondPlayerCard;
                sp.playCard(spMove.card);

                Player tricker = getTricker(fpMove, spMove, game.talon.trump.suit);
                tricker.addTrick(new Trick(fpMove.card, spMove.card, id++));

                Pair result = getWinnerPair(game, tricker, getOpponent(new Player[]{fp, sp}, tricker), level + 1);
                result.trickerId = tricker.id;
                if (result.winner.id.equals(fp.id)) {
                    // First player wins
                    if (firstPlayerWinsBestResult == null
                            || (result.loser.getTricksValue() > firstPlayerWinsBestResult.loser.getTricksValue()) // Maximize second player tricks
                            || (result.loser.getTricksValue() == firstPlayerWinsBestResult.loser.getTricksValue() && result.winner.getTricksValue() < firstPlayerWinsBestResult.winner.getTricksValue()) // Minimize first player tricks
                    ) {
                        firstPlayerWinsBestResult = result;
                    }
                } else {
                    // First player loses
                    if (firstPlayerLosesBestResult == null
                            || result.loser.getTricksValue() < firstPlayerLosesBestResult.loser.getTricksValue() // Minimize first players tricks
                            || (result.loser.getTricksValue() == firstPlayerLosesBestResult.loser.getTricksValue() && result.winner.getTricksValue() > firstPlayerLosesBestResult.winner.getTricksValue()) // Maximize second player tricks
                    ) {
                        firstPlayerLosesBestResult = result;
                    }
                }
            }
            Pair result = firstPlayerLosesBestResult != null ? firstPlayerLosesBestResult : firstPlayerWinsBestResult;
            if (bestResult == null) {
                bestResult = result;
                bestCard = firstPlayerCard;
            } else if (bestResult.winner.id.equals(firstPlayer.id)) {
                if (result.winner.id.equals(firstPlayer.id)) {
                    // First player wins
                    if (result.loser.getTricksValue() < bestResult.loser.getTricksValue()
                            || result.loser.getTricksValue() == bestResult.loser.getTricksValue() && result.winner.getTricksValue() > bestResult.winner.getTricksValue()) {
                        // Minimize losers tricks
                        bestResult = result;
                        bestCard = firstPlayerCard;
                    } else if (result.loser.getTricksValue() == bestResult.loser.getTricksValue() && result.winner.getTricksValue() == bestResult.winner.getTricksValue()) {
                        // Maximize winners tricks
                        if (result.trickerId.equals(firstPlayer.id) && firstPlayerCard.value.value > bestCard.value.value) {
                            // Winner ticker, play higher card
                            bestResult = result;
                            bestCard = firstPlayerCard;
                        } else if (result.trickerId.equals(secondPlayer.id) && firstPlayerCard.value.value < bestCard.value.value) {
                            // Winner not ticker, play lower card
                            bestResult = result;
                            bestCard = firstPlayerCard;
                        }
                    }
                }
            } else if (bestResult.loser.id.equals(firstPlayer.id)) {
                if (result.loser.id.equals(firstPlayer.id)) {
                    // First player loses
                    if (result.loser.getTricksValue() > bestResult.loser.getTricksValue()
                            || result.loser.getTricksValue() == bestResult.loser.getTricksValue() && result.winner.getTricksValue() < bestResult.winner.getTricksValue()) {
                        // Maximize  losers tricks
                        bestResult = result;
                        bestCard = firstPlayerCard;
                    } else if (result.loser.getTricksValue() == bestResult.loser.getTricksValue() && result.winner.getTricksValue() == bestResult.winner.getTricksValue()) {
                        // Minimize winners tricks
                        if (result.trickerId.equals(firstPlayer.id) && firstPlayerCard.value.value > bestCard.value.value) {
                            // Winner ticker, play higher card
                            bestResult = result;
                            bestCard = firstPlayerCard;
                        } else if (result.trickerId.equals(secondPlayer.id) && firstPlayerCard.value.value < bestCard.value.value) {
                            // Winner not ticker, play lower card
                            bestResult = result;
                            bestCard = firstPlayerCard;
                        }
                    }
                } else {
                    // First player wins
                    bestResult = result;
                    bestCard = firstPlayerCard;
                }
            }
        }
        return bestResult;
    }
}
