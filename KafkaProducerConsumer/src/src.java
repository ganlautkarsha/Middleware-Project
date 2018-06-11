//package democlient;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
public class src {
	static URL url;
	public static void main(String[] args) throws IOException
	{
		url=new URL("http://35.188.26.231:8080/Server/servlet");
		try
		{
		HttpURLConnection connection =  (HttpURLConnection)url.openConnection(); 
		JSONObject j = new JSONObject();
		j.put("item", "apple");
		j.put("price", 60);
		j.put("storename", "walmart");
		j.put("date", "10-10-2018");
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setDefaultUseCaches (false);
		connection.setRequestProperty("Content-Type", "application/json");
//		connection.setRequestProperty("name", "xyz");
		
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
