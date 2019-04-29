package com.mygdx.game.GameLogic;

public class Card {

    public int type; //[0:Luck] [1:Trial]
    public String msg;
    public int playerGain;
    public int playerPos;
    public int others;

    Card(int type, String msg, int playerGain, int playerPos, int others) {
        this.type = type;
        this.msg = msg;
        this.playerGain = playerGain;
        this.playerPos = playerPos;
        this.others = others;
    }
}