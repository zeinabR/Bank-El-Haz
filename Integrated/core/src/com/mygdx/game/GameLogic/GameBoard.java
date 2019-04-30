package com.mygdx.game.GameLogic;

import com.mygdx.game.GraphicsPlayer;
import com.mygdx.game.Notification;

import java.util.LinkedList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// this class play the role of the game master in addition to the banker role.
public class GameBoard {
    public GameBoard(int n_players) {
        initBlocks();
        initCards();
        initPlayers(n_players);
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
    public LinkedList<GamePlayer> players;
    private static int disc;

    private static int []citiesPerCategory;
    static int luckIndex = 0;
    static int trialIndex = 0;

    GamePlayer playerDeciding;
    GraphicsPlayer playerDecidingGraphicsPlayer;

    // returns true if an animation should be played (i.e. the user is moving). False otherwise.
    public int updateUserPosition(int playerID, GraphicsPlayer graphicsPlayer) {

        System.out.println("Updating position of Player " + playerID);

        GamePlayer currentPlayer = players.get(playerID);
        playerDeciding = currentPlayer;
        playerDecidingGraphicsPlayer = graphicsPlayer;
        System.out.println("Initial pos = " + currentPlayer.position + "!");
        disc = throwDisc();

        if (currentPlayer.Prison) {
            currentPlayer.Prison = false;
        }
        if (currentPlayer.fastBus){
            currentPlayer.position = (currentPlayer.position+disc*2) %NUM_BLOCKS;
            graphicsPlayer.startAnimatedMotion(currentPlayer.position, (currentPlayer.position - disc * 2 + 1) % NUM_BLOCKS);
            currentPlayer.fastBus = false;
        }
        else {
            currentPlayer.position = (currentPlayer.position + disc) %NUM_BLOCKS;
//            System.out.print("MOVE " + disc + " TILES ONLY!");
            graphicsPlayer.startAnimatedMotion(currentPlayer.position, (currentPlayer.position - disc + 1) % NUM_BLOCKS);
        }

        BoardBlock currentBlock = blocks[currentPlayer.position];
        switch (currentBlock.type) {
            case 0:
                break;
            case 1:
            case 2:
                TrialHazak(currentPlayer, graphicsPlayer, currentBlock.type);
                break;
            default:
                break;
        }

        return disc;
    }

    public void processCurrentBlock(int playerID, GraphicsPlayer graphicsPlayer) {
        GamePlayer currentPlayer = players.get(playerID);

        BoardBlock currentBlock = blocks[currentPlayer.position];
        System.out.print("Player " + playerID + " You're now at " + currentBlock.name + " at pos " + currentPlayer.position + "\n");

        switch (currentBlock.type){
            case 0: //City
                City city =((City)currentBlock);
                if (!city.sold)
                    freeCity(currentPlayer, graphicsPlayer, city);
                else if(city.ownerID!= currentPlayer.id) // A passenger
                    passenger(currentPlayer, graphicsPlayer, city);
                else //Owner
                    owner(currentPlayer, graphicsPlayer, city);
                break;
            case 1: // Luck Block
            case 2: // Trial Block
//                TrialHazak(currentPlayer, graphicsPlayer, currentBlock.type);
                break;
            case 3: // Start
                System.out.println("At The Start again!");
                currentPlayer.account += 200;
                break;
            case 4: // Bus
                System.out.println("Fast Bus, Yaaa!");
                currentPlayer.fastBus = true;
                break;
            case 5: // Prison
                System.out.println("NooOooOoo! Plz get me out");
                currentPlayer.Prison = true;
                break;
            default:
                System.out.print("I don't know where i'm now!");
        }
        System.out.println("player # " + playerID + ": current account = " + currentPlayer.account +"\n");
    }

    public void notifyBuyDecision(boolean result) {
        BoardBlock currentBlock = blocks[playerDeciding.position];
        GamePlayer player = playerDeciding;

        if (!result) return;

        if (currentBlock.type == 0) {
            City city = (City)currentBlock;
            if (!city.sold) {
                city.sold = true;
                city.ownerID = player.id;
                player.account -= city.buyCost; //minus price from player
                player.myCities.add(city); // add the city to the player's collection
                player.citiesPerCategory[city.category]++;
                String message;
                if(player.citiesPerCategory[city.category] == citiesPerCategory[city.category])
                    message = "Congrats! You just completed the city's group!";
                else
                    message = String.format("Congrats! %s is now yours!", city.name);
                playerDecidingGraphicsPlayer.notifications.add(new Notification(message, -1));
            } else if (city.ownerID == playerDeciding.id) {
                if (!city.garage) {
                    player.account -= city.garageCost;
                    city.garage= true;
                    playerDecidingGraphicsPlayer.notifications.add(new Notification(
                            String.format("%s's garage is now yours!", city.name), -1));
                } else if (!city.rest) {
                    player.account -= city.restCost;
                    city.rest = true;
                    playerDecidingGraphicsPlayer.notifications.add(new Notification(
                            String.format("%s's rest is now yours!", city.name), -1));
                } else if (!city.market) {
                    player.account -= city.marketCost;
                    city.market = true;
                    playerDecidingGraphicsPlayer.notifications.add(new Notification(
                            String.format("%s's market is now yours!", city.name), -1));
                }
            }
        }
    }


    private static void freeCity(GamePlayer player, GraphicsPlayer gp, City city) {
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // TODO: PRINT THIS IN THE GUI.
        city.printCityInfo();
        if (player.account > city.buyCost) {
            gp.notifications.add(new Notification(city.buyCost, city.name, -1));
        }
    }
    private static void passenger(GamePlayer player, GraphicsPlayer gp, City city){
            int totalRent = city.rent;
            if(city.garage)
                totalRent += city.garageRent;
            if(city.rest)
                totalRent += city.restRent;
            if(city.market)
                totalRent += city.marketRent;

            gp.notifications.add(new Notification(String.format("You should pay $%5d to Player %1d", totalRent, city.ownerID), -1));

            player.account -= totalRent;
            if(player.account <= 0)
                freePlayer(player, gp);
    }

    private  static  void owner(GamePlayer player, GraphicsPlayer graphicsPlayer, City city) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if(player.citiesPerCategory[city.category] == citiesPerCategory[city.category]) {
            // TODO: PRINT THIS INFO IN THE GUI.
            city.printCityInfo();
            // Notify the player of his or her options.
            if (!city.garage) {
                if (player.account > city.garageCost) {
                    graphicsPlayer.notifications.add(new Notification(city.garageCost,
                            String.format("a Garage for %s", city.name), -1));
                }
            } else if (!city.rest) {
                if (player.account > city.restCost) {
                    graphicsPlayer.notifications.add(new Notification(city.restCost,
                            String.format("a Rest coast for %s", city.name), -1));

                }
            } else if (!city.market) {
                if (player.account > city.marketCost) {
                    graphicsPlayer.notifications.add(new Notification(city.marketCost,
                            String.format("a Market cost for %s", city.name), -1));
                }
            }
        }
    }

    private void TrialHazak(GamePlayer player, GraphicsPlayer graphicsPlayer, int blockType){
        Card card;
        if(blockType == 1) {
            card = luckCards[luckIndex];
            luckIndex = (luckIndex + 1) % NUM_CARDS;
            System.out.print("Lets see your luck XD \n");
        }
        else{ //(currentBlock.type == 2)
            card = trialCards[trialIndex];
            trialIndex = (trialIndex+1) % NUM_CARDS;
            System.out.print("Trial Time! \n");
        }
        graphicsPlayer.notifications.add(new Notification(card.msg, -1));
//        System.out.print(card.msg + "\n");

        if(card.others > 0){//other players should pay to him
            for(int j=0; j<players.size(); j++)
                if(players.get(j) != player)
                    if(players.get(j).account < card.others)//player account is less than what he should pay,then he lose
                        freePlayer(player, graphicsPlayer);
                    else {
                        players.get(j).account -= card.others;
                        player.account += card.playerGain;
                    }
        }
        else if (card.playerGain != 0){//if +ive, take money from bank not players OR if -ive, pay money
            player.account += card.playerGain;
        }
        if (card.playerPos != 0) {
            if (card.playerPos < 0) {
//                player.position += card.playerPos;
                return;
            }
            else if (card.playerPos == prisonPosition)
                player.Prison = true;
            int nextTile = (player.position + 1) % NUM_BLOCKS;
            player.position = card.playerPos;
            graphicsPlayer.startAnimatedMotion(player.position, nextTile);
        }
    }

    private  static  void freePlayer(GamePlayer player, GraphicsPlayer graphicsPlayer){
        graphicsPlayer.notifications.clear();
        graphicsPlayer.notifications.add(
                new Notification("Sorry, you are out! Best of luck next time!", -1));
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

    private void  initPlayers(int num_players){
        players = new LinkedList<GamePlayer>();
        for (int i = 0; i < num_players; i++) {
            players.add(new GamePlayer(i));
        }
    }

    private static void initCards(){
        luckCards = new Card[NUM_CARDS];
        trialCards = new Card[NUM_CARDS];

        luckCards[0] = new Card(0,"You're too funny, pay $100!", -100, 0, 0);
        luckCards[1] = new Card(0,"You won $50 for being a true patriot!", 50, 0, 0);
        luckCards[2] = new Card(0,"You've got a baby! Take $50 from every player", 50, 0, 50);
        luckCards[3] = new Card(0, "Your stepmom gave you $100. Go to Bahrain to take them", 100, BahranPosition, 0);
        luckCards[4] = new Card(0,"You have four kids. Pay $25 for school supplies for each.",-25, 0, 0);
        luckCards[5] = new Card(0,"Your insured car broke down. Take $200 from insurance.", 200, 0, 0);

        trialCards[0] = new Card(1, "You dodged the draft. Go to prison right away!", 0, prisonPosition, 0);
        trialCards[1] = new Card(1, "Take $25 from each player!", 25, 0, 25);
        trialCards[2] = new Card(1, "You lost your case. Pay $15 for the lawyers.", 15,0,0);
        trialCards[3] = new Card(1, "You won the lottery. Take $100!", 100,0,0);
        trialCards[4] = new Card(1, "You broke the traffic lights. Pay an $40 fine!", -40,0,0);
        trialCards[5] = new Card(1, "You are innocent! Take $200!", 200,0,0);
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
        blocks[26] = new City("Oman",250,210,550, 800,25,140,700,900,9);
        blocks[27] = new City("Luxor",200,200,550, 750,30,130,600,850,7);
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