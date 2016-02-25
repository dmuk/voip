package voip;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

public class WaveFormWindow extends JFrame{

    public WaveFormWindow() throws HeadlessException {
    }
    
    BufferStrategy bs;
    Graphics g;

    public WaveFormWindow(String string) throws HeadlessException {
        super(string);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(10, 10);
        this.setSize(800, 600);
        this.setVisible(true);
        this.setBackground(Color.black);
        this.createBufferStrategy(2);
        bs = this.getBufferStrategy();
        g = bs.getDrawGraphics();
    }


    
    public void drawBytes(byte[] b){
        int xoff = (this.getContentPane().getWidth()/2) - b.length/2;
        int yoff = this.getContentPane().getHeight()/2;
        g.setColor(Color.green);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.fillRect(xoff-1, this.getHeight() -(yoff-1), 2, 2);
        for(int i = 1; i < b.length; i++){
            g.fillRect(xoff + i -1, this.getHeight() - (yoff + b[i] -1), 2, 2);
            g.drawLine(xoff + i - 1, this.getHeight() -(yoff + b[i-1]) ,xoff + i , this.getHeight()-(yoff + b[i]));
        }
        bs.show();
    }
    

    
    public static void main(String[] args) {
        new WaveFormWindow("window");
    }


    
}