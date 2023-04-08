package edu.uic.cs478.spring23.project4;

import android.os.Handler;
import android.os.Message;

public class Board {
    private static int[][] board;
    private static Handler mHandler;
    public static int handlerInit;
    public static final int MOVE_MADE = 0;
    public static final int EMPTY = 0;
    public static final int RED = 1;
    public static final int BLUE = 2;

    public Board(Handler mHandler) {
        // Initialize the board with empty slots
        handlerInit = 0;
        board = new int[][] {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        this.mHandler =mHandler;
    }

    public synchronized static void movePiece(PieceData oldPiece, PieceData newPiece) {
        try{
            Thread.sleep(1000);
        } catch (InterruptedException i) {

        }
        int oldX = oldPiece.getX();
        int oldY = oldPiece.getY();

        int newX = newPiece.getX();
        int newY = newPiece.getY();

        if(oldX != -1) {
            board[oldX][oldY] = EMPTY;
        }
        board[newX][newY] = newPiece.getID();

        oldPiece.setX(newX);
        oldPiece.setY(newY);

        Message msg = mHandler.obtainMessage(MOVE_MADE, oldX,oldY, oldPiece);
        mHandler.sendMessage(msg);
    }

    public synchronized static int[][] getBoard() {
        return board;
    }

//    public boolean isSlotEmpty(int row, int col) {
//        return board[row][col] == -1;
//    }
//
//    public boolean isPieceAt(int row, int col, int player) {
//        return board[row][col] == player;
//    }
//
//    public void placePiece(int row, int col, int player) {
//        board[row][col] = player;
//    }
//
//    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
//        int player = board[fromRow][fromCol];
//        board[fromRow][fromCol] = 0;
//        board[toRow][toCol] = player;
//    }
//
//    public boolean hasThreeInARow(int player) {
//        // Check rows
//        for (int row = 0; row < 3; row++) {
//            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
//                return true;
//            }
//        }
//
//        // Check columns
//        for (int col = 0; col < 3; col++) {
//            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
//                return true;
//            }
//        }
//
//        // Check diagonals
//        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
//            return true;
//        }
//
//        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
//            return true;
//        }
//
//        return false;
//    }
//
//    public boolean isBoardFull() {
//        for (int[] row : board) {
//            for (int col : row) {
//                if (col == -1) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

//    public int[][] getBoard() {
//        return board;
//    }
}
