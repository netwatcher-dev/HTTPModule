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
package packet;

import exception.SyncException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


public class IpPacket
{
    private InputStream in;
    private IP hipV4,hipV6;
    private HeaderTcp htcp;
    private byte[] payload;
    private byte version;
    private int payloadLength;
    public int protocol;
    

    public IpPacket(InputStream in)
    {
        this.in = in;
        this.hipV4 = new HeaderIPv4();
        this.hipV6 = new HeaderIPv6();
        this.htcp = new HeaderTcp();
    }
    
    /**
     * This method reads a packet coming from the extractor system. 
     * Only IPv4/v6 and TCP protocols are accepted. 
     * The payload of TCP is extracted at the end.
     * @return true if the packet is correctly readed, false otherwise 
     * @throws IOException
     * @throws SyncException 
     */
    public boolean readPacket() throws IOException, SyncException
    { 
        byte[] bytes = new byte[20];
        
        this.read(bytes,0,20); /* Header IPv4 or beginning of header IPv6 */
        
        version = bytes[0]; /* IP VERSION */

        if( (version&0xF0) == 0x40) /* IPv4 */
        {
            /*Reading IP Header*/   
            hipV4.loadData(bytes);

            /*Skip IP Header option*/
            if(hipV4.getHeaderLength() != 20)
                this.skip(hipV4.getSizeOfOptionData());  
            
            /*Assigning the contained protocol */
            protocol = hipV4.getProtocol();
            payloadLength = hipV4.getPayloadLength();
        }
        else if( (version&0xF0) == 0x60) /* IPv6 */
        {
            byte[] bytes2 = new byte[40]; /* Adding previous array of byte */
            System.arraycopy(bytes, 0, bytes2, 0, bytes.length);
            
            /*Reading IP Header*/
            hipV6.loadData(this.read(bytes2,20,20)); /* End of Header IPv6 */
            
            /*Assigning the contained protocol */
            protocol = hipV6.getProtocol();
            payloadLength = hipV6.getPayloadLength();
        } 
        else
        {
            throw new SyncException("IP Version is incorrect. Probably a desynchronization over the network"); 
        }
        
        if(protocol != 6) /* IF not TCP */
        {
            this.skip(payloadLength);
            return false;
        }
        
        byte[] bytes2 = new byte[20];
        
        /* Reading TCP Header*/   
        htcp.loadData(this.read(bytes2,0,20));    
        
        /*Skip TCP Header option*/
        if(htcp.getDataOffset() != 20)
            this.skip(htcp.getSizeOfOptionData());          

        /*Reading Payload*/
        if((version&0xF0) == 0x40) /* IPv4 */
            payload = new byte[hipV4.getPayloadLength()-htcp.getDataOffset()];
        else /* IPv6 */
            payload = new byte[hipV6.getPayloadLength()-htcp.getDataOffset()];
        
        this.read(payload,0,payload.length);
        return true;
        
    }
    
    /**
     * 
     * @param bytes     readed bytes are placed here.
     * @param start     starting index in the array of readed bytes
     * @param size      number of element to read
     * @return          readed bytes.
     * @throws IOException 
     */
    public byte[] read(byte[] bytes,int start, int size) throws IOException
    {
        int off,off_bis;
        
        off = in.read(bytes,start,size);
        if(off < 0)
            throw new EOFException("No more data in the stream");      

        while(off < size)
        {
            off_bis =  in.read(bytes,off,size-off);
            if(off_bis < 0)
                throw new EOFException("No more data in the stream"); 
            off += off_bis;
        }
        
        return bytes;
    }
    
    /**
     * This method skips a fixed number of bytes from the socket.
     * @param size     number of bytes to skip.
     * @throws IOException 
     */
    public void skip(int size) throws IOException
    {
        long off, off_bis;
        
        off = in.skip(size);
        if(off == 0)
            throw new EOFException("No more data in the stream");  

        while(off < size)
        {
            off_bis = in.skip(size-off);
            if(off_bis == 0)
                throw new EOFException("No more data in the stream"); 
            off += off_bis;
        }
    }
    
    
    public IP getHeaderIP()
    {
        if((version&0xF0) == 0x40) /* IPv4 */
            return hipV4;
        else /* IPv6 */
            return hipV6;
    }
    
    public HeaderTcp getHeaderTCP()
    {
        return htcp;
    }
    
    public byte[] getPayload()
    {
        return payload;
    }
    
    /* FOR DEBUG */
    public void readPayload()
    {
        for(int i=0;i<payload.length;i++)
        {
            System.out.println(Integer.toHexString(payload[i]&0xFF));
        }
    }
    
    public String toString()
    {
        if((version&0xF0) == 0x40) /* IPv4 */
            return "[ver4]"+((version&0xF0)>>4)
                    +":[ip.src]"+this.hipV4.getIpDestination()
                    +":[tcp.src]"+this.htcp.getSourcePort()
                    +":[ip.dst]"+this.hipV4.getIpSource()
                    +":[tcp.dst]"+this.htcp.getDestinationPort()
                    +":[proto]"+this.hipV4.getProtocol()
                    +":[ip_option]"+this.hipV4.getSizeOfOptionData()
                    +":[hl]"+this.hipV4.getHeaderLength()
                    +":[pl]"+this.hipV4.getPayloadLength()
                    +":[tl]"+this.hipV4.getTotalLength()
                    +":[tcp_option]"+this.htcp.getSizeOfOptionData()
                    +":[off]"+this.htcp.getDataOffset()
                    +":[seq]"+this.htcp.getSequenceNumber();
        else /* IPv6 */
            return "[ver6]"+((version&0xF0)>>4)
                    +":[ip.src]"+this.hipV6.getIpDestination()
                    +":[tcp.src]"+this.htcp.getSourcePort()
                    +":[ip.dst]"+this.hipV6.getIpSource()
                    +":[tcp.dst]"+this.htcp.getDestinationPort()
                    +":[proto]"+this.hipV6.getProtocol()
                    //+":"+this.hipV6.getSizeOfOptionData()
                    +":[hl]"+this.hipV6.getHeaderLength()
                    +":[pl]"+this.hipV6.getPayloadLength()
                    +":[tl]"+this.hipV6.getTotalLength()
                    +":[tcp_option]"+this.htcp.getSizeOfOptionData()
                    +":[off]"+this.htcp.getDataOffset()
                    +":[seq]"+this.htcp.getSequenceNumber();
    }
}
