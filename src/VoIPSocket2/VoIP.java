package VoIPSocket2;

import voip.*;
import java.util.Scanner;

/**
 *
 * @author ybm14yju
 */
public class VoIP extends Thread {
    
    public static int socket;
    public static String clientIPName;
    public static int packets = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        VoiceSender sender = new VoiceSender();
        VoiceReceiver receiver = new VoiceReceiver();
                
        Scanner sc = new Scanner(System.in);
        System.out.println("Select a socket you want to use (1-3)");
        VoIP.socket = sc.nextInt();
        System.out.println("Enter the client IP/Machine name you want to connect to: ");
        VoIP.clientIPName = sc.next();
        System.out.println("Enter an amount of packets to send: ");
        VoIP.packets = sc.nextInt();
        
        sender.start();
        receiver.start();
        
        try {
            sender.join();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        try {
            receiver.join();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        
        System.out.println(VoiceReceiver.received + " packets / " + VoIP.packets);
    }
}
