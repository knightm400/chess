package ui;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessBoard;
import server.Server;
import ui.WebSocket.WebSocketClient;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Gameplay {
    private static final String emSpace = EscapeSequences.EMPTY;
    private WebSocketClient webSocketClient;
    private ChessGame.TeamColor playerColor;
    private ServerFacade serverFacade;
    private ChessGame chessGame;

    public Gameplay(ServerFacade serverFacade) throws Exception {
        this.webSocketClient = new WebSocketClient("ws://localhost:8080/connect");
        this.playerColor = ChessGame.TeamColor.WHITE;
        this.serverFacade = serverFacade;
        this.chessGame = new ChessGame();
        initializechessBoard();
    }

    private void initializechessBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        this.chessGame.setBoard(board);
    }

    public void enterGameplayLoop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 'Help' to know what other actions to take");
        while (true) {
            String command = scanner.nextLine();
            if ("leave".equalsIgnoreCase(command)) {
                System.out.println("Exiting game...");
                break;
            } else {
                processCommand(command);
                System.out.println("Enter 'Help' to know what other actions to take");
            }
        }
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

    public void processCommand(String command) {
        switch (command.toLowerCase()) {
            case "help":
                displayHelp();
                break;
            case "redraw":
                System.out.println("Redrawing the chessboard...");
                drawChessboard();
                break;
            default:
                if (command.matches("[a-h][1-8] [a-h][1-8]( q| r| b| n)?")) {
                    try {
                        String[] parts = command.split(" ");
                        ChessPosition start = new ChessPosition(notationToRow(parts[0]), notationToCol(parts[0]));
                        ChessPosition end = new ChessPosition(notationToRow(parts[1]), notationToCol(parts[1]));
                        ChessPiece.PieceType promotionType = null;
                        if(parts[1].length() > 2) {
                            switch(parts[1].charAt(3)) {
                                case 'q':
                                    promotionType = ChessPiece.PieceType.QUEEN;
                                    break;
                                case 'r':
                                    promotionType = ChessPiece.PieceType.ROOK;
                                    break;
                                case 'b':
                                    promotionType = ChessPiece.PieceType.BISHOP;
                                    break;
                                case 'n':
                                    promotionType = ChessPiece.PieceType.KNIGHT;
                                    break;
                            }
                        }
                        ChessMove move = new ChessMove(start, end, promotionType);
                        chessGame.makeMove(move);
                        System.out.println("Move made. It's now the turn of " + chessGame.getTeamTurn());
                        drawChessboard();
                    } catch (Exception e) {
                        System.out.println("Error processing move: " + e.getMessage());
                    }
                } else {
                    System.out.println("Unknown command. Type 'Help' to see a list of available commands.");
                }
                break;
        }
    }

    private int notationToRow(String notation) {
        return Integer.parseInt(notation.substring(1));
    }

    private int notationToCol(String notation) {
        return notation.charAt(0) - 'a' + 1;
    }


    public void leaveGame(int gameId) {
        try {
            String authToken = "someAuthToken";
            webSocketClient.leaveGame(authToken, gameId);
            System.out.println("You have left the game.");
            webSocketClient.closeConnection();

            transitionBackToPostLogin();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error leaving game: " + e.getMessage());
        }
    }

    private void transitionBackToPostLogin() {
        PostLogin postLogin = new PostLogin(serverFacade);
        postLogin.displayMenu();
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
            ServerFacade serverFacade = new ServerFacade();
            Gameplay gameplay = new Gameplay(serverFacade);
            gameplay.drawChessboard();
            System.out.println(RESET_TEXT_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
