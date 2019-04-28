// import sun.net.*;
import java.net.*;
import java.util.Scanner; 
import java.io.*;

public class MainFlow {
    public static void main(String [] args) {
        System.out.println("Game Starting :)");
        System.out.println("Enter 'h' if you want to create a nem game room! or 'r' to connect to an existing one:");
        Scanner scan = new Scanner(System.in);
        char s = scan.next().charAt(0); 
        if(s=='h'|| s=='H'){
            //code to listen to incoming data packets.
        }
        else if(s=='r' || s=='R'){
            //code to send datagrams somewhere?

        }
                


    }



    void connectToRoom(){
        

    }

    public void multicast( String multicastMessage) throws IOException {
        DatagramSocket socket;
        InetAddress group;
        byte[] buf;
        socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        buf = multicastMessage.getBytes();

        DatagramPacket packet 
        = new DatagramPacket(buf, buf.length, group, 4446);
        socket.send(packet);
        socket.close();
      }



} 
