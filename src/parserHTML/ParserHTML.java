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
package parserHTML;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ParserHTML
{
    private Document doc;
    
        
    public ParserHTML(String html)
    {
        doc = Jsoup.parse(html);
    }
    
    public List<String> getUri()
    {
        /*Extract xxx from  <a href="xxx" > </a>  */
        List<String> l = new LinkedList();
        
        Elements links = doc.select("a");
        for(Element e : links)
            l.add(e.attr("href"));

        /* <area> </area> */
        links = doc.select("area");
        for(Element e : links)          
            l.add(e.attr("href"));
        
        links = doc.select("form");
        for(Element e : links)          
            l.add(e.attr("action"));

        return l;
    }
    
    public List<String> getFrame()
    {
        /*<iframe src="" >*/
        List<String> l = new LinkedList();
        Elements frame = doc.select("iframe");
        
        for(Element e : frame)
            l.add(e.attr("src"));
        
        /*<frame src="">*/
        frame = doc.select("frame");
        
        for(Element e : frame)
            l.add(e.attr("src"));
        
        
        
        return l;
    }
    
    public List<String> getImg()
    {
        /*Extract xxx from  <img src="xxx" > </img>  */
        List<String> l = new LinkedList();
        Elements links = doc.select("img");
        
        for(Element e : links)
            l.add(e.attr("src"));

        return l;
    }
    
    public List<String> getLink()
    {
        /*Extract xxx from  <LINK href="xxx" />  */
        List<String> l = new LinkedList();
        Elements links = doc.select("link");
        
        for(Element e : links)
            l.add(e.attr("href"));

        return l;
    }
    
    public boolean isHTMLDocument()
    {
        if(doc.select("body").size() > 0 && doc.select("head").size() > 0)
            return true;
        
        return false;
    }
    
    
    public boolean hasEmptyBody()
    {
        
        String body = doc.select("body").toString();
        
        Pattern pattern = Pattern.compile("\\<script.*?\\</script\\>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(body);
        
        body = matcher.replaceAll("").replaceAll("<body>|</body>", "").replaceAll(" ", ""); /* Remove javascript between balise <script> </script>*/
        
        
        if(body.length() > 0)
            return false;
        else
            return true;
        
    }
    
    public int getSizeInBody()
    {   
        String body = doc.select("body").toString();
        Pattern pattern = Pattern.compile("\\<script.*?\\</script\\>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(body);
        
        body = matcher.replaceAll(""); /* Remove javascript between balise <script> </script>*/
        
        return body.length();
    }
   
    @Override
    public String toString()
    {
        return doc.toString();
    }
    
    
}
