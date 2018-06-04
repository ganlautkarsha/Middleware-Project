package middleware.grocery.shopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button uploadActivityBtn = findViewById(R.id.uploadbtn);
        Button buyActivityBtn = findViewById(R.id.buybtn);

        uploadActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(startIntent);
            }
        });
    }
}
