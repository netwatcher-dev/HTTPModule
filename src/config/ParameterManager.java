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
package config;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


public class ParameterManager {
    /* Default values*/
    private static int MAX_MAP_CAPACITY = 200;
    private static int Min_BODY_SIZE = 50;
    private static String DEFAULT_IP_EXTRACTION = "127.0.0.1";
    
    private static ParameterManager self;
    private Properties prop;
    
    public ParameterManager() {
        try {
            prop = new Properties();
            prop.load(ParameterManager.class.getResourceAsStream("config.properties"));
        } catch (IOException ex) {
            prop = null; /* No file detected */
            System.err.println("Error in the loading of the properties files. Default values will be used");
        }
    }
    
    public static ParameterManager getInstance()
    {
        if(self == null)
            self = new ParameterManager();
        
        return self;
    }
    
    public int getMapCapacity()
    {
        try
        {
            if(prop != null && prop.containsKey("max_number_of_items_in_the_map"))
                return Integer.parseInt(prop.getProperty("max_number_of_items_in_the_map"));
        }
        catch(NumberFormatException ex)
        {
            System.err.println("NumberFormatException: Not an Integer value (max_number_of_items_in_the_map)");
        }

        return MAX_MAP_CAPACITY;
    }
    
    public int getMinHtmlBodySize()
    {
        try
        {
            if(prop != null && prop.containsKey("minimum_body_character"))
                return Integer.parseInt(prop.getProperty("minimum_body_character"));
        }
        catch(NumberFormatException ex)
        {
            System.err.println("NumberFormatException: Not an Integer value (minimum_body_character)");
        }

        return Min_BODY_SIZE;
    }
    
    public String getIpExtraction()
    {
        if(prop != null && prop.containsKey("ip_address_extraction_system"))
                return prop.getProperty("ip_address_extraction_system");
        
        return DEFAULT_IP_EXTRACTION;
    }
    
    public List<String> getBlackList()
    {
        List<String> bl = new LinkedList<String>();
        
        if(prop != null && prop.containsKey("blacklist"))
        {
            StringTokenizer tokens = new StringTokenizer(prop.getProperty("blacklist"), ";");
        
            while(tokens.hasMoreTokens()) 
            {
                bl.add(tokens.nextToken());
            } 
        }
        
        return bl;
        
    }
   
    
    
    
    
}
