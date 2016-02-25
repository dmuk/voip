package voip;

import CMPC3M06.AudioRecorder;
import javax.sound.sampled.LineUnavailableException;

class recorderTest{
    public static void main(String[] args) throws Exception{
        AudioRecorder rec = null;
        rec = new AudioRecorder();

        
        long start, finish;
        
        for (int i = 0; i < 64; i++) {
            Thread.sleep(31);
            start = System.currentTimeMillis();
            rec.getBlock();
            finish = System.currentTimeMillis();
            System.out.println(finish - start);
        }
    }
}