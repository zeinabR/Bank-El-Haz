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

    private static final int NUM_BLOCKS = 14;//34
    private static final int NUM_CARDS = 10;
    private static  final int MAX_NUM_PLAYERS = 6;
    private static  final int prisonPosition = 13;//arbitrary till now
    private static  final int BahranPosition = 4;
    
    private static BoardBlock [] blocks;
    private static Card[] luckCards; 
    private static Card[] trialCards;
    private static LinkedList<Player> players;
    private static int disc;

    private static void executeGame() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int luckIndex = 0;
        int trialIndex = 0;

        int loopIndex =0; //just for tracing
        while (true) {
            System.out.println("Loop Index = " + loopIndex);
            for (int i=0; i<players.size() ; i++){
                Player currentPlayer = players.get(i);
                disc = throwDisc();
                if(currentPlayer.Prison){
                    currentPlayer.Prison = false;
                    break;
                }
                if(currentPlayer.fastBus){
                    currentPlayer.position = (currentPlayer.position+disc*2) %NUM_BLOCKS;
                    currentPlayer.fastBus = true;
                }
                else
                    currentPlayer.position = (currentPlayer.position+disc) %NUM_BLOCKS;
                BoardBlock currentBlock = blocks[currentPlayer.position];
                System.out.print("Block index" + currentPlayer.position + "\n");
                Card card;

                switch (currentBlock.type){
                    case 0://City
                        City city =((City)currentBlock);
                        if (!city.sold){
                            city.printCityInfo();//print the whole city card
                            System.out.print("Do you want to buy this city?? No, Yes");
                            String buy = br.readLine();
                            if (buy.equals("Yes")||buy.equals("yes")) {
                                if (currentPlayer.account > city.buyCost) {
                                    currentPlayer.account -= city.buyCost; //minus price from player
                                    currentPlayer.myCities.add((City) currentBlock); // add the city to the player's collection
                                    city.sold = true;
                                    city.ownerID = currentPlayer.id;
                                    System.out.println("Congrats! now it's yours.");
                                } else
                                    System.out.println("Sorry, You don't have enough money ):");
                            }
                        }
                        else if(city.ownerID!= currentPlayer.id) {//A passenger
                            System.out.print("You Should pay " + city.rent + " to player with id " + currentPlayer.id + "\n");
                            currentPlayer.account -= city.rent;
                        }
                        else //Owner
                            System.out.print("City Name: " + city.name + "is yours now\n");
                        break;
                    case 1://Luck Block
                    case 2://Trial Block
                        if(currentBlock.type == 1){
                            card = luckCards[luckIndex];
                            luckIndex ++;
                            System.out.print("Lets see your luck XD \n");
                        }
                        else{ //(currentBlock.type == 2)
                            card = trialCards[trialIndex];
                            trialIndex ++;
                            System.out.print("Trial Time! \n");
                        }
                        System.out.print(card.msg + "\n");

                        if(card.others > 0){//other players should pay to him
                            for(int j=0; j<players.size(); j++)
                                if(players.get(i) != currentPlayer)
                                    if(players.get(i).account < card.others)//player account is less than what he should pay,then he lose
                                        players.get(i).out = true;//player is OUT
                                    else {
                                        players.get(i).account -= card.others;
                                        currentPlayer.account += card.playerGain;
                                    }
                        }
                        else if (card.playerGain != 0){//if +ive, take money from bank not players OR if -ive, pay money
                            currentPlayer.account += card.playerGain;
                        }
                        if (card.playerPos != 0){
                            if(card.playerPos < 0)
                                currentPlayer.position += card.playerPos;
                            else
                                if (card.playerPos == prisonPosition)
                                    currentPlayer.Prison = true;
                                currentPlayer.position = card.playerPos;
                        }
                        break;
                    case 3://Start
                        System.out.print("At The Start again!");
                        currentPlayer.account += 200;
                        break;
                    case 4://Bus
                        System.out.print("Fast Bus, Yaaa!");
                        currentPlayer.fastBus = true;
                        break;
                    case 5://prison
                        System.out.print("NooOooOoo! Plz get me out");
                        currentPlayer.Prison = true;
                        break;
                    default:
                        System.out.print("I don't know where i'm now!");
                }
                System.out.println("my current account = " + currentPlayer.account +"\n");
            }
            loopIndex ++;
        }
    }

    private static int throwDisc(){
        Random rand = new Random();
        int n = rand.nextInt(6);//[0 - 5]
        return n+1;//to be in the range [1 - 6]
    }

    private static void  initPlayers(){
        players = new LinkedList<Player>();
        Player p1 = new Player(1);
        players.add(p1);
    }

    private static void initCards(){
        luckCards = new Card[NUM_CARDS];
        trialCards = new Card[NUM_CARDS];

        luckCards[0] = new Card(0,"انت كثير الهزار, ادفع 100 جنيه لما سببنه بهزارك", -100, 0, 0);
        luckCards[1] = new Card(0,"انت وطني مخلص, ربحت جائزة كيف تخدم أمتك, خذ 50 جنيه", 50, 0, 0);
        luckCards[2] = new Card(0,"حظك من السماء, رزقت بطفل جميا خذ 50 جنيه من كل لاعب بدل هدية", 50, 0, 50);
        luckCards[3] = new Card(0, "حماتك تحبك, ارسلت لك 100 جنيه حوالةز اذهب علي البحرين لتقبضها", 100, BahranPosition, 0);
        luckCards[4] = new Card(0,"عندك 4 اطفال, ادفع 25 جنيه عن كل طفل مصاريف مدرسة",-25, 0, 0);
        luckCards[5] = new Card(0,"حصل صدام بسيارتك المؤمنة, تدفع لك شركة التأمين 200 جنيه تصليحها, و ارجع 5 خانات للخلف", 200, -5, 0);

        trialCards[0] = new Card(1, "هارب من العسكرية, اذهب للسجن حالا", 0, prisonPosition, 0);
        trialCards[1] = new Card(1, "ربحت قضيتك ضد زملائك, خذ 25 جنيه من كل لاعب", 25, 0, 25);
        trialCards[2] = new Card(1, "خسرت قضيتك, ادفع 15 جنيه اتعاب محاماة", 15,0,0);

    }

    private static void initBlocks(){
        blocks = new BoardBlock[NUM_BLOCKS];

        blocks[0] = new BoardBlock(3, "البداية") ;
        blocks[1] = new City("غزة",250,200,600,800,30, 130,650,910);
        blocks[2] = new City("بغداد",250,200,600, 800,28,120,600,850);
        blocks[3] = new BoardBlock(1, "حظك");
        blocks[4] = new City("البحرين",90,80,250, 350,15,60,300,480);
        blocks[5] = new City("عدن",100,100,310, 450,17,72,380,600);
        blocks[6] = new City("النادي",150,100,310, 450,30,72,380,600);
        blocks[7] = new City("دمشق",350,320,900, 1250,50,250,1200,1850);
        blocks[8] = new City("الخرطوم",200,170,370, 500,27,130,630,1850);
        blocks[9] = new BoardBlock(2, "محكمة");
        blocks[10] = new City("بورسعيد",250,210,550, 800,30,140,700,900);
        blocks[11] = new BoardBlock(4, "الاتوبيس السريع") ;
        blocks[12] = new City("عمان",250,200,550, 750,30,130,600,850);
        blocks[13] = new BoardBlock(5, "السجن") ;
    }
}