package VoIPSocket2;

import CMPC3M06.AudioRecorder;
import java.io.*;
import java.net.*;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.*;
import voip.VoIPPacket;

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
        byte sn = 0;
        int depth = 4;
        VoIPPacket[][] loadBlock = new VoIPPacket[depth][depth];
        VoIPPacket[] interleave = new VoIPPacket[depth*depth];
        
        while(running){
            try{
                sn=0;
                long before = System.currentTimeMillis();
                //Load packets into dxd block
                for(int x = 0; x < loadBlock.length; x++){
                    for(int y = 0; y < loadBlock.length; y++){
                        VoIPPacket p = new VoIPPacket(sn , recorder.getBlock());
                        loadBlock[x][y] = p;
                        sn++;
                    }
                }
                long after = System.currentTimeMillis();
//                System.out.println("Interleave Delay: " + (after - before));
                
                //Interleave packets
                for(int i = 0; i < loadBlock.length; i++){
                    for(int j = 0; j < loadBlock.length; j++){
                        interleave[j*loadBlock.length + (loadBlock.length - 1 - i)] = loadBlock[i][j];
                    }
                }
                
                //Send Packets
//                System.out.println("Sending: ");
//                System.out.println(Arrays.toString(interleave));
                for (VoIPPacket p : interleave) {
//                    System.out.println(p);
                    DatagramPacket packet = new DatagramPacket(p.getPackagedData(), p.getPackagedData().length, clientIP, PORT);
                    sending_socket.send(packet);
                }
            }
            catch (IOException e){
                System.out.println("ERROR: VoiceSender: Some random IO error occured!");
            }
        }
        //Close socket and stop the recorder
        sending_socket.close();
        recorder.close();
    }
}