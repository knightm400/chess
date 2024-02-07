package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn = TeamColor.WHITE;
    public ChessGame() {
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    public void switchTurns() {
        if (teamTurn == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        List<ChessMove> moves = new ArrayList<>();
        if (piece == null) {
            return moves;
        }
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board,startPosition);

        for (ChessMove move : potentialMoves) {
            ChessPiece originalPiece = board.getPiece(move.getEndPosition());
            board.movePiece(move);
            if (!isInCheck(piece.getTeamColor())) {
                moves.add(move);
            }
            board.setPiece(move.getStartPosition(), piece);
            board.setPiece(move.getEndPosition(), originalPiece);
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at the start position.");
        }

        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("It's not " + piece.getTeamColor() + "'s turn.");
        }

        Collection<ChessMove> validMoves =  validMoves(move.getStartPosition());
        boolean isValid = false;
        for (ChessMove validMove : validMoves) {
            if (validMove.getEndPosition().equals(move.getEndPosition())) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new InvalidMoveException("Move is not valid.");
        }
        board.movePiece(move);
        switchTurns();

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor) {
                    kingPosition = currentPosition;
                    break;
                }
            }
            if (kingPosition!= null) {
                break;
            }
        }
        if (kingPosition == null) {
            return false;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> validMoves = currentPiece.pieceMoves(board, currentPosition);
                    for (ChessMove move : validMoves) {
                        ChessPosition destinationPosition = move.getEndPosition();
                        if (destinationPosition.equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(currentPosition);
                    for (ChessMove move : validMoves) {
                        ChessPiece originalPiece = board.getPiece(move.getEndPosition());
                        board.movePiece(move);
                        boolean stillInCheck = isInCheck(teamColor);
                        board.setPiece(move.getStartPosition(), currentPiece);
                        board.setPiece(move.getEndPosition(), originalPiece);
                        if (!stillInCheck) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
