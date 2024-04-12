package ui;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessBoard;
import ui.WebSocket.WebSocketClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.List;

import static ui.EscapeSequences.*;

public class Gameplay {
    private static final String emSpace = EscapeSequences.EMPTY;
    private WebSocketClient webSocketClient;
    private ChessGame.TeamColor playerColor;
    private ServerFacade serverFacade;
    private ChessGame chessGame;
    private ChessBoardRenderer chessBoardRenderer;
    private int gameId;

    public Gameplay(ServerFacade serverFacade) throws Exception {
        this.webSocketClient = new WebSocketClient("ws://localhost:8080/connect");
        this.playerColor = ChessGame.TeamColor.WHITE;
        this.serverFacade = serverFacade;
        this.chessGame = new ChessGame();
        initializeChessBoard();
        this.chessBoardRenderer = new ChessBoardRenderer(this.chessGame, this.playerColor);
    }

    public void initializeChessBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        this.chessGame.setBoard(board);
    }

    public void enterGameplayLoop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("To make a move, enter the position of the piece and the position you want to move to in the format 'e2 e4'.");
        System.out.println("Enter 'Help' to know what other actions you can take.");
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
        chessBoardRenderer.drawChessboard();
    }

    public void highlightLegalMoves(String piecePosition) {
        try {
            ChessPosition position = new ChessPosition(notationToRow(piecePosition), notationToCol(piecePosition));
            ChessPiece piece = chessGame.getBoard().getPiece(position);
            if (piece != null && piece.getTeamColor() == playerColor) {
                Collection<ChessMove> legalMoves = chessGame.validMoves(position);
                List<ChessPosition> legalPositions = new ArrayList<>();
                for (ChessMove move : legalMoves) {
                    legalPositions.add(move.getEndPosition());
                }
                chessBoardRenderer.setHighlightedPositions(position, legalPositions);
                drawChessboard();
            } else {
                System.out.println("No piece at that position or not your piece!");
            }
        } catch (Exception e) {
            System.out.println("Error highlighting legal moves: " + e.getMessage());
        }
    }

    public void joinGameAsPlayer(int gameId, ChessGame.TeamColor playerColor) throws Exception {
        this.gameId = gameId;
        this.playerColor = playerColor;
        String authToken = "someAuthToken";
        webSocketClient.joinGameAsPlayer(authToken, gameId, playerColor);
        this.chessBoardRenderer = new ChessBoardRenderer(this.chessGame, this.playerColor);
        drawChessboard();
    }

    public void joinGameAsObserver(int gameId) throws Exception {
        this.gameId = gameId;
        this.playerColor = ChessGame.TeamColor.WHITE;
        String authToken = "someAuthToken";
        webSocketClient.joinGameAsObserver(authToken, gameId);
        drawChessboard();
    }

    public void makeMove(int gameId, ChessMove move) throws Exception {
        String authToken = "someAuthToken";
        webSocketClient.makeMove(authToken, gameId, move);
        chessBoardRenderer.clearHighlights();
        drawChessboard();
    }

    public void resignGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you sure you want to resign the game? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();
        if ("yes".equals(response)) {
            try {
                String authToken = "someAuthToken";
                webSocketClient.resignGame(authToken, this.gameId);
                System.out.println("You have resigned from the game.");
                drawChessboard();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error resigning from the game: " + e.getMessage());
            }
        } else {
            System.out.println("Resignation cancelled.");
            drawChessboard();
        }
    }

    public static void displayHelp() {
        System.out.println("Available Commands:");
        System.out.println("- 'Help': Displays this message.");
        System.out.println("- 'Redraw': Redraws the chessboard.");
        System.out.println("- 'Leave': Exits the current game.");
        System.out.println("- 'Resign': Resign from the game.");
        System.out.println("- 'Highlight': Enter the position of the piece to highlight its legal moves.");
        System.out.println("- Make Move: Make a move in the format 'e2 e4'.");
    }

    public void processCommand(String command) {
        switch (command.toLowerCase()) {
            case "help":
                displayHelp();
                break;
            case "redraw":
                System.out.println("Redrawing the chessboard...");
                initializeChessBoard();
                chessGame.resetTurn();
                chessBoardRenderer.clearHighlights();
                drawChessboard();
                break;
            case "resign":
                resignGame();
                break;
            case "highlight":
                System.out.println("Enter the position of the piece to highlight its legal moves (e.g., 'e2'):");
                String positionInput = new Scanner(System.in).nextLine().trim().toLowerCase();
                if (positionInput.matches("[a-h][1-8]")) {
                    highlightLegalMoves(positionInput);
                } else {
                    System.out.println("Invalid position. Please enter a position like 'e2'.");
                }
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
                        chessBoardRenderer.clearHighlights();
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