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
package decision;

import config.ParameterManager;
import httpObject.Request;
import httpObject.Response;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class IA
{

    private ManageFF mFF; /* Management of Firefox */
    private int id; /* Number of tabulation in Firefox */
    private LinkedList<Tab> listTab; /* List of tabulation opened in firefox */
    private final Object object; /* Sync object */
    private List<String> blacklist; /* Blacklist */
    private int minimum_body_character; /* Minimum size of the body in a page HTML to be display */
    
    public IA()
    {      
        id = 0;
        listTab = new LinkedList<Tab>();
        object = new Object();
        
        blacklist = ParameterManager.getInstance().getBlackList();       
        minimum_body_character = ParameterManager.getInstance().getMinHtmlBodySize();

    }
    
   
    
    public synchronized void addEntry(Request req, Response res)
    {
            
            
//            /*
//             * PROCESSING TAB MANAGEMENT
//             */
//            if(!res.isHTML())
//            {
//                System.out.println("KO (Not HTML): "+req);
//            }
//             
//            if(res.getCodeResponse() != 200)
//            {
//                System.out.println("KO (Not code 200): "+req);
//            }
//            
//            if(!res.isHTMLDocument())
//            {
//                System.out.println("KO (No HTML balise): "+req);
//            }
//        
//            if(res.getSizeInBody() < minimum_body_character)
//            {
//                System.out.println("KO (Size not suffisant): "+res.getSizeInBody()+"  "+req);
//            }
//            
            if(res.isHTML() && res.getCodeResponse() == 200 && res.isHTMLDocument()  /*&& !res.isEmpty()*/ && res.getSizeInBody() >= minimum_body_character)
            {   
                 
                /* Connexion to firefox */
                try {           
                    mFF = new ManageFF();
                } catch (IOException ex) {
                    System.err.println("Connexion to Firefox extension failed, please start the Firefox extension");
                }
                
                boolean found = false;
                
                for(Tab t : listTab)
                {
                    if(isBlackListed(req))
                    {
                        found = true;
                        break;
                    }
                    /* 1) Included , not displayed */
                    if(t.isIncluded(req)){
                        found = true;
                        break;
                    }
                    /* 2) Refresh a tab */                  
                    if(t.isLinked(req)) 
                    {
                         mFF.openTab(req.toString(), t.id);                        
                         listTab.add(new Tab(req,res,t.id));
                         listTab.remove(t);
                         found = true;                   
                         break;              
                    } 
                }
                if(found == false)
                {
                    /* New tab */                 
                    id++;
                    mFF.openTab(req.toString(), id);
                    listTab.add(new Tab(req,res,id));
                }
            }
        
    }

    public void newResponseAvailable() 
    {
        synchronized(object)
        {
            object.notify();
        }
    }

    private boolean isBlackListed(Request req) 
    {     
        if(req.getCompleteTarget() != null)
        {
            for(String e : blacklist)
            {   
                if(req.getCompleteTarget().contains(e))
                {
                    System.out.println("Blacklisted: "+req.getCompleteTarget());
                    return true;
                }
            }
        }
        return false;
    }
}
