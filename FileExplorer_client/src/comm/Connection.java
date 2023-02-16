/*
 * Connection.java
 *
 * Created on 14 czerwiec 2007, 01:38
 *
 */

package comm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * @author  Piotr Œlusarczyk, pslusarczyk@modasys.eu
 * @version 2007_06_24
 */
public class Connection {
    
    /** Creates a new instance of Connection */
    public Connection() {
    }
  
    
public static final String callServlet(String url) throws IOException,UnsupportedEncodingException {
        HttpConnection http = null;
        DataInputStream iStrm = null;
        try {
            http = (HttpConnection) Connector.open(url);
            http.setRequestMethod(HttpConnection.GET);
            if (http.getResponseCode() == HttpConnection.HTTP_OK) {
                iStrm = new DataInputStream(http.openInputStream());
                int length = (int) http.getLength();
                if (length > 0) {
                    byte servletData[] = new byte[length];
                    iStrm.read(servletData);
                    return new String(servletData,"UTF-8");
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (iStrm != null)
                iStrm.close();
            if (http != null)
                http.close();
        }
        return null;
    }    
    
}
