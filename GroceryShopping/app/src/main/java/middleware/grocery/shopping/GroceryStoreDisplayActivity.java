package middleware.grocery.shopping;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GroceryStoreDisplayActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;

    Set<String> shoppingListDict;
    Map<String, Map<String, String>> analyzedListMap = new TreeMap<>();
    TextView loadWordTextView;

    private String FILE_NAME = "stores_list.json";
    private String FILE_PATH = "GroceryStoreList";

    File myExternalFile;
    String storeList = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestPermissions();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_store_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<String> shoppingList = null;

        if (getIntent().hasExtra("middleware.grocery.shopping.shopping.list")) {
            shoppingList = getIntent().getExtras().getStringArrayList("middleware.grocery.shopping.shopping.list");
        }

        shoppingListDict = new HashSet<>(shoppingList);

        loadWordTextView = findViewById(R.id.loadTextView);

        myExternalFile = new File(getExternalFilesDir(FILE_PATH), FILE_NAME);
        storeList = load();

        JSONObject storeDict = null;
        try {
            storeDict = new JSONObject(storeList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator<String> storeNames;
        storeNames = storeDict.keys();
        loadWordTextView.setText("");

        while (storeNames.hasNext()) {
            String storeName = storeNames.next();
            try {
                JSONObject itemList = storeDict.getJSONObject(storeName);
                analyzeListForOneStore(storeName, itemList);
            } catch (JSONException e) {
                // Something went wrong!
            }
        }

        printAnalyzedListMap();

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!= null) {
                    loadWordTextView.append("Longitude: " + location.getLongitude() + "\n" + "Latitude: " + location.getLatitude());
                }
            }
        });
    }

    private String load() {
        String myData = "";
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData += strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myData;
    }

    private void analyzeListForOneStore(String storeName, JSONObject itemInfo) {
        Set<String> listDict = new HashSet<>(shoppingListDict);
        Map<String, String> itemListMap = new TreeMap<>();
        Iterator<String> items = itemInfo.keys();
        while (items.hasNext()) {
            String itemName = items.next();
            if (listDict.contains(itemName)) {
                String price = "";
                try {
                    price = itemInfo.getString(itemName);
                    if (price.equals("0")) {
                        price = "No price Found";
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Something's wrong",Toast.LENGTH_SHORT).show();
                }
                itemListMap.put(itemName, price);
                listDict.remove(itemName);
            }
        }

        for (String aListDict : listDict) {
            itemListMap.put(aListDict, "Item not available");
        }
        analyzedListMap.put(storeName, itemListMap);
        listDict.clear();
    }

    private void printAnalyzedListMap(){
        for (Map.Entry<String, Map<String, String>> storeName : analyzedListMap.entrySet()) {
            loadWordTextView.append(storeName.getKey() + ":\n\n");
            for (Map.Entry<String, String> itemMap : storeName.getValue().entrySet())
                loadWordTextView.append("   " + itemMap.getKey() + ":   " + itemMap.getValue() + "\n\n");
            loadWordTextView.append("\n\n\n");
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
