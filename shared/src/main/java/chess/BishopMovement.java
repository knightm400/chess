package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovement extends ChessMovement {

    public BishopMovement(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        addMovesInDirection(board, myPosition, validMoves, DIAGONAL_DIRECTIONS);

        return validMoves;
    }
}