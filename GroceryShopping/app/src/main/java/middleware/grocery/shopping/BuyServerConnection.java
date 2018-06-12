package middleware.grocery.shopping;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BuyServerConnection extends AppCompatActivity implements Runnable {

    static URL url;
    private final TextView loadWordTextView;
    String UID;
    ArrayList list;
    JSONObject location;
    BuyServerConnection(String U, ArrayList li, JSONObject l)
    {
        setContentView(R.layout.activity_grocery_store_display);
        loadWordTextView = findViewById(R.id.loadTextView);
        this.UID=U;
        this.list=li;
        this.location=l;
    }

    @Override
    public void run() {
        try {
            url = new URL("http://35.188.26.231:8080/middleserver/servlet");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try
        {
            HttpURLConnection connection =  (HttpURLConnection)url.openConnection();
//            jsonEntity js = new jsonEntity("xyz",20);

            JSONObject innerObj = new JSONObject();
            innerObj.put("list", list);
            innerObj.put("location", location);

            JSONObject j = new JSONObject();
            // Change this accordingly
//            j.put(UID, innerObj);
            j.put("name", "Tushar");
            j.put("age", 64);

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
//            System.out.println("status : " + status);
             loadWordTextView.append("status : " + status);
        }
        catch(Exception e)
        {
            loadWordTextView.append(e.toString());
        }
    }
}
