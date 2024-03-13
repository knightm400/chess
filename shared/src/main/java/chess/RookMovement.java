package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovement extends ChessMovement {
    public RookMovement(ChessGame.TeamColor teamColor) {
        super.teamColor = teamColor;
    }

    @Override
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] direction : STRAIGHT_DIRECTIONS) {
            addMovesInDirection(board, myPosition, validMoves, STRAIGHT_DIRECTIONS);
        }
        return validMoves;
    }

}