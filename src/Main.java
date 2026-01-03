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
                System.out.println("Illegal move.");
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

        return attemptMove(b, pieceChar, tr, tc, white, capture);
    }

    // ================= SAFE MOVE =================
    static boolean attemptMove(String[][] b, char piece, int tr, int tc, boolean white, boolean capture) {

        for (int fr = 0; fr < 8; fr++) {
            for (int fc = 0; fc < 8; fc++) {

                String p = b[fr][fc];
                if (!isCorrectPiece(p, piece, white)) continue;

                if (!isLegalPieceMove(b, fr, fc, tr, tc, piece, white, capture))
                    continue;

                // simulate
                String tempFrom = b[fr][fc];
                String tempTo = b[tr][tc];
                b[tr][tc] = tempFrom;
                b[fr][fc] = EMPTY;

                boolean illegal = isKingInCheck(b, white);

                if (!illegal) return true;

                // revert
                b[fr][fc] = tempFrom;
                b[tr][tc] = tempTo;
            }
        }
        return false;
    }

    // ================= MOVE RULES =================
    static boolean isLegalPieceMove(String[][] b, int fr, int fc, int tr, int tc,
                                    char piece, boolean white, boolean capture) {

        int dr = tr - fr, dc = tc - fc;

        if (!capture && !b[tr][tc].equals(EMPTY)) return false;
        if (capture && b[tr][tc].equals(EMPTY)) return false;

        switch (piece) {

            case 'P' -> {
                int dir = white ? -1 : 1;
                if (!capture && dc == 0 && dr == dir) return true;
                if (!capture && dc == 0 && dr == 2 * dir &&
                        ((white && fr == 6) || (!white && fr == 1)) &&
                        b[fr + dir][fc].equals(EMPTY)) return true;
                if (capture && Math.abs(dc) == 1 && dr == dir) return true;
            }

            case 'N' -> {
                return (Math.abs(dr) == 2 && Math.abs(dc) == 1) ||
                        (Math.abs(dr) == 1 && Math.abs(dc) == 2);
            }

            case 'B' -> {
                return Math.abs(dr) == Math.abs(dc) && clearPath(b, fr, fc, tr, tc);
            }

            case 'R' -> {
                return (dr == 0 || dc == 0) && clearPath(b, fr, fc, tr, tc);
            }

            case 'Q' -> {
                return (dr == 0 || dc == 0 || Math.abs(dr) == Math.abs(dc))
                        && clearPath(b, fr, fc, tr, tc);
            }

            case 'K' -> {
                return Math.abs(dr) <= 1 && Math.abs(dc) <= 1;
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
                    kr = r;
                    kc = c;
                }

        return isSquareAttacked(b, kr, kc, !whiteKing);
    }

    static boolean isSquareAttacked(String[][] b, int tr, int tc, boolean byWhite) {

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                if (isCorrectPiece(b[r][c], 'P', byWhite)
                        && isLegalPieceMove(b, r, c, tr, tc, 'P', byWhite, true))
                    return true;

                for (char p : "NBRQK".toCharArray())
                    if (isCorrectPiece(b[r][c], p, byWhite)
                            && isLegalPieceMove(b, r, c, tr, tc, p, byWhite, true))
                        return true;
            }
        return false;
    }

    // ================= HELPERS =================
    static boolean clearPath(String[][] b, int fr, int fc, int tr, int tc) {
        int dr = Integer.compare(tr, fr);
        int dc = Integer.compare(tc, fc);
        fr += dr;
        fc += dc;
        while (fr != tr || fc != tc) {
            if (!b[fr][fc].equals(EMPTY)) return false;
            fr += dr;
            fc += dc;
        }
        return true;
    }

    static boolean isCorrectPiece(String p, char piece, boolean white) {
        return switch (piece) {
            case 'P' -> white ? p.equals(WP) : p.equals(BP);
            case 'N' -> white ? p.equals(WN) : p.equals(BN);
            case 'B' -> white ? p.equals(WB) : p.equals(BB);
            case 'R' -> white ? p.equals(WR) : p.equals(BR);
            case 'Q' -> white ? p.equals(WQ) : p.equals(BQ);
            case 'K' -> white ? p.equals(WK) : p.equals(BK);
            default -> false;
        };
    }

    static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
