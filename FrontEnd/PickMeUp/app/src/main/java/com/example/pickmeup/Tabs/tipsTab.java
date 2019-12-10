package com.example.pickmeup.Tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickmeup.R;
import com.example.pickmeup.webLinks;

public class tipsTab extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_tab);

        Button btnHome1 = findViewById(R.id.btnHome1);
        btnHome1.setOnClickListener(view -> {
            Intent startIntent4 = new Intent(getApplicationContext(), webLinks.class);
            //show how to pass info to second screen
            startActivity(startIntent4);

        });

        Button vid1 = findViewById(R.id.vid1);
        vid1.setOnClickListener(view -> {
            String link1 = "https://www.youtube.com/watch?v=_-ZZ7nuFTkE";
            Uri webadd1 = Uri.parse(link1);

            Intent gotoLink1 = new Intent(Intent.ACTION_VIEW, webadd1);
            if(gotoLink1.resolveActivity(getPackageManager()) !=null) {
                startActivity(gotoLink1);
            }
        });

        Button Vid2 = findViewById(R.id.Vid2);
        Vid2.setOnClickListener(view -> {
            String link2 = "https://www.youtube.com/watch?v=PukmYnApL2Y";
            Uri webadd2 = Uri.parse(link2);

            Intent gotoLink2 = new Intent(Intent.ACTION_VIEW, webadd2);
            if(gotoLink2.resolveActivity(getPackageManager()) !=null) {
                startActivity(gotoLink2);
            }
        });

    }
}
