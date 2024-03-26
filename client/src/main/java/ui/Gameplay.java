package ui;

import static ui.EscapeSequences.*;

public class Gameplay {
    private static final String SPACING = " ";
    private static final String VERTICAL_LINE = "|";

    public static void drawChessboard() {
        // Adjusted labels for alignment
        String top = "    a    b    c   d    e   f    g    h";
        String bottom = "    a    b    c   d    e   f    g    h"; // Consistent with top labels
        // Chessboard rows with vertical lines added for grid separation
        String[] rows = new String[]{
                "8 " + VERTICAL_LINE + WHITE_ROOK + VERTICAL_LINE + WHITE_KNIGHT + VERTICAL_LINE + WHITE_BISHOP + VERTICAL_LINE + WHITE_QUEEN + VERTICAL_LINE + WHITE_KING + VERTICAL_LINE + WHITE_BISHOP + VERTICAL_LINE + WHITE_KNIGHT + VERTICAL_LINE + WHITE_ROOK + VERTICAL_LINE,
                "7 " + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE,
                "6 " + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE,
                "5 " + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE,
                "4 " + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE,
                "3 " + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE,
                "2 " + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE,
                "1 " + VERTICAL_LINE + BLACK_ROOK + VERTICAL_LINE + BLACK_KNIGHT + VERTICAL_LINE + BLACK_BISHOP + VERTICAL_LINE + BLACK_QUEEN + VERTICAL_LINE + BLACK_KING + VERTICAL_LINE + BLACK_BISHOP + VERTICAL_LINE + BLACK_KNIGHT + VERTICAL_LINE + BLACK_ROOK + VERTICAL_LINE
        };

        System.out.println(top);
        for (String row : rows) {
            System.out.println(row + " " + (9 - Character.getNumericValue(row.charAt(0))));
        }
        System.out.println(bottom);
    }

    public static void main(String[] args) {
        System.out.println(ERASE_SCREEN);
        System.out.println(SET_TEXT_COLOR_GREEN);
        drawChessboard();
        System.out.println(RESET_TEXT_COLOR);
    }
}
