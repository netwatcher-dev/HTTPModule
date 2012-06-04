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

import java.net.MalformedURLException;
import java.net.URL;

public class Post extends HttpMessage
{
    private String target;
    private String httpVersion;
    private boolean toProxy;
    
    public Post()
    {   
        toProxy = false;
    }

    public Post(String targ, String ver) 
    {
        this();
        this.target = targ;
        this.httpVersion = ver;
    }
    
    
    public void setTarget(String target)
    {   
        try 
        {
            URL u = new URL(target);
            this.target = u.getFile();
            this.params.put(HOST, u.getHost());
        } 
        catch (MalformedURLException ex) 
        {
            this.target = target;
        }
        
        
    }
    
    public String getTarget()
    {
        return target;
    }
    

    public int getContentLength()
    {
        if(params.containsKey(ResponseInterface.CONTENT_LENGTH))
        {
            return Integer.parseInt(params.get(ResponseInterface.CONTENT_LENGTH));
        }
        else
        {
            return -1;
        }
    }
    
   
    public String getHost()
    {
        if(this.params.containsKey("Host"))
            return this.params.get("Host");
        else
            return "Unknown host";
    }

    /**
     * @return the httpVersion
     */
    public String getHttpVersion() {
        return httpVersion;
    }

    /**
     * @param httpVersion the httpVersion to set
     */
    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
}
