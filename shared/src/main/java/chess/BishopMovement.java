package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovement extends ChessMovement{

    public BishopMovement(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }


    //create a new set of classes
    @Override
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] direction : DIAGONAL_DIRECTIONS) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }
                //write this in ChessMovement and then call it

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
        return validMoves;
    }
}