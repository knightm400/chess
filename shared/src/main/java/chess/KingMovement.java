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
            int newRow = myPosition.getRow() + move[0];
            int newCol = myPosition.getColumn() + move[1];
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece destinationPiece = board.getPiece(newPosition);
                if (destinationPiece == null || destinationPiece.getTeamColor() != this.teamColor) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return validMoves;
    }
}