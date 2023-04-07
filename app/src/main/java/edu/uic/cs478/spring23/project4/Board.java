package edu.uic.cs478.spring23.project4;

public class Board {
    private int[][] board;
    public static final int EMPTY = 0;
    public static final int RED = 1;
    public static final int BLUE = 2;

    public Board() {
        // Initialize the board with empty slots
        board = new int[][] {
                {-1, -1, -1},
                {-1, -1, -1},
                {-1, -1, -1}
        };
    }

    public boolean isSlotEmpty(int row, int col) {
        return board[row][col] == -1;
    }

    public boolean isPieceAt(int row, int col, int player) {
        return board[row][col] == player;
    }

    public void placePiece(int row, int col, int player) {
        board[row][col] = player;
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        int player = board[fromRow][fromCol];
        board[fromRow][fromCol] = -1;
        board[toRow][toCol] = player;
    }

    public boolean hasThreeInARow(int player) {
        // Check rows
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }

        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }

        return false;
    }

    public boolean isBoardFull() {
        for (int[] row : board) {
            for (int col : row) {
                if (col == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getBoard() {
        return board;
    }
}
