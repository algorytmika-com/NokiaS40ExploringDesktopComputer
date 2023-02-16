/*
 * FileExplorerServlet.java
 *
 * Created on 13 czerwiec 2007, 00:24
 */

package explorer;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.swing.ImageIcon;

/**
 *
 * @author  Piotr Œlusarczyk, pslusarczyk@modasys.eu
 * @version 2007_06_24
 */
public class FileExplorerServlet extends HttpServlet {
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    FileList fl;

    private String url = "http://localhost:8084/FileExplorer_servlet/FileExplorerServlet";
    
    public static final String INIT = "$initialize$";
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String paramVal= INIT;

        Enumeration parameters = request.getParameterNames();
 
        for (;parameters.hasMoreElements();){

            String paramName = (String)parameters.nextElement();
            paramVal = request.getParameter(paramName);
   
        }
        
        if (paramVal.equals("$getServletPath$")){
            paramVal = getPath(url);
            
        } else if(paramVal.indexOf("$getBack$")>0){ 
    
            File f = new File(paramVal.substring(0, paramVal.indexOf("$getBack$")));
            paramVal = f.getParent();        

            if (paramVal == null){
                paramVal = "$initialize$";
            }else{
               paramVal = paramVal.replace('\\', '/'); 
            }
        }
        
        if (paramVal.indexOf(".mid")>0){            // jeœli plik midi
            
        response.setContentType("audio/midi");
        try{
            FileInputStream fis = new FileInputStream(paramVal);
            byte[] audioData = new byte[fis.available()];
            fis.read(audioData);
            if (audioData!=null){
                if (audioData.length>0){
                    ServletOutputStream sos = (ServletOutputStream)response.getOutputStream();
                    sos.write(audioData);
                    response.setContentLength(audioData.length);
                    sos.flush();
                    sos.close();
                }
            }
        }catch (Exception e){
        }
        
        }else if(paramVal.indexOf(".png")>0 || paramVal.indexOf(".jpg")>0){       // jeœli plik png
         
        response.setContentType("application/octet-stream");
        
        try{
            FileInputStream fis = new FileInputStream(paramVal);
            ImageIcon ii = new ImageIcon(paramVal);
            byte[] pngData = new byte[fis.available()];
            fis.read(pngData);
            if (pngData!=null){
                if (pngData.length>0){
                    ServletOutputStream sos = (ServletOutputStream)response.getOutputStream();
                    sos.write(pngData);
                    response.setContentLength(pngData.length);
                    response.setHeader("width",new Integer(ii.getIconWidth()).toString());
                    response.setHeader("height",new Integer(ii.getIconHeight()).toString());
                    sos.flush();
                    sos.close();
                }
            }
            
        }catch (Exception e){
        }  
             
        }else if(paramVal.indexOf(".txt")>0 || paramVal.indexOf(".dat")>0){       // jeœli plik textowy
            response.setContentType("text/html;charset=UTF-8");
            OutputStreamWriter osw = null;
        try {
            osw = new  OutputStreamWriter(response.getOutputStream(),"UTF-8");
            FileInputStream fis = new FileInputStream(paramVal);
            byte[] dane = new byte[fis.available()];
            fis.read(dane);
            String content = new String(dane,"UTF-8");
           
            response.setContentLength(content.getBytes("UTF-8").length);
            osw.write(content);
            osw.flush();
            osw.close();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
            
        }else if(paramVal.equals(INIT)){       // jeœli plik textowy
            
            fl = new FileList();
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();  
            out.print(fl.initialize(getPath(url)));
        }else{
            if (paramVal.indexOf(".")>0){           // jeœli plik nieznany
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print("ten plik jest nierozpoznawalny".trim());
                out.close();
            }else{                                  // jeœli katalog
                fl = new FileList();
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print(fl.reply(paramVal));
                out.close();
            }
        }
    }
    
    String getPath(String url){
            String longPath = this.getServletContext().getRealPath(url);
            longPath = longPath.substring(0, longPath.indexOf("\\http:")); 
            return longPath.replace('\\', '/');
    }
  
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}

    class AlphabeticComparator implements Comparator{
        public int compare(Object o1, Object o2) {
            String s1 = (String)o1;
            String s2 = (String)o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }

    }

    class DirFilter implements FilenameFilter{
      private Pattern pattern;
      
      public DirFilter(String regex){
          pattern = Pattern.compile(regex);
      }
      
      public boolean accept(File dir, String name){
          return pattern.matcher(new File(name).getName()).find();
      }
      
    }
