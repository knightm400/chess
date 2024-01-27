package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovement extends ChessMovement {
    public PawnMovement(ChessGame.TeamColor teamColor) {
        super.teamColor = teamColor;
    }

    @Override
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int direction = (super.teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        addPawnMoveIfClear(board, myPosition, validMoves, direction, 0, false);

        if (isInitialPosition(myPosition)) {
            addInitialDoubleMoveIfClear(board, myPosition, validMoves, direction);
        }

        addPawnMoveIfClear(board, myPosition, validMoves, direction, -1, true);
        addPawnMoveIfClear(board, myPosition, validMoves, direction, 1, true);

        return validMoves;
    }

    private boolean isInitialPosition(ChessPosition myPosition) {
        return (super.teamColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                (super.teamColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7);
    }

    private void addInitialDoubleMoveIfClear(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int direction) {
        int newRow = myPosition.getRow() + direction;
        int newCol = myPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(newRow, newCol);

        if (board.getPiece(newPosition) == null) {
            newPosition = new ChessPosition(newRow + direction, newCol);
            if (board.getPiece(newPosition) == null) {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }

    private void addPawnMoveIfClear(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int rowOffset, int colOffset, boolean isCapture) {
        int newRow = myPosition.getRow() + rowOffset;
        int newCol = myPosition.getColumn() + colOffset;
        ChessPosition newPosition = new ChessPosition(newRow, newCol);

        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            ChessPiece destinationPiece = board.getPiece(newPosition);
            if (isCapture) {
                if (destinationPiece != null && destinationPiece.getTeamColor() != super.teamColor) {
                    addMoveOrPromotion(validMoves, myPosition, newPosition);
                }
            } else {
                if (destinationPiece == null) {
                    addMoveOrPromotion(validMoves, myPosition, newPosition);
                }
            }
        }
    }

    private void addMoveOrPromotion(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessPosition newPosition) {
        if (newPosition.getRow() == 1 || newPosition.getRow() == 8) {
            validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
        } else {
            validMoves.add(new ChessMove(myPosition, newPosition, null));
        }
    }
}
