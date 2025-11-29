public class Main {
    // Toggle this to false if your console can't display Unicode chess symbols
    static final boolean USE_UNICODE = true;

    static final String[] UNICODE_WHITE = {"\u2656","\u2658","\u2657","\u2655","\u2654","\u2657","\u2658","\u2656"}; // R N B Q K B N R
    static final String[] UNICODE_BLACK = {"\u265C","\u265E","\u265D","\u265B","\u265A","\u265D","\u265E","\u265C"}; // r n b q k b n r
    static final String WHITE_PAWN = "\u2659"; // pawn
    static final String BLACK_PAWN = "\u265F"; // pawn

    public static void main(String[] args) {
        String[][] board = new String[8][8];
        initializeBoard(board);
        printBoard(board);
    }

    // Initialize the board with the standard chess starting position
    static void initializeBoard(String[][] board) {
        if (USE_UNICODE) {
            // Black pieces on rank 8 and pawns on rank 7
            for (int col = 0; col < 8; col++) {
                board[0][col] = UNICODE_BLACK[col];
                board[1][col] = BLACK_PAWN;
            }
            // Empty squares
            for (int row = 2; row <= 5; row++) {
                for (int col = 0; col < 8; col++) board[row][col] = " . ";
            }
            // White pawns on rank 2 and white pieces on rank 1
            for (int col = 0; col < 8; col++) {
                board[6][col] = WHITE_PAWN;
                board[7][col] = UNICODE_WHITE[col];
            }
        } else {

            String[] white = {"R","N","B","Q","K","B","N","R"};
            String[] black = {"r","n","b","q","k","b","n","r"};
            for (int col = 0; col < 8; col++) {
                board[0][col] = black[col];
                board[1][col] = "p";
            }
            for (int row = 2; row <= 5; row++) {
                for (int col = 0; col < 8; col++) board[row][col] = ".";
            }
            for (int col = 0; col < 8; col++) {
                board[6][col] = "P";
                board[7][col] = white[col];
            }
        }
    }

    // Print the board with ranks and files
    static void printBoard(String[][] board) {
        System.out.println();
        for (int row = 0; row < 8; row++) {
            int rank = 8 - row;
            System.out.print(rank + " "); // rank label
            for (int col = 0; col < 8; col++) {
                String sq = board[row][col];
                // ensure even-width for nicer alignment
                if (sq.length() == 1) sq = " " + sq + " ";
                else if (sq.length() == 2) sq = " " + sq;
                System.out.print(sq + " ");
            }
            System.out.println();
        }
        // file labels
        System.out.println("   a  b  c  d  e  f  g  h");
        System.out.println();
    }
}
