 

import java.sql.Statement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class jServlet
 */
@WebServlet("/servlet")
public class servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public static class jsonent
    {
    	String item,storename,date;
    	float price;
    	public jsonent(){}
    	public String getItem() {return item;}
    	public String getStorename() {return storename;}
    	public String getdate() {return date;}
    	public float getPrice() {return price;}
    }
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servlet() {
        super();
        // TODO Auto-generated constructor stub
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
    public String formQuery(jsonent j,String table_name)
    {
    	String ret="";
//    	String table_name = "catalog";
    	
    	ret="insert into " + table_name + " values ('"+j.getItem()+"',"+j.getPrice()+",'"+j.getStorename()+"','"+j.getdate()+"');";
    	return ret;
    }
    public String insertintodb(String query)
    {
    	try {
			//Class.forName("com.postgresql.jdbc.Driver");		
    		Class.forName("org.postgresql.Driver");
    		System.out.println("Query is : " +query);
			//Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middleware?user=postgres&password=coupde4$grace&useSSL=false");
			Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middlewaredb?user=postgres&password=passwd&useSSL=true");

			connect.prepareStatement(query).execute();
			connect.close();
			
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return e.toString();
    	}
    	return "ok";
			
    }
    
    public String insertduplicate(jsonent j,String query)
    {
    	try {
			//Class.forName("com.postgresql.jdbc.Driver");		
    		Class.forName("org.postgresql.Driver");
    		System.out.println("Query is : " +query);
			//Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middleware?user=postgres&password=coupde4$grace&useSSL=false");
			Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middlewaredb?user=postgres&password=passwd&useSSL=true");
			Statement st=connect.createStatement();
			//get min price for item
			ResultSet rs=null;
			String checkQuery="select price from catalog where item='"+j.getItem()+"' and storename='"+j.getStorename()+"' and date=(select max(date) from catalog where item='"+j.getItem()+"' and storename='"+j.getStorename()+"') limit 1;";
			System.out.println(checkQuery);
			rs=st.executeQuery(checkQuery);
			float oldprice=0;
			while(rs.next())
			{
				oldprice=rs.getFloat("price");
				System.out.println("price"+oldprice);
			}
			System.out.println("oldprice"+oldprice);
			System.out.println("price"+j.getPrice());
			//compare price
			if(j.getPrice()!=oldprice)
			{
				//insert
				connect.prepareStatement(query).execute();
				connect.close();
			}
			else
			{
				return "!ok";
			}
			//else chuck
			
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return e.toString();
    	}
    	return "ok";
			
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(request==null)
		{
			System.out.println("Init done");
			return;
		}
		System.out.println("CP : "+System.getenv("classpath"));
		response.getWriter().append("Served at: ").append(request.getContextPath());
		System.out.println("Received "+request.getContentLength()+request.getContentType());
		String payload = getBody(request);
		payload = payload.substring(7);
		System.out.println("Received payload is: "+payload);//request.getReader().readLine());
		
//		JSONObject j = null;
//		try {
//			j = new JSONObject(payload);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			System.out.println("Name is " + j.getString("name"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Gson gson = new Gson();
		jsonent j = gson.fromJson(payload,jsonent.class);
//		System.out.println("payload is "+j.getName() + " " + j.getage());
		String query = formQuery(j,"catalog");
		
		String result = insertduplicate(j,query);
		if(result.equals("ok"))
		{
		System.out.println("insert complete!");
		query = formQuery(j,"duplicateCatalog");
		result = insertintodb(query);
		}
		if(result.equals("ok"))
		{
			response.getWriter().println("ALL OK");
			return;
		}
		else
		{
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.println(result);
		}
		
		//String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		//System.out.println("test is "+test);
		//System.out.println("received text is "+ request.getAttribute("text"));
		//String name = request.getParameter("name");
		//System.out.println("Name = "+name);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
//		System.out.println("Inside post method");
//		System.out.println("Received payload is: "+getBody(request));//request.getReader().readLine());
	}

}
