import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(System.in);

    static final boolean USE_UNICODE = true;

    static final String[] UNICODE_BLACK = {
            "\u2656","\u2658","\u2657","\u2655","\u2654","\u2657","\u2658","\u2656"
    };

    static final String[] UNICODE_WHITE = {
            "\u265C","\u265E","\u265D","\u265B","\u265A","\u265D","\u265E","\u265C"
    };

    static final String BLACK_PAWN = "\u2659";
    static final String WHITE_PAWN = "\u265F";

    public static void main(String[] args) {

        String[][] board = new String[8][8];
        initializeBoard(board);

        boolean whiteTurn = true;

        while (true) {
            printBoard(board);

            if (whiteTurn) {
                System.out.print("White move (e2 e4): ");
            } else {
                System.out.print("Black move (e7 e5): ");
            }

            String move = in.nextLine().trim();

            if (move.equalsIgnoreCase("exit")) break;

            if (!processMove(board, move, whiteTurn)) {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            whiteTurn = !whiteTurn;
        }
    }

    static void initializeBoard(String[][] board) {

        for (int col = 0; col < 8; col++) {
            board[0][col] = UNICODE_BLACK[col];
            board[1][col] = BLACK_PAWN;
        }

        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = ".";
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
                System.out.print(" " + board[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println("   a  b  c  d  e  f  g  h\n");
    }

    static boolean processMove(String[][] board, String move, boolean whiteTurn) {

        String[] parts = move.split(" ");
        if (parts.length != 2) return false;

        int fromCol = parts[0].charAt(0) - 'a';
        int fromRow = 8 - (parts[0].charAt(1) - '0');

        int toCol = parts[1].charAt(0) - 'a';
        int toRow = 8 - (parts[1].charAt(1) - '0');

        if (!inBounds(fromRow, fromCol) || !inBounds(toRow, toCol)) {
            return false;
        }

        String piece = board[fromRow][fromCol];

        if (piece.equals(".")) return false;

        // Pawn logic (simple)
        // Pawn logic (with first double move)
        if (whiteTurn && piece.equals(WHITE_PAWN)) {

            // One square forward
            if (fromCol == toCol &&
                    toRow == fromRow - 1 &&
                    board[toRow][toCol].equals(".")) {

                movePiece(board, fromRow, fromCol, toRow, toCol);
                return true;
            }

            // Two squares forward (first move)
            if (fromCol == toCol &&
                    fromRow == 6 &&
                    toRow == 4 &&
                    board[5][toCol].equals(".") &&
                    board[4][toCol].equals(".")) {

                movePiece(board, fromRow, fromCol, toRow, toCol);
                return true;
            }
        }

        if (!whiteTurn && piece.equals(BLACK_PAWN)) {

            // One square forward
            if (fromCol == toCol &&
                    toRow == fromRow + 1 &&
                    board[toRow][toCol].equals(".")) {

                movePiece(board, fromRow, fromCol, toRow, toCol);
                return true;
            }

            // Two squares forward (first move)
            if (fromCol == toCol &&
                    fromRow == 1 &&
                    toRow == 3 &&
                    board[2][toCol].equals(".") &&
                    board[3][toCol].equals(".")) {

                movePiece(board, fromRow, fromCol, toRow, toCol);
                return true;
            }
        }


        return false;
    }

    static void movePiece(String[][] board, int fr, int fc, int tr, int tc) {
        board[tr][tc] = board[fr][fc];
        board[fr][fc] = ".";
    }

    static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
