import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(System.in);

    static final String EMPTY = " ＿ ";

    // BLACK
    static final String BP = " ♟ ";
    static final String BR = " ♜ ";
    static final String BN = " ♞ ";
    static final String BB = " ♝ ";
    static final String BQ = " ♛ ";
    static final String BK = " ♚ ";

    // WHITE
    static final String WP = " ♙ ";
    static final String WR = " ♖ ";
    static final String WN = " ♘ ";
    static final String WB = " ♗ ";
    static final String WQ = " ♕ ";
    static final String WK = " ♔ ";

    public static void main(String[] args) {

        String[][] board = new String[8][8];
        setup(board);

        boolean whiteTurn = true;

        while (true) {
            printBoard(board);

            System.out.print((whiteTurn ? "White" : "Black") + " move: ");
            String move = in.nextLine().trim();

            if (move.equalsIgnoreCase("exit")) break;

            if (!processMove(board, move, whiteTurn)) {
                System.out.println("Invalid move.");
                continue;
            }

            if (isKingInCheck(board, !whiteTurn)) {
                System.out.println("CHECK!");
            }

            whiteTurn = !whiteTurn;
        }
    }

    // ================= SETUP =================
    static void setup(String[][] b) {
        String[] backW = {WR, WN, WB, WQ, WK, WB, WN, WR};
        String[] backB = {BR, BN, BB, BQ, BK, BB, BN, BR};

        for (int i = 0; i < 8; i++) {
            b[0][i] = backB[i];
            b[1][i] = BP;
            b[6][i] = WP;
            b[7][i] = backW[i];
        }

        for (int r = 2; r <= 5; r++)
            for (int c = 0; c < 8; c++)
                b[r][c] = EMPTY;
    }

    // ================= PRINT =================
    static void printBoard(String[][] b) {
        System.out.println();
        for (int r = 0; r < 8; r++) {
            System.out.print((8 - r) + " ");
            for (int c = 0; c < 8; c++)
                System.out.print(b[r][c]);
            System.out.println();
        }
        System.out.println("   a  b  c  d  e  f  g  h\n");
    }

    // ================= MOVE PARSER =================
    static boolean processMove(String[][] b, String move, boolean white) {

        boolean capture = move.contains("x");
        move = move.replace("x", "");

        char pieceChar = Character.isUpperCase(move.charAt(0)) ? move.charAt(0) : 'P';
        int offset = pieceChar == 'P' ? 0 : 1;

        int tc = move.charAt(offset) - 'a';
        int tr = 8 - (move.charAt(offset + 1) - '0');

        if (!inBounds(tr, tc)) return false;

        return switch (pieceChar) {
            case 'P' -> pawnMove(b, tr, tc, white, capture);
            case 'N' -> knightMove(b, tr, tc, white);
            case 'B' -> slidingMove(b, tr, tc, white, true, false);
            case 'R' -> slidingMove(b, tr, tc, white, false, true);
            case 'Q' -> slidingMove(b, tr, tc, white, true, true);
            default -> false;
        };
    }

    // ================= PAWN =================
    static boolean pawnMove(String[][] b, int tr, int tc, boolean white, boolean capture) {

        int dir = white ? -1 : 1;
        String pawn = white ? WP : BP;

        // Capture
        if (capture) {
            for (int dc : new int[]{-1, 1}) {
                int fr = tr - dir, fc = tc + dc;
                if (inBounds(fr, fc) && b[fr][fc].equals(pawn)
                        && !b[tr][tc].equals(EMPTY)) {
                    move(b, fr, fc, tr, tc);
                    return true;
                }
            }
            return false;
        }

        // One step
        int fr = tr - dir;
        if (inBounds(fr, tc) && b[fr][tc].equals(pawn) && b[tr][tc].equals(EMPTY)) {
            move(b, fr, tc, tr, tc);
            return true;
        }

        // Two steps
        int start = white ? 6 : 1;
        if (fr == start + dir && b[start][tc].equals(pawn)
                && b[start + dir][tc].equals(EMPTY)
                && b[tr][tc].equals(EMPTY)) {
            move(b, start, tc, tr, tc);
            return true;
        }

        return false;
    }

    // ================= KNIGHT =================
    static boolean knightMove(String[][] b, int tr, int tc, boolean white) {

        String knight = white ? WN : BN;
        int[][] d = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};

        for (int[] m : d) {
            int fr = tr + m[0], fc = tc + m[1];
            if (inBounds(fr, fc) && b[fr][fc].equals(knight)) {
                move(b, fr, fc, tr, tc);
                return true;
            }
        }
        return false;
    }

    // ================= SLIDERS =================
    static boolean slidingMove(String[][] b, int tr, int tc, boolean white,
                               boolean diag, boolean straight) {

        String bishop = white ? WB : BB;
        String rook = white ? WR : BR;
        String queen = white ? WQ : BQ;

        int[][] dirs = {
                {1,0},{-1,0},{0,1},{0,-1},
                {1,1},{1,-1},{-1,1},{-1,-1}
        };

        for (int[] d : dirs) {
            boolean isDiag = Math.abs(d[0]) == Math.abs(d[1]);
            if ((isDiag && !diag) || (!isDiag && !straight)) continue;

            int r = tr + d[0], c = tc + d[1];
            while (inBounds(r, c) && b[r][c].equals(EMPTY)) {
                r += d[0];
                c += d[1];
            }

            if (inBounds(r, c) &&
                    (b[r][c].equals(bishop) || b[r][c].equals(rook) || b[r][c].equals(queen))) {
                move(b, r, c, tr, tc);
                return true;
            }
        }
        return false;
    }

    // ================= CHECK =================
    static boolean isKingInCheck(String[][] b, boolean whiteKing) {

        String king = whiteKing ? WK : BK;
        int kr = -1, kc = -1;

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (b[r][c].equals(king)) {
                    kr = r; kc = c;
                }

        String enemyQueen = whiteKing ? BQ : WQ;
        String enemyRook = whiteKing ? BR : WR;
        String enemyBishop = whiteKing ? BB : WB;
        String enemyKnight = whiteKing ? BN : WN;

        int[][] knightD = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        for (int[] d : knightD) {
            int r = kr + d[0], c = kc + d[1];
            if (inBounds(r,c) && b[r][c].equals(enemyKnight)) return true;
        }

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : dirs) {
            int r = kr + d[0], c = kc + d[1];
            while (inBounds(r,c) && b[r][c].equals(EMPTY)) {
                r += d[0]; c += d[1];
            }
            if (!inBounds(r,c)) continue;

            if (b[r][c].equals(enemyQueen)) return true;
            if ((d[0]==0||d[1]==0) && b[r][c].equals(enemyRook)) return true;
            if (Math.abs(d[0])==1 && Math.abs(d[1])==1 && b[r][c].equals(enemyBishop)) return true;
        }

        return false;
    }

    // ================= HELPERS =================
    static void move(String[][] b, int fr, int fc, int tr, int tc) {
        b[tr][tc] = b[fr][fc];
        b[fr][fc] = EMPTY;
    }

    static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
