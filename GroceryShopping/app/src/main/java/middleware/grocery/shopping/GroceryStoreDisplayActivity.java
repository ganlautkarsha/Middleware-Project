package middleware.grocery.shopping;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GroceryStoreDisplayActivity extends AppCompatActivity {

    TextView loadWordTextView;
    String preferredDistance = "";
    ArrayList<String> shoppingList = null;
    Set<String> shoppingListDict = new HashSet<>();

    double longitude = 0;
    double latitude = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_store_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /***************************Get Data From Shopping List*********************************/

        if (getIntent().hasExtra("middleware.grocery.shopping.shopping.list")) {
            shoppingList = getIntent().getExtras().getStringArrayList("middleware.grocery.shopping.shopping.list");
        }

        if (getIntent().hasExtra("middleware.grocery.shopping.preferred.distance")) {
            preferredDistance = getIntent().getExtras().getString("middleware.grocery.shopping.preferred.distance");
        }

        if (getIntent().hasExtra("middleware.grocery.shopping.longitude")) {
            longitude = getIntent().getExtras().getDouble("middleware.grocery.shopping.longitude");
        }

        if (getIntent().hasExtra("middleware.grocery.shopping.latitude")) {
            latitude = getIntent().getExtras().getDouble("middleware.grocery.shopping.latitude");
        }

        for (String item : shoppingList) {
            shoppingListDict.add(item.toLowerCase());
        }
//        shoppingListDict = new HashSet<>(shoppingList);

        loadWordTextView = findViewById(R.id.loadTextView);
        loadWordTextView.setMovementMethod(new ScrollingMovementMethod());

        /***************************Connection Code Start*********************************/
        serverConnection();
    }

    /***************************Get Body*********************************/

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

    /***************************Server Connection*********************************/

    private void serverConnection() {
        new Thread(new Runnable() {
            public void run() {
                try{
                    URL url=new URL("http://35.188.26.231:8080/Server/listServlet");
                    HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
                    JSONObject j = new JSONObject();

//                    loadWordTextView.append(shoppingList.toString().replace("[","").replace("]", "") + "\n" +
//                            Double.toString(longitude) + ", " + Double.toString(latitude) + "\n" + Integer.parseInt(preferredDistance.split(" ")[0]) + "\n");

                    j.put("list", shoppingList.toString().replace("[","").replace("]", "").replace(" ", "").trim().toLowerCase());
                    j.put("locX", latitude);
                    j.put("locY", longitude);
                    j.put("radius", Integer.parseInt(preferredDistance.split(" ")[0]));
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setDefaultUseCaches (false);
                    connection.setRequestProperty("Content-Type", "application/json");

                    loadWordTextView.append("\nSending String: \n" + j.toString() + "\n");

                    ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
                    objOut.writeObject(j.toString());
                    objOut.flush();
                    objOut.close();
                    int status = connection.getResponseCode();
                    loadWordTextView.append("\nstatus : " + status + "\n\n\n");
                    String response = getBody(connection);
                    response = response.replace("[", "").replace("]", "");

                    JSONObject responseObj = new JSONObject(response);

                    Iterator<String> storeNames;
                    storeNames = responseObj.keys();

                    Map<String, Map<String, String>> analyzedListMap = new TreeMap<>();

                    while (storeNames.hasNext()) {
                        Map<String, String> itemListMap = new TreeMap<>();
                        Set<String> listDict = new HashSet<>(shoppingListDict);
                        String storeName = storeNames.next();
                        try {
                            JSONObject itemList = responseObj.getJSONObject(storeName);
                            Iterator<String> items = itemList.keys();
                            while (items.hasNext()) {
                                String itemName = items.next();
                                if (listDict.contains(itemName)) {
                                    String price = "";
                                    try {
                                        price = itemList.getString(itemName);
                                        if (price.equals("0")) {
                                            price = "No price Found";
                                        }
                                    } catch (JSONException e) {
                                        loadWordTextView.append(e.toString());
                                    }
                                    itemListMap.put(itemName, price);
                                    listDict.remove(itemName);
                                }
                                for (String aListDict : listDict) {
                                    itemListMap.put(aListDict, "Item not available");
                                }
                            }
                            listDict.clear();
                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                        analyzedListMap.put(storeName, itemListMap);
                    }

                    for (Map.Entry<String, Map<String, String>> storeName : analyzedListMap.entrySet()) {
                        loadWordTextView.append(storeName.getKey() + ":\n\n");
                        for (Map.Entry<String, String> itemMap : storeName.getValue().entrySet())
                            loadWordTextView.append("   " + itemMap.getKey() + ":   " + itemMap.getValue() + "\n\n");
                        loadWordTextView.append("\n\n");
                    }

                } catch(Exception e) {
                    loadWordTextView.append("\n\n\nException: \n" + e.toString());
                }
            }
        }).start();
    }
}
