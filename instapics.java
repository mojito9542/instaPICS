
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 *
 * @author Mojito9542
 * @version 1.0
 * @since 03/05/2018
 * 
 */
public class instapics {
    private Document doc;
    
    public String getJson(String url) throws IOException
    {
       doc = Jsoup.connect(url).timeout(10000).get();

       return doc.select("script").get(2).toString().substring(52);
    }
    public static void saveImage(String imageUrl,String path) throws IOException {
	URL url = new URL(imageUrl);
	String fileName = url.getFile();
	String destName = path + fileName.substring(fileName.lastIndexOf("/"));
        InputStream is = url.openStream();
	OutputStream os = new FileOutputStream(destName);
 
	byte[] b = new byte[2048];
	int length;
 
	while ((length = is.read(b)) != -1) {
		os.write(b, 0, length);
	}
 
	is.close();
	os.close();
}
    public static void main(String[] args) throws IOException, ParseException {
        
        System.out.println("ENTER THE USERNAME");
        String s=new Scanner(System.in).next();
        String url ="https://www.instagram.com/"+s;
        
        String res= new instapics().getJson(url);
        int  l=res.length();
        res= res.substring(0, l-10);
        JSONParser parser = new JSONParser();
       JSONObject json = (JSONObject) parser.parse(res);
       JSONObject entry=(JSONObject)json.get("entry_data");
        JSONArray profile=(JSONArray)entry.get("ProfilePage");
        JSONObject user=(JSONObject)profile.get(0);
        JSONObject obj=(JSONObject)user.get("graphql");
        JSONObject req=(JSONObject)obj.get("user");
        String name=(String) req.get("full_name");
        Boolean isPrivate=(Boolean) req.get("is_private");
        System.out.println(name+"\n"+isPrivate);
        if(isPrivate)
            System.out.println("Full name: "+name+"\nThe account is private....");
        else
        {
            req= (JSONObject)req.get("edge_owner_to_timeline_media");
            JSONArray edges=(JSONArray)req.get("edges");
            int i=0;
            String path= System.getProperty("user.home") + "/Desktop/"+name;

            File dir = new File(path);
    
    // attempt to create the directory here
    boolean successful = dir.mkdir();
    if (successful)
    {
      // creating the directory succeeded
      System.out.println("directory was created successfully");
    }
    else
    {
      // creating the directory failed
      System.out.println("path already exist");
    }
            for(Object j:edges)
            {
               JSONObject ob=(JSONObject)j;
                ob=(JSONObject)ob.get("node");
                String link="";
                link=(String)ob.get("display_url");
                saveImage(link,path); 
                i++;
                System.out.println("image downloaded "+(i)+" out of "+edges.size());
            }
           
        }
       
    }
    
    
}
