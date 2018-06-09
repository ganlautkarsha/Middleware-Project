

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

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
    	String name;
    	int age;
    	public jsonent(){}
    	public String getName() {return name;}
    	public int getage() {return age;}
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
    public String formQuery(jsonent j)
    {
    	String ret="";
    	String table_name = "dummytable";
    	ret="insert into " + table_name + " values ('"+j.getName()+"','"+j.getage()+"');";
    	return ret;
    }
    public String insertintodb(String query)
    {
    	try {
			//Class.forName("com.postgresql.jdbc.Driver");		
    		Class.forName("org.postgresql.Driver");
    		System.out.println("Query is : " +query);
			//Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middleware?user=postgres&password=coupde4$grace&useSSL=false");
			Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/middleware?user=postgres&password=passwd&useSSL=true");

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
		System.out.println("payload is "+j.getName() + " " + j.getage());
		String query = formQuery(j);
		String result = insertintodb(query);
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
