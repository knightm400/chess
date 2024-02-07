package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece.PieceType piecetype = piece.getPieceType();

        if (piecetype.equals(PieceType.BISHOP)) {
            BishopMovement bm = new BishopMovement(piece.getTeamColor());
            return bm.calculateValidMoves(board, myPosition);
        }
        if (piecetype.equals(PieceType.KNIGHT)) {
            KnightMovement km = new KnightMovement(piece.getTeamColor());
            return km.calculateValidMoves(board,myPosition);
        }
        if (piecetype.equals(PieceType.ROOK)) {
            RookMovement rk = new RookMovement(piece.getTeamColor());
            return rk.calculateValidMoves(board,myPosition);
        }
        if (piecetype.equals(PieceType.QUEEN)) {
            QueenMovement qk = new QueenMovement(piece.getTeamColor());
            return qk.calculateValidMoves(board,myPosition);
        }
        if (piecetype.equals(PieceType.PAWN)) {
            PawnMovement pm = new PawnMovement(piece.getTeamColor());
            return pm.calculateValidMoves(board,myPosition);
        }
        if (piecetype.equals(PieceType.KING)) {
            KingMovement kingm = new KingMovement(piece.getTeamColor());
            return kingm.calculateValidMoves(board,myPosition);
        }
        return null;
    }


    @Override
    public String toString() {
        return pieceColor.toString().charAt(0) + "-" + type.toString().charAt(0);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) object;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}