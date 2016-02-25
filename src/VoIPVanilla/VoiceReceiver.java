package VoIPVanilla;

import CMPC3M06.AudioPlayer;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        byte[] receivedTime;
        byte[] times;
        ArrayList<Byte> full = new ArrayList<>();
        ArrayList<Byte> store = new ArrayList<>();
        
        for(byte j = 1; j < VoIP.packets; j++){
            full.add(j);
        }
        
        while(running){
            try{
                
                VoIPPacket receivedData = null;
                byte[] buffer = new byte[523];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
                
                receiving_socket.setSoTimeout(5000); //How long the receiver will wait to receive the next packet (ms)
                try{
                     receiving_socket.receive(packet);
                     receivedData = new VoIPPacket(buffer);
                     sn = receivedData.getSequenceNo();
                     store.add(sn);
                     receivedTime = receivedData.getReceivedTime();
                     times = receivedData.getTimeStamp();
                     String text1 = new String(receivedTime, "UTF-8");
                     String text2 = new String(times, "UTF-8");
                     SimpleDateFormat test = new SimpleDateFormat("mm:ss:SSSS");
                     Date time1 = test.parse(text1);
                     Date time2 = test.parse(text2);
                     long difference = (time2.getTime() - time1.getTime()) & 0xFF;
                     System.out.println("Packet " + sn + " received. Latency: " + difference + "ms" + ". Sent " + text1 + ". Received: " + text2);

                     VoiceReceiver.received++;
                 }
                 catch(SocketTimeoutException e){
                     System.err.println("Packet Timeout...");
                     n++;
                 } catch (ParseException ex) {
                     System.out.println(ex);
                 }
                
                if(receivedData != null){
                    player.playBlock(receivedData.getAudio());
                }
                n++;
            }
            catch(IOException e){
                System.out.println("ERROR: VoiceReceiver: Some random IO error occured!");
            }
        }
        //Close the socket and stop the player
        receiving_socket.close();
        player.close();
        full.removeAll(store);
        System.out.println("Packets Lossed: " + full);
        System.out.println("Number of Packets Lossed: " + full.size());
    }
}