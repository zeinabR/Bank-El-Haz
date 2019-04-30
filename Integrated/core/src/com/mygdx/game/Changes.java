package com.mygdx.game;

import java.io.Serializable;

public class Changes implements Serializable {
    public int id;
    public int newPos;

    Changes(int id, int newPos) {
        this.id = id;
        this.newPos = newPos;
    }
}
