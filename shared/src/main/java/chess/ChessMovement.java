package chess;

import java.util.Collection;

public abstract class ChessMovement {
    ChessGame.TeamColor teamColor;


    abstract Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition);
    //BishopMovement, KnightMovement, overrides this class
    //
}
