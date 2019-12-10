package com.example.pickmeup.Tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickmeup.R;
import com.example.pickmeup.webLinks;

public class storeTab extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_tab);

        Button ghomebtn = findViewById(R.id.ghomebtn);
        ghomebtn.setOnClickListener(view -> {
            Intent startIntent5 = new Intent(getApplicationContext(), webLinks.class);
            //show how to pass info to second screen
            startActivity(startIntent5);

        });

        Button shopnbtn = findViewById(R.id.shopnbtn);
        shopnbtn.setOnClickListener(view -> {
            String link3 = "https://www.nike.com/";
            Uri webadd3 = Uri.parse(link3);

            Intent gotoLink3 = new Intent(Intent.ACTION_VIEW, webadd3);
            if(gotoLink3.resolveActivity(getPackageManager()) !=null) {
                startActivity(gotoLink3);
            }
        });

        Button shopadbtn = findViewById(R.id.shopadbtn);
        shopadbtn.setOnClickListener(view -> {
            String link4 = "https://www.adidas.com/us/basketball";
            Uri webadd4 = Uri.parse(link4);

            Intent gotoLink4 = new Intent(Intent.ACTION_VIEW, webadd4);
            if(gotoLink4.resolveActivity(getPackageManager()) !=null) {
                startActivity(gotoLink4);
            }
        });

    }
}
