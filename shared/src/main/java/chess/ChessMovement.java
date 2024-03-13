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


}
