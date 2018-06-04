package democlient;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
public class src {
	static URL url;
	public static void main(String[] args) throws IOException
	{
		url=new URL("http://35.192.75.39:8080/middleserver/servlet");
		try
		{
		HttpURLConnection connection =  (HttpURLConnection)url.openConnection(); 
		jsonEntity js=new jsonEntity("xyz",20);
		JSONObject j = new JSONObject();
		j.put("name", "xyz");
		j.put("age", 20);
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setDefaultUseCaches (false);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("name", "xyz");
		
		ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
        objOut.writeObject(j.toString());
        objOut.flush();
        objOut.close();
        int status = connection.getResponseCode();
		System.out.println("status : " + status);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}
