package schnapsen.logic;

import schnapsen.models.*;

import java.util.List;

import static schnapsen.utils.SchnapsenUtils.*;

public class GameLogic {

    public static Player playGame(Game game) {
        Talon talon = game.talon;
        List<Player> players = game.players;
        Player firstPlayer = players.get(0);
        Player secondPlayer = players.get(1);

        Player winner;
        Player closedTalonPlayer = null;
        while (true) {
            if (talon.isEmpty()) {
                winner = new EmptyTalonLogic().getWinner(game, firstPlayer, secondPlayer);
                break;
            } else {
                Move firstMove = firstPlayer.getFirstMove(game);
                if (firstMove.closeTalon) {
                    closedTalonPlayer = firstPlayer;
                }
                if (firstPlayer.getTricksValue() >= 66) {
                    winner = firstPlayer;
                    break;
                }

                firstPlayer.selfMove(firstMove);
                secondPlayer.opponentMove(firstMove);
                Move secondMove = secondPlayer.getSecondMove(game, firstMove);
                secondPlayer.selfMove(secondMove);
                firstPlayer.opponentMove(secondMove);

                Player tricker = getTricker(firstMove, secondMove, talon.trump.suit);
                tricker.addTrick(new Trick(firstMove.card, secondMove.card));

                if (tricker.getTricksValue() >= 66 || tricker.hand.isEmpty()) {
                    winner = tricker;
                    break;
                }

                firstPlayer = tricker;
                secondPlayer = getOpponent(players, tricker);

                if (talon.canDrawCards()) {
                    firstPlayer.drawCard(talon.drawCard());
                    secondPlayer.drawCard(talon.drawCard());
                }
            }
        }
        Player loser = getOpponent(players, winner);
        int points;
        if (closedTalonPlayer != null) {
            if (closedTalonPlayer == winner && winner.getTricksValue() >= 66) {
                points = getPoints(loser.getTricksValue());
            } else {
                if (closedTalonPlayer == winner) {
                    winner = loser;
                    loser = getOpponent(players, winner);
                }
                points = Math.max(2, getPoints(loser.getTricksValue()));
            }
        } else {
            points = getPoints(loser.getTricksValue());
        }
        winner.addPoints(points);
        return winner;
    }
}
