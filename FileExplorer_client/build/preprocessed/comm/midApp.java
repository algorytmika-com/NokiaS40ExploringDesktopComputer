/*
 * midApp.java
 *
 * Created on 24 czerwiec 2007, 19:49
 */
package comm;


import java.io.IOException;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author  Piotr Œlusarczyk, pslusarczyk@modasys.eu
 * @version 2007_06_24
 */
public class midApp extends MIDlet {
    
    Display ds;
    StartCanvas sc;
    MainList ml;
    Image[] images = new Image[8];
    Image splashScreen_up, splashScreen_down = null;
  
    public midApp(){
        ds = Display.getDisplay(this);
        
    }
 
    public void startApp() {
        
        try{
            //ladowanie zasobow
            splashScreen_up = Image.createImage("/res/com_up.png");
            splashScreen_down = Image.createImage("/res/com_down.png");
            
            sc = new StartCanvas(splashScreen_up, splashScreen_down);
            ds.setCurrent(sc);
            long time = System.currentTimeMillis();
            
            //laduj zasoby
            images[0] = Image.createImage("/res/up_16x16.png"); // strza³ka
            images[1] = Image.createImage("/res/folder_16x16.png"); //ikona dla katalogu 
            images[2] = Image.createImage("/res/unknown_16x16.png"); //ikona dla pliku nieznanego 
            images[3] = Image.createImage("/res/graphic_16x16.png"); // ikona dla pliku graficznego
            images[4] = Image.createImage("/res/midi_16x16.png"); // ikona dla pliku midi
            images[5] = Image.createImage("/res/text_16x16.png"); // ikona dla pliku tekstowego
            images[6] = Image.createImage("/res/disk_16x16.png"); // ikona dla dysku
            images[7] = Image.createImage("/res/web_16x16.png"); // ikona dla po³aczenia
      
            while (System.currentTimeMillis()-time <7000){
                try{
                    Thread.sleep(700);
                    sc.refresh((byte)10);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            ml = new MainList(this);
            ds.setCurrent(ml);
            
        }catch (IOException e){
            destroyApp(true);
            notifyDestroyed();
        }

    }
    
    public void pauseApp() {
        //nie implementowana na tym etapie
        //powinna zawieraæ implementacjê zatrzymania 
        //ewentualnych w¹tków
        //w trakcie gdy gra/aplikacja jest w stanie paused
    }
    
    public void destroyApp(boolean unconditional) {
    }
    

}
