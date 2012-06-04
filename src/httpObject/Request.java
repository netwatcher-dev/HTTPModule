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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.BASE64Decoder;

public class Request extends HttpMessage  implements RequestInterface
{
    private String target;
    private String httpVersion;
    private boolean toProxy;
    private boolean firstRequest;
      
    
    public Request()
    {   
        toProxy = false;
        firstRequest = false;
    }
    
    public Request(String targ, String ver)
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
    
   

    @Override
    public boolean equals(Object o) 
    {
        try{
            if(o instanceof Request)
            {
                Request req = (Request)o;
                if(req.params.containsKey(HOST) && this.params.containsKey(HOST) && !req.params.get(HOST).equals(this.params.get(HOST)))
                    return false;

                return req.target.equals(this.target);

            }
        }
        catch(Exception ex)
        {
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() 
    {
        int hash = 5;
        int targetInt = (this.target != null ? this.target.hashCode() : 0) ;
        int hostInt = (this.params.containsKey(HOST) ? this.params.get(HOST).hashCode() : 0);
        hash = 37 * hash + targetInt + hostInt;
        return hash;
     
    }
    
    
    public boolean hasAuthorization()
    {
        return this.params.containsKey("Authorization");
    }
    
    public String getAuthorization()
    {
        if(hasAuthorization())
        {   
            Pattern pattern = Pattern.compile("Basic (.*)");
            Matcher match = pattern.matcher(this.params.get("Authorization"));
            if(match.find())
            {   
                try {
                    byte[] decoder = new BASE64Decoder().decodeBuffer(match.group(1));
                    return new String(decoder);
                } catch (IOException ex) {
                    System.out.println("(request)Decode64 failed");
                }
            }
            
        }
        return "" ;
    }
    
    /**
     * @return the toProxy
     */
    public boolean isToProxy() 
    {
        return toProxy;
    }

    /**
     * @param toProxy the toProxy to set
     */
    public void setToProxy(boolean toProxy) 
    {
        this.toProxy = toProxy;
    }

    public String getTarget() {
        return target;
    }

    public String getHost()
    {
        String ret = "";
        if(this.params.containsKey(HOST))
            ret = params.get(HOST);
        
        return ret;
    }
    
    public String getReferer()
    {
        String ret = "";
        if(this.params.containsKey(REFERER))
        {
            try {
                URL u = new URL(params.get(REFERER));
                ret = u.getHost();
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    
    public void setHost(String IPServer) {
        this.params.put(HOST, IPServer);
        hasDnsHost = false;
    }
    

    
    
    /**
     * 
     * @return the url with the host/IP
     */
    public String getCompleteTarget()
    {        
        if(this.params.containsKey(HOST))
            return this.params.get(HOST)+this.target;
        else
            return this.target;
    }

    /**
     * @return the firstRequest
     */
    public boolean isFirstRequest() {
        return firstRequest;
    }

    /**
     * @param firstRequest the firstRequest to set
     */
    public void setFirstRequest(boolean firstRequest) {
        this.firstRequest = firstRequest;
    }

    @Override
    public String toString() 
    {
        return this.getCompleteTarget();
    }

    /**
     * @return the hasDnsHost
     */
    public boolean hasDnsHost() 
    {
        return hasDnsHost;
    }
    
    public boolean isModified()
    {
        return this.params.containsKey(IF_MODIFIED_SINCE);
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
