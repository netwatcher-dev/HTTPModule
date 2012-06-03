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
package utils;

import java.io.EOFException;
import java.util.LinkedList;
import packet.IpPacket;

public class MyReaderV2
{   private LinkedList<byte[]> queue;
    private byte[] stream;
    private byte stream_to_read;
    private int marker;
    private final Object sync, sync2;
    private int paquetCount;
    private int fin;

    /* Contructor */
    public MyReaderV2()
    {
        marker = 0;
        sync = new Object();
        sync2 = new Object();
        paquetCount = 0;
        fin = 0;
        queue = new LinkedList<byte[]>();
    }     
    
    /* Read a sequence of char*/
    public String readLine() throws EOFException, InterruptedException
    {   
        StringBuilder builder = new StringBuilder();
        char c;
        int a;
        while(true)
        {   
            
            if((a = this.read()) == 0x0d) /* \r */
                if((a = this.read()) == 0x0a) /* \n */
                    break;
            builder.append((char)a);
            
        }
        return builder.toString();
    }
    
    /* Read one byte */
    public int read() throws EOFException, InterruptedException
    {   
        synchronized(sync)
        {
            if(queue.isEmpty())
            {
                sync.wait();
                return read();            
            }
        
            if(queue.isEmpty())
            {
                sync.wait();                
            }
            
            stream_to_read = queue.getLast()[marker++];
            
            if(marker == queue.getLast().length)
            {
                queue.removeLast();
                marker = 0;
            }
            return stream_to_read;
        }
    }
    
    public boolean addByte(IpPacket ipp)
    {   
        synchronized(sync)
        {
            if(ipp.getPayload().length > 0)
            {
                stream = new byte[ipp.getPayload().length];
                System.arraycopy(ipp.getPayload(), 0, stream, 0, ipp.getPayload().length);
                queue.addFirst(stream);
                sync.notify();
            }
        }
        
        return false;
    }
    
    public boolean isFirstPaquet()
    {
        return this.paquetCount < 2 ;
    }

    public void close() 
    {
        fin = 2;
        synchronized(sync)
        {
            sync.notify();
        }
    }
}
