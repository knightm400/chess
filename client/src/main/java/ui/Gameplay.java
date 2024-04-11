package ui;
import chess.ChessGame;
import chess.ChessMove;
import ui.WebSocket.WebSocketClient;

import static ui.EscapeSequences.*;

public class Gameplay {
    private static final String emSpace = EscapeSequences.EMPTY;
    private WebSocketClient webSocketClient;
    private ChessGame.TeamColor playerColor;

    public Gameplay() throws Exception {
        this.webSocketClient = new WebSocketClient("ws://localhost:8080/connect");
        this.playerColor = ChessGame.TeamColor.WHITE;
    }

    public void drawChessboard() {
        System.out.print(RESET_TEXT_COLOR);
        boolean isWhiteBottom = this.playerColor == ChessGame.TeamColor.WHITE;
        drawSingleBoard(isWhiteBottom); // white at bottom
    }

    private static void drawSingleBoard(boolean isWhiteBottom) {
        String lightSquare = EscapeSequences.SET_BG_COLOR_CREAM;
        String darkSquare = EscapeSequences.SET_BG_COLOR_DARK_GREEN;

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
                if (!piece.equals(EMPTY)) {
                    String pieceColor = piece.contains("WHITE") ? SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_BLACK;
                    System.out.print(pieceColor + piece + RESET_TEXT_COLOR);
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

    private static String determinePiece(int col, int row, boolean isWhiteBottom) {
        if (!isWhiteBottom) {
            if (row == 1) {
                switch (col) {
                    case 1:
                    case 8:
                        return WHITE_ROOK;
                    case 2:
                    case 7:
                        return WHITE_KNIGHT;
                    case 3:
                    case 6:
                        return WHITE_BISHOP;
                    case 4:
                        return WHITE_QUEEN;
                    case 5:
                        return WHITE_KING;
                }
            } else if (row == 2) {
                return WHITE_PAWN;
            } else if (row == 7) {
                return BLACK_PAWN;
            } else if (row == 8) {
                switch (col) {
                    case 1:
                    case 8:
                        return BLACK_ROOK;
                    case 2:
                    case 7:
                        return BLACK_KNIGHT;
                    case 3:
                    case 6:
                        return BLACK_BISHOP;
                    case 4:
                        return BLACK_QUEEN;
                    case 5:
                        return BLACK_KING;
                }
            }
        } else {
            if (row == 2) {
                return WHITE_PAWN;
            } else if (row == 7) {
                return BLACK_PAWN;
            } else if (row == 1 || row == 8) {
                if (col == 1 || col == 8) {
                    return row == 1 ? WHITE_ROOK : BLACK_ROOK;
                } else if (col == 2 || col == 7) {
                    return row == 1 ? WHITE_KNIGHT : BLACK_KNIGHT;
                } else if (col == 3 || col == 6) {
                    return row == 1 ? WHITE_BISHOP : BLACK_BISHOP;
                } else if (col == 4) {
                    return row == 1 ? WHITE_QUEEN : BLACK_QUEEN;
                } else if (col == 5) {
                    return row == 1 ? WHITE_KING : BLACK_KING;
                }
            }

        }
        return EMPTY;
    }

    public void joinGameAsPlayer(int gameId, ChessGame.TeamColor playerColor) throws Exception {
        this.playerColor = playerColor;
        String authToken = "someAuthToken";
        webSocketClient.joinGameAsPlayer(authToken, gameId, playerColor);
        drawChessboard();
    }

    public void joinGameAsObserver(int gameId) throws Exception {
        this.playerColor = ChessGame.TeamColor.WHITE;
        String authToken = "someAuthToken";
        webSocketClient.joinGameAsObserver(authToken, gameId);
        drawChessboard();
    }

    public void makeMove(int gameId, ChessMove move) throws Exception {
        String authToken = "someAuthToken";
        webSocketClient.makeMove(authToken, gameId, move);
    }

    public void leaveGame(int gameId) throws Exception {
        String authToken = "someAuthToken";
        webSocketClient.leaveGame(authToken, gameId);
    }

    public void resignGame(int gameId) throws Exception {
        String authToken = "someAuthToken";
        webSocketClient.resignGame(authToken, gameId);
    }




    public static void displayHelp() {
        System.out.println("Available Commands:");
        System.out.println("- Help: Displays this message.");
        System.out.println("- Redraw Chess Board: Redraws the chessboard.");
        System.out.println("- Leave: Exits the current game.");
        System.out.println("- Make Move: Make a move in the format 'e2 e4'.");
        System.out.println("- Resign: Resign from the game.");
        System.out.println("- Highlight Legal Moves: Show legal moves for a piece.");
    }

    public static void leaveGame() {
        System.out.println("Leaving the game...");
    }

    public static void makeMove(String move) {
        System.out.println("Making move: " + move);
    }

    public static void resignGame(String piece) {
        System.out.println("Highlighting legal moves for: " + piece);
    }

    public static void main(String[] args) {
        try {
            System.out.println(ERASE_SCREEN);
            System.out.println(SET_TEXT_COLOR_WHITE);
            Gameplay gameplay = new Gameplay();
            gameplay.drawChessboard();
            System.out.println(RESET_TEXT_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
