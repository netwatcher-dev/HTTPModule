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
package profil_ff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;

public class FirefoxProfile {
    
    public FirefoxProfile()
    {       
    }
    
    
    public Process StartFirefox()
    {   
        String os = System.getProperty("os.name");
        System.out.println("OS: "+os);
        if("Mac OS X".equals(os))
            return startFirefoxOnMacOsX();
        else if("Linux".equals(os))
            return startFirefoxOnLinux();
        else
            return startFirefoxOnUnknownOS();
        
    }
    
    private Process startFirefoxOnMacOsX()
    {
        String [] ex = new String[3];
        ex[0] = "/Applications/firefox.app/Contents/MacOS/Firefox-bin";
        ex[1] = "-profile";
        ex[2] = SetUpProfile();
        try {
            return java.lang.Runtime.getRuntime().exec(ex);
        } catch (IOException ex1) {
            
            JOptionPane.showMessageDialog(null,"Mozilla Firefox not found","Firefox",JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private Process startFirefoxOnLinux()
    {
        String [] ex = new String[3];
        ex[0] = "firefox";
        ex[1] = "-profile";
        ex[2] = SetUpProfile();
        try {
            return java.lang.Runtime.getRuntime().exec(ex);
        } catch (IOException ex1) {
            
            JOptionPane.showMessageDialog(null,"Mozilla Firefox not found","Firefox",JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private Process startFirefoxOnUnknownOS()
    {
        return startFirefoxOnLinux();
    }
    
    private String SetUpProfile()
    {
        InputStream input = FirefoxProfile.class.getResourceAsStream("extension_ff.zip");  
        try {
            return unzip(input, System.getProperty("java.io.tmpdir"));
        } catch (IOException ex) {
            System.err.println("Creation Firefox Profile failed : "+ex.getMessage());
            return "";
        }
    }
    
    private static String unzip(InputStream zipStream, String dir) throws IOException{
        
        File folder = new File(dir);
        File root = null;

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipStream));
        ZipEntry ze = null;
        try {
            while((ze = zis.getNextEntry()) != null){

                File tmp = new File(folder.getCanonicalPath(), ze.getName());
                
                if(root == null)
                    root = new File(folder.getAbsolutePath(), ze.getName());
                
                /* Directory creation */
                if (ze.isDirectory()) {
                    tmp.mkdirs();
                    continue;
                }
               
                /* File creation */
                tmp.getParentFile().mkdirs();
                OutputStream fos = new BufferedOutputStream(new FileOutputStream(tmp));
           
                
                try {
                    try {
                        byte[] buf = new byte[8192];
                        int bytesRead;
                        while (-1 != (bytesRead = zis.read(buf)))
                            fos.write(buf, 0, bytesRead);
                    }
                    finally {
                        fos.close();
                    }
                }
                catch (IOException ioe) 
                {
                    tmp.delete();
                    throw ioe;
                }
            }
        }
        finally {
            zis.close();
        }
        
        return root.getAbsolutePath();
    }
}

