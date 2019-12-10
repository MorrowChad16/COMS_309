package com.example.pickmeup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickmeup.Tabs.newsTab;
import com.example.pickmeup.Tabs.storeTab;

public class webLinks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_links);

        Button btn1News = findViewById(R.id.btn1News);
        btn1News.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), newsTab.class);

            startActivity(startIntent);

        });

        Button btnStore = findViewById(R.id.btnStore);
        btnStore.setOnClickListener(view -> {
            Intent startIntent2 = new Intent(getApplicationContext(), storeTab.class);
            //show how to pass info to second screen
            startActivity(startIntent2);

        });

        Button btnVid = findViewById(R.id.btnVid);
        btnVid.setOnClickListener(view -> {
            Intent startIntent3 = new Intent(getApplicationContext(), storeTab.class);
            //show how to pass info to second screen
            startActivity(startIntent3);

        });


    }
}
