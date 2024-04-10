package ui;

import static ui.EscapeSequences.*;

// Want a grid, and labels for letters and numbers.
public class Gameplay {
    private static final String emSpace = EscapeSequences.EMPTY;

    public static void drawChessboard() {
        String lightSquare = EscapeSequences.SET_BG_COLOR_CREAM;
        String darkSquare = EscapeSequences.SET_BG_COLOR_DARK_GREEN;

        String coloredWhitePiece = EscapeSequences.SET_TEXT_COLOR_BLACK + SET_BG_COLOR_WHITE + "%s" + EscapeSequences.RESET_TEXT_COLOR + RESET_BG_COLOR;
        String coloredBlackPiece = EscapeSequences.SET_TEXT_COLOR_YELLOW + "%s" + EscapeSequences.RESET_TEXT_COLOR;

        System.out.print("  ");
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(emSpace + c + emSpace);
        }
        System.out.println();


        for (int row = 8; row >= 1; row--) {
            System.out.print(row + emSpace);
            for (int col = 1; col <= 8; col++) {
                String squareColor = (row + col) % 2 == 0 ? lightSquare : darkSquare;
                String piece = determinePiece(col, row);
                piece = piece.equals(EMPTY) ? emSpace: String.format((Character.isUpperCase(piece.charAt(0)) ? coloredWhitePiece : coloredBlackPiece), piece);
                System.out.print(squareColor + piece + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR + emSpace + row);
        }

        System.out.print("  ");
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(emSpace + c + emSpace);
        }
        System.out.println();
    }

    private static void printEmptyRow() {
        for (int col = 1; col <= 8; col++) {
            String squareColor = col % 2 == 0 ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_CREAM;
            System.out.print(squareColor + emSpace + EscapeSequences.RESET_BG_COLOR);
        }
        System.out.println();
    }

    private static String determinePiece(int col, int row) {
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
        return EMPTY;
    }

    public static void main(String[] args) {
        System.out.println(ERASE_SCREEN);
        System.out.println(SET_TEXT_COLOR_GREEN);
        drawChessboard();
        System.out.println(RESET_TEXT_COLOR);
    }
}
