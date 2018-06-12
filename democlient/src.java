package democlient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
public class src {
	static URL url;
	public static String getBody(HttpURLConnection request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
	public static void main(String[] args) throws IOException
	{
		//String address = "104.154.197.139";
		String samplejson = "{\"list\": \"apple,Banana,Orange\", \"locX\": 33.64859, \"locY\": -117.8399, \"radius\": 5}";
		String address = "localhost";
		url=new URL("http://"+address+":8080/middleserver/listServlet");
		try
		{
		HttpURLConnection connection =  (HttpURLConnection)url.openConnection(); 
		//jsonEntity js=new jsonEntity("From client",24);
		JSONObject j = new JSONObject();
		//j.put("name", "From client");
		//j.put("age", 24);
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setDefaultUseCaches (false);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("name", "xyz");
		
		ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
        objOut.writeObject(samplejson/*j.toString()*/);
        objOut.flush();
        objOut.close();
        int status = connection.getResponseCode();
        String response = getBody(connection);
		System.out.println("status : " + status);
		System.out.println("received response : "+ response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}
