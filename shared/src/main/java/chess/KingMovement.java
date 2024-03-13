package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovement extends ChessMovement {

    public KingMovement(ChessGame.TeamColor teamColor) {
        super.teamColor = teamColor;
    }

    @Override
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] move : KING_MOVES) {
            addMovesInDirection(board, myPosition, validMoves, KING_MOVES);
        }
        return validMoves;
    }
}