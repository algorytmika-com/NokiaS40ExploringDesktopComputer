/*
 * FileList.java
 *
 * Created on 20 czerwiec 2007, 09:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package explorer;

import java.io.File;

/**
 *
 * @author  Piotr Œlusarczyk, pslusarczyk@modasys.eu
 * @version 2007_06_24
 */
public class FileList {
    
    /** Creates a new instance of FileList */
    public FileList() {
    }
    
    public String reply(String folder){
        
        File path = new File(folder);
        String[] list;
        
        list = path.list();
        String[] sendList = new String[list.length];
        String replyText;

        replyText = folder + "<$0$>";
            
        for (int i = 0; i < list.length; i++) {
            String longName = folder+ "/"+ list[i];

            File f = new File(longName);

            if (f.isDirectory()){
                sendList[i] = "1"; //1 ozn katalog
            }else {

                if (list[i].indexOf(".txt")>0 || list[i].indexOf(".dat")>0){
                    sendList[i] = "5";//5 ozn. plik tekstowy
                }else if (list[i].indexOf(".png")>0 || list[i].indexOf(".jpg")>0){
                    sendList[i] = "3";// 3 ozn plik graficzny
                }else if (list[i].indexOf(".mid")>0){
                    sendList[i] = "4";// 4 ozn plik midi
                }else {
                    sendList[i] = "2";// 2 ozn plik nieznany
                }
            }

            int j = i+1;
            sendList[i] = sendList[i]+list[i]+"<$"+j+"$>";
            replyText = replyText + sendList[i];
        }        
  
        return replyText;
    }
    
    public String initialize(String folderName){
        
        File path = new File(folderName);
        
        File[] rootList;
            
        rootList = path.listRoots();
        String replyText = "Moj komputer" + "<$0$>";
        int j = 0;
            for (int i = 0; i < rootList.length; i++) {
                j = i+1;

                replyText = replyText + "6" +rootList[i].getAbsolutePath()+"<$"+j+"$>"; // 6 ozn. dysk
            } 
        j++;
        replyText = replyText + "1" + folderName + "<$"+j+"$>";

        return replyText.replace('\\', '/');
    }
    
}
