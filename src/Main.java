import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(System.in);

    static final String EMPTY = "·";

    static final String BLACK_PAWN = "\u2659";
    static final String WHITE_PAWN = "\u265F";

    static final String[] UNICODE_BLACK = {
            "\u2656","\u2658","\u2657","\u2655","\u2654","\u2657","\u2658","\u2656"
    };

    static final String[] UNICODE_WHITE = {
            "\u265C","\u265E","\u265D","\u265B","\u265A","\u265D","\u265E","\u265C"
    };

    public static void main(String[] args) {

        String[][] board = new String[8][8];
        initializeBoard(board);

        boolean whiteTurn = true;

        while (true) {
            printBoard(board);

            System.out.print(whiteTurn ? "White move: " : "Black move: ");
            String move = in.nextLine().trim().toLowerCase();

            if (move.equals("exit")) break;

            if (!processPawnMove(board, move, whiteTurn)) {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            whiteTurn = !whiteTurn;
        }
    }

    // ================= BOARD SETUP =================

    static void initializeBoard(String[][] board) {

        for (int col = 0; col < 8; col++) {
            board[0][col] = UNICODE_BLACK[col];
            board[1][col] = BLACK_PAWN;
        }

        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = EMPTY;
            }
        }

        for (int col = 0; col < 8; col++) {
            board[6][col] = WHITE_PAWN;
            board[7][col] = UNICODE_WHITE[col];
        }
    }

    static void printBoard(String[][] board) {

        System.out.println();
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " ");
            for (int col = 0; col < 8; col++) {
                System.out.printf(" %s ", board[row][col]);
            }
            System.out.println();
        }
        System.out.println("   a  b  c  d  e  f  g  h\n");
    }

    // ================= PAWN MOVE LOGIC =================

    static boolean processPawnMove(String[][] board, String move, boolean whiteTurn) {

        // Expect format like "e4"
        if (move.length() != 2) return false;

        int toCol = move.charAt(0) - 'a';
        int toRow = 8 - (move.charAt(1) - '0');

        if (!inBounds(toRow, toCol)) return false;
        if (!board[toRow][toCol].equals(EMPTY)) return false;

        String pawn = whiteTurn ? WHITE_PAWN : BLACK_PAWN;
        int direction = whiteTurn ? -1 : 1;
        int startRow = whiteTurn ? 6 : 1;

        int foundFromRow = -1;

        for (int row = 0; row < 8; row++) {
            if (board[row][toCol].equals(pawn)) {

                // One-square move
                if (row + direction == toRow) {
                    foundFromRow = row;
                }

                // Two-square move from starting rank
                if (row == startRow &&
                        row + 2 * direction == toRow &&
                        board[row + direction][toCol].equals(EMPTY)) {

                    foundFromRow = row;
                }
            }
        }

        if (foundFromRow == -1) return false;

        board[toRow][toCol] = pawn;
        board[foundFromRow][toCol] = EMPTY;
        return true;
    }

    static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
