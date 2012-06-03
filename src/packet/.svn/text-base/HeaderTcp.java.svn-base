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

public class HeaderTcp
{
    private final float MIN_HEADER_SIZE = 20;
    private byte[]  bytes;  
    
    public HeaderTcp()
    {      
    }
    
    public void loadData(byte[] b)
    {
        bytes = b.clone();
    }
    
    public int getSourcePort()
    {
        int i = (bytes[0]&0xff)<<8;
        i += bytes[1]&0xff;
        return i;
    }
    
    public int getDestinationPort()
    {
        int i = (bytes[2]&0xff)<<8;
        i += bytes[3]&0xff;
        return i;
    }
    
    public long getSequenceNumber()
    {
       int i = ((0xff & bytes[4]) << 24) | ((0xff & bytes[5]) << 16) |
            ((0xff & bytes[6]) << 8) | (0xff & bytes[7]);
       return ((long)i)&0xFFFFFFFFL;
       
       
       
    }
    
    public boolean isSyn()
    {
        return (bytes[13] & 0x02) != 0;
    }
    
    public boolean isFin()
    {
        return (bytes[13] & 0x01) != 0;
    }
    
    /**
     * 
     * @return tcp header size (in byte)
     */
    public int getDataOffset()
    {
        byte b = bytes[12]; 
        
        b = (byte) (b>>4);
        b &= 0x0F;
        return (b*4);
    }
    
    public int getSizeOfOptionData()
    {
        return (int) (getDataOffset()-MIN_HEADER_SIZE);
    }
    
    public boolean isZeroChecksum()
    {
        //System.out.println("Byte 16 "+bytes[16]+" byte 17 "+bytes[17]);
        if(bytes[16] == 0 && bytes[17] == 0)
            return true;
        
        return false;
    }
    
}
