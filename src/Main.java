import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(System.in);

    // 3-character wide cells for alignment
    static final String EMPTY = " ＿ ";

    static final String[] UNICODE_WHITE = {
            " ♜ "," ♞ "," ♝ "," ♛ "," ♚ "," ♝ "," ♞ "," ♜ "
    };

    static final String[] UNICODE_BLACK = {
            " ♖ "," ♘ "," ♗ "," ♕ "," ♔ "," ♗ "," ♘ "," ♖ "
    };

    static final String WHITE_PAWN = " ♟ ";
    static final String BLACK_PAWN = " ♙ ";

    static final String BLACK_KNIGHT = " ♘ ";
    static final String WHITE_KNIGHT = " ♞ ";

    public static void main(String[] args) {

        String[][] board = new String[8][8];
        initializeBoard(board);

        boolean whiteTurn = true;

        while (true) {
            printBoard(board);

            System.out.print(whiteTurn ? "White move: " : "Black move: ");
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

    // Handles pawn + knight moves with captures
    static boolean processMove(String[][] board, String move, boolean whiteTurn) {

        move = move.trim();

        // ---------- KNIGHT ----------
        if (move.startsWith("N")) {
            return processKnightMove(board, move, whiteTurn);
        }

        // ---------- PAWN ----------
        return processPawnMove(board, move, whiteTurn);
    }

    static boolean processPawnMove(String[][] board, String move, boolean whiteTurn) {

        boolean capture = move.contains("x");

        int toCol, toRow;
        int fromCol;

        if (capture) {
            fromCol = move.charAt(0) - 'a';
            toCol = move.charAt(2) - 'a';
            toRow = 8 - (move.charAt(3) - '0');
        } else {
            toCol = move.charAt(0) - 'a';
            toRow = 8 - (move.charAt(1) - '0');
            fromCol = toCol;
        }

        if (!inBounds(toRow, toCol)) return false;

        if (whiteTurn) {

            // Capture
            if (capture) {
                int fromRow = toRow + 1;
                if (fromRow < 8 &&
                        board[fromRow][fromCol].equals(WHITE_PAWN) &&
                        !board[toRow][toCol].equals(EMPTY)) {

                    movePiece(board, fromRow, fromCol, toRow, toCol);
                    return true;
                }
            }

            // One step
            if (toRow + 1 < 8 &&
                    board[toRow + 1][toCol].equals(WHITE_PAWN) &&
                    board[toRow][toCol].equals(EMPTY)) {

                movePiece(board, toRow + 1, toCol, toRow, toCol);
                return true;
            }

            // Two step
            if (toRow == 4 &&
                    board[6][toCol].equals(WHITE_PAWN) &&
                    board[5][toCol].equals(EMPTY)) {

                movePiece(board, 6, toCol, toRow, toCol);
                return true;
            }

        } else {

            // Capture
            if (capture) {
                int fromRow = toRow - 1;
                if (fromRow >= 0 &&
                        board[fromRow][fromCol].equals(BLACK_PAWN) &&
                        !board[toRow][toCol].equals(EMPTY)) {

                    movePiece(board, fromRow, fromCol, toRow, toCol);
                    return true;
                }
            }

            // One step
            if (toRow - 1 >= 0 &&
                    board[toRow - 1][toCol].equals(BLACK_PAWN) &&
                    board[toRow][toCol].equals(EMPTY)) {

                movePiece(board, toRow - 1, toCol, toRow, toCol);
                return true;
            }

            // Two step
            if (toRow == 3 &&
                    board[1][toCol].equals(BLACK_PAWN) &&
                    board[2][toCol].equals(EMPTY)) {

                movePiece(board, 1, toCol, toRow, toCol);
                return true;
            }
        }

        return false;
    }

    static boolean processKnightMove(String[][] board, String move, boolean whiteTurn) {

        boolean capture = move.contains("x");

        int toCol = move.charAt(capture ? 2 : 1) - 'a';
        int toRow = 8 - (move.charAt(capture ? 3 : 2) - '0');

        if (!inBounds(toRow, toCol)) return false;

        String knight = whiteTurn ? WHITE_KNIGHT : BLACK_KNIGHT;

        int[][] offsets = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] o : offsets) {
            int fr = toRow + o[0];
            int fc = toCol + o[1];

            if (inBounds(fr, fc) && board[fr][fc].equals(knight)) {

                if (capture && board[toRow][toCol].equals(EMPTY)) return false;
                if (!capture && !board[toRow][toCol].equals(EMPTY)) return false;

                movePiece(board, fr, fc, toRow, toCol);
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
