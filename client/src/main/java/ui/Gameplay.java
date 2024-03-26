package ui;

import static ui.EscapeSequences.*;

public class Gameplay {
    private static final String SPACING = " ";
    private static final String VERTICAL_LINE = "|";

    public static void drawChessboard() {
        String[] rowsWhiteAtBottom = new String[]{
                VERTICAL_LINE + WHITE_ROOK + VERTICAL_LINE + WHITE_KNIGHT + VERTICAL_LINE + WHITE_BISHOP + VERTICAL_LINE + WHITE_QUEEN + VERTICAL_LINE + WHITE_KING + VERTICAL_LINE + WHITE_BISHOP + VERTICAL_LINE + WHITE_KNIGHT + VERTICAL_LINE + WHITE_ROOK + VERTICAL_LINE,
                VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE + WHITE_PAWN + VERTICAL_LINE,
                VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE,
                VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE,
                VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE,
                VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE + EMPTY + VERTICAL_LINE,
                VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE + BLACK_PAWN + VERTICAL_LINE,
                VERTICAL_LINE + BLACK_ROOK + VERTICAL_LINE + BLACK_KNIGHT + VERTICAL_LINE + BLACK_BISHOP + VERTICAL_LINE + BLACK_QUEEN + VERTICAL_LINE + BLACK_KING + VERTICAL_LINE + BLACK_BISHOP + VERTICAL_LINE + BLACK_KNIGHT + VERTICAL_LINE + BLACK_ROOK + VERTICAL_LINE
        };

        String[] rowsBlackAtBottom = new String[rowsWhiteAtBottom.length];
        for (int i = 0; i < rowsWhiteAtBottom.length; i++) {
            rowsBlackAtBottom[i] = rowsWhiteAtBottom[rowsWhiteAtBottom.length - 1 - i];
        }

        System.out.println("Chessboard with White at the bottom:");
        for (String row : rowsWhiteAtBottom) {
            System.out.println(row);
        }

        System.out.println();

        System.out.println("Chessboard with Black at the bottom:");
        for (String row : rowsBlackAtBottom) {
            System.out.println(row);
        }
    }

    public static void main(String[] args) {
        System.out.println(ERASE_SCREEN);
        System.out.println(SET_TEXT_COLOR_GREEN);
        drawChessboard();
        System.out.println(RESET_TEXT_COLOR);
    }
}
