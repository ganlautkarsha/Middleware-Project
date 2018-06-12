

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class ConsumerServlet
 */
@WebServlet("/ConsumerServlet")
public class ConsumerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static ArrayList<String> subscribedItems=null;   
    static HashMap<String,JSONObject> updatedItems=null;
    SampleConsumer kfconsumer; 
    static boolean flag = false;
    //SampleConsumer
    /**
     * @see HttpServlet#HttpServlet()
     */
    public static void updateItems(String key, JSONObject value)
    {
    	if(updatedItems==null)
    		updatedItems = new HashMap<String,JSONObject>();
    	updatedItems.put(key, value);
    }
    public ConsumerServlet() {
        super();
        //kfconsumer.start();
        // TODO Auto-generated constructor stub
    }
    public static ArrayList<String> getList()
    {
    	return subscribedItems;
    }
    public static String putList(ArrayList<String> items)
    {
    	if(subscribedItems==null)
    	{
    		subscribedItems = new ArrayList<String>();
    	}
    	for(int i=0;i<items.size();i++) {
    		if(!subscribedItems.contains(items.get(i)))
    		{
    			subscribedItems.add(items.get(i));
    		}
    	}
    	BufferedWriter writer = null;
		try {
			String toWrite = "subs  "+subscribedItems.toString();
			//Runtime.getRuntime().exec("sudo echo \"" + toWrite + "\" > /home/ganla_utkarsha/looog/log_list");
			writer = new BufferedWriter(new FileWriter("D:\\log\\log_list.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
		try {
			writer.append("subs  "+subscribedItems.toString());
			writer.append("\n\n\n");
			if(updatedItems!=null)
				writer.append("ups   "+updatedItems.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return e.getMessage();
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return e.getMessage();
		}
		return "ok";
    }
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
    public static String getBody(HttpServletRequest request) throws IOException {

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
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		if(flag==false)
		{
			flag=true;
			kfconsumer= new SampleConsumer();
			kfconsumer.start();
		}
		try
		{
		JSONObject resp = new JSONObject();
		String body = getBody(request);
		String req = body.substring(7);
		JSONObject inputList = new JSONObject(req);
		String itemstr = inputList.getString("list");
		System.out.println("in do get"+itemstr);
		ArrayList<String> items = new ArrayList<String>(Arrays.asList(itemstr.split(",")));
		int updatecount=0;
		for(int i=0;i<items.size();i++)
		{
			if(updatedItems.containsKey(items.get(i)))
			{
				resp.put(items.get(i), updatedItems.get(items.get(i)).toString());
				updatecount++;
			}
		}
		if(updatecount!=0)
			response.getWriter().append(resp.toString());
		else
			response.getWriter().append("No updates");
		}
		catch (Exception e) {
			// TODO: handle exception
			response.getWriter().append(e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
