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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import reconstitution.HttpRequestResponse;

public class ThreadWatcher implements Runnable{
    private Map<String, HttpRequestResponse> connectionsMap;
    private List<String> toBeDeleted;
    public ThreadWatcher(Map<String, HttpRequestResponse> connectionsMap) {
        this.connectionsMap = connectionsMap;
        this.toBeDeleted = new LinkedList<String>();
    }


    
    @Override
    public void run() 
    {
        long my_time;
        
        while(true)
        {
            try 
            {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            
             my_time = System.currentTimeMillis();

            for(Entry<String,HttpRequestResponse> e:  connectionsMap.entrySet())
            {
                if(my_time - e.getValue().thread_inspector > 15000)
                {  
                    e.getValue().thisone.interrupt();
                    connectionsMap.remove(e.getKey());           
                }
            }
        }
    }
    
}
