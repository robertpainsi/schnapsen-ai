//package schnapsen.models;
//
//public class GameState {
//    public static class TalonState {
//        final Card trump;
//        final boolean closed;
//        final int numberOfCards;
//        final boolean canClose;
//
//        public TalonState(Talon talon) {
//            this.trump = talon.trump;
//            this.closed = talon.closed;
//            this.numberOfCards = talon.cards.size();
//            this.canClose = talon.canClose();
//        }
//    }
//
//    public static class OpponentState {
//        final int points;
//        final int numberOfCards;
//
//        public OpponentState(int points, int numberOfCards) {
//            this.points = points;
//            this.numberOfCards = numberOfCards;
//        }
//    }
//
//    final TalonState talonState;
//    final OpponentState opponentState;
//
//    public GameState(TalonState talonState, OpponentState opponentState) {
//        this.talonState = talonState;
//        this.opponentState = opponentState;
//    }
//}
