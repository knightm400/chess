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
            addMovesInDirection(board, myPosition, validMoves, direction);
        }
        return validMoves;
    }

    private void addMovesInDirection(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int[] direction) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        while (true) {
            row += direction[0];
            col += direction[1];

            if (row < 1 || row > 8 || col < 1 || col > 8) {
                break;
            }

            ChessPiece destinationPiece = board.getPiece(new ChessPosition(row, col));
            if (destinationPiece == null) {
                validMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (destinationPiece.getTeamColor() != super.teamColor) {
                validMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }
        }
    }
}

