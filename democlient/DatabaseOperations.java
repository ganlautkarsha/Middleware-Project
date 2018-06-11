package democlient;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseOperations
{
    private Connection connect = null;
    private Statement statement = null;
//    private ResultSet resultSet = null;
    String ip="localhost";
    String url = "jdbc:postgresql://"+ip+"/middlewaredb";
     String user = "postgres";
    String password = "passwd";
    
    public DatabaseOperations() throws SQLException {
		// TODO Auto-generated constructor stub
    	connect = DriverManager.getConnection(url, user, password);
        statement = connect.createStatement();
	}
    
    @SuppressWarnings("finally")
	public ResultSet executeOperation() throws SQLException {
    	ResultSet resultSet=null;
    	Statement delStatement=null;
        try {
        	String operation="select * from duplicateCatalog;";
             resultSet= statement.executeQuery(operation);
             delStatement=connect.createStatement();
             delStatement.execute("delete from duplicateCatalog");
           
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            
            System.out.println("Close Connection");
            delStatement.close();
            return resultSet;
        }
    }
    
//    @SuppressWarnings("finally")
//	public ArrayList<String> getTopics () throws SQLException {
//    	ArrayList<String> topics=new ArrayList<>();
//        try {
//            resultSet = statement.executeQuery("select distinct(item) from catalog;");
//            while(resultSet.next())
//            {
//            	topics.add(resultSet.getString("item"));
//            }
//           
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            
//            System.out.println("Close Connection");
//            return topics;
//        }
//    }

    public void close() {
        try {
//            if(resultSet != null) resultSet.close();
//            if (statement != null) statement.close();
            if (connect != null) connect.close();
        } catch (Exception e) {

        }
    }
//    String url = "jdbc:postgresql://localhost/tdm_multithreading_test";
//    String user = "postgres";
//    String password = "password";

}