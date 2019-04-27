package main.java;

public class City implements boardBlock {
    String cityName;

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

    City(String name,int buyCost,int garageCost, int restCost, int marketCost, int rent, int garageRent, int restRent, int marketRent ){
        cityName = name;
        this.buyCost = buyCost;
        this.garageCost = garageCost;
        this.restCost = restCost;
        this.marketCost = marketCost;
        this.rent = rent;
        this.garageRent = garageRent;
        this.restRent = restRent;
        this.marketRent = marketRent;
    }
    private void printCityInfo(){
        System.out.print("City Name: " + cityName + "\n");
        System.out.print("يدفع المار لصاحب هذا البلد ايجار مرور فقط " + rent + " جنيه. \n");
    }
    public void execute(){
        printCityInfo();
    }
}
