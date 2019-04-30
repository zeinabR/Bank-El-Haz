package com.mygdx.game.Network;

// import sun.net.*;
import java.net.*;
import java.util.Scanner;
// import java.util.concurrent.TimeUnit;
// import java.awt.datatransfer.FlavorListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// import com.sun.tools.javac.util.Pair;

import java.io.*;
import java.util.Enumeration;
import java.util.InputMismatchException;
import java.util.Random;



public class MainFlow {


    public NetworkManager init() {

        //initialize Network Manager
        NetworkManager net_mang=new NetworkManager();
        net_mang.startNotifier();



        //Get user game state (host or player)
        System.out.println("Game Starting :)");
        Scanner scan = new Scanner(System.in);


        while(true)
        {
            System.out.println("Enter 'm' if you want to create a nem game room! or 'r' to connect to an existing one:");
            char s = scan.next().charAt(0);
            if(s=='m'|| s=='M'){ //If room master, listen for other players
                net_mang.isRoomMaster();
                break;
            }
            else if(s=='r' || s=='R'){ //if not room master, connect to existing room
                net_mang.isPlayer();
                if(net_mang.wrong_info){
                    System.out.println("Room probably doesn't exist!");
                }
                else if(net_mang.master_dis){
                    System.out.println("OOPS! It seems like master disconnectd before starting the game! start over");
                } else {
                    break;
                }
            }
            else{ //wrong input dude!
                System.out.println("please enter a valid input , 'm' or 'r': ");
            }
        }

        System.out.println("lets play!");

        return net_mang;
//        int curr_turn=0;
//
//        while(true){
//            if(net_mang.my_turn==curr_turn){
//
//
//                System.out.println("hi there, enter something! it is meeee " + curr_turn);
//                char s = scan.next().charAt(0);
//                if(s=='q') {
//                    break;
//                }
//
//                for(int i=0; i<net_mang.num_connected.get()+1; i++){
//                    if(i!=curr_turn){
//                        net_mang.send_Object((Object)"Game steps",net_mang.players_ips[i],net_mang.players_ports[i]);
//
//                    }
//
//                }
//
//            }
//            else {
//                if( net_mang.players_connected[curr_turn].get()){
//                    if(!net_mang.my_Socket.isClosed()){
//                        try{
//                            net_mang.my_Socket.close();
//                            net_mang.my_Socket = new ServerSocket(net_mang.my_port);
//                        }catch(IOException e){
//                            System.out.println(e.getMessage());
//                            System.out.println("ioexcept here");
//                        }
//                    }
//
//                    System.out.println("Not my turn, is actually the turn of player "+ (int)curr_turn);
//                    Object rcv = net_mang.rcv_Object(10*1000);
//                    System.out.println((String)rcv);
//
//                }
//                else{
//                    // skip turn, here could release their assets if not released
//                }
//
//            }
//            curr_turn++;
//            curr_turn=curr_turn%(net_mang.num_connected.get()+1);
//        }
    }
}


