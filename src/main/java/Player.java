package main.java;

import java.util.LinkedList;

public class Player {
    public int id;
    public int position;
    public int account;
    public boolean out;
    public boolean fastBus;
    public boolean Prison;

    LinkedList<City> myCities;

    Player(int id){
        this.id = id;
        position = 0;
        account = 1200;
        out = false;
        fastBus = false;
        Prison= false;
        myCities = new LinkedList<City>(); //25 max number of cities
    }
}
