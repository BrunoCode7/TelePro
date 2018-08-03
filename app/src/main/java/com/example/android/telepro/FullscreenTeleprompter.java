package com.example.android.telepro;

import android.content.SharedPreferences;
import android.lib.widget.verticalmarqueetextview.VerticalMarqueeTextView;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.example.android.telepro.fragments.MainScreenFragment.ARTICLE_KEY_0;

public class FullscreenTeleprompter extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_teleprompter);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int speed = sharedPreferences.getInt("seek_bar_key", 10);
        VerticalMarqueeTextView textView1 = findViewById(R.id.telepro_textview_fs);
        String mArticle = getIntent().getStringExtra(ARTICLE_KEY_0);
        textView1.setMarqueeSpeed(speed);
        textView1.setText(mArticle);
    }
}