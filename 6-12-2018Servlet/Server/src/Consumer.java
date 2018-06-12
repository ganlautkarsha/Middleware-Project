//
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.json.JSONObject;
//
//import scala.Array;
//
///**
// * Servlet implementation class Consumer
// */
//@WebServlet("/Consumer")
//public class Consumer extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//       
//    /**
//     * @see HttpServlet#HttpServlet()
//     */
//    public Consumer() {
//        super();
//        // TODO Auto-generated constructor stub
//    }
//    public static String getBody(HttpServletRequest request) throws IOException {
//
//        String body = null;
//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader bufferedReader = null;
//
//        try {
//            InputStream inputStream = request.getInputStream();
//            if (inputStream != null) {
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                char[] charBuffer = new char[128];
//                int bytesRead = -1;
//                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
//                    stringBuilder.append(charBuffer, 0, bytesRead);
//                }
//            } else {
//                stringBuilder.append("");
//            }
//        } catch (IOException ex) {
//            throw ex;
//        } finally {
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close();
//                } catch (IOException ex) {
//                    throw ex;
//                }
//            }
//        }
//
//        body = stringBuilder.toString();
//        return body;
//    }
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		String req = getBody(request);
//		
//		String liststr = req.substring(7);
//		JSONObject list = new JSONObject(liststr);
//		//ArrayList<String> items = new ArrayList<String>(Arrays.asList(itemstr.split(":")));
//		//Gson gson = new Gson();
//		//ArrayList<String> items = gson.fromJson(b, new TypeToken<ArrayList<String>>() {}.getType());
////		for(String s:items)
////			System.out.println(s);
//		
//		ArrayList<String> items = null;
//		Iterator<String> it = list.keys();
//		while(it.hasNext())
//		{
//			String key = it.next();//System.out.println(it.next());
//			if(key.equals("list"))
//			{
//				String itemstr = list.getString(key);
//				items = new ArrayList<String>(Arrays.asList(itemstr.split(",")));				
//			}
//		}
//		String appId=items.get(0);
//		SampleConsumer consumer=new SampleConsumer(items,appId);
////		ArrayList<String> itemsList=new ArrayList<>();
////		itemsList.add("apple");
////		itemsList.add("banana");
//		// create kafka consumer
////		consumer.returnResponse().toString()
//		response.getWriter().append(consumer.returnResponse().toString());
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		
//		doGet(request, response);
//	}
//}
