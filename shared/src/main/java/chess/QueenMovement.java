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

    private void addMovesInDirection(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int[][] directions) {
        for (int[] direction : directions) {
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
}
