package main.java;

import java.util.LinkedList;

public class Player {
    public int id;
    public int position;
    public int account;
    LinkedList<City> myCities;

    Player(int id){
        this.id = id;
        position = 0;
        account = 1200;

        myCities = new LinkedList<City>(); //25 max number of cities
    }
}
