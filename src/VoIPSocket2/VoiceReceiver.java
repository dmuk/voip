package VoIPSocket2;

import CMPC3M06.AudioPlayer;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;
import voip.VoIPPacket;

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
        VoIPPacket prevPacket = null;
        byte[] silence = new byte[512];
        Arrays.fill(silence, (byte)0);
        VoIPPacket silencePacket = new VoIPPacket((byte)-1, silence);
        VoIPPacket[] deInterleave = new VoIPPacket[16];
        
        for(byte j = 1; j < VoIP.packets; j++){
            full.add(j);
        }
        
        VoIPPacket lastFromPrev = new VoIPPacket((byte)15, silence);
        boolean[] hasPacket = new boolean[9];
        
        while(running){
            try{
                VoIPPacket receivedData = null;
                byte[] buffer = new byte[523];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
                Arrays.fill(deInterleave, silencePacket);
                
                receiving_socket.setSoTimeout(32); //How long the receiver will wait to receive the next packet (ms)
                for(int x = 0; x < deInterleave.length; x++){
                    try{
                         receiving_socket.receive(packet);
                         receivedData = new VoIPPacket(buffer);
                         sn = receivedData.getSequenceNo();
                         
                         deInterleave[sn] = receivedData;
                         prevPacket = receivedData;

                         store.add(sn);

                         VoiceReceiver.received++;
                     }
                     catch(SocketTimeoutException e){
                         if(prevPacket != null){
                            deInterleave[sn] = prevPacket;
                         }
                         else {
                             deInterleave[sn].setAudio(silence);
                         }
                     }
                }
                for (byte i = 0; i < deInterleave.length; i++) {
                    if(deInterleave[i].equals(silencePacket)){
                        System.err.printf("Packet %d dropped. ", i);
                        if(i != 0){
                            byte[] b = new byte[512];
//                            if(i != deInterleave.length-1){
//                                linearFill(b, deInterleave[i-1].getAudio()[deInterleave[i-1].getAudio().length-1], deInterleave[i+1].getAudio()[0]);
//                            } else {
//                                linearFill(b, deInterleave[i-1].getAudio()[deInterleave[i-1].getAudio().length-1], (byte)0);
//                            }
                            Arrays.fill(b, deInterleave[i-1].getAudio()[deInterleave[i-1].getAudio().length-1]);
                            deInterleave[i].setSequenceNo(i);
                            deInterleave[i].setAudio(b);
                            System.err.printf("replaced by prev packet %d. \n", i-1);
                        }
                        else {
                            byte[] b = new byte[512];
//                            if(i != deInterleave.length-1){
//                                linearFill(b, lastFromPrev.getAudio()[lastFromPrev.getAudio().length-1], deInterleave[i+1].getAudio()[0]);
//                            } else {
//                                linearFill(b, lastFromPrev.getAudio()[lastFromPrev.getAudio().length-1], (byte)0);
//                            }

                            Arrays.fill(b, lastFromPrev.getAudio()[lastFromPrev.getAudio().length-1]);
                            deInterleave[i].setSequenceNo(i);
                            deInterleave[i].setAudio(b);
                            System.err.print("replaced by prev packet from last block \n");
                        }
                    }
                }
//                System.out.println("Unsorted: " + Arrays.toString(deInterleave));
//                Arrays.sort(deInterleave);
//                System.out.println("Play order: " + Arrays.toString(deInterleave));
                
//                System.out.println("Playing: ");
                for(VoIPPacket p : deInterleave){
//                    System.out.println(p);
                    player.playBlock(p.getAudio());
                    n++;
                }
                lastFromPrev = deInterleave[deInterleave.length-1];
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
    
    public static void linearFill(byte[] b, double start, double end){
        for(int i = 0; i < b.length; i++){
            b[i] = (byte) ((start + (((end-start) / (b.length)) * i)));
        }
        System.out.println("start: "+start+" end: " + end + " data: \n" + Arrays.toString(b));
    }
}