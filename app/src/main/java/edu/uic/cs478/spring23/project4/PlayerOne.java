package edu.uic.cs478.spring23.project4;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import java.util.Random;

public class PlayerOne implements Runnable{
    private int numPieces = 0;
    private PieceData[] pieces = null;
    public static Handler playerOne;
    @Override
    public void run() {
        // prepare the loop
        Looper.prepare();
        //initialize the player handler when ever making a move
        playerOne = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Board.MOVE_MADE:
                        doMove();
                        break;
                    default: break;
                }
            }
        };
        // let the Board know that the playerone thread is ready
        Board.handlerInit++;
        // start the loop
        Looper.loop();
    }
    // PlayerOne's strategy to moving the piece
    private void doMove() {
        // if there are less than three pieces on the board, add a new piece
        if (numPieces < 3) {
            // if pieces array is null, initialize it with size 3
            if(pieces == null) {
                pieces = new PieceData[3];
            }
            // get a new random position for the piece
            pieces[numPieces] = getRandomPosition();
            // move the piece to the new spot on the board with color red
            Board.movePiece(new PieceData(-1, -1, Board.RED), pieces[numPieces]);
            // increase the number of pieces on the board
            numPieces++;
            return;
        }
        // if there are already three pieces on the board
        // get a random number to select a random piece to move
        int pieceNum = new Random().nextInt(3);
        // get a new random position for the piece to move to
        PieceData randomPosition = getRandomPosition();
        // move the selected piece to the new spot on the board
        Board.movePiece(pieces[pieceNum], randomPosition);
    }
    // getting the position at any random spot
    private PieceData getRandomPosition() {
        int x, y;
        do {
            x = new Random().nextInt(3);
            y = new Random().nextInt(3);
        } while (Board.getBoard()[x][y] != Board.EMPTY);

        return new PieceData(x, y, Board.RED);
    }
    // end the playerOne thread
    public static void endLooper() {
        playerOne.removeCallbacksAndMessages(null);
        playerOne.getLooper().quit();
    }
}
