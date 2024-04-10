package ui;
import static ui.EscapeSequences.*;

public class Gameplay {
    private static final String emSpace = EscapeSequences.EMPTY;

    public static void drawChessboard() {
        String lightSquare = EscapeSequences.SET_BG_COLOR_CREAM;
        String darkSquare = EscapeSequences.SET_BG_COLOR_DARK_GREEN;

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


        for (int row = 8; row >= 1; row--) {
            System.out.print(row + emSpace);
            for (int col = 1; col <= 8; col++) {
                String squareColor = (row + col) % 2 == 0 ? darkSquare : lightSquare;
                System.out.print(squareColor);
                String piece = determinePiece(col, row);
                if (!piece.equals(EMPTY)) {
                    String pieceColor = piece.contains("WHITE") ? SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_BLACK;
                    System.out.print(pieceColor);
                    System.out.print(piece);
                    System.out.print(RESET_TEXT_COLOR);
                } else {
                    System.out.print(emSpace);
                }

                System.out.print(RESET_BG_COLOR);
            }
            System.out.println(emSpace + row);
        }

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
        System.out.println(SET_TEXT_COLOR_WHITE);
        drawChessboard();
        System.out.println(RESET_TEXT_COLOR);
    }
}
