package VoIPVanilla;

import CMPC3M06.AudioRecorder;
import java.io.*;
import java.net.*;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;
import voip.*;

public class VoiceSender extends Thread {
    
    static DatagramSocket sending_socket;

    @Override
    public void run() {
        //Port for voip
        final int PORT = 55555;
        
        //IP Address to send to
        InetAddress clientIP = null;
        try {
		clientIP = InetAddress.getByName(VoIP.clientIPName);  //CHANGE localhost to IP or NAME of client machine
	} catch (UnknownHostException e) {
                System.out.println("ERROR: VoiceSender: Could not find client IP");
                System.exit(0);
	}
        
        //Open socket to send data from
        switch(VoIP.socket){
            case 1:
                try{
                    sending_socket = new DatagramSocket();
                } catch (SocketException e){
                    System.out.println("ERROR: VoiceSender: Could not open UDP socket to send from.");
                    System.exit(0);
                }
                break;
            case 2:
                try{
                    sending_socket = new DatagramSocket2();
                } catch (SocketException e){
                    System.out.println("ERROR: VoiceSender: Could not open UDP socket to send from.");
                    System.exit(0);
                }
                break;
            case 3:
                try{
                    sending_socket = new DatagramSocket3();
                } catch (SocketException e){
                    System.out.println("ERROR: VoiceSender: Could not open UDP socket to send from.");
                    System.exit(0);
                }
                
                break;
            default:
                try{
                    sending_socket = new DatagramSocket();
                } catch (SocketException e){
                    System.out.println("ERROR: VoiceSender: Could not open UDP socket to send from.");
                    System.exit(0);
                }
                break;
        }
        
        //Initialise AudioRecorder objects
        AudioRecorder recorder = null;
        try {
           recorder = new AudioRecorder();
        } catch (LineUnavailableException ex) {
            System.out.println("Recording Device: Line Unavailable");
        }

        /**************************
         * MAIN LOOP
         *************************/
        boolean running = true;
        byte sn = 1;
        
        while(running){
            try{

                VoIPPacket p = new VoIPPacket(sn, recorder.getBlock());
                
                DatagramPacket packet = new DatagramPacket(p.getPackagedData(), p.getPackagedData().length, clientIP, PORT);
                sending_socket.send(packet);
                sn++;
            }
            catch (IOException ex){
                System.out.println("ERROR: VoiceSender: Some random IO error occured!");
            }
        }
        //Close socket and stop the recorder
        sending_socket.close();
        recorder.close();
    }
}