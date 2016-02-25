package voip;
import java.text.SimpleDateFormat;

public class VoIPPacket implements Comparable<VoIPPacket>{
    private byte[] audio; //512 bytes
    private byte sequenceNo; //1 byte
    private byte[] packagedData;
    private byte[] timeStamp; //10 bytes
    private byte[] receivedTime;
    
    //Constructor for packet to be sent
    public VoIPPacket(byte sequenceNo, byte[] audio){
        this.audio = audio;
        this.sequenceNo = sequenceNo;
        this.timeStamp = new SimpleDateFormat("mm:ss:SSSS").format(System.currentTimeMillis()).getBytes();
        this.packagedData = new byte[audio.length+timeStamp.length+1];
        this.packagedData[0] = sequenceNo;
        System.arraycopy(timeStamp, 0, this.packagedData, 1, timeStamp.length);
        System.arraycopy(audio, 0, this.packagedData, 11, audio.length);
    }
    
    
    //Constructor for packet being received
    public VoIPPacket(byte[] receivedData){
        this.packagedData = receivedData;
        this.sequenceNo = receivedData[0];
        this.timeStamp = new SimpleDateFormat("mm:ss:SSSS").format(System.currentTimeMillis()).getBytes();
        
        this.receivedTime = new byte[10];
        for(int i = 0; i < this.receivedTime.length; i++){
            this.receivedTime[i] = receivedData[i+1];
        }
        
        this.audio = new byte[receivedData.length-11];
        System.arraycopy(receivedData, 11, this.audio, 0, audio.length);
    }
    
    public byte[] getPackagedData(){
        return this.packagedData;
    }
    
    public byte getSequenceNo(){
        return this.sequenceNo;
    }

    public void setSequenceNo(byte sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
    
    public byte[] getAudio(){
        return this.audio;
    }
    
    public void setAudio(byte[] audio){
        this.audio = audio;
    }
    
    public byte[] getTimeStamp(){
        return this.timeStamp;
    }
    
    public byte[] getReceivedTime(){
        return this.receivedTime;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Packet ").append(this.sequenceNo);
        return sb.toString();
    }
    
    @Override
    public int compareTo(VoIPPacket t) {
        if(this.sequenceNo == t.sequenceNo){
            return 0;
        }
        else if(this.sequenceNo < t.sequenceNo){
            return -1;
        }
        else {
            return 1;
        }
    }
}