package comm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
/*
 * MainList.java
 *
 * Created on 15 marzec 2007, 20:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author  Piotr Œlusarczyk, pslusarczyk@modasys.eu
 * @version 2007_06_24
 */
public class MainList extends List implements CommandListener{
    Connection con;
    
    private midApp parent;
    
    private Alert alert;
    
    private Command exitCmd = new Command("WyjdŸ", Command.EXIT, 0);
    private Command okCmd = new Command("Wybierz", Command.OK, 0);
    private Command backCmd = new Command("Wróæ", Command.BACK, 0);
    private Command helpCmd = new Command("Pomoc", Command.HELP, 0);
    
    private static final String urlCore =   "http://localhost:8084/FileExplorer_servlet/"+
                                        "FileExplorerServlet?"+
                                        "path=";
    public static final String INIT = "$initialize$";
    public static final String BACK = "$getBack$";
    public static final String ROOT = "Moj komputer";
    private String currentPath = "c:";
    private String formerPath = "c:";
    private String pressedFolder;
    private Form imageForm;
    private ImageItem imageItem;
    private Object helloForm;
    private String initialUrl;
    private String odpowiedz = null;

    /** Creates a new instance of MainList */
    public MainList(midApp parent) {
        super("Explorator PeCeTa", List.IMPLICIT);
        this.parent = parent;

        initialUrl =   urlCore + INIT;
        this.append("Po³¹cz", parent.images[7]);

        this.addCommand(okCmd);
        this.addCommand(exitCmd);
        this.addCommand(helpCmd);

        this.setCommandListener(this);
        
    }
    
    private void communicate(String url){

        try{
            if (url.indexOf(".png")>0 || url.indexOf(".jpg")>0){         //obs³uga plików png
            imageForm = new Form(currentPath);
                try{
                    Object unknown = downloadImageFromServer(replaceSpaces(url));
                    //System.out.pintln(url);
                    if (unknown instanceof Image){
                        Image responseImage = (Image)unknown;
                        imageItem = new ImageItem(pressedFolder ,responseImage,Item.LAYOUT_CENTER, currentPath);
                        imageForm.append(imageItem);
                        imageForm.addCommand(backCmd);
                        imageForm.setCommandListener(this);
                        parent.ds.setCurrent(imageForm);

                    }else if (unknown instanceof String){
                        Alert a = new Alert("Blad","B³¹d odnajdywania obrazu",null,AlertType.ERROR);
                        parent.ds.setCurrent(a);
                        displayList(odpowiedz);
                    }
                }catch (Exception e){
                    Alert a = new Alert("Blad","B³¹d podczas komunikacji",null,AlertType.ERROR);
                    parent.ds.setCurrent(a);
                    displayList(odpowiedz);
                    e.printStackTrace();
                }
            }else if (url.indexOf(".mid")>0){       //obs³uga plików mid
                try{
                    Object unknown = downloadTuneFromServer(replaceSpaces(url));
                    if (unknown instanceof Player){
                        Player responseTune = (Player)unknown;
                        responseTune.realize();
                        responseTune.start();

                        parent.ds.setCurrent(this);
                        displayList(odpowiedz);                        
                    }else if (unknown instanceof String){
                        Alert a = new Alert("Blad","B³¹d odnajdywania pliku audio",null,AlertType.ERROR);
                        parent.ds.setCurrent(a);
                        displayList(odpowiedz);
                    }

                }catch (IOException e){
                    e.printStackTrace();
                    Alert al = new Alert("Komunikacja", "B³¹d komunikacji z servletem",null,AlertType.ERROR);
                    parent.ds.setCurrent(al);
                    displayList(odpowiedz);
                }catch (MediaException e){
                    e.printStackTrace();
                    Alert al = new Alert("Blad", "B³¹d z odtwarzaniem",null,AlertType.ERROR);
                    parent.ds.setCurrent(al);
                    displayList(odpowiedz);
                }
                
            }
            else if (url.indexOf(".txt")>0 || url.indexOf(".dat")>0){        //obs³uga plików tekstowych
                String odpowiedz = con.callServlet(replaceSpaces(url));
                this.setTitle(currentPath);

                try{
                    TextBox zawartosc = new TextBox(pressedFolder,"", 5000,TextField.ANY);
                    parent.ds.setCurrent(zawartosc);
                    try {
                        zawartosc.setString(odpowiedz);
                    } catch(IllegalArgumentException ex){
                        Alert al = new Alert("Blad", "Tekst nie mo¿e byæ d³u¿szy ni¿" +
                                "5000 znaków",null,AlertType.ERROR);
                        parent.ds.setCurrent(al);
                    }
                  zawartosc.addCommand(backCmd);
                  zawartosc.setCommandListener(this);

                }catch (Exception e){
                    Alert a = new Alert("Blad","B³¹d podczas komunikacji",null,AlertType.ERROR);
                    parent.ds.setCurrent(a);
                    displayList(odpowiedz);
                }

            }else{
                if (url.indexOf(".")>0){            //obs³uga plików nieznanych

                    Alert a = new Alert("Info","Ten plik nie jest obslugiwany",null,AlertType.INFO);
                    parent.ds.setCurrent(a);
                    displayList(odpowiedz);

                }else{                                          //obs³uga folderów
                     odpowiedz = con.callServlet(replaceSpaces(url));
                     displayList(odpowiedz);
                }

            }

         }catch (IOException e){
            Alert al = new Alert("Komunikacja", "B³¹d komunikacji z servletem",null,AlertType.ERROR);
            parent.ds.setCurrent(al);
            this.append("Po³¹cz ponownie", parent.images[7]);
        }catch (NullPointerException ex){
            Alert al = new Alert("Komunikacja", "Prawdopodobnie brak praw dostêpu",null,AlertType.ERROR);
            parent.ds.setCurrent(al);
            this.append("Po³¹cz ponownie", parent.images[7]);
        }        


    }
    

    
    public void commandAction(Command command, Displayable displayable) {
        if (command == okCmd){

            if (this.getSelectedIndex()==0){
                if (initialUrl.indexOf(INIT)>0){
                    this.deleteAll();
                    communicate(initialUrl);
                }else{
                    this.deleteAll();
                    communicate(urlCore + currentPath + BACK);
                } 
    
            }else {
                   pressedFolder = this.getString(this.getSelectedIndex());
                   formerPath = currentPath;
                   System.out.println("path: " + currentPath);
                   this.deleteAll();
                   String restOfUrl = null;
                   if (currentPath.equals(ROOT)){
                       restOfUrl = urlCore + pressedFolder;
                       initialUrl="";
                   }else{
                       restOfUrl = urlCore + currentPath + "/" + pressedFolder;
                   }
                   communicate(restOfUrl.trim()); 
                   System.out.println(restOfUrl.trim());
            }
      
        }else if (command == backCmd){
            parent.ds.setCurrent(this);
            displayList(odpowiedz);
   
        }else if (command == helpCmd){
            Alert a = new Alert("Pomoc", "Eksplorator katalogów i plików," +
                    " obs³ugiwane s¹ pliki graficzne (.png, .jpg)," +
                    " muzyczne(.mid) oraz tekstowe (.txt, .dat)." +
                    "Zoptymalizowano pod Nokiê S40, " +
                    "Umo¿liwia przegl¹d katalogów peceta, dostêpne s¹ katalogi " +
                    "i pliki, które maj¹ te same prawa dostêpu co servlet",
                    null,AlertType.INFO);

            parent.ds.setCurrent(a);
            
        }else if (command == exitCmd){
            parent.destroyApp(true);
            parent.notifyDestroyed();
        }
    }
    
    private void displayList(String odpowiedz){
    this.odpowiedz = odpowiedz;       
    currentPath = odpowiedz.substring(0, odpowiedz.indexOf("<$0$>"));
    this.setTitle(currentPath);
    this.append("...", parent.images[0]);
    int i=0;
    String beginStr= null;
    String endStr = null;
    String check = "<$"+0+"$>";
    while (odpowiedz.indexOf(check)>0){  

      beginStr = "<$"+i+"$>";
      int j = i+1;
      endStr =  "<$"+j+"$>";
      String category = odpowiedz.substring(odpowiedz.indexOf(beginStr)+beginStr.length(),
              odpowiedz.indexOf(beginStr)+beginStr.length()+1);

      this.append(odpowiedz.substring(odpowiedz.indexOf(beginStr)+beginStr.length()+1,
              odpowiedz.indexOf(endStr)), parent.images[Integer.parseInt(category)]);

      int k = j+1;
      check = "<$"+k+"$>";
      i++;
    }          
    }
    
    private String replaceSpaces(String url){
      while (url.indexOf(" ")>0){
      url = url.substring(0, url.indexOf(" "))+"%20"
         + url.substring(url.indexOf(" ")+1);
                                
      }   
     return url;
    }
    
    public static final Object downloadImageFromServer(String url) throws IOException {
        HttpConnection connection = (HttpConnection) Connector.open(url);
        DataInputStream iStrm = connection.openDataInputStream();
        
        String type = connection.getHeaderField("Content-Type");
        try {
            
            if ( (type.equals("text/plain")) || (type.equals("text/xml"))){
                int length = (int) connection.getLength();
                if (length > 0) {
                    byte servletData[] = new byte[length];
                    iStrm.read(servletData);
                    try{
                        return new String(servletData,"UTF-8");
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                }
            }else if (type.equals("application/octet-stream")){

                byte imageData[];
                int length = (int) connection.getLength();
                if (length != -1) {
                    imageData = new byte[length];
                    iStrm.readFully(imageData);
                } else  // Length not available...
                {
                    ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
                    int ch;
                    while ((ch = iStrm.read()) != -1)
                        bStrm.write(ch);
                    imageData = bStrm.toByteArray();
                    bStrm.close();
                }
                return Image.createImage(imageData, 0, imageData.length);
            }
        } finally {
            // Clean up
            if (iStrm != null)
                iStrm.close();
            if (connection != null)
                connection.close();
        }
        return null;
    }

    public Display getDisplay() {                         
        return Display.getDisplay(parent);
    }  

        public static final Object downloadTuneFromServer(String url) throws IOException, MediaException {
        HttpConnection connection = (HttpConnection) Connector.open(url);
        DataInputStream iStrm = connection.openDataInputStream();
        
        String type = connection.getHeaderField("Content-Type");
        try {
            
            if ( (type.equals("text/plain")) || (type.equals("text/xml"))){
                int length = (int) connection.getLength();
                if (length > 0) {
                    byte servletData[] = new byte[length];
                    iStrm.read(servletData);
                    try{
                        return new String(servletData,"UTF-8");
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                }
            }else if (type.indexOf("audio")>-1){
                byte tuneData[];
                int length = (int) connection.getLength();
                if (length != -1) {
                    tuneData = new byte[length];
                    iStrm.readFully(tuneData);
                } else  // Length not available...
                {
                    ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
                    int ch;
                    while ((ch = iStrm.read()) != -1)
                        bStrm.write(ch);
                    tuneData = bStrm.toByteArray();
                    bStrm.close();
                }
                // Create the tune player from the byte array
                ByteArrayInputStream bis = new ByteArrayInputStream(tuneData);
                Player player = Manager.createPlayer(bis,type);
                return player;//Image.createImage(imageData, 0, imageData.length);
            }
        } finally {
            // Clean up
            if (iStrm != null)
                iStrm.close();
            if (connection != null)
                connection.close();
        }
        return null;
    }
    
}
