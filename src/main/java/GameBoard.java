package main.java;

import java.util.Random;

// this class play the role of the game master in addition to the banker role.
public class GameBoard {
    public static void main(String[] args) {
        GameBoard gameBoard = new GameBoard();
        initBlocks();
        initCards();
        gameBoard.initPlayers();

        while (true)
        {
            for (int i=0; i<NUM_PLAYERS ; i++){
                disc = throwDisc();
                players[i].position = (players[i].position+disc) %NUM_BLOCKS;
                BoardBlock currentBlock = blocks[players[i].position];
                currentBlock.execute(players[i].id);//till now just print some words


            }
        }
    }

    private static final int NUM_BLOCKS = 7;//34
    private static final int NUM_CARDS = 10;
    private static final int NUM_PLAYERS = 1;

    private static BoardBlock [] blocks;
    private static Card[] hazakCards;
    private static Card[] mahkmacards;
    private static Player[] players;
    private static int disc;

    private static int throwDisc(){
        Random rand = new Random();
        int n = rand.nextInt(6);//[0 - 5]
        return n+1;//to be in the range [1 - 6]
    }

    private void  initPlayers(){
        players = new Player[NUM_PLAYERS];
        players[0] = new Player(1);
    }

    private static void initCards(){
        hazakCards = new Card[NUM_CARDS];
        mahkmacards = new Card[NUM_CARDS];

        hazakCards[0] = new Card(0,"انت كثير الهزار, ادفع 100 جنيه لما سببنه بهزارك");
        hazakCards[1] = new Card(0,"انت وطني مخلص, ربحت جائزة كيف تخدم أمتك, خذ 50 جنيه");
        hazakCards[2] = new Card(0,"حظك من السماء, رزقت بطفل جميا خذ 50 جنيه من كل لاعب بدل هدية");
        hazakCards[3] = new Card(0,"حماتك تحبك, ارسلت لك 100 جنيه حوالةز اذهب علي البحرين لتقبضها");
        hazakCards[4] = new Card(0,"عندك 4 اطفال, ادفع 25 جنيه عن كل طفل مصاريف مدرسة");
        hazakCards[5] = new Card(0,"حصل صدام بسيارتك المؤمنة, تدفع لك شركة التأمين 200 جنيه تصليحها, و ارجع 5 خانات للخلف");

        mahkmacards[0] = new Card(1, "هارب من العسكرية, اذهب للسجن حالا");
        mahkmacards[1] = new Card(1, "ربحت قضيتك ضد زملائك, خذ 25 جنيه من كل لاعب");
        mahkmacards[2] = new Card(1, "خسرت قضيتك, ادفع 15 جنيه اتعاب محاماة");

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

    }
}