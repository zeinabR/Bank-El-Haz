package main.java;

public class Card{

    public int type; //[0:Hazak] [1:Mahkma]
    public String msg;

    Card(int type, String msg){
        this.type = type;
        this.msg = msg;
    }

    public void execute() {
        System.out.print(msg + "\n");
    }
}
