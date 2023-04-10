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
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static ImageView[][] boardImage;
    private static Thread player1;
    private static Thread player2;
    private static Button startBTN;
    // The UI thread handler created
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Board.MOVE_MADE:
                    updateGame((PieceData) msg.obj, msg.arg1, msg.arg2);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set the threads to null
        player1 = null;
        player2 = null;
        // initialize the UI of imageview
        boardImage = new ImageView[3][3];
        boardImage[0][0] = (ImageView)findViewById(R.id.slot1);
        boardImage[0][1] = (ImageView)findViewById(R.id.slot2);
        boardImage[0][2] = (ImageView)findViewById(R.id.slot3);
        boardImage[1][0] = (ImageView)findViewById(R.id.slot4);
        boardImage[1][1] = (ImageView)findViewById(R.id.slot5);
        boardImage[1][2] = (ImageView)findViewById(R.id.slot6);
        boardImage[2][0] = (ImageView)findViewById(R.id.slot7);
        boardImage[2][1] = (ImageView)findViewById(R.id.slot8);
        boardImage[2][2] = (ImageView)findViewById(R.id.slot9);

        // initalize the new game button
        startBTN = (Button)findViewById(R.id.new_game_button);
        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // when new game is pressed again, the existing thread is deleted
                if (player1 != null) {
                    endThreads();
                }
                // creating a new board to start the game from fresh
                new Board(mHandler);
                // creating the thread of the players
                player1 = new Thread(new PlayerOne());
                player2 = new Thread(new PlayerTwo());
                // clear the UI to blank spots
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        boardImage[i][j].setImageResource(R.drawable.empty_slot);
                    }
                }
                // reset the handler initialized to 0 until the new thread are finished
                Board.handlerInit = 0;
                // start the player threads
                player1.start();
                player2.start();
                Toast.makeText(MainActivity.this, "New Game Start", Toast.LENGTH_SHORT).show();
                // decide which player start first
                int startingPlayer = new Random().nextInt(2);
                // until the looper are finished initializing
                while (Board.handlerInit < 2) ;
                if (startingPlayer == 0) {
                    PlayerOne.playerOne.sendMessage(PlayerOne.playerOne.obtainMessage(Board.MOVE_MADE));
                } else {
                    PlayerTwo.playerTwo.sendMessage(PlayerTwo.playerTwo.obtainMessage(Board.MOVE_MADE));
                }
            }

        });

    }

    private void updateGame(PieceData newPiece, int oldX, int oldY) {
        // check if is existing piece
        if (oldX != -1) {
            boardImage[oldX][oldY].setImageResource(R.drawable.empty_slot);
        }
        // get the new coordinates
        int newX = newPiece.getX();
        int newY = newPiece.getY();

        // placing the new piece to the different spot
        if (newPiece.getID() == Board.RED) {
            boardImage[newX][newY].setImageResource((R.drawable.red_piece));
        } else {
            boardImage[newX][newY].setImageResource((R.drawable.blue_piece));
        }
        // get the id to any possible winner
        int id = checkForWinner();
        // there's a winner so end the threads and declare the winner
        if (id != -1) {
            endThreads();
            if (id == Board.BLUE) {
                Toast.makeText(this, "Blue Won this Match!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Red Won this Match!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        // no winner so continue sending message to player one or two
        if (newPiece.getID() == Board.RED) {
            PlayerTwo.playerTwo.sendMessage(PlayerTwo.playerTwo.obtainMessage(Board.MOVE_MADE, oldX, oldY, newPiece));
        } else {
            PlayerOne.playerOne.sendMessage(PlayerOne.playerOne.obtainMessage(Board.MOVE_MADE, oldX, oldY, newPiece));
        }
    }

    private int checkForWinner() {
        // get the current board
        int[][] boardState = Board.getBoard();
        // checking if the row or column have a three matching color and give the winner to their id
        for (int i = 0; i < 3; i++) {
            int idCol = boardState[i][0];
            int idRow = boardState[0][i];
            int horizontal = 0;
            int vertical = 0;
            for (int j = 0; j < 3; j++) {
                if (boardState[i][j] == idCol) {
                    horizontal++;
                }
                if (boardState[j][i] == idRow) {
                    vertical++;
                }
            }
            // find winner from horizontal
            if (idCol != Board.EMPTY && horizontal == 3) {
                return idCol;
            }
            // find winner from vertical
            if (idRow != Board.EMPTY && vertical == 3) {
                return idRow;
            }

        }
        return -1;
    }

    private void endThreads() {
        // end the player1 and player2 threads
        PlayerOne.playerOne.post(new Runnable() {
            @Override
            public void run() {
                PlayerOne.endLooper();
            }
        });
        while (player1.isAlive());
        PlayerTwo.playerTwo.post(new Runnable() {
            @Override
            public void run() {
                PlayerTwo.endLooper();
            }
        });
        while (player2.isAlive());
        // set thread to null
        player1 = null;
        player2 = null;
        // clear the handler queue
        mHandler.removeCallbacksAndMessages(null);
    }
}

