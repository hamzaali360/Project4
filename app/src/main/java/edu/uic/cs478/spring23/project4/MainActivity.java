package edu.uic.cs478.spring23.project4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int UPDATE_BOARD = 1;
    private static final int UPDATE_STATUS = 2;
    private static final int NEW_GAME = 3;
    private Handler mHandler;
    private Board mBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case UPDATE_BOARD:
                        updateBoard((int[]) msg.obj);
                        break;
                    case UPDATE_STATUS:
                        updateStatus((String) msg.obj);
                        break;
                    case NEW_GAME:
                        newGame();
                        break;
                }
                // Handle the message from the worker threads here
            }
        };
        mBoard = new Board();
        updateBoard(mBoard.getBoardState());
        updateStatus("To start a new game, click the New Game button.");

        Button newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(NEW_GAME);
            }
        });
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // Update the UI here
//            }
//        });
    }
    private void updateBoard(int[] boardState) {
        ImageView[] slots = new ImageView[9];
        slots[0] = findViewById(R.id.slot1);
        slots[1] = findViewById(R.id.slot2);
        slots[2] = findViewById(R.id.slot3);
        slots[3] = findViewById(R.id.slot4);
        slots[4] = findViewById(R.id.slot5);
        slots[5] = findViewById(R.id.slot6);
        slots[6] = findViewById(R.id.slot7);
        slots[7] = findViewById(R.id.slot8);
        slots[8] = findViewById(R.id.slot9);

        for (int i = 0; i < 9; i++) {
            int state = boardState[i];
            if (state == Board.EMPTY) {
                slots[i].setImageResource(R.drawable.empty_slot);
            } else if (state == Board.RED) {
                slots[i].setImageResource(R.drawable.red_piece);
            } else if (state == Board.BLUE) {
                slots[i].setImageResource(R.drawable.blue_piece);
            }
        }
    }
    private void updateStatus(String status) {
        TextView statusView = findViewById(R.id.status);
        statusView.setText(status);
    }

    private void newGame() {
        mBoard.newGame();
        updateBoard(mBoard.getBoardState());
        updateStatus("New game started.");
    }
}

public class WorkerThread extends Thread {
    private static final int MOVE = 1;
    private static final int GAME_OVER = 2;

    private Handler mHandler;
    private Board mBoard;
    private int mPlayer;
    private boolean mGameOver;

    public WorkerThread(Handler handler, Board board, int player) {
        mHandler = handler;
        mBoard = board;
        mPlayer = player;
        mGameOver = false;
    }

    @Override
    public void run() {
        while (!mGameOver) {
            int[] move = getMove();
            mBoard.makeMove(mPlayer, move[0], move[1]);
            if (mBoard.checkWin(mPlayer)) {
                mHandler.sendEmptyMessage(GAME_OVER);
                mGameOver = true;
            } else {
                // Determine the next move
                int nextMove = mCurrentPlayer == PLAYER_ONE ?
                        mPlayerOne.getNextMove(mBoard) : mPlayerTwo.getNextMove(mBoard);
            }
            // Check if the move is valid
            if (isValidMove(nextMove)) {
                // Update the board
                mBoard[nextMove] = mCurrentPlayer;

                // Check for a win
                if (isWinningMove(nextMove)) {
                    // Send a message to the UI thread that the game is over
                    mHandler.sendEmptyMessage(GAME_OVER);
                    mGameOver = true;
                } else {
                    // Switch to the other player
                    mCurrentPlayer = mCurrentPlayer == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE;

                    // Send a message to the UI thread to update the display
                    Message msg = mHandler.obtainMessage(UPDATE_BOARD, mBoard);
                    mHandler.sendMessage(msg);

                    // Send a message to the other player's thread to take its turn
                    if (mCurrentPlayer == PLAYER_ONE) {
                        mPlayerOneHandler.sendEmptyMessage(TAKE_TURN);
                    } else {
                        mPlayerTwoHandler.sendEmptyMessage(TAKE_TURN);
                    }
                }
            } else {
                // Send a message to the current player's thread to take its turn again
                if (mCurrentPlayer == PLAYER_ONE) {
                    mPlayerOneHandler.sendEmptyMessage(TAKE_TURN);
                } else {
                    mPlayerTwoHandler.sendEmptyMessage(TAKE_TURN);
                }
            }
        }
    }
    /**
     * Checks if a move is valid.
     */
    private boolean isValidMove(int move) {
        return mBoard[move] == EMPTY_SLOT;
    }

    /**
     * Checks if a move is a winning move.
     */
    private boolean isWinningMove(int move) {
        // Check if the move creates a winning row
        if (mBoard[move] == mBoard[getOppositeCorner(move)] && mBoard[move] == mBoard[getAdjacentSlot(move)]) {
            return true;
        }

        // Check if the move creates a winning column
        if (mBoard[move] == mBoard[getOppositeCorner(move)] && mBoard[move] == mBoard[getOppositeCorner(getAdjacentSlot(move))]) {
            return true;
        }

        // Check if the move creates a winning diagonal
        if (move % 2 == 0 && mBoard[move] == mBoard[4] && mBoard[move] == mBoard[getOppositeCorner(move)]) {
            return true;
        }

        return false;
    }

    /**
     * Gets the opposite corner of a slot.
     */
    private int getOppositeCorner(int slot) {
        switch (slot) {
            case 0:
                return 8;
            case 2:
                return 6;
            case 6:
                return 2;
            case 8:
                return 0;
            default:
                return -1;
        }
    }

    /**
     * Gets the adjacent slot of a slot.
     */
    private int getAdjacentSlot(int slot) {
        switch (slot) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 0;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 3;
            case 6:
                return 7;
            case 7:
                return 8;
            case 8:
                return 6;
            default:
                return -1;
        }
    }
    private void handleMove(int slot) {
        // get the current player
        Player currentPlayer = mCurrentPlayer.get();

        // move the piece
        int currentSlot = currentPlayer.getSlot();
        mBoard[currentSlot] = 0;
        mBoard[slot] = currentPlayer.getId();
        currentPlayer.setSlot(slot);

        // check for a win
        boolean hasWon = checkWin(currentPlayer.getId());
        if (hasWon) {
            Message message = mHandler.obtainMessage(GAME_OVER, currentPlayer);
            mHandler.sendMessage(message);
            mGameOver = true;
        } else {
            // check for a tie
            boolean isTie = checkTie();
            if (isTie) {
                mHandler.sendEmptyMessage(GAME_OVER);
                mGameOver = true;
            } else {
                // switch to the other player
                mCurrentPlayer.set(mPlayer1 == currentPlayer ? mPlayer2 : mPlayer1);

                // determine the next move of the worker threads
                if (currentPlayer == mPlayer1) {
                    int nextMove = mPlayer1Strategy.getNextMove(mBoard, mPlayer1.getId(), mPlayer2.getId());
                    Message message = mHandler.obtainMessage(PLAYER1_MOVE, nextMove);
                    mHandler.sendMessage(message);
                } else {
                    int nextMove = mPlayer2Strategy.getNextMove(mBoard, mPlayer2.getId(), mPlayer1.getId());
                    Message message = mHandler.obtainMessage(PLAYER2_MOVE, nextMove);
                    mHandler.sendMessage(message);
                }
            }
        }
    }

    private boolean checkWin(int player) {
        // check for a win on rows
        for (int i = 0; i < 9; i += 3) {
            if (mBoard[i] == player && mBoard[i+1] == player && mBoard[i+2] == player) {
                return true;
            }
        }

        // check for a win on columns
        for (int i = 0; i < 3; i++) {
            if (mBoard[i] == player && mBoard[i+3] == player && mBoard[i+6] == player) {
                return true;
            }
        }

        // check for a win on diagonals
        if (mBoard[0] == player && mBoard[4] == player && mBoard[8] == player) {
            return true;
        }

        if (mBoard[2] == player && mBoard[4] == player && mBoard[6] == player) {
            return true;
        }

        return false;
    }

    private boolean checkTie() {
        for (int i = 0; i < 9; i++) {
            if (mBoard[i] == 0) {
                return false;
            }
        }
        return true;
    }
}
private class WorkerThreadHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_MAKE_MOVE:
                int move = getNextMove();
                Message moveMsg = mUiHandler.obtainMessage(MSG_WORKER_MOVE, move);
                mUiHandler.sendMessage(moveMsg);
                break;
        }
    }

    private int getNextMove() {
        // Implement your strategy to determine the next move
        // Return the move as an integer
    }
}
private class UiThreadHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WORKER_MOVE:
                int move = (int) msg.obj;
                makeMove(move);
                checkForWin();
                break;
        }
    }
}

