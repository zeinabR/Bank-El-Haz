package com.mygdx.game.GameLogic;

import java.io.Serializable;

public class BoardBlock implements Serializable {
    String name;
    int type;   //[0:city,club&gas_station] [1:Luck] [2:Trial] [3:Start] [4:Bus] [5:Prison]

    BoardBlock(){}
    BoardBlock(int type, String name){
        this.name = name;
        this.type = type;
    }
}
