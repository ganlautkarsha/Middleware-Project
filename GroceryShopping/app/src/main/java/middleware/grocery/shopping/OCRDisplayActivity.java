package middleware.grocery.shopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OCRDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String storeName = "", OCRText = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrdisplay);

//        TextView storeNameField = findViewById(R.id.storeName);
//        storeNameField.setText(text);


        if (getIntent().hasExtra("middleware.grocery.shopping.storeName")) {
            storeName = getIntent().getExtras().getString("middleware.grocery.shopping.storeName");
        }

        if (getIntent().hasExtra("middleware.grocery.shopping.OCRString")) {
            OCRText = getIntent().getExtras().getString("middleware.grocery.shopping.OCRString");
        }

        extractNameAndPrice(storeName, OCRText);

        Button retryBtn = findViewById(R.id.retryBtn);

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(startIntent);
                finish();
            }
        });
    }

    private void extractNameAndPrice (String storeName, String OCRText) {
        TextView storeTextView = findViewById(R.id.storeName);
        EditText OCRItem = findViewById(R.id.OCRItem);
        EditText OCRPrice = findViewById(R.id.OCRPrice);
        TextView dollarTextField = findViewById(R.id.dollarTextField);

        if (storeName.toLowerCase().equals("walmart") | storeName.toLowerCase().equals("terget")) {
            List<String> OCRLineList = Arrays.asList(OCRText.split("\n"));
            ArrayList<String> OCRNameList = new ArrayList<>();

            OCRNameList.addAll(OCRLineList);
            for (String line: OCRLineList) {
                if (line.matches("(\\$?s?(\\d{1,3}\\.?\\d{2}))")) {
                    OCRNameList.remove(line);
                    dollarTextField.setText("$");
                    if (line.matches("\\d{4}")) {
                        OCRPrice.setText(line.replaceAll("[^\\d.]", "").substring(0,2) + "." + line.substring(2));
                    }
                    else {
                        OCRPrice.setText(line.replaceAll("[^\\d.]", ""));
                    }
                    break;
                }
                else {
                    dollarTextField.setText("");
                    OCRPrice.setHint("No Price Found. Please Enter.");
                }
            }

            OCRItem.setText(OCRNameList.get(0).toUpperCase());
            storeTextView.setText(storeName);
        }
        else {
            storeTextView.setText(storeName);
        }
    }
}
