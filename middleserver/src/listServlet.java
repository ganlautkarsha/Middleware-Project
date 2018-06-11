

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class listServlet
 */
@WebServlet("/listServlet")
public class listServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String originLocation="";
	double locX=0,locY=0;
	int radius=0;   
	ArrayList<String> items = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
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
    public listServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		String req = getBody(request);
		String liststr = req.substring(7);
		JSONObject list = new JSONObject(liststr);
		//ArrayList<String> items = new ArrayList<String>(Arrays.asList(itemstr.split(":")));
		//Gson gson = new Gson();
		//ArrayList<String> items = gson.fromJson(b, new TypeToken<ArrayList<String>>() {}.getType());
//		for(String s:items)
//			System.out.println(s);
		
		
		Iterator<String> it = list.keys();
		while(it.hasNext())
		{
			String key = it.next();//System.out.println(it.next());
			if(key.equals("list"))
			{
				String itemstr = list.getString(key);
				items = new ArrayList<String>(Arrays.asList(itemstr.split(",")));				
			}
			else if(key.equals("locX"))
			{
				locX=list.getDouble(key);
			}
			else if(key.equals("locY"))
			{
				locY=list.getDouble(key);
			}
			else if(key.equals("radius"))
			{
				radius=list.getInt(key);
			}
		}
		for(String s:items)
			System.out.println(s);
		System.out.println("Radius = " + radius);
		originLocation=Double.toString(locX) + "," + Double.toString(locY);
		System.out.println("Loc x = "+Double.toString(locX)+"\nLoc y = "+Double.toString(locY));
		String result=somefunction();
		response.getWriter().append(result);
	}
	public String somefunction()
	{
		ArrayList<String> stores = new ArrayList<String>();
		JSONObject retJson = new JSONObject();
		try {
			String query = "select * from store;";
			//Class.forName("com.postgresql.jdbc.Driver");		
    		Class.forName("org.postgresql.Driver");
    		System.out.println("Query is : " +query);
			//Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middleware?user=postgres&password=coupde4$grace&useSSL=false");
			Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middlewaredb?user=postgres&password=passwd&useSSL=false");
			//Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middleware?user=postgres&password=passwd&useSSL=true");
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery(query);//connect.prepareStatement(query).execute();
			String mapURL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&";
			String origins = "origins="+originLocation;
			String key="key=AIzaSyDlFa3uMhEfkUNZD30SDh6EmZRoq4FtKJk";
			while(rs.next())
			{
				System.out.println(rs.getString(1) + " " + rs.getDouble(2) + " " + rs.getDouble(3));
				String storename = rs.getString("storename");
				double locX=rs.getDouble("locx");//rs.getDouble(1);
				double locY=rs.getDouble("locy");
				String location= Double.toString(locX) + "," + Double.toString(locY);
				String destination = "destinations="+location;
				mapURL = mapURL + origins + "&" + destination + "&" + key;
				double distance = someotherfunction(mapURL);
				if(distance<=radius)
					stores.add(storename);
					
			}
			
			for(int i=0;i<stores.size();i++)
			{
				double storecost=0;
				String store=stores.get(i);
				HashMap<String,String> prices = new HashMap<String,String>();
				for(int j=0;j<items.size();j++)
				{
					String item=items.get(j);
					String pricequery = "select price from catalog where storename='"+store+"' and item='"+item+"' order by date desc;";
					try {
						rs=stmt.executeQuery(pricequery);						
						String priceStr="";
						if(rs.next())
						{
							priceStr=rs.getString("price");
						}
						if(priceStr.equals(""))
						{
							// item not found
							prices.put(item,"0");
						}
						else
						{
							prices.put(item, priceStr);
							storecost+=Double.parseDouble(priceStr);
						}
					}
					catch(SQLException e)
					{
						// item not found
						prices.put(item,"0");
					}
				}
				retJson.append(store, prices);

			}
			connect.close();
			
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return e.toString();
    	}
		System.out.println(retJson.toString());
    	return "ok";
	}
	public double someotherfunction(String url) throws MalformedURLException
	{
		URL ur = new URL(url);
		double distance=-1;
		try
		{
		HttpURLConnection connection =  (HttpURLConnection)ur.openConnection(); 
		//jsonEntity js=new jsonEntity("From client",24);
		//JSONObject j = new JSONObject();
		//j.put("name", "From client");
		//j.put("age", 24);
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		//connection.setDoOutput(true);
		//connection.setUseCaches(false);
		//connection.setDefaultUseCaches (false);
		//connection.setRequestProperty("Content-Type", "application/json");
		//connection.setRequestProperty("name", "xyz");
		
		//ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
        //objOut.writeObject(samplejson/*j.toString()*/);
        //objOut.flush();
        //objOut.close();
        int status = connection.getResponseCode();
        String response = getBody(connection);
        JSONObject js = new JSONObject(response);
        //String distance = ((JSONObject)((JSONArray)((JSONObject)js.get("rows")).get("elements")).get("distance")).getString("text");

        String ja = js.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getString("text");//.optJSONArray(0).getString(0);
        System.out.println("status : " + status);
		System.out.println("received response : "+ response);
		distance = Double.parseDouble(ja.split(" ")[0]);
        System.out.println("Distance is " + distance);
        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return distance;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
