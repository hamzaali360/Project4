package edu.uic.cs478.spring23.project4;

public class PieceData {
    private int x;
    private int y;
    private int playerID;

    public PieceData(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.playerID = id;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY(){
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public void setID(int id) {
        playerID = id;
    }
    public int getID() {
        return playerID;
    }

}

