import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(System.in);

    static final String EMPTY = " ＿ ";

    // BLACK pieces (top of board)
    static final String BLACK_PAWN = " ♟ ";
    static final String BLACK_ROOK = " ♜ ";
    static final String BLACK_KNIGHT = " ♞ ";
    static final String BLACK_BISHOP = " ♝ ";
    static final String BLACK_QUEEN = " ♛ ";
    static final String BLACK_KING = " ♚ ";

    // WHITE pieces (bottom of board)
    static final String WHITE_PAWN = " ♙ ";
    static final String WHITE_ROOK = " ♖ ";
    static final String WHITE_KNIGHT = " ♘ ";
    static final String WHITE_BISHOP = " ♗ ";
    static final String WHITE_QUEEN = " ♕ ";
    static final String WHITE_KING = " ♔ ";

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
                System.out.println("Invalid move.");
                continue;
            }

            whiteTurn = !whiteTurn;
        }
    }

    static void initializeBoard(String[][] board) {

        String[] blackBack = {
                BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK
        };

        String[] whiteBack = {
                WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
                WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK
        };

        for (int c = 0; c < 8; c++) {
            board[0][c] = blackBack[c];
            board[1][c] = BLACK_PAWN;
            board[6][c] = WHITE_PAWN;
            board[7][c] = whiteBack[c];
        }

        for (int r = 2; r <= 5; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = EMPTY;
    }

    static void printBoard(String[][] board) {
        System.out.println();
        for (int r = 0; r < 8; r++) {
            System.out.print((8 - r) + " ");
            for (int c = 0; c < 8; c++)
                System.out.print(board[r][c]);
            System.out.println();
        }
        System.out.println("   a  b  c  d  e  f  g  h\n");
    }

    // Supports: e4, exd5, Nf3, Bc4, Rxe5
    static boolean processMove(String[][] board, String move, boolean whiteTurn) {

        boolean capture = move.contains("x");
        move = move.replace("x", "");

        char pieceChar = Character.isUpperCase(move.charAt(0)) ? move.charAt(0) : 'P';
        int idx = pieceChar == 'P' ? 0 : 1;

        String target = move.substring(idx);
        int col = target.charAt(0) - 'a';
        int row = 8 - (target.charAt(1) - '0');

        if (!inBounds(row, col)) return false;

        return switch (pieceChar) {
            case 'P' -> pawnMove(board, row, col, whiteTurn, capture);
            case 'N' -> knightMove(board, row, col, whiteTurn);
            case 'B' -> slidingMove(board, row, col, whiteTurn, true, false);
            case 'R' -> slidingMove(board, row, col, whiteTurn, false, true);
            default -> false;
        };
    }

    // ================= PIECES =================

    static boolean pawnMove(String[][] b, int tr, int tc, boolean white, boolean capture) {

        int dir = white ? -1 : 1;
        String pawn = white ? WHITE_PAWN : BLACK_PAWN;
        String enemyPawn = white ? BLACK_PAWN : WHITE_PAWN;

        // Capture
        if (capture) {
            for (int dc : new int[]{-1, 1}) {
                int fr = tr - dir, fc = tc + dc;
                if (inBounds(fr, fc) && b[fr][fc].equals(pawn)
                        && !b[tr][tc].equals(EMPTY)
                        && !b[tr][tc].equals(pawn)) {
                    movePiece(b, fr, fc, tr, tc);
                    return true;
                }
            }
            return false;
        }

        // One step
        int fr = tr - dir;
        if (inBounds(fr, tc) && b[fr][tc].equals(pawn) && b[tr][tc].equals(EMPTY)) {
            movePiece(b, fr, tc, tr, tc);
            return true;
        }

        // Two step
        int startRow = white ? 6 : 1;
        if (tr == (white ? 4 : 3) && b[startRow][tc].equals(pawn)
                && b[startRow + dir][tc].equals(EMPTY)) {
            movePiece(b, startRow, tc, tr, tc);
            return true;
        }

        return false;
    }

    static boolean knightMove(String[][] b, int tr, int tc, boolean white) {

        String knight = white ? WHITE_KNIGHT : BLACK_KNIGHT;

        int[][] jumps = {
                {-2,-1},{-2,1},{-1,-2},{-1,2},
                {1,-2},{1,2},{2,-1},{2,1}
        };

        for (int[] j : jumps) {
            int fr = tr + j[0], fc = tc + j[1];
            if (inBounds(fr, fc) && b[fr][fc].equals(knight)) {
                movePiece(b, fr, fc, tr, tc);
                return true;
            }
        }
        return false;
    }

    static boolean slidingMove(String[][] b, int tr, int tc, boolean white,
                               boolean diag, boolean straight) {

        String bishop = white ? WHITE_BISHOP : BLACK_BISHOP;
        String rook = white ? WHITE_ROOK : BLACK_ROOK;

        int[][] dirs = diag
                ? new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}}
                : new int[][]{{1,0},{-1,0},{0,1},{0,-1}};

        for (int[] d : dirs) {
            int r = tr + d[0], c = tc + d[1];
            while (inBounds(r, c) && b[r][c].equals(EMPTY)) {
                r += d[0]; c += d[1];
            }
            if (inBounds(r, c) &&
                    (b[r][c].equals(bishop) || b[r][c].equals(rook))) {
                movePiece(b, r, c, tr, tc);
                return true;
            }
        }
        return false;
    }

    // ================= HELPERS =================

    static void movePiece(String[][] b, int fr, int fc, int tr, int tc) {
        b[tr][tc] = b[fr][fc];
        b[fr][fc] = EMPTY;
    }

    static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
