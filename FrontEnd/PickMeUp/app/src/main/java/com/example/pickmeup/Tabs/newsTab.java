package com.example.pickmeup.Tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickmeup.R;
import com.example.pickmeup.webLinks;

public class newsTab extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_tab);

        Button ghbtn = findViewById(R.id.ghbtn);
        ghbtn.setOnClickListener(view -> {
            Intent startIntent6 = new Intent(getApplicationContext(), webLinks.class);
            //show how to pass info to second screen
            startActivity(startIntent6);

        });


        Button espnbtn = findViewById(R.id.espnbtn);
        espnbtn.setOnClickListener(view -> {
            String link6 = "https://www.espn.com/";
            Uri webadd6 = Uri.parse(link6);

            Intent gotoLink6 = new Intent(Intent.ACTION_VIEW, webadd6);
            if(gotoLink6.resolveActivity(getPackageManager()) !=null) {
                startActivity(gotoLink6);
            }
        });


        Button nbabtn = findViewById(R.id.nbabtn);
        nbabtn.setOnClickListener(view -> {
            String link7 = "https://www.nba.com/";
            Uri webadd7 = Uri.parse(link7);

            Intent gotoLink7 = new Intent(Intent.ACTION_VIEW, webadd7);
            if(gotoLink7.resolveActivity(getPackageManager()) !=null) {
                startActivity(gotoLink7);
            }
        });

    }
}
