package com.example.pickmeup.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickmeup.R;
import com.example.pickmeup.ui.ui.skillhelp.SkillHelpFragment;

public class SkillHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_help_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SkillHelpFragment.newInstance())
                    .commitNow();
        }
    }
}
