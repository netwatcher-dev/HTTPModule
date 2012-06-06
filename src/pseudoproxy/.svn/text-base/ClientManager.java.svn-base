/*
                    GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.

                            Preamble

  The GNU General Public License is a free, copyleft license for
software and other kinds of works.

  The licenses for most software and other practical works are designed
to take away your freedom to share and change the works.  By contrast,
the GNU General Public License is intended to guarantee your freedom to
share and change all versions of a program--to make sure it remains free
software for all its users.  We, the Free Software Foundation, use the
GNU General Public License for most of our software; it applies also to
any other work released this way by its authors.  You can apply it to
your programs, too.

  When we speak of free software, we are referring to freedom, not
price.  Our General Public Licenses are designed to make sure that you
have the freedom to distribute copies of free software (and charge for
them if you wish), that you receive source code or can get it if you
want it, that you can change the software or use pieces of it in new
free programs, and that you know you can do these things.

  To protect your rights, we need to prevent others from denying you
these rights or asking you to surrender the rights.  Therefore, you have
certain responsibilities if you distribute copies of the software, or if
you modify it: responsibilities to respect the freedom of others.
*/
package pseudoproxy;

import module.moduleHTTP;
import httpObject.Request;
import httpObject.RequestInterface;
import httpObject.ResponseInterface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author ben
 */
public class ClientManager extends Thread
{
    private Socket socket;
    private Map<RequestInterface, ResponseInterface> map;
    private boolean found;
    
    public ClientManager(Socket socket, Map<RequestInterface, ResponseInterface> map)
    {
        this.socket = socket;
        this.map = map;
    }

    @Override
    public void run() 
    {
        try 
        {
            found = false;
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));            
            Request request = new Request();
            //request.setToProxy(true);
            boolean newReq = true;
            boolean getReq = false;

            while(true)
            {
                if(socket.isClosed())
                    break;

                String line = br.readLine();
                
                if (line == null)
                    break;
                
                
                if (line.equals(""))
                {
                    manageRequest(request, socket.getOutputStream());
                    request = new Request();
                    //request.setToProxy(true);
                    newReq = true;
                    continue;
                }
                
                if(newReq)
                {
                    newReq = false;

                    String result [] = line.split("\\s");
                    
                    if(result.length < 3)
                    {
                        System.out.println("invalid request ");
                        getReq = false;
                        continue;
                    }
                    
                    getReq = result[0].equals("GET");
                    request.setTarget(result[1]);
                    request.setHttpVersion(result[2]);
                    
                    if(module.moduleHTTP.redirect_map.containsValue(request.getCompleteTarget()))
                    {
                        for(Entry<String,String> s : module.moduleHTTP.redirect_map.entrySet())
                        {
                            if(s.getValue().equals(request.getCompleteTarget()))
                                request.setTarget("http://"+s.getKey());
                        }  
                    }
                    
                }
                else if(getReq)
                {
                     request.parseParam(line);
                     if(request.isModified())
                     {
                        Writer out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.write("HTTP/1.1 304 Not Modified\r\n");
                        out.write("Connection: keep-alive\r\n");
                        out.write("\r\n");
                        out.flush();
                     }
                }
                else
                {
                    System.out.println("not a get request ");
                }
            }            
        } 
        catch (IOException ex) 
        {
            System.out.println("Connection reset");
        }
        
    }
    
    private void manageRequest(Request request, OutputStream os) throws IOException
    {
        while(true)
        {
            if(map.containsKey(request))
            {
                byte [] Buffer = new byte[1024];
                ResponseInterface ri = map.get(request);
                InputStream is = ri.getInputStream();

                int readed;
                while( (readed = is.read(Buffer)) >= 0)
                {
                    os.write(Buffer, 0, readed);                
                }
                os.flush();
                os.close();
                //map.remove(request);
                break;
            }
            else
            {
                try {
                    moduleHTTP.waiting_map.put(request, this);
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    if(found == true)
                    {
                        moduleHTTP.waiting_map.remove(request);
                        continue;
                    }
                }
             
                Writer out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


                String msg = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">"
                         +"<html><head>\n"
                         +"<title>404 Not Found</title>\n"
                         +"</head><body>\n"
                         +"<h1>Pseudo Proxy : Not Found</h1>\n"
                         +"<p>The requested URL "+request+" was not found on this server.</p>\n"
                         +"</body></html>";

                out.write("HTTP/1.1 404 Not Found\r\n");
                out.write("Content-Length: "+msg.length()+"\r\n");
                out.write("Connection: close\r\n");
                out.write("Content-Type: text/html; charset=iso-8859-1\r\n");

                out.write("\r\n");
                out.write(msg);
                out.flush();
                
                break;
                
            }
        }
    }
    
    public synchronized  void eventFound()
    {
        found = true;
        this.interrupt();
    }
}
