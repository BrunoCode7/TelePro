package com.example.android.telepro;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import static com.example.android.telepro.fragments.MainScreenFragment.ARTICLE_KEY_0;

public class FullscreenTeleprompter extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_teleprompter);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int speed = sharedPreferences.getInt("seek_bar_key", 1);
        final TextView textView1 = findViewById(R.id.telepro_textview_fs);
        final ScrollView scrollView=findViewById(R.id.telepro_scroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofInt(scrollView,"scrollY", 0, textView1.getBottom());
                objectAnimator.setDuration(500000/speed);
                objectAnimator.start();

            }
        });
        String mArticle = getIntent().getStringExtra(ARTICLE_KEY_0);
        textView1.setText(mArticle);
    }
}