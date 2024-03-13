package chess;

import java.util.ArrayList;
import java.util.Collection;
public class QueenMovement extends ChessMovement{
    public QueenMovement(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        addMovesInDirection(board, myPosition, validMoves, DIAGONAL_DIRECTIONS);
        addMovesInDirection(board, myPosition, validMoves, STRAIGHT_DIRECTIONS);

        return validMoves;
    }

}
