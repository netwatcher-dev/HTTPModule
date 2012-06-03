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
package demultiplexer;

import exception.SyncException;
import decision.IA;
import reconstitution.HttpRequestResponse;
import packet.IpPacket;
import utils.ThreadWatcher;

import httpObject.RequestInterface;
import httpObject.ResponseInterface;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import packet.IP;



/**
 * This class is the starting point of the HTTP Module. Each IP packet is
 * stored in a list corresponding to a pair of ip source and ip destination. This
 * is a way to isolate the exchange between a client and a server.
 * 
 */
public class Demultiplex implements Runnable
{
    private static final Boolean REQUEST = true;
    private static final Boolean RESPONSE = false;
    
    private Map<String, HttpRequestResponse> connectionsMap;
    private InputStream in;
    private Map<RequestInterface, ResponseInterface> map;
    private List<RequestInterface> time_list;
    private IA ia;
    private HashMap<String, Boolean> activeStream;
    private boolean newAdress;
    
    public Demultiplex(InputStream in, Map<RequestInterface, ResponseInterface> map, List<RequestInterface> list)
    {
        this.in = in;
        this.map = map;
        this.connectionsMap = new ConcurrentHashMap<String, HttpRequestResponse>();
        this.ia = new IA(); 
        this.time_list = list;
        this.activeStream = new HashMap<String, Boolean>();
        this.newAdress = false;
    }
    
    @Override
    public void run()
    {  
        /* Run the ThreadWatcher */
        new Thread(new ThreadWatcher(connectionsMap)).start();
        /* Working loop */
        IpPacket ip = new IpPacket(in);
        while(true)
        {
            /* New Ip Packet */ 
            try 
            {
                if(!ip.readPacket()) /* Reading IP, TCP */  
                {
                    System.out.println("Packet ignored " +ip.protocol + "("+IP.getProtocolName(ip.protocol) +")");
                    continue;
                }
          
                if(module.moduleHTTP.debug_mode)
                {
                    System.out.println("Packet recieved from : "+ip.getHeaderIP().getIpSource() + " to :"+ip.getHeaderIP().getIpDestination());
                }
            }
            catch(EOFException eofe) /* EOF == Closed connection */
            {

                for( String k : connectionsMap.keySet())
                {
                     connectionsMap.get(k).getReaderRequest().close();
                     connectionsMap.get(k).getReaderResponse().close();
                }
                try {
                    in.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Demultiplex.class.getName()).log(Level.SEVERE, null, ex1);
                }
                System.err.println("End Of Stream: " + eofe.getMessage());
                System.exit(0);
                
            }
            catch (IOException ex) /* I/O Exception */
            {   
                try {
                    in.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Demultiplex.class.getName()).log(Level.SEVERE, null, ex1);
                }
                System.err.println("I/O exception: "+ex.getMessage());
                System.exit(0);
            }
            catch (SyncException ex) /* Synchronization Exception */
            {   
                try {
                    in.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Demultiplex.class.getName()).log(Level.SEVERE, null, ex1);
                }
                System.err.println("Synchronization exception: "+ex.getMessage());
                System.exit(0);
            }
            
            
            /* Stream information IP.src + TCP.src && IP.dst + TCP.dst */
            String part1 = ip.getHeaderIP().getIpSource()+ip.getHeaderTCP().getSourcePort();
            String part2 = ip.getHeaderIP().getIpDestination()+ip.getHeaderTCP().getDestinationPort();
            
            /* This stream has been already identified?*/
            if(ip.getHeaderTCP().isSyn() && !connectionsMap.containsKey(part1+part2) && !connectionsMap.containsKey(part2+part1))
            {
                activeStream.put(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination(), REQUEST);
                activeStream.put(ip.getHeaderIP().getIpDestination()+ip.getHeaderIP().getIpSource(), RESPONSE);
                this.newAdress = true;
            }
                
            
            /* Drop packet from an unknown stream (Unknown == no SYN flag) */
            if(!activeStream.containsKey(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()))
                continue;
            
            
            
            /* Association based on ip,port source and ip,port destination */
            if(connectionsMap.containsKey(part1+part2))
            {
                /*Add packet to the corresponding list*/
                HttpRequestResponse entry = connectionsMap.get(part1+part2);
                if(activeStream.containsKey(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()) && activeStream.get(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()) == REQUEST)
                {
                    if(entry.getReaderRequest().addByte(ip))
                        connectionsMap.remove(part1+part2);
                }
                else if(activeStream.containsKey(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()) && activeStream.get(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()) == RESPONSE)
                {
                    if(entry.getReaderResponse().addByte(ip))
                        connectionsMap.remove(part1+part2);
                }
            }
            else if(connectionsMap.containsKey(part2+part1))
            {      
                /*Add payload to the corresponding list*/
                HttpRequestResponse entry = connectionsMap.get(part2+part1);
                if(activeStream.containsKey(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()) && activeStream.get(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()) == REQUEST)
                {
                    if(entry.getReaderRequest().addByte(ip))
                        connectionsMap.remove(part1+part2);
                }
                else if(activeStream.containsKey(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()) && activeStream.get(ip.getHeaderIP().getIpSource()+ip.getHeaderIP().getIpDestination()) == RESPONSE)
                {
                    if(entry.getReaderResponse().addByte(ip))
                        connectionsMap.remove(part1+part2);
                }
            }
            else if(this.newAdress == true)
            {
                HttpRequestResponse hrr = new HttpRequestResponse(ia);
                connectionsMap.put(part1+part2,hrr);
                hrr.thisone = new Thread(hrr);
                hrr.thisone.start();                    
                this.newAdress = false;
                
                if(module.moduleHTTP.debug_mode)
                {
                    System.out.println("Thread started for "+ip.getHeaderIP().getIpSource() + "("+ip.getHeaderTCP().getSourcePort()+") - :"+ip.getHeaderIP().getIpDestination()+" ("+ip.getHeaderTCP().getSourcePort()+")");
                }
                
            }
        }
    }
}
