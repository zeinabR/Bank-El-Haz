package main.java;

public class City extends BoardBlock {
    int category; //for coloring and a game feature

    //for checking
    boolean sold;
    boolean garage;
    boolean rest;
    boolean market;
    int ownerID;

    //for the owner
    int buyCost;
    int garageCost;
    int restCost;
    int marketCost;

    // for the passengers
    int rent;
    int garageRent;
    int restRent;
    int marketRent;

    City(String name, int buyCost, int garageCost, int restCost, int marketCost, int rent, int garageRent, int restRent, int marketRent, int category) {
        type = 0;
        this.name = name;
        this.buyCost = buyCost;
        this.garageCost = garageCost;
        this.restCost = restCost;
        this.marketCost = marketCost;
        this.rent = rent;
        this.garageRent = garageRent;
        this.restRent = restRent;
        this.marketRent = marketRent;
        this.category = category;
    }

    protected void printCityInfo() {
        System.out.print("City Name: " + name + "\n");
        System.out.print("يدفع المار لصاحب هذا البلد ايجار مرور فقط " + rent + " جنيه. \n");
        System.out.print("الجراج -- " + garageRent + " جنيه. \n");
        System.out.print("الاستراحة -- " + restRent + " جنيه. \n");
        System.out.print("السوق -- " + marketRent + " جنيه. \n");
        System.out.print("====================== \n");
        System.out.print("السعر -- " + buyCost + " جنيه. \n");
        System.out.print("الجراج -- " + garageCost + " جنيه. \n");
        System.out.print("الاستراحة -- " + restCost + " جنيه. \n");
        System.out.print("السوق -- " + marketCost + " جنيه. \n");
    }
}