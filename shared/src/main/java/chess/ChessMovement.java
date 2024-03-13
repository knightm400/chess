package chess;

import java.util.Collection;

public abstract class ChessMovement {
    protected static final int[][] DIAGONAL_DIRECTIONS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    protected static final int[][] STRAIGHT_DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    protected static final int[][] KING_MOVES = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };
    protected ChessGame.TeamColor teamColor;

    abstract Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition);

    protected void addMovesInDirection(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int[][] directions) {
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
                } else if (destinationPiece.getTeamColor() != this.teamColor) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    break;
                } else {
                    break;
                }
            }
        }
    }
}
