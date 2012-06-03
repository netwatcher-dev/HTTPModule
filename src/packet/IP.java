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

public abstract class IP 
{
    protected final int HEADER_SIZE_V4 = 20; 
    protected final int HEADER_SIZE_V6 = 40;
    protected byte[] bytes;
    
    public IP()
    {    
    }
    
    public static String getProtocolName(int num)
    {
        switch(num)
        {
            case 1 : return "ICMP";
            case 2 : return "IGMP";
            case 3 : return "GGP";
            case 4 : return "IPv4";
            case 5 : return "Internet Stream Protocol";
            case 6 : return "TCP";
            case 8 : return "EGP";
            case 9 : return "IGP";
            case 17: return "UDP";
            case 27: return "RDP";
            case 41: return "IPv6";
            case 58: return "ICMP for IPv6";
            case 88: return "EIGRP";
            case 89: return "OSPF";
            default: return "Unknown";     
        }

    }
    
    public void loadData(byte[] b)
    {
        bytes = b.clone();
    }
    
    public int getVersion()
    {
        return (int)((bytes[0]&0xF0)>>4);
    }
    
    public boolean isIPv4()
    {
        return (this.getVersion() == 4);
    }
    
    public boolean isIPv6()
    {
        return (this.getVersion() == 6);
    }
        
    public abstract int getHeaderLength();
    public abstract int getPayloadLength();
    public abstract String getIpSource();
    public abstract String getIpDestination();
    public abstract int getSizeOfOptionData();
    public abstract int getTotalLength();
    public abstract int getProtocol();
       
}
