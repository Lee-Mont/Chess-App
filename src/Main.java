import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(System.in);

    static final String EMPTY = " · ";

    static final String[] UNICODE_BLACK = {
            " ♜ "," ♞ "," ♝ "," ♛ "," ♚ "," ♝ "," ♞ "," ♜ "
    };

    static final String[] UNICODE_WHITE = {
            " ♖ "," ♘ "," ♗ "," ♕ "," ♔ "," ♗ "," ♘ "," ♖ "
    };

    static final String BLACK_PAWN = " ♟ ";
    static final String WHITE_PAWN = " ♙ ";

    public static void main(String[] args) {

        String[][] board = new String[8][8];
        initializeBoard(board);

        boolean whiteTurn = true;

        while (true) {
            printBoard(board);

            System.out.print(whiteTurn ? "White move (e4): " : "Black move (e5): ");
            String move = in.nextLine().trim().toLowerCase();

            if (move.equals("exit")) break;

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
                System.out.print(board[row][col]);
            }
            System.out.println();
        }
        System.out.println("   a  b  c  d  e  f  g  h\n");
    }

    static boolean processMove(String[][] board, String move, boolean whiteTurn) {

        if (move.length() != 2) return false;

        int toCol = move.charAt(0) - 'a';
        int toRow = 8 - (move.charAt(1) - '0');

        if (!inBounds(toRow, toCol)) return false;

        // Find pawn that can move there
        if (whiteTurn) {
            int fromRow = toRow + 1;
            if (fromRow < 8 && board[fromRow][toCol].equals(WHITE_PAWN)
                    && board[toRow][toCol].equals(EMPTY)) {

                movePiece(board, fromRow, toCol, toRow, toCol);
                return true;
            }
        } else {
            int fromRow = toRow - 1;
            if (fromRow >= 0 && board[fromRow][toCol].equals(BLACK_PAWN)
                    && board[toRow][toCol].equals(EMPTY)) {

                movePiece(board, fromRow, toCol, toRow, toCol);
                return true;
            }
        }

        return false;
    }

    static void movePiece(String[][] board, int fr, int fc, int tr, int tc) {
        board[tr][tc] = board[fr][fc];
        board[fr][fc] = EMPTY;
    }

    static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
