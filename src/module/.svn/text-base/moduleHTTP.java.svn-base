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
package module;

import demultiplexer.Demultiplex;
import httpObject.RequestInterface;
import httpObject.ResponseInterface;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import profil_ff.FirefoxProfile;
import pseudoproxy.ClientManager;
import pseudoproxy.Server;


public class moduleHTTP 
{
    /* Map where are stored HTTP Request that have no yet HTTP Response */
    public static Map<RequestInterface, ClientManager> waiting_map = Collections.synchronizedMap(
            new HashMap<RequestInterface, ClientManager>());
    /* Map where are stored HTTP Request - HTTP Response */
    public static Map<RequestInterface, ResponseInterface> map = Collections.synchronizedMap(
            new HashMap<RequestInterface, ResponseInterface>());
    /* Map where are HTTP response 301 and 302, in order to retreive the old URL before the redirection */
    public static Map<String, String> redirect_map = Collections.synchronizedMap(
            new HashMap<String, String>());
    /* Ordered list of HTTP Request */
    public static List<RequestInterface> time_list = Collections.synchronizedList(
            new LinkedList<RequestInterface>());
    /* TextArea allowing to show information about POST Request */
    public static JTextArea area = new JTextArea();
    
    public static boolean debug_mode;
    
    public static void main(String[] args) 
    {     
        int port_number = -1; 
        InetAddress ip_address = null;
        debug_mode = false;
        
        if(args.length == 0)
        {
            System.out.println("Usage: -ip <ip> -port <port number> Connection to the core.");
            return;
        }
        else
        {
            for(int i = 0; i < args.length; i++)
            {
                if(args[i].equals("-d"))
                {
                    debug_mode = true;
                }else if(args[i].equals("-ip"))
                {
                    try {
                        ip_address = InetAddress.getByName(args[i+1]);
                    } catch (UnknownHostException ex) {
                        System.out.println("Usage: need a valid ip address");
                        System.exit(-1);
                    }
                }else if(args[i].equals("-port"))
                {
                    try{
                        port_number = Integer.parseInt(args[i+1]);
                    if(port_number < 0 || port_number > 65535)
                    {
                       System.out.println("Usage: port number has to be in the range 1..65535");
                       System.exit(-1); 
                    }
                    }catch(NumberFormatException e)
                    {
                        System.out.println("Usage: the argument has to be a port number");
                        System.exit(-1);
                    }
                }
            }
            
            if(ip_address == null)
            {
                System.out.println("Usage: need a valid ip address");
                System.exit(-1);  
            }
            
            if(port_number == -1)
            {
                System.out.println("Usage: need a port number");
                System.exit(-1);  
            }
            
        }

               
        
        
        /* Information treatment */
        try 
        {
            new Thread(new Demultiplex(new BufferedInputStream(new Socket(ip_address, port_number).getInputStream()), map, time_list)).start();          
            
        } 
        catch (UnknownHostException ex) 
        {
            System.err.println("UnknownHostException: "+ex.getMessage());
            System.exit(-1);
        } 
        catch (IOException ex) 
        {
            System.err.println("Socket connection error: "+ex.getMessage());
            System.exit(-1);
        }
        
        /* Starting Firefox */
        final Process ff_process = new FirefoxProfile().StartFirefox();
        if(ff_process == null)
        {
            System.exit(-1);
        }
        
        System.out.println("Connected to IP : "+ip_address+", port : "+port_number);
        
        /* Starting the proxy server (Browser side) */
        final Server serv = new Server(1200, map);
        serv.start();
        
        /* GUI POST CONSOLE */
        SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				//On crée une nouvelle instance de notre JDialog
				JDialog dialog = new JDialog();
                                JScrollPane pane = new JScrollPane(area);
				dialog.setSize(400, 300);
				dialog.setTitle("MODULE HTTP POST Console"); 
                                area.setRows(15);
                                area.setColumns(40);
                                dialog.add(pane);
                                area.setEditable(false);
                                dialog.pack();
				dialog.setVisible(true);
				dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                area.append("Module started! Enjoy.\n");
			}
	});
        
        /* Kill Mozilla Firefox at the end */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                ff_process.destroy();
                serv.stopServer();
            }
            });
    }
}
