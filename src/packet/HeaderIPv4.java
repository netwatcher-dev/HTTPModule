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

public class HeaderIPv4 extends IP
{
    public HeaderIPv4()
    {   
    }
    
    @Override
    public int getHeaderLength()
    {
        byte b = bytes[0];            
        return ((b&0x0F)*4);
    }
    
    @Override
    public int getTotalLength()
    {
        int i = (bytes[2]&0xff)<<8 | (bytes[3]&0xff);
        return i;
    }
    
    @Override
    public String getIpSource()    
    {
        return (bytes[12]&0xff) +"."+(bytes[13]&0xff)+"."+(bytes[14]&0xff)+"."+(bytes[15]&0xff);
    }
    
    @Override
    public String getIpDestination()
    {
        return (bytes[16]&0xff) +"."+(bytes[17]&0xff)+"."+(bytes[18]&0xff)+"."+(bytes[19]&0xff);
    }
    
    @Override
    public int getSizeOfOptionData()
    {
        return (int) (getHeaderLength()-HEADER_SIZE_V4);
    }

    @Override
    public int getPayloadLength() {
        return getTotalLength()-getHeaderLength();
    }

    @Override
    public int getProtocol() {
        int i = (bytes[9]&0xff);
        return i;
    }
       
}