package edu.uic.cs478.spring23.project4;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Random;

public class PlayerTwo implements Runnable{
    public static final int MOVE_MADE = 0;
    private int numPieces = 0;
    private PieceData[] pieces = null;
    public static Handler playerTwo;
    int[][] personalBoard;

    public void run() {
        Looper.prepare();
        playerTwo = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MOVE_MADE:
                        updateBoard(new PieceData(msg.arg1,msg.arg2,1),(PieceData)msg.obj);
                        doMove();
                        break;
                    default: break;
                }
            }
        };
//        Board.handlerInit++;
//        Looper.loop();
    }
    private void doMove() {
        if (numPieces < 3) {
            if(pieces == null) {
                pieces = new PieceData[3];
            }
            pieces[numPieces] = getRandomPosition();
            Board.movePiece(new PieceData(-1,-1, Board.BLUE), pieces[numPieces]);
            updateBoard(new PieceData(-1,-1,Board.BLUE), pieces[numPieces]);
            numPieces++;
            return;
        }
        PieceData bestMove = findBestPossibleMove();
        int pieceNum = -1;
        for(int i = 0; i < 3; i++) {
            if (bestMove.getID() == 3 && pieces[i].getX() != bestMove.getX()){
                pieceNum = i;
                break;
            }
            else if (bestMove.getID() == 4 && pieces[i].getY() != bestMove.getX())
                pieceNum = i;
            break;
        }
        PieceData newSpot = getBestPossibleOpening(bestMove);
        updateBoard(pieces[pieceNum], newSpot);
        Board.movePiece(pieces[pieceNum], newSpot);
    }
    private void updateBoard(PieceData oldPiece, PieceData newPiece) {
        if(newPiece == null) {
            return;
        }
        int oldX = oldPiece.getX();
        int oldY = oldPiece.getY();
        int newX = newPiece.getX();
        int newY = newPiece.getY();
        if(oldX != -1) {
            personalBoard[oldX][oldY] = 0;
        }
        if(newPiece.getID() == 2) {
            personalBoard[newX][newY] = 1;
        }
        else {
            personalBoard[newX][newY] = -1;
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
        playerTwo.removeCallbacksAndMessages(null);
        playerTwo.getLooper().quit();
    }
   private PieceData findBestPossibleMove() {
        PieceData best = new PieceData(-1,-1,-1);
        for(int i = 0; i < 3; i++) {
            int horizontal = 0;
            int vertical = 0;
            for (int j = 0; j < 3; j++){
                horizontal += personalBoard[i][j];
                vertical += personalBoard[j][i];
            }
            if(best.getY() < vertical) {
                best.setX(i);
                best.setY(vertical);
                best.setID(4);
                if(vertical == 2) {
                    return best;
                }
            }
            else if(best.getY() < horizontal) {
                best.setX(i);
                best.setY(horizontal);
                best.setID(3);
                if(horizontal == 2) {
                    return best;
                }
            }
       }
        return best;
   }
   private PieceData getBestPossibleOpening(PieceData bestMove) {
        for(int i = 0; i < 3; i++) {
            if(bestMove.getID() == 3 && Board.getBoard()[bestMove.getX()][i] == Board.EMPTY) {
                return new PieceData(bestMove.getX(), i, Board.BLUE);
            }
            else if(bestMove.getID() == 4 && Board.getBoard()[i][bestMove.getX()] == Board.EMPTY){
                return new PieceData(i, bestMove.getX(), Board.BLUE);
            }
        }
        return getRandomPosition();
   }
}
