package com.mygdx.game.GameLogic;

import java.io.Serializable;
import java.util.LinkedList;

public class GamePlayer implements Serializable {
    private static final int NUM_CATEGORIES = 10;

    public int id;
    public int position;
    public int account;
//    boolean out;
    public boolean fastBus;
    public boolean Prison;

    LinkedList<City> myCities;
    int []citiesPerCategory;
    boolean [] Category;

    GamePlayer(int id){
        this.id = id;
        position = 0;
        account = 1200;
//        out = false;
        fastBus = false;
        Prison= false;
        myCities = new LinkedList<City>(); //25 max number of cities
        citiesPerCategory = new int[NUM_CATEGORIES];
        Category = new boolean[NUM_CATEGORIES];
    }
}
