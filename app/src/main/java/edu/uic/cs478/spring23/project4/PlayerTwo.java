package edu.uic.cs478.spring23.project4;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Arrays;
import java.util.Random;

public class PlayerTwo implements Runnable{
    private int numPieces = 0;
    private PieceData[] pieces = null;
    public static Handler playerTwo;
    int[][] personalBoard;
    @Override
    public void run() {
        // prepare the loop
        Looper.prepare();
        // create the playerTwo handler
        playerTwo = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Board.MOVE_MADE:
                        updateBoard(new PieceData(msg.arg1,msg.arg2,1),(PieceData)msg.obj);
                        doMove();
                        break;
                    default: break;
                }
            }
        };
        personalBoard = new int[3][3];
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                personalBoard[i][j] = 0;
        Board.handlerInit++;
        Looper.loop();
    }
    private void doMove() {
        if (numPieces < 3) {
            // If there are less than 3 pieces on the board, add a new random piece
            // and update the board
            pieces = pieces == null ? new PieceData[3] : pieces;
            PieceData piece = getRandomPosition();
            pieces[numPieces++] = piece;
            Board.movePiece(new PieceData(-1, -1, Board.BLUE), piece);
            updateBoard(new PieceData(-1, -1, Board.BLUE), piece);
            return;
        }
        // Find the best possible move
        PieceData bestMove = findBestPossibleMove();

        // Search for a piece that can be moved to the best possible move
        PieceData piece = null;
        for (int i = 0; i < 3; i++) {
            boolean isRow = bestMove.getID() == 3;
            boolean isCol = bestMove.getID() == 4;
            boolean isPieceMatched = isRow ? pieces[i].getX() == bestMove.getX() : pieces[i].getY() == bestMove.getX();
            if (isRow || isCol) {
                if (!isPieceMatched) {
                    piece = pieces[i];
                    break;
                }
            }
        }
        // Find the best possible opening spot and move the piece there
        PieceData newSpot = getBestPossibleOpening(bestMove);
        updateBoard(piece, newSpot);
        Board.movePiece(piece, newSpot);
    }
    // update the playerTwo personal board to help decide their new piece
    private void updateBoard(PieceData oldPiece, PieceData newPiece) {
        if(newPiece == null) {
            return;
        }
        int oldX = oldPiece.getX();
        int oldY = oldPiece.getY();
        int newX = newPiece.getX();
        int newY = newPiece.getY();
        // Remove the old piece from the board if there was one
        if (oldX != -1) {
            personalBoard[oldX][oldY] = 0;
        }

        // Add the new piece to the board
        personalBoard[newX][newY] = newPiece.getID() == 2 ? 1 : -1;
    }
    // getting the position at any random spot
    private PieceData getRandomPosition() {
        int x, y;
        do {
            x = new Random().nextInt(3);
            y = new Random().nextInt(3);
        } while (Board.getBoard()[x][y] != Board.EMPTY);

        return new PieceData(x, y, Board.BLUE);
    }
    // ends the player2 thread
    public static void endLooper() {
        playerTwo.removeCallbacksAndMessages(null);
        playerTwo.getLooper().quit();
    }
    // finding the best possible spot to put the piece by finding the max number of 2
    // of either on the column or the row
    private PieceData findBestPossibleMove() {
        PieceData best = new PieceData(-1, -1, -1);
        for (int i = 0; i < 3; i++) {
            int rowSum = Arrays.stream(personalBoard[i]).sum();
            int colSum = Arrays.stream(getColumn(i)).sum();
            if (rowSum > best.getY() || colSum > best.getY()) {
                best.setX(i);
                best.setY(Math.max(rowSum, colSum));
                best.setID(rowSum > colSum ? 3 : 4);
                if (best.getY() == 2) {
                    return best;
                }
            }
        }
        return best;
    }

    private int[] getColumn(int col) {
        int[] column = new int[3];
        for (int i = 0; i < 3; i++) {
            column[i] = personalBoard[i][col];
        }
        return column;
    }
   // help find where the opening that have the ID of vertical or horizontal add to the piece data
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
