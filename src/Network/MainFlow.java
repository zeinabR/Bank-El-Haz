
// import sun.net.*;
import java.net.*;
import java.util.Scanner;
// import java.util.concurrent.TimeUnit;
// import java.awt.datatransfer.FlavorListener;

// import com.sun.tools.javac.util.Pair;

import java.io.*;
import java.util.Enumeration;
import java.util.Random; 



public class MainFlow {

 
    public static void main(String [] args) {

        //initialize Network Manager
        NetworkManager net_mang=new NetworkManager();
        

        //Get user game state (host or player)
        System.out.println("Game Starting :)");
        Scanner scan = new Scanner(System.in);
        

        while(true)
        {
            System.out.println("Enter 'm' if you want to create a nem game room! or 'r' to connect to an existing one:");
            char s = scan.next().charAt(0);
            if(s=='m'|| s=='M'){ //If room master, listen for other players

                net_mang.isRoomMaster();
                // while(true){}
                break;

            }
            else if(s=='r' || s=='R'){ //if not room master, connect to exsiting room
                
                boolean successful=net_mang.isPlayer();
                if(successful){
                    break;
                }
                else{
                    System.out.println("Something went wrong!!, try again");
                }
                // while(true){}
               
            
            }
            else{ //wrong input dude!
                System.out.println("please enter a valid input , 'm' or 'r': ");
               
            }

        }

       

        

        System.out.println("lets play!");
        int curr_turn=0;

        while(true){
            if(net_mang.my_turn==curr_turn){
                //let servers know you're alive!
                // net_mang.send_Object((Object)net_mang.my_turn);
                
                System.out.println("hi there, enter something! it is meeee "+curr_turn);
                char s = scan.next().charAt(0);
                if(s=='q'){
                    break;
                }
              

                net_mang.send_Object((Object)"Game steps");

            }
            else{
                if(! net_mang.my_Socket.isClosed()){
                    try{
                        net_mang.my_Socket.close();
                        net_mang.my_Socket= new ServerSocket(net_mang.my_port);
                    }catch(IOException e){
                        System.out.println(e.getMessage());
                        System.out.println("ioexcept here");
                    }

                }
               
                System.out.println("Not my turn, is actually the turn of player "+ (int)curr_turn);
                Object rcv =net_mang.rcv_Object(6*1000); 
                System.out.println((String)rcv);

            }
            curr_turn++;
            curr_turn=curr_turn%(net_mang.num_connected+1);


        }

        
        // while(true){}
       
      


    }




} 


class NetworkManager{

    String multicast_address="230.0.0.0"; //Address for multicasting
    InetAddress my_IP;
    ServerSocket my_Socket;
    ServerSocket listener;
    int my_port;
    int room_num;
    MulticastSocket mul_socket ;
   

    String [] players_ips=new String[4]; //Max number of players will be 8
    int [] players_ports=new int[4];
    boolean [] player_connected={false,false,false,false};
    int num_connected=0;
    int my_turn;
    Scanner scan;

    // Contructor ///////////////////////////
    NetworkManager(){
        getMyIp();
        scan = new Scanner(System.in);

    }

    Object rcv_Object(int timeout){
        Object rcv=null;
        try{
            my_Socket.setSoTimeout(timeout);
            Socket s=my_Socket.accept(); 
           
            ObjectInputStream din=new ObjectInputStream(s.getInputStream());  
    
            rcv=din.readObject(); 
            
          
        
            din.close();  
            s.close();  
            
         

        }catch(SocketTimeoutException e){
            //Call function that syas player is not there
            System.out.println(e.getMessage());
            System.out.println("timeout probl");

        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
            System.out.println("class prob");
    
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.out.println("ioexcep");
        }
        return rcv;

    }

    void send_Object(Object message){
        for(int i=0; i<=num_connected; i++){
            try{      
                if(i!=my_turn){
                    Socket s=new Socket(players_ips[i],players_ports[i]);  
                    System.out.println("sending alive to "+i);
        
                    ObjectOutputStream dout=new ObjectOutputStream(s.getOutputStream());  
                    dout.writeObject(message);  
                    dout.flush();  
                    dout.close();  
                    s.close(); 

                }
                
            }catch(ConnectException e){
               //Do nothing for now
               System.out.println("uh uh, seems we have lost one of us "+ i+ "but we must carry on");

            }
            catch(Exception e){
                System.out.println(e);
            }   
            
        }


    }

    
    // Set up game room! ///////////////////////////
    
    // --Room Master Methods 
    void isRoomMaster(){

        
        try{
            my_turn=0; //Host is the first to play, always
            my_Socket= new ServerSocket(0);
            my_port=my_Socket.getLocalPort();
            addPlayer((my_IP.getHostAddress()).trim(), my_port, 0);
    
            // Scanner scan = new Scanner(System.in);
            char s='l';
            Thread thread = new Thread(new Runnable() //Threading to take user input to stop blocking for other connections
            { public void run(){createRoom();} });
            thread.start();

            
            while(s!='s'){ //will not be able to press enter without atleaast one player playing.
                System.out.println("enter 's' to start game");
                s = scan.next().charAt(0);
            }
            mul_socket.close();
            thread.join();

            sendPlayersInfo();

                
            // scan.close();


       } catch(InterruptedException e){
            System.out.println(e.getMessage());
       }
       catch(IOException e){
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
                

                if(Integer.parseInt(arr_rec[0])==room_num){
                    

                    num_connected++;
                    //add to list of game players
                    addPlayer(arr_rec[1], Integer.parseInt(arr_rec[2]), num_connected);

                    //Send to player their turn -> confirmation of correct connection to room
                    sendTurn(num_connected);
               
                    if(num_connected==3){
                        System.out.println("Max Number of players reached!!No more will be added");
                        break;
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
        String[] arr_rec = rec.split(" ", 3);
        mul_socket.leaveGroup(group);
        mul_socket.close();
        return arr_rec;
    
    }

    void sendTurn(int num){

        System.out.println("sending turn to socket "+players_ips[num]+players_ports[num]);

        try{      
      
            Socket s=new Socket(players_ips[num],players_ports[num]);  
            // System.out.println("I closed socket! "+s.isClosed());
        

            ObjectOutputStream dout=new ObjectOutputStream(s.getOutputStream());  
            dout.writeObject(num);
            String[] host_data={(my_IP.getHostAddress()).trim(),Integer.toString(my_port)};
            dout.writeObject(host_data);
            dout.flush();  
            dout.close();  
            s.close();  
            }catch(ConnectException e){
                num_connected--;
            }
            catch(Exception e){
                System.out.println(e);
            }   
    }

    void sendPlayersInfo(){

        for( int i=1; i<=num_connected; i++){
            try{      
      
                Socket s=new Socket(players_ips[i],players_ports[i]);  
                System.out.println("sending info to "+i);
    
                ObjectOutputStream dout=new ObjectOutputStream(s.getOutputStream());  
                dout.writeObject(players_ips);  
                dout.writeObject(players_ports);  
                dout.writeInt(num_connected);
                dout.flush();  
                dout.close();  
                s.close();  
            }catch(ConnectException e){
               //Do nothing for now
               System.out.println("uh uh, seems we have lost one of us "+ i+ "but we must carry on");

            }
            catch(Exception e){
                System.out.println(e);
            }   
        }

    }
    // --Room Master Methods 
    boolean isPlayer(){
            boolean startGame=false,connected=false;

            connectToRoom();
            connected =recvTurn();
        

            System.out.println("Waiting for RoomMaster to start game!");
            if(connected){
                startGame=recvPlayersInfo();
            }
           

        for(int i=0; i<=num_connected; i++){
            System.out.println(players_ips[i]+' '+players_ports[i]);
        }

        return startGame&connected;

          

       
                
    }
   
    void connectToRoom(){

        try{
            my_Socket= new ServerSocket(0);
            // Scanner scan = new Scanner(System.in);
            System.out.println("Connect to room, please enter a room number:");
            // while(!scan.hasNextLine()){
            // }
            room_num = scan.nextInt();
            my_port=my_Socket.getLocalPort();
            String connection_data=room_num+" "+(my_IP.getHostAddress()).trim()+" "+my_port;
            System.out.println("stuff are:"+connection_data);
            sendMulticast(connection_data);
            // scan.close();
            // recieveHostInfo();
    
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        

    }

    void sendMulticast( String multicastMessage) throws IOException {
        DatagramSocket socket;
        InetAddress group;
        byte[] buf;
        socket = new DatagramSocket();
        // socket.setInterface(my_IP);
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
        System.out.println("my add "+(my_IP.getHostAddress()).trim()+my_port);
        

        try{
            my_Socket.setSoTimeout(5000);
            Socket s=my_Socket.accept(); 
           
            ObjectInputStream din=new ObjectInputStream(s.getInputStream());  
    
            
            my_turn=(int)din.readObject();  
            String[] host_data=(String[])din.readObject();
            players_ips[0]=host_data[0];
            players_ports[0]=Integer.parseInt(host_data[1]);
            System.out.println("Host add "+players_ips[0]+" "+ players_ports[0]);

            System.out.println("I got my tur: "+my_turn);  
            din.close();  
            s.close();  
            return true;

        }catch(SocketTimeoutException e){
            System.out.println(e.getMessage());
            return false;
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
            return false;

        }
        catch(IOException e){
            System.out.println(e.getMessage());
            return false;
        }
        
    }

    boolean recvPlayersInfo(){
        
        while(true){

            try{
                my_Socket.setSoTimeout(3000);
                Socket s=my_Socket.accept(); 
    
                ObjectInputStream  din=new ObjectInputStream(s.getInputStream());  
                players_ips=(String[])din.readObject(); 
                players_ports=(int[])din.readObject();  
                num_connected=din.readInt();
                din.close();  
                s.close();  
               return true;
            }
            catch(ClassNotFoundException e){
                System.out.println(e.getMessage());
                return false;
    
    
            }
            catch(SocketTimeoutException e){
                
                try{
                    System.out.println("checking for room master");
                    Socket s=new Socket(players_ips[0],players_ports[0]);  
                    s.close();
                }
                catch(ConnectException ec){
                    //Do nothing for now
                    System.out.println("no master!");

                    System.out.println(ec);
                    // break;
                    return false;
    
                }
                catch(IOException eo){
                    System.out.println(eo.getMessage());
                }
    

                continue;
                
            }
            catch(IOException e){
                System.out.println(e.getMessage());
                return false;
            }

        }
      

    }

    // Utility ///////////////////////////
    void addPlayer(String ip, int port, int num){
        // num_connected++;
        players_ips[num]=ip;
        players_ports[num]=port;
    }

    void getMyIp(){
        // try {
        //     my_IP=InetAddress.getAddress();
        //     System.out.println(my_IP);
        // } catch (UnknownHostException e) {
        //     e.printStackTrace();
        // }
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
