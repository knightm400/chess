package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;

public class ChessBoardRenderer {
    private ChessGame.TeamColor playerColor;
    private static final String emSpace = EscapeSequences.EMPTY;
    private ChessGame chessGame;
    private ChessPosition selectedPosition = null;
    private List<ChessPosition> legalMovePositions = new ArrayList<>();
    private static final String SET_BG_COLOR_DARK_YELLOW = "\u001b[48;5;178m";
    private static final String SET_TEXT_COLOR_WHITE = "\u001b[37m";
    private static final String SET_TEXT_COLOR_LIGHT_GRAY = "\u001b[97m";
    private static final String SET_TEXT_BOLD = "\u001b[1m";


    public ChessBoardRenderer(ChessGame chessGame, ChessGame.TeamColor playerColor) {
        this.chessGame = chessGame;
        this.playerColor = playerColor;
    }

    public void setHighlightedPositions(ChessPosition selected, List<ChessPosition> legalMoves) {
        this.selectedPosition = selected;
        this.legalMovePositions = legalMoves;
    }

    public void clearHighlights() {
        this.selectedPosition = null;
        this.legalMovePositions.clear();
    }

    public void drawChessboard() {
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
                ChessPosition currentPosition = new ChessPosition(displayRow, displayCol);
                boolean isDarkSquare = (displayRow + displayCol) % 2 == 0;
                if (currentPosition.equals(selectedPosition)) {
                    System.out.print(SET_BG_COLOR_GREEN);
                } else if (legalMovePositions.contains(currentPosition)) {
                    System.out.print(isDarkSquare ? SET_BG_COLOR_DARK_YELLOW : SET_BG_COLOR_YELLOW);
                } else {
                    System.out.print(isDarkSquare ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_CREAM);
                }
                String piece = determinePiece(displayCol, displayRow, isWhiteBottom);
                if (!piece.equals(emSpace)) {
                    String pieceColor = (piece.matches("[♙♘♗♖♕♔]")) ? SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_BLACK;
                    System.out.print(cellPadding + pieceColor + piece + cellPadding);
                    System.out.print(RESET_TEXT_COLOR);
                } else {
                    System.out.print(emSpace);
                }
                System.out.print(RESET_BG_COLOR);
            }
            System.out.println(emSpace + displayRow);
            System.out.print(RESET_BG_COLOR);
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
                case PAWN: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
                case KNIGHT: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
                case BISHOP: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
                case ROOK: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
                case QUEEN: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
                case KING: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
                default: return emSpace;
            }
        } else {
            return emSpace;
        }
    }
}