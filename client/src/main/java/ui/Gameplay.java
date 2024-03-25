package ui;

import static ui.EscapeSequences.*;
public class Gameplay {
    public static void drawChessboard() {
        String top = "   a  b  c  d  e  f  g  h";
        String[] rows = new String[]{
                "8 ♜  ♞  ♝  ♛  ♚  ♝  ♞  ♜",
                "7 ♟  ♟  ♟  ♟  ♟  ♟  ♟  ♟",
                "6    .     .    .     .",
                "5 .     .    .     .   ",
                "4    .     .    .     .",
                "3 .     .    .     .   ",
                "2 ♙  ♙  ♙  ♙  ♙  ♙  ♙  ♙",
                "1 ♖  ♘  ♗  ♕  ♔  ♗  ♘  ♖"
        };

        System.out.println(top);

        for (int i = 0; i < rows.length; i++) {
            System.out.println(rows[i] + " " + (8 - i));
        }

        System.out.println(top);
    }

    public static void main(String[] args) {
        System.out.println(ERASE_SCREEN);
        System.out.println(SET_TEXT_COLOR_GREEN);
        drawChessboard();
        System.out.println(RESET_TEXT_COLOR); 
    }
}
