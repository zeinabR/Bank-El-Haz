package main.java;
import java.util.Random;

// this class play the role of the game master in addition to the banker role.
public class GameBoard {
    public static void main(String[] args) {
        GameBoard gameBoard = new GameBoard();
        gameBoard.initBlocks();

        int disc =  gameBoard.throwDisc();
    }

    private static final int NUM_BLOCKS = 3;
    boardBlock [] blocks;

    public void initBlocks(){
        blocks = new boardBlock[NUM_BLOCKS];

        blocks[0] = new TheStart();
        blocks[1] = new City("بغداد",250,200,600, 800,28,120,600,850);
        blocks[2] = new City("غزة",250,200,600,800,30, 130,650,910);

    }

    public int throwDisc(){
        Random rand = new Random();
        int n = rand.nextInt(6);//[0 - 5]
        return n+1;//to be in the range [1 - 6]
    }
}
