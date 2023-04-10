package edu.uic.cs478.spring23.project4;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
        board = new int[][]{{0, 0, 0},
                            {0, 0, 0},
                            {0, 0, 0}};

        this.mHandler =mHandler;
    }

    public synchronized static void movePiece(PieceData oldPiece, PieceData newPiece) {
        try{
            Thread.sleep(1000);
        } catch (InterruptedException i) {
            Log.i("myError","an error has affected in pause");
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


        mHandler.sendMessage(mHandler.obtainMessage(MOVE_MADE, oldX,oldY, oldPiece));
    }

    public synchronized static int[][] getBoard() {
        return board;
    }

}
