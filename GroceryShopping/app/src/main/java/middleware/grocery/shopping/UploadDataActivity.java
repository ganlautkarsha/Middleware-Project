package middleware.grocery.shopping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_data);

        String itemName = "", storeName = "", date = "";
        float itemPrice = (float) 0.0;

        if (getIntent().hasExtra("middleware.grocery.shopping.itemPrice")) {
            itemPrice = getIntent().getExtras().getFloat("middleware.grocery.shopping.itemPrice");
        }

        if (getIntent().hasExtra("middleware.grocery.shopping.itemName")) {
            itemName = getIntent().getExtras().getString("middleware.grocery.shopping.itemName");
        }

        if (getIntent().hasExtra("middleware.grocery.shopping.date")) {
            date = getIntent().getExtras().getString("middleware.grocery.shopping.date");
        }

        if (getIntent().hasExtra("middleware.grocery.shopping.storeName")) {
            storeName = getIntent().getExtras().getString("middleware.grocery.shopping.storeName");
        }

        serverConnection(itemName, storeName, date, itemPrice);
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

    private void serverConnection(final String itemName, final String storeName, final String date, final float itemPrice) {
        new Thread(new Runnable() {
            public void run() {
                TextView statusView = findViewById(R.id.statusView);
                try {
                    statusView.append("Begin");
                    URL url=new URL("http://35.188.26.231:8080/Server/servlet");
                    HttpURLConnection connection =  (HttpURLConnection)url.openConnection();
                    JSONObject j = new JSONObject();
                    j.put("item", itemName.toLowerCase());
                    j.put("price", itemPrice);
                    j.put("storename", storeName.toLowerCase());
                    j.put("date", date.toLowerCase());
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setDefaultUseCaches (false);
                    connection.setRequestProperty("Content-Type", "application/json");

                    ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
                    objOut.writeObject(j.toString());
                    objOut.flush();
                    objOut.close();
                    int status = connection.getResponseCode();
                    String response = getBody(connection);
                    statusView.setText("Status: "+ status);
                    statusView.append("\nResponse: \n" + response);
                    statusView.append("\n\n\nDetails: " + itemName + "\n " + storeName + "\n " + date + "\n " + Float.toString(itemPrice));
                } catch(Exception e) {
                    statusView.setText("Error: " + e.toString());
                    statusView.append("\n\n\nDetails: " + itemName + "\n " + storeName + "\n " + date + "\n " + Float.toString(itemPrice));
                }
            }
        }).start();
    }
}
