package com.smokless.smokeless;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch strictModeSwitch;
    private TextView strictModeHelper;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "SmokeLessPrefs"; // Replace with your preference name
    private static final String KEY_STRICT_MODE = "strictMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        strictModeSwitch = findViewById(R.id.strictModeSwitch);
        strictModeHelper = findViewById(R.id.switchStrictModeHelper);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Load the current state of the switch from SharedPreferences
        boolean isStrictModeEnabled = sharedPreferences.getBoolean(KEY_STRICT_MODE, false);
        strictModeSwitch.setChecked(isStrictModeEnabled);
        if (isStrictModeEnabled) {
            strictModeHelper.setText("This mode is harder but provides better results");
        } else {
            strictModeHelper.setText("This is a gentle mode that cheers you up !\nPerfect for beginners");
        }

        // Add a listener to the switch to update SharedPreferences when it's toggled
        strictModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the strict mode setting in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_STRICT_MODE, isChecked);
                editor.apply();
                if (isChecked) {
                    strictModeHelper.setText("This mode is harder but provides better results");
                } else {
                    strictModeHelper.setText("This is a gentle mode that cheers you up !\nPerfect for beginners");
                }
            }
        });
    }

    public void onMenuIconClicked(View view) {
        // Determine the current context (main or settings) based on your logic.
        boolean isInSettings = true; // Set this to true if you're in the settings context, or false if in the main context.

        if (!isInSettings) {
            // Open the settings activity.
            Intent settingsIntent = new Intent(this, SettingsActivity.class); // Replace SettingsActivity with your actual settings activity class.
            startActivity(settingsIntent);
        } else {
            // Switch back to the main activity.
            Intent mainIntent = new Intent(this, MainActivity.class); // Replace MainActivity with your actual main activity class.
            startActivity(mainIntent);
        }
    }
    public void redirectToGitHub(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/thdelmas/Smoke-Less"));
        startActivity(intent);
    }

    // Function to redirect to LinkedIn
    public void redirectToLinkedIn(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/th%C3%A9ophile-delmas-92275b16b/"));
        startActivity(intent);
    }

    // Function to redirect to Email
    public void redirectToEmail(View v) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:contact@theophile.world"));
        startActivity(intent);
    }
}