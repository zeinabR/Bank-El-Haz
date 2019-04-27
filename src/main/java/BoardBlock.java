package main.java;

public class BoardBlock {
    String name;
    int type;   //[0:city,club&gas_station] [1:Hazak] [2:Mahkma] [3:Start] [4:Bus] [5:Prison]

    BoardBlock(){}
    BoardBlock(int type, String name){
        this.name = name;
        this.type = type;
    }

    public void execute(int playerID) {
        System.out.print(name + "\n");
    }
}
