package VoIPSocket1;

import CMPC3M06.AudioPlayer;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;
import voip.*;

public class VoiceReceiver extends Thread{
    
    static DatagramSocket receiving_socket;
    public static int received = 0;
    
    @Override
    public void run() {
        //Port to open socket on
        final int PORT = 55555;
        
        //DatagramSocket receiving_socket;
        switch(VoIP.socket){
            case 1:
                try{
                    receiving_socket = new DatagramSocket(PORT);
                } catch (SocketException e){
                    System.out.println("ERROR: VoiceReceiver: Could not open UDP socket to send from.");
                    System.exit(0);
                }
                break;
            case 2:
                try{
                    receiving_socket = new DatagramSocket2(PORT);
                } catch (SocketException e){
                    System.out.println("ERROR: VoiceReceiver: Could not open UDP socket to send from.");
                    System.exit(0);
                }
                break;
            case 3:
                try{
                    receiving_socket = new DatagramSocket3(PORT);
                } catch (SocketException e){
                    System.out.println("ERROR: VoiceReceiver: Could not open UDP socket to send from.");
                    System.exit(0);
                }
                
                break;
            default:
                try{
                    receiving_socket = new DatagramSocket(PORT);
                } catch (SocketException e){
                    System.out.println("ERROR: VoiceReceiver: Could not open UDP socket to send from.");
                    System.exit(0);
                }
                break;
        }
        
        
        //Intialize AudioPlayer
        AudioPlayer player = null;
        try {
            player = new AudioPlayer();
        } catch (LineUnavailableException ex) {
            System.out.println("Speakers: Line Unavailable");
        }
        
        /**************************
         * MAIN LOOP
         *************************/
        
        boolean running = true;
        byte sn = 0;
        int n = 1;
        ArrayList<Byte> full = new ArrayList<>();
        ArrayList<Byte> store = new ArrayList<>();
        VoIPPacket prevPacket = null;
        byte[] silence = new byte[512];
        
        for(byte j = 1; j < VoIP.packets; j++){
            full.add(j);
        }
        
        while(running){
            try{
                
                VoIPPacket receivedData = null;
                byte[] buffer = new byte[5131];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
                
                receiving_socket.setSoTimeout(2000); //How long the receiver will wait to receive the next packet (ms)
                try{
                     receiving_socket.receive(packet);
                     receivedData = new VoIPPacket(buffer);
                     sn = receivedData.getSequenceNo();
                     store.add(sn);

                     VoiceReceiver.received++;
                 }
                 catch(SocketTimeoutException e){
                     System.err.println("Packet Timeout...");
                     n++;
                 }
                
                if(receivedData !=  null){
                    player.playBlock(receivedData.getAudio());
                }

            }
            catch(IOException e){
                System.out.println("ERROR: VoiceReceiver: Some random IO error occured!");
            }
        }
        //Close the socket and stop the player
        receiving_socket.close();
        player.close();
        
        System.out.println(full.removeAll(store));
        System.out.println(full);
        System.out.println(full.size());
    }
}