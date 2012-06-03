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
package reconstitution;

import utils.MyReaderV2;
import decision.IA;
import httpObject.Post;
import httpObject.Request;
import httpObject.Response;
import packet.IpPacket;
import config.ParameterManager;
import module.moduleHTTP;

import java.io.EOFException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class HttpRequestResponse implements Runnable
{
    private MyReaderV2 readerRequest;
    private MyReaderV2 readerResponse;
    private IA ia;
    private String IPServer;
    private Request request;
    private Response response;
    private Post post;
    private int max_item_size;
    private IpPacket ip;
    public long thread_inspector;
    public Thread thisone;
           
    
    public HttpRequestResponse(IA ia)
    {
        readerRequest = new MyReaderV2(); /* Request Reader */        
        readerResponse = new MyReaderV2(); /* Response Reader */
        
        this.ia = ia;
        
        this.max_item_size = ParameterManager.getInstance().getMapCapacity(); /* Initialisation of maximum map capacity */
        
        this.thread_inspector = System.currentTimeMillis(); /* Initialisation of time for the thread inspector*/
    }
    

    
    @Override
    public void run() 
    {
        boolean matched = false;
        /* Working loop */
        while(true)
        {
            thread_inspector = System.currentTimeMillis();
            try
            {
/*-------------- POST HEADER ------------*/   
                String s1 = readerRequest.readLine().trim(); 

                Pattern pattern = Pattern.compile("POST\\s(.*)\\s(HTTP/1\\..)");
                Matcher matcher = pattern.matcher(s1);

                if(matcher.matches())
                {
                    matched = true;

                    int b1;
                    int marker=0;
                    StringBuilder str = new StringBuilder();

                    post = new Post(matcher.group(1),matcher.group(2));

                    for(String s;!(s= readerRequest.readLine()).isEmpty();)
                        post.parseParam(s);

                    if(post.getContentLength() < 0) /* No data */
                        continue;

                    while(true)
                    {
                        b1 = readerRequest.read();                       
                        str.append((char)b1);
                        marker += 1;                      
                        if(marker == post.getContentLength())
                        {
                            /* Notification in the TextArea */
                            module.moduleHTTP.area.append("@ "+post.getHost()+" : ");
                            module.moduleHTTP.area.append(str.toString());
                            module.moduleHTTP.area.append("\n---\n");
                            module.moduleHTTP.area.setCaretPosition(module.moduleHTTP.area.getDocument().getLength());
                            break;
                        }
                    }

                    if(!manageResponse())
                        System.out.println("Error in manage Response POST");
                }

/*-------------- GET HEADER ------------*/
                pattern = Pattern.compile("GET\\s(.*)\\s(HTTP/1\\..)");
                matcher = pattern.matcher(s1);

                if(matcher.matches())
                {
                    matched = true;
                    request = new Request(matcher.group(1), matcher.group(2));
                    for(String s;!(s= readerRequest.readLine()).isEmpty();)
                    {
                        request.parseParam(s);
                    }

                    /* Host exists or not? IP if added instead of dns */
                    if(request.getHost().isEmpty())
                        request.setHost(IPServer);

                    if(!manageResponse())
                        System.out.println("Error in manage Response GET");
                }

                if(matched == true)
                    matched = false;

            }
            catch(EOFException eofe)
            {
                break;
            }
            catch(InterruptedException e)
            {
                break;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
          
              
        }
    }

    private void Completed()
    {
        /* If request == null, it's a Post */
        if(request == null) 
            return;
        
        /* CODE 301 or 302 Moved Permanently/Temporarily */
        if((response.getCodeResponse() == 301 || response.getCodeResponse() == 302)&& !response.getNewLocation().isEmpty())
        {
            String oldLoc = request.getCompleteTarget();
            request.setTarget(response.getNewLocation());
            moduleHTTP.redirect_map.put(request.getCompleteTarget(), oldLoc);
        }
        
        /* Link request in response */
        response.setRequest(request); 
        
        /* Special case for Authorization Basic Login and Password*/
        if(request.hasAuthorization())
        {
            module.moduleHTTP.area.append("@ "+request.getHost()+" : ");
            module.moduleHTTP.area.append("Authorization basic: "+request.getAuthorization());
            module.moduleHTTP.area.append("\n---\n");
            module.moduleHTTP.area.setCaretPosition(module.moduleHTTP.area.getDocument().getLength());
        }
        
        /* MAP IS FULL */
        while(module.moduleHTTP.time_list.size() >= max_item_size)
        {
            module.moduleHTTP.map.remove(module.moduleHTTP.time_list.remove(0));
        }
        
        /* MAP INSERTION */
        module.moduleHTTP.map.put(request, response); /* Insert in the map */
        module.moduleHTTP.time_list.add(request); /* Insert in the time line*/
        
        if(module.moduleHTTP.waiting_map.containsKey(request))
            module.moduleHTTP.waiting_map.get(request).eventFound();
        
        /* DECISION UNIT */
        ia.addEntry(request, response);
    }
    
    public boolean manageResponse() throws EOFException, InterruptedException, IOException
    {
        /*-------------- RESPONSE HEADER ------------*/
        if(!readHeaderResponse())
        {
            System.out.println("Header reading problem");
            return false;
        }

        /*-------------- RESPONSE PAYLOAD ------------*/
        if(!response.isChunked()) /* NOT CHUNKED */
        {
            return readContentResponse();
        }
        else /*CHUNKED*/
        {
             return readContentChunkedResponse();
        }

    }
    
    private boolean readContentResponse() throws IOException, InterruptedException
    {
        int b, marker = 0;
        if(response.getContentLength() < 0) /* Nothing to read */     
            return true;
        
        while(true)
        {
            b = readerResponse.read();                       
            response.getOutputStream().write(b);
            
            if(response.isHTML()) /* Keep data when HTML */
                response.addByteInRawData(b);
            
            marker += 1; /* Next byte */

            if(marker == response.getContentLength())
            {
                Completed(); /* Recieved all we need */
                return true;
            }
        }
    }
    
    private boolean readContentChunkedResponse() throws EOFException, InterruptedException, IOException
    {
        int b, marker;
        int totalsize = 0;
        while(true)
        {            
            /* Reading of the size of the chunk */
            StringBuilder size_str = new StringBuilder();

            marker = 0;
            int chunkSize = 0;
            while(true)
            {                      
                b = readerResponse.read();     
                response.getOutputStream().write(b);
                if((char)b != '\n')
                {
                    size_str.append((char)b);
                }
                else
                {
                    try{
                        //System.out.println("Chuncked hex"+size_str.toString());
                        chunkSize = Integer.parseInt(
                            size_str.toString().trim().replaceAll("[\r\n]+", ""), 16);
                        //System.out.println("Chuncked "+chunkSize);
                        break;
                    }
                    catch(NumberFormatException ex)
                    {
                        System.out.println("chunked problem");
                        return false;
                    }                                
                }                                       
            }

            /* Reached the end of the response when chunked size is 0 */
            if(chunkSize == 0)
            {
                response.setContentLength(""+totalsize);
                Completed();
                return true;
            } 

            /* Read chunk */
            while(true)
            {
                if(marker == chunkSize) /* Reached the end of the chunk */
                {
                    totalsize += marker;
                    response.getOutputStream().write(readerResponse.read()); /* Escape \r */
                    response.getOutputStream().write(readerResponse.read()); /* Escape \n */
                    break;
                }
                /* Reading the chunk */
                b = readerResponse.read();
                response.getOutputStream().write(b);
                
                if(response.isHTML()) /* Keep data when HTML */
                    response.addByteInRawData(b);
                
                marker +=1; /* Next byte */
                
            } 
        }
  
    }
    
    private boolean readHeaderResponse() throws EOFException, InterruptedException
    {
        String s = readerResponse.readLine().trim();
                
        while(s.isEmpty() || !s.matches(".*(HTTP/1\\..)\\s(\\d\\d\\d)(.*)"))  
           s = readerResponse.readLine().trim();

        Pattern pattern = Pattern.compile(".*(HTTP/1\\..)\\s(\\d\\d\\d)(.*)"); 
        Matcher matcher = pattern.matcher(s);

        response = new Response();

        if(matcher.matches())
        {   
            response.setCodeResponse(matcher.group(2));
            response.putRawHeader(matcher.group(1)+" "+matcher.group(2)+" "+matcher.group(3)); /* Begin of header */
            while(!(s = readerResponse.readLine().trim()).isEmpty()) /* All arguments */
            {
                response.parseParam(s);
                response.putRawHeader(s);
            }
            response.putRawHeader(""); /* End of header */ 

        }
        else
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * @return the reader
     */
    public MyReaderV2 getReaderRequest()
    {
        return readerRequest;
    }
    
    public MyReaderV2 getReaderResponse()
    {
        return readerResponse;
    }
}
