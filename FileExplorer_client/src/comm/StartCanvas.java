package comm;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
/*
 * StartCanvas.java
 *
 * Created on 24 czerwiec 2007, 20:02
 *
 */

/**
 * @author  Piotr Œlusarczyk, pslusarczyk@modasys.eu
 * @version 2007_06_24
 */
public class StartCanvas extends Canvas{
    
    byte progress = 0;
    Image img_up, img_down;
    
    /**
     * Creates a new instance of StartCanvas
     */
    public StartCanvas(Image img_up, Image img_down) {
        this.img_up = img_up;
        this.img_down = img_down;
        this.setFullScreenMode(true);
    }
    
    protected void paint(Graphics g) {
        g.setColor(0xFFFFFF);
        g.fillRect(0,0,getWidth(), getHeight());
        
        g.setColor(0xFF0000);
        g.fillRect((getWidth() - 100)/2,getHeight()/2 - 5 ,progress, 10);
        
        g.setColor(0x000000);
        g.drawRect((getWidth() - 100)/2,getHeight()/2 - 5 ,100, 10);
        
        g.drawImage(img_up, getWidth()/2 - img_up.getWidth()/2, 5,20);
        g.drawImage(img_down, getWidth()/2 - img_down.getWidth()/2, getHeight() - 5- img_down.getHeight(),20);
    }
    
    public void refresh(byte progress){
        this.progress += progress;
        repaint();
        serviceRepaints();
    }
    
}
