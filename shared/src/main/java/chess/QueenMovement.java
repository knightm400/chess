package chess;

import java.util.ArrayList;
import java.util.Collection;
public class QueenMovement extends ChessMovement{
    public QueenMovement(ChessGame.TeamColor teamColor) {
        super.teamColor = teamColor;
    }

    @Override
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] diagonalDir = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int[][] perpendicularDir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        addQueenMoves(board, myPosition, validMoves, diagonalDir);
        addQueenMoves(board, myPosition, validMoves, perpendicularDir);

        return validMoves;
    }

    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int[][] directions) {
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
