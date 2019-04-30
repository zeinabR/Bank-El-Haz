package main.java;

import java.util.LinkedList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// this class play the role of the game master in addition to the banker role.
public class GameBoard {
    public static void main(String[] args)throws IOException {
        initBlocks();
        initCards();
        initPlayers();
        executeGame();
    }

    private static final int NUM_BLOCKS = 34;
//    private static final int NUM_CITIES = 5;
    private static final int NUM_CARDS = 6;
    private static  final int MAX_NUM_PLAYERS = 4;
    private static  final int prisonPosition = 24;
    private static  final int BahranPosition = 11;
    private static final int NUM_CATEGORIES = 10;

    private static BoardBlock [] blocks;
    private static Card[] luckCards; 
    private static Card[] trialCards;
    private static LinkedList<Player> players;
    private static int disc;

    private static int []citiesPerCategory;
    static int luckIndex = 0;
    static int trialIndex = 0;

    private static void executeGame() throws IOException {
        while (true) {
            for (int i=0; i<players.size() ; i++){
                System.out.println("player id#" + i);
                Player currentPlayer = players.get(i);
                disc = throwDisc(); 
                if(currentPlayer.Prison){
                    currentPlayer.Prison = false;
                    continue;
                }
                if(currentPlayer.fastBus){
                    currentPlayer.position = (currentPlayer.position+disc*2) %NUM_BLOCKS;
                    currentPlayer.fastBus = true;
                }
                else
                    currentPlayer.position = (currentPlayer.position+disc) %NUM_BLOCKS;
                BoardBlock currentBlock = blocks[currentPlayer.position];
                System.out.print("Your now at " + currentBlock.name + "\n");

                switch (currentBlock.type){
                    case 0://City
                        City city =((City)currentBlock);
                        if (!city.sold)
                            freeCity(currentPlayer,city);
                        else if(city.ownerID!= currentPlayer.id) //A passenger
                            passenger(currentPlayer,city);
                        else //Owner
                            owner(currentPlayer,city);
                        break;
                    case 1://Luck Block
                    case 2://Trial Block
                        TrialHazak(currentPlayer, currentBlock.type);
                        break;
                    case 3://Start
                        System.out.println("At The Start again!");
                        currentPlayer.account += 200;
                        break;
                    case 4://Bus
                        System.out.println("Fast Bus, Yaaa!");
                        currentPlayer.fastBus = true;
                        break;
                    case 5://prison
                        System.out.println("NooOooOoo! Plz get me out");
                        currentPlayer.Prison = true;
                        break;
                    default:
                        System.out.print("I don't know where i'm now!");
                }
                System.out.println("player # " + i + ": current account = " + currentPlayer.account +"\n");
            }
        }
    }

    private  static  void freeCity(Player player, City city) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        city.printCityInfo();//print the whole city card
        System.out.print("Do you want to buy this city?? No, Yes");
        String buy = br.readLine();
        if (buy.equals("Yes")||buy.equals("yes")) {
            if (player.account > city.buyCost) {
                city.sold = true;
                city.ownerID = player.id;
                player.account -= city.buyCost; //minus price from player
                player.myCities.add(city); // add the city to the player's collection
                player.citiesPerCategory[city.category]++;
                if(player.citiesPerCategory[city.category] == citiesPerCategory[city.category])
                    System.out.println("Congrats! You just complete this group");
                else
                    System.out.println("Congrats! now it's yours.");
            } else
                System.out.println("Sorry, You don't have enough money ):");
        }
    }
    private static void passenger(Player player, City city){
            int totalRent = city.rent;
            if(city.garage)
                totalRent += city.garageRent;
            if(city.rest)
                totalRent += city.restRent;
            if(city.market)
                totalRent += city.marketRent;
            System.out.print("You Should pay " + totalRent + " to player with id " + city.ownerID + "\n");

            player.account -= totalRent;
            if(player.account <= 0)
                freePlayer(player);
    }

    private  static  void owner(Player player, City city)throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if(player.citiesPerCategory[city.category] == citiesPerCategory[city.category]){
            city.printCityInfo();
            System.out.println("Do you want to build something more here?");
            System.out.println("0 -> Maybe later.   1->Garage.  2->Rest.    3->Market");
            int build = Integer.parseInt(br.readLine());
            switch (build){
                case 1:
                    if(city.garage){
                        System.out.println("You have already built it");
                        break;
                    }
                    if (player.account > city.garageCost) {
                        player.account -= city.garageCost;
                        city.garage= true;
                    }
                    else
                        System.out.println("Sorry, You don't have enough money ):");
                    break;
                case 2:
                    if(city.rest){
                        System.out.println("You have already built it");
                        break;
                    }
                    if (player.account > city.restCost) {
                        player.account -= city.restCost;
                        city.rest= true;
                    }
                    else
                        System.out.println("Sorry, You don't have enough money ):");
                    break;
                case 3:
                    if(city.market){
                        System.out.println("You have already built it");
                        break;
                    }
                    if (player.account > city.marketCost) {
                        player.account -= city.marketCost;
                        city.market= true;
                    }
                    else
                        System.out.println("Sorry, You don't have enough money ):");
                    break;
            }
        }
        else
            System.out.print("City Name: " + city.name + "is yours now\n");

    }
    private static void TrialHazak(Player player, int blockType){
        Card card;
        if(blockType == 1){
            card = luckCards[luckIndex];
            luckIndex = (luckIndex+1) % NUM_CARDS;
            System.out.print("Lets see your luck XD \n");
        }
        else{ //(currentBlock.type == 2)
            card = trialCards[trialIndex];
            trialIndex = (trialIndex+1) % NUM_CARDS;
            System.out.print("Trial Time! \n");
        }
        System.out.print(card.msg + "\n");

        if(card.others > 0){//other players should pay to him
            for(int j=0; j<players.size(); j++)
                if(players.get(j) != player)
                    if(players.get(j).account < card.others)//player account is less than what he should pay,then he lose
                        freePlayer(player);
                    else {
                        players.get(j).account -= card.others;
                        player.account += card.playerGain;
                    }
        }
        else if (card.playerGain != 0){//if +ive, take money from bank not players OR if -ive, pay money
            player.account += card.playerGain;
        }
        if (card.playerPos != 0){
            if(card.playerPos < 0)
                player.position += card.playerPos;
            else
            if (card.playerPos == prisonPosition)
                player.Prison = true;
            player.position = card.playerPos;
        }
    }

    private  static  void freePlayer(Player player){
        System.out.print("Sorry you are out. Good Luck Next Time." +
                "! \n");
        for(int i=0; i< player.myCities.size(); i++){
            player.myCities.get(i).sold = false;
            player.myCities.get(i).garage = false;
            player.myCities.get(i).rest = false;
            player.myCities.get(i).market = false;
        }
    }

    private static int throwDisc(){
        Random rand = new Random();
        int n = rand.nextInt(6);//[0 - 5]
        return n+1;//to be in the range [1 - 6]
    }

    private static void  initPlayers(){
        players = new LinkedList<Player>();
        Player p1 = new Player(0);
        Player p2 = new Player(1);
        players.add(p1);
        players.add(p2);
    }

    private static void initCards(){
        luckCards = new Card[NUM_CARDS];
        trialCards = new Card[NUM_CARDS];

        luckCards[0] = new Card(0,"You're too funny, pay $100!", -100, 0, 0);
        luckCards[1] = new Card(0,"You won $50 for being a true patriot!", 50, 0, 0);
        luckCards[2] = new Card(0,"You've got a baby! Take $50 from every player", 50, 0, 50);
        luckCards[3] = new Card(0, "Your stepmom gave you $100. Go to Bahrain to take them", 100, BahranPosition, 0);
        luckCards[4] = new Card(0,"You have four kids. Pay $25 for school supplies for each.",-25, 0, 0);
        luckCards[5] = new Card(0,"You've been in an accident in an insured car. Take $200 from insurance.", 200, 0, 0);

        trialCards[0] = new Card(1, "You dodged the draft. Go to prison right away!", 0, prisonPosition, 0);
        trialCards[1] = new Card(1, "You won your case against the other players. Take $25 from each!", 25, 0, 25);
        trialCards[2] = new Card(1, "You lost your case. Pay $15 for the lawyers.", 15,0,0);
        trialCards[3] = new Card(1, "You won the lottery. Take $100!", 100,0,0);
        trialCards[4] = new Card(1, "You broke the traffic lights. Pay an $40 fine!", -40,0,0);
        trialCards[5] = new Card(1, "You were falsely accused and have been proven innocent. Take $200!", 200,0,0);
    }

    private static void initBlocks(){
        blocks = new BoardBlock[NUM_BLOCKS];

        blocks[0] = new BoardBlock(3, "Start") ;
        blocks[1] = new City("Jerusalem",300,250,750,1000,35, 190,900,1550,1);
        blocks[2] = new City("Gaza",250,200,600,800,30, 130,650,910,1);
        blocks[3] = new BoardBlock(1, "Your Luck");
        blocks[4] = new City("Beirut",300,200,750, 1000,32,180,850,1500,2);
        blocks[5] = new City("Riyadh",250,200,600, 800,29,130,650,750,2);
        blocks[6] = new City("Baghdad",250,200,600, 800,28,120,600,850,2);
        blocks[7] = new City("The Club",150,0,0, 0,30,0,0,0,0);
        blocks[8] = new City("Benghazi",150,130,440, 550,22,75,275,650,3);
        blocks[9] = new City("Aden",100,100,310, 450,17,72,380,600,3);
        blocks[10] = new BoardBlock(2, "The Court");
        blocks[11] = new City("Bahain",90,80,250, 350,15,60,300,480,3);
        blocks[12] = new BoardBlock(1, "Your Luck");
        blocks[13] = new City("Casablanca",250,200,550, 750,30,130,600,850,4);
        blocks[14] = new City("Gas Station",300,0,0, 0,30,0,0,0,0);
        blocks[15] = new City("Tunis",200,100,310, 450,17,72,380,600,4);
        blocks[16] = new City("Algeria",200,100,310, 450,17,72,380,600,4);
        blocks[17] = new BoardBlock(4, "The Autobus") ;
        blocks[18] = new City("Alexandria",225,300,850, 1200,45,220,1000,1700,5);
        blocks[19] = new City("Aleppo",200,250,750, 1000,35,185,850,1500,5);
        blocks[20] = new BoardBlock(2, "The Court");
        blocks[21] = new City("Aswan",200,165,360, 490,27,130,700,900,6);
        blocks[22] = new City("Damascus",250,320,900, 1250,50,250,1200,1850,6);
        blocks[23] = new City("Cairo",450,400,1200, 1600,55,320,1500,2400,6);
        blocks[24] = new BoardBlock(5, "Prison");
        blocks[25] = new City("Khartoum",200,170,370, 500,27,130,630,1850,7);
        blocks[26] = new City("Luxor",200,210,550, 800,25,140,700,900,9);
        blocks[27] = new City("Oman",250,200,550, 750,30,130,600,850,7);
        blocks[28] = new City("Port Said",250,210,550, 800,30,140,700,900,7);
        blocks[29] = new BoardBlock(1, "Your Luck");
        blocks[30] = new City("Sana'a",250,170,370, 500,25,130,630,1850,8);
        blocks[31] = new BoardBlock(2, "The Court");
        blocks[32] = new City("Kuwait",250,200,550, 750,25,130,600,850,8);
        blocks[33] = new City("Qatar",150,210,550, 800,20,140,700,900,8);


        citiesPerCategory = new int[NUM_CATEGORIES];
        citiesPerCategory[0] = 0; //for club and gas station
        citiesPerCategory[1] = 2;
        citiesPerCategory[2] = 3;
        citiesPerCategory[3] = 3;
        citiesPerCategory[4] = 3;
        citiesPerCategory[5] = 2;
        citiesPerCategory[6] = 3;
        citiesPerCategory[7] = 3;
        citiesPerCategory[8] = 3;
        citiesPerCategory[9] = 1; //Luxor

    }
}