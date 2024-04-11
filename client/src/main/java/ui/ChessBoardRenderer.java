package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class ChessBoardRenderer {
    private ChessGame.TeamColor playerColor;
    private static final String emSpace = EscapeSequences.EMPTY;
    private ChessGame chessGame;

    public ChessBoardRenderer(ChessGame chessGame, ChessGame.TeamColor playerColor) {
        this.chessGame = chessGame;
        this.playerColor = playerColor;
    }

    public void drawChessboard() {
        System.out.print(RESET_TEXT_COLOR);
        boolean isWhiteBottom = this.playerColor == ChessGame.TeamColor.WHITE;
        drawSingleBoard(isWhiteBottom); // white at bottom
    }

    private void drawSingleBoard(boolean isWhiteBottom) {
        String lightSquare = EscapeSequences.SET_BG_COLOR_CREAM;
        String darkSquare = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        String cellPadding = " ";

        if (isWhiteBottom) {
            System.out.print("\u2007" + emSpace + "\u2002" + "\u2005" + "\u2006");
            System.out.print('a' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('b' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('c' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('d' + "\u2003" + "\u2004" + "\u2005" + "\u2009");
            System.out.print('e' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('f' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('g' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('h' + "\u2003" + "\u2004" + "\u2005");
            System.out.println();
        } else {
            System.out.print("\u2007" + emSpace + "\u2002" + "\u2005" + "\u2006");
            System.out.print('h' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('g' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('f' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('e' + "\u2003" + "\u2004" + "\u2005" + "\u2009");
            System.out.print('d' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('b' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('c' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('a' + "\u2003" + "\u2004" + "\u2005");
            System.out.println();
        }

        for (int row = 8; row >= 1; row--) {
            int displayRow = isWhiteBottom ? row : 9 - row;
            System.out.print(displayRow + emSpace);
            for (int col = 1; col <= 8; col++) {
                int displayCol = isWhiteBottom ? col : 9 - col;
                String squareColor = ((displayRow + displayCol) % 2 == 0) ? darkSquare : lightSquare;
                System.out.print(squareColor);
                String piece = determinePiece(displayCol, displayRow, isWhiteBottom);
                if (!piece.equals(emSpace)) {
                    String pieceColor = piece.contains("WHITE") ? SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + RESET_TEXT_COLOR : SET_TEXT_COLOR_BLACK + RESET_TEXT_COLOR;
                    System.out.print(cellPadding + pieceColor + piece + cellPadding);
                } else {
                    System.out.print(emSpace);
                }
                System.out.print(RESET_BG_COLOR);
            }
            System.out.println(emSpace + displayRow);
        }

        if (isWhiteBottom) {
            System.out.print("\u2007" + emSpace + "\u2002" + "\u2005" + "\u2006");
            System.out.print('a' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('b' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('c' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('d' + "\u2003" + "\u2004" + "\u2005" + "\u2009");
            System.out.print('e' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('f' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('g' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('h' + "\u2003" + "\u2004" + "\u2005");
            System.out.println();
        } else {
            System.out.print("\u2007" + emSpace + "\u2002" + "\u2005" + "\u2006");
            System.out.print('h' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('g' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('f' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('e' + "\u2003" + "\u2004" + "\u2005" + "\u2009");
            System.out.print('d' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('b' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('c' + "\u2003" + "\u2004" + "\u2005");
            System.out.print('a' + "\u2003" + "\u2004" + "\u2005");
            System.out.println();
        }
    }

    private String determinePiece(int col, int row, boolean isWhiteBottom) {
        int boardRow = isWhiteBottom ? row : 9 - row;
        ChessPosition position = new ChessPosition(boardRow, col);
        ChessPiece piece = chessGame.getBoard().getPiece(position);
        if (piece != null) {
            switch (piece.getPieceType()) {
                case PAWN: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "♙" : "♟";
                case KNIGHT: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "♘" : "♞";
                case BISHOP: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "♗" : "♝";
                case ROOK: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "♖" : "♜";
                case QUEEN: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "♕" : "♛";
                case KING: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "♔" : "♚";
                default: return emSpace;
            }
        } else {
            return emSpace;
        }
    }
}
