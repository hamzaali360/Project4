package edu.uic.cs478.spring23.project4;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Random;

public class PlayerOne implements Runnable{
    public static final int MOVE_MADE = 0;
    private int numPieces = 0;
    private PieceData[] pieces = null;
    public static Handler playerOne;

    public void run() {
        Looper.prepare();
        playerOne = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MOVE_MADE:
                        doMove();
                        break;
                    default: break;
                }
            }
        };
        Board.handlerInit++;
        Looper.loop();
    }
    private void doMove() {
        if (numPieces < 3) {
            if(pieces == null) {
                pieces = new PieceData[3];
            }
            pieces[numPieces] = getRandomPosition();
        }
    }
    private  PieceData getRandomPosition() {
        int x = new Random().nextInt(3);
        int y = new Random().nextInt(3);
        while(!(Board.getBoard()[x][y] == Board.EMPTY)) {
            x = new Random().nextInt(3);
            y = new Random().nextInt(3);
        }
        return new PieceData(x, y, 1);
    }
    public static void endLooper() {
        playerOne.removeCallbacksAndMessages(null);
        playerOne.getLooper().quit();
    }
}
