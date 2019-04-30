package com.mygdx.game.Network;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkManager {
    String multicast_address="230.0.0.0"; //Address for multicasting
    InetAddress my_IP;
    public ServerSocket my_Socket;
    ServerSocket[] listeners={null,null,null,null};
    public int my_port;
    int room_num;
    MulticastSocket mul_socket ;
    public AtomicBoolean[] players_connected={new AtomicBoolean(),new AtomicBoolean(),new AtomicBoolean(),new AtomicBoolean()};


    public String [] players_ips=new String[4]; //Max number of players will be 8
    public int [] players_ports=new int[4];
    int [][] all_listener_ports=new int[4][4];
    public AtomicInteger num_connected= new AtomicInteger();
    public int my_turn;
    Scanner scan;
    boolean master_dis=false,wrong_info=false;


    boolean successful_rcv=false,startGame=false,connected=false;

    // Contructor ///////////////////////////
    NetworkManager(){
        getMyIp();
        scan = new Scanner(System.in);
    }

    void startNotifier(){


        Thread notifier = new Thread(new Runnable() //Threading to take user input to stop blocking for other connections
        { public void run(){sendAlive();} });
        notifier.start();



    }

    void checkAlive(int num){
        System.out.println(num+" checking it alive ");
        Socket s=null;
        while(true){
            try{
                listeners[num].setSoTimeout(3000);
                s= listeners[num].accept();
                ObjectInputStream din=new ObjectInputStream(s.getInputStream());
                int ans = (int) ((Integer) din.readObject());
                din.close();
                s.close();
                if(ans!=1)
                    break;

            } catch(SocketTimeoutException e){
                System.out.println(e.getMessage());
                System.out.println("SocketTimeoutException occured!");
                if(!startGame && num==0){
                    players_connected[0].set(false);
                    System.out.println("Sorry! host is dead! you need to restart man!");
                }
                break;
            }
            catch(ClassNotFoundException e){
                System.out.println(e.getMessage());
                System.out.println("ClassNotFoundException occured!");
                break;
            }
            catch(IOException e){
                System.out.println(e.getMessage());
                System.out.println("IOException occured!");
                break;
            }
        }
        try{
            System.out.println("player num" + num+ "is disconnected");
            listeners[num].close();
            num_connected.set(num_connected.get()-1);
            players_connected[num].set(false);

        }catch(IOException e){
            System.out.println("can't close listener socket");
        }

    }

    void sendAlive(){
        while(true){
            try{
                for(int i=0; i<4; i++){
                    if(players_connected[i].get()&& i!=my_turn)
                    {
                        send_Object(1,players_ips[i],all_listener_ports[i][my_turn]);
                    }
                }

                Thread.sleep(1000);

            } catch(InterruptedException e){
                System.out.println(e.getMessage());
                break;

            }

        }
    }





    public Object rcv_Object(int timeout){

        Object rcv=null;
        try{
            my_Socket.setSoTimeout(timeout);
            Socket s= my_Socket.accept();
            ObjectInputStream din=new ObjectInputStream(s.getInputStream());
            rcv=din.readObject();
            din.close();
            s.close();
            successful_rcv=true;
        } catch(SocketTimeoutException e){
            //Call function that syas player is not there
            System.out.println(e.getMessage());
            System.out.println("SocketTimeoutException occured!");
            successful_rcv=false;
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
            System.out.println("ClassNotFoundException occured!");
            successful_rcv=false;
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("IOException occured!");
            successful_rcv=false;
        }
        return rcv;

    }

    public ObjectInputStream getObjectInputStream(int timeout) {
        ObjectInputStream din = null;
        try{
            my_Socket.setSoTimeout(timeout);
            Socket s= my_Socket.accept();
            din=new ObjectInputStream(s.getInputStream());
        } catch(SocketTimeoutException e){
            //Call function that syas player is not there
            System.out.println(e.getMessage());
            System.out.println("SocketTimeoutException occured!");
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("IOException occured!");
            successful_rcv=false;
        }
        return din;
    }

    public DataInputStream getDataInputStream(int timeout) {
        DataInputStream din = null;
        try{
            my_Socket.setSoTimeout(timeout);
            Socket s= my_Socket.accept();
            din=new DataInputStream(s.getInputStream());
        } catch(SocketTimeoutException e){
            //Call function that syas player is not there
            System.out.println(e.getMessage());
            System.out.println("SocketTimeoutException occured!");
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("IOException occured!");
            successful_rcv=false;
        }
        return din;
    }

    public void send_Object(Object message, String ip,int port){
        try{
            Socket s = new Socket(ip, port);
            ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
            dout.writeObject(message);
            dout.flush();
            dout.close();
            s.close();
        } catch(ConnectException e){
            //Do nothing for now
            System.out.println("uh uh, Connection error with ip: "+ip+ " port: "+port);
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public DataOutputStream getDataOutputStream(String ip, int port) {
        DataOutputStream dout = null;
        try{
            Socket s = new Socket(ip, port);
            dout = new DataOutputStream(s.getOutputStream());
        } catch(ConnectException e){
            //Do nothing for now
            System.out.println("uh uh, Connection error with ip: "+ip+ " port: "+port);
        } catch(Exception e) {
            System.out.println(e);
        }
        return dout;
    }

    public ObjectOutputStream getObjectOutputStream(String ip, int port) {
        ObjectOutputStream dout = null;
        try{
            Socket s = new Socket(ip, port);
            dout = new ObjectOutputStream(s.getOutputStream());
        } catch(ConnectException e){
            //Do nothing for now
            System.out.println("uh uh, Connection error with ip: "+ip+ " port: "+port);
        } catch(Exception e) {
            System.out.println(e);
        }
        return dout;
    }

    // Set up game room! ///////////////////////////

    // --Room Master Methods
    void isRoomMaster(){
        num_connected.set(0);
        try{
            my_turn=0; //Host is the first to play, always
            my_Socket= new ServerSocket(0);
            my_port=my_Socket.getLocalPort();
            addPlayer((my_IP.getHostAddress()).trim(), my_port, 0);

            // Scanner scan = new Scanner(System.in);
            char s='l';
            Thread create_room_t = new Thread(new Runnable() //Threading to take user input to stop blocking for other connections
            { public void run(){createRoom();} });
            create_room_t.start();

            while(s != 's'){ //will not be able to press enter without at leaast one player playing.
                System.out.println("enter 's' to start game");
                s = scan.next().charAt(0);
            }
            mul_socket.close();
            create_room_t.join();
            sendPlayersInfo();
        } catch(InterruptedException e){
            System.out.println(e.getMessage());
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    void createRoom(){
        //Generate room number
        Random rand = new Random();
        room_num = rand.nextInt(1000);
        System.out.println("Room number: "+room_num);

        while(true){ //keep listening for connections untill roomMaster starts gae or max number of players reached
            try{
                System.out.println("I'm a Master!");
                //recieve room_num, player IP, player socket
                String[] arr_rec=recieveMulticast();
                System.out.println(num_connected.get());
                if(Integer.parseInt(arr_rec[0])==room_num && num_connected.get()<3){
                    int curr_turn=0;
                    num_connected.set(num_connected.get()+1);

                    for(int i=0; i<num_connected.get()+1; i++){
                        if(!players_connected[i].get()){
                            curr_turn=i;
                            break;
                        }
                    }
                    System.out.println("connected with "+ arr_rec[1]+" and main port "+arr_rec[2]+ "and listener port "+arr_rec[3]+"curr turn "+curr_turn);
                    // all_listener_ports[0][curr_turn]= Integer.parseInt(arr_rec[3]);
                    //add to list of game players
                    addPlayer(arr_rec[1], Integer.parseInt(arr_rec[2]),curr_turn);
                    listeners[curr_turn]=new ServerSocket(0);
                    all_listener_ports[0][curr_turn]=listeners[curr_turn].getLocalPort();
                    all_listener_ports[curr_turn][0]=Integer.parseInt(arr_rec[3]);
                    all_listener_ports[curr_turn][1]=Integer.parseInt(arr_rec[4]);
                    all_listener_ports[curr_turn][2]=Integer.parseInt(arr_rec[5]);
                    all_listener_ports[curr_turn][3]=Integer.parseInt(arr_rec[6]);
                    listenToPlayer(curr_turn);
                    //Send to player their turn -> confirmation of correct connection to room
                    sendTurn(curr_turn, all_listener_ports[0][curr_turn] );
                    if(num_connected.get()==3){
                        System.out.println("Max Number of players reached!!No more will be added");
                    }
                }

            }
            catch(IOException e){
                System.out.println("Exitig setup, starting game!");
                System.out.println(e.getMessage());
                break;
            }
        }
    }


    void listenToPlayer(final int turn){


        Thread listener_t = new Thread(new Runnable() //Threading to take user input to stop blocking for other connections
        { public void run(){checkAlive(turn);} });
        listener_t.start();




    }

    String[] recieveMulticast() throws IOException{

        byte[] buf = new byte[256];
        String rec;
        mul_socket = new MulticastSocket(4446);
        mul_socket.setInterface(my_IP);
        InetAddress group = InetAddress.getByName(multicast_address);
        mul_socket.joinGroup(group);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        mul_socket.receive(packet);
        rec = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Recieved: "+rec);
        String[] arr_rec = rec.split(" ", 7);
        mul_socket.leaveGroup(group);
        mul_socket.close();
        return arr_rec;

    }

    void sendTurn(int num, int listening_port){

        System.out.println("sending turn to socket "+players_ips[num]+players_ports[num]);
        //send player turn
        send_Object(num,players_ips[num],players_ports[num]);
        //preparing my data as a room master to send
        String[] master_data={(my_IP.getHostAddress()).trim(),Integer.toString(my_port),Integer.toString(listening_port)};
        send_Object(master_data,players_ips[num],players_ports[num]);
    }

    void sendPlayersInfo(){

        for( int i=1; i<= num_connected.get(); i++){

            //Here we could check if player is actually alive


            //send necessary data to other players
            send_Object(players_ips,players_ips[i],players_ports[i]);
            send_Object(players_ports,players_ips[i],players_ports[i]);
            send_Object(all_listener_ports,players_ips[i],players_ports[i]);
            send_Object(num_connected.get(),players_ips[i],players_ports[i]);

        }

    }


    // --Room Master Methods
    void isPlayer(){
        startGame=false;connected=false;
        connectToRoom();

        connected =recvTurn();
        System.out.println("Waiting for RoomMaster to start game!");

        if(connected){
            startGame=recvPlayersInfo();
            if(startGame==false){
                master_dis=true;
            }
        }
        else{
            wrong_info=true;
        }
        for(int i=0; i<=num_connected.get(); i++){
            System.out.println(players_ips[i]+' '+players_ports[i]);
        }
        for(int i=1; i<=num_connected.get(); i++){
            if(i!=my_turn){
                listenToPlayer(i);
                players_connected[i].set(true);

            }

        }




    }

    void connectToRoom(){
        while(true){
            try{
                my_Socket= new ServerSocket(0); //instansiate my socket and port
                my_port=my_Socket.getLocalPort();
                System.out.println("Connect to room, please enter a room number:");
                room_num = scan.nextInt();
                for(int i=0; i<4; i++){
                    listeners[i]= new ServerSocket(0);

                }
                String connection_data=room_num+" "+(my_IP.getHostAddress()).trim()+" "+my_port+" "+listeners[0].getLocalPort()
                        +" "+listeners[1].getLocalPort()+" "+listeners[2].getLocalPort()+" "+listeners[3].getLocalPort();
                System.out.println(connection_data);
                sendMulticast(connection_data);
                players_connected[0].set(true);
                listenToPlayer(0);
                break;
            }
            catch(IOException e){
                System.out.println(e.getMessage());
                break;
            }
            catch(InputMismatchException e1){

                System.out.println("wronnnggg inputt, please enter a number");
                scan=new Scanner(System.in);


            }


        }


    }

    void sendMulticast( String multicastMessage) throws IOException {
        DatagramSocket socket;
        InetAddress group;
        byte[] buf;
        socket = new DatagramSocket();
        group = InetAddress.getByName(multicast_address);
        buf = multicastMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
        for(int i=0; i<10; i++)
        {
            socket.send(packet);
        }
        socket.close();
    }

    boolean recvTurn(){

        Object temp=rcv_Object(5000);

        if(temp!=null){
            my_turn=(int) ((Integer)temp);
            String[] host_data=(String[])rcv_Object(5000);
            if(successful_rcv){
                addPlayer(host_data[0],Integer.parseInt(host_data[1]),0);
                all_listener_ports[0][my_turn]=Integer.parseInt(host_data[2]);
                System.out.println("I got my tur: "+my_turn);
            }

            return successful_rcv;
        }
        System.out.println("nooooooo");

        //thread for host

        return false;


    }

    boolean recvPlayersInfo(){
        // successful_rcv=false;
        // while( players_connected[0].get()& !successful_rcv){
        //     players_ips=(String[]) rcv_Object(1000);

        // }
        // successful_rcv=false;

        // while( players_connected[0].get()& !successful_rcv){
        //     players_ips=(String[]) rcv_Object(1000);

        // }
        // successful_rcv=false;
        // while( players_connected[0].get()& !successful_rcv){
        //     all_listener_ports=(int[][]) rcv_Object(1000);

        // }
        // successful_rcv=false;
        // int temp=0;
        // while( players_connected[0].get()& !successful_rcv){
        //    temp =(int) rcv_Object(1000);
        // }

        // num_connected.set(temp);

        players_ips=(String[]) rcv_Object(0);
        players_ports=(int[]) rcv_Object(0);
        all_listener_ports=(int[][]) rcv_Object(0);
        num_connected.set((int) ((Integer)rcv_Object(0)));

        return successful_rcv;
    }

    // Utility ///////////////////////////
    void addPlayer(String ip, int port, int num){
        // num_connected++;
        players_ips[num]=ip;
        players_ports[num]=port;
        players_connected[num].set(true);
    }

    void getMyIp(){
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            // handle error
        }
        String result = null;
        if (interfaces != null) {
            while (interfaces.hasMoreElements() && result==null) {
                NetworkInterface i = interfaces.nextElement();
                Enumeration<InetAddress> addresses = i.getInetAddresses();
                while (addresses.hasMoreElements() && (result == null || result.isEmpty())) {
                    my_IP = addresses.nextElement();
                    if (!my_IP.isLoopbackAddress()  &&
                            my_IP.isSiteLocalAddress()) {
                        result = my_IP.getHostAddress();
                    }
                }
            }
        }

        System.out.println(my_IP);

    }



}