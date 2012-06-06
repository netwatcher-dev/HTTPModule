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
package httpObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import parserHTML.ParserHTML;

public class Response extends HttpMessage implements ResponseInterface
{
    private String rawHeader;
    
    private File file;
    private ByteArrayOutputStream bytes;
    private OutputStream os;
    private int responseCode;
    private RawHTML rawHtml;
    private Request associetedRequest;
    
    public Response()
    {
        super();
        rawHeader = "";
        this.file = null;
        this.bytes = null;
        this.os = null;
        this.rawHtml = new RawHTML(this);
    }
    
    public void putRawHeader(String chunk)
    {
        rawHeader += chunk+"\r\n";
    }
    
    public String getRawHeader()
    {
        return rawHeader;
    }
        
    /**
     * Get the value of the field Content-Length in HTTP Response Header
     * @return -1 if Content-Length is not defined, the Content-Length otherwise
     */
    public int getContentLength()
    {
        if(params.containsKey(CONTENT_LENGTH))
        {
            return Integer.parseInt(params.get(CONTENT_LENGTH));
        }
        else
        {
            return -1;
        }
    }
    
    public String getContentType()
    {
        if(params.containsKey(CONTENT_LENGTH))
        {
            return params.get(CONTENT_TYPE);
        }
        else
        {
            return "";
        }
    }
    
    public void setContentLength(String len)
    {
        params.put(CONTENT_LENGTH, len);
    }
    
    public String getReferer()
    {
        if(params.containsKey(REFERER))
                return params.get(REFERER);
        return "";
    }
    
    public String getNewLocation()
    {
        if(params.containsKey(LOCATION))
        {   
            return params.get(LOCATION);
        }
        else
        {
            return "";
        }
    }
    
    public boolean isHTMLDocument()
    {
        if(/*this.isHTML() &&*/ this.getRawData().toLowerCase().contains("<head") 
                && this.getRawData().toLowerCase().contains("</head>")
                && this.getRawData().toLowerCase().contains("<body")
                && this.getRawData().toLowerCase().contains("</body>")
                && this.getRawData().toLowerCase().contains("<html")
                && this.getRawData().toLowerCase().contains("</html>")
                )
        {           
                 return true;
        }
        
        return false;
    }
    
    public Set<String> getURL()
    {
       Set<String> urlSet = new HashSet<String>();
       if(this.isHTML())
       {
             ParserHTML p = new ParserHTML(this.getRawData());
             
             for(String s : p.getUri())
             {
                 if(s.contains("#"))
                    s = s.substring(0, s.indexOf('#'));
                 
                 
                 urlSet.add(ensureAbsoluteURL(getAssociatedRequest().getCompleteTarget(),s));
 
             }        
       } 
       return urlSet;
    }
    
    public Set<String> getInclude()
    {
       Set<String> frameSet = new HashSet<String>();
       if(this.isHTML())
       {
             ParserHTML p = new ParserHTML(this.getRawData());
             
             for(String s : p.getFrame())
             {
                 if(s.contains("#"))
                    s = s.substring(0, s.indexOf('#'));
                 
                 
                 frameSet.add(ensureAbsoluteURL(getAssociatedRequest().getCompleteTarget(),s));
             }              
       } 
       return frameSet;
    }
    
    
    public boolean isEmpty()
    {
       if(this.isHTML())
       {
             ParserHTML p = new ParserHTML(this.getRawData());
             return p.hasEmptyBody();       
       } 
       return false;
    }
    
    public int getSizeInBody()
    {
       if(this.isHTML())
       {
             ParserHTML p = new ParserHTML(this.getRawData());
             return p.getSizeInBody();      
       }
       return -1;
    }
    
    public boolean isHTML()
    {
        if(params.containsKey(CONTENT_TYPE) 
                && params.get(CONTENT_TYPE).contains("text/html"))
            return true;
        return false;
    }
    
    public boolean isGZIP()
    {
        if(params.containsKey(CONTENT_ENCODING) 
                && params.get(CONTENT_ENCODING).contains("gzip"))
            return true;
        return false;
    }
    
    /**
     * 
     * @return true if the reponse is chunked, false otherwise
     */
    public boolean isChunked()
    {
        if(params.containsKey(TRANSFER_ENCODING) 
                && params.get(TRANSFER_ENCODING).contains("chunked"))
            return true;
        
        return false;
    }
    
    public void setCodeResponse(String code) {
        this.responseCode = Integer.parseInt(code);
    }

    public int getCodeResponse() {
        return this.responseCode;
    }
    
    public void addByteInRawData(int b)
    {
        rawHtml.addByte(b);
    }
    
    public String getRawData()
    {
        
        return rawHtml.toString();

    }
    
    @Override
    public InputStream getInputStream() throws IOException 
    {
        ByteArrayOutputStream baos = 
                new ByteArrayOutputStream(rawHeader.length()+getContentLength());
        
        if(this.bytes != null)
        {
            baos.write(rawHeader.getBytes());
            baos.write(this.bytes.toByteArray());       
        
            return new ByteArrayInputStream(baos.toByteArray());
        }
        else if(this.file != null)
        {
            return new FileInputStream(file);
        }
        else
        {
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException 
    {
        if(this.os != null)
            return this.os;
        
        if(this.isChunked() || getContentLength() > 16384)
        {
            this.file = File.createTempFile("tmp"+Math.random(), ".netwatcher");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(rawHeader.getBytes());
            this.os = fos;
            return fos;
        }
        else    
        {       
            this.bytes = new ByteArrayOutputStream(getContentLength());
            this.os = this.bytes;
            return this.bytes;
        }
    }
    
     public String ensureAbsoluteURL(String base, String maybeRelative) 
     {
        if (maybeRelative.startsWith("http")) 
        {
            return maybeRelative;
        } 
        else 
        {
            if(!base.startsWith("http"))
                base = "http://"+base;
            try 
            {
               return new URL(new URL(base), maybeRelative).toExternalForm();
            } 
            catch (MalformedURLException e) 
            {
               return "";
            }
        }

     }

    public void setRequest(Request request) {
        this.associetedRequest = request;
    }

    /**
     * @return the associetedRequest
     */
    public Request getAssociatedRequest() {
        return associetedRequest;
    }
    

    private class RawHTML 
    {
        private Response resp;
        private ByteArrayOutputStream byteArray;

        public RawHTML(Response r){
            this.resp = r;
            byteArray = new ByteArrayOutputStream();
        }

        public void addByte(int b)
        {
            byteArray.write(b);
        }

        public String toString()
        {
            /* Uncompressing data */
            if(resp.isGZIP())
            {
                try 
                {
                    GZIPInputStream gzipInputStream = 
                            new GZIPInputStream(
                                    new ByteArrayInputStream(byteArray.toByteArray()));
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = gzipInputStream.read(buf)) > 0) {
                            bytes.write(buf, 0, len);
                    }                   
                    
                    return bytes.toString();
                
                } catch (IOException ex) {
                    System.err.println("GZIP Uncompress problem:"+ex.getMessage());
                }
                
            }

            return byteArray.toString();           
        }    
    }
    
   
    
}

