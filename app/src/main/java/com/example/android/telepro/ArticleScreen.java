package com.example.android.telepro;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.telepro.database.ArticleContract;
import com.example.android.telepro.fragments.MainScreenFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.telepro.database.ArticleContract.ArticleEntry.CONTENT_URI;
import static com.example.android.telepro.fragments.MainScreenFragment.ARTICLES_LOADER_ID;
import static com.example.android.telepro.fragments.MainScreenFragment.ARTICLE_ID;
import static com.example.android.telepro.fragments.MainScreenFragment.ARTICLE_KEY;
import static com.example.android.telepro.fragments.MainScreenFragment.SUBJECT_KEY;

public class ArticleScreen extends AppCompatActivity {

    @BindView(R.id.article_et)
    EditText articleEt;
    @BindView(R.id.subject_et)
    EditText subjectEt;
    private int id;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_screen);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String article = intent.getStringExtra(ARTICLE_KEY);
        String subject = intent.getStringExtra(SUBJECT_KEY);
        id = intent.getIntExtra(ARTICLE_ID, 0);
        if (article != null) {
            articleEt.setText(article);
            subjectEt.setText(subject);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (id > 0) {
            mCursor = getContentResolver().query(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(), null, null, null, null);
        }
        if (mCursor != null) {
            updateArticle();
            mCursor.close();
        } else {
            addArticle();
        }
    }

    private void addArticle() {
        String subject = subjectEt.getText().toString();
        String article = articleEt.getText().toString();
        if (subject.length() == 0 || article.length() == 0) {
            Toast.makeText(getApplicationContext(),getString(R.string.failed), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ArticleContract.ArticleEntry.COLUMN_NAME_SUBJECT, subject);
        contentValues.put(ArticleContract.ArticleEntry.COLUMN_NAME_ARTICLE, article);

        Uri uri = getContentResolver().insert(CONTENT_URI, contentValues);
        if (uri != null) {
            Toast.makeText(getApplicationContext(),getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateArticle() {
        String subject = subjectEt.getText().toString();
        String article = articleEt.getText().toString();
        if (subject.length() == 0||article.length() == 0) {
            Toast.makeText(getApplicationContext(),getString(R.string.failed), Toast.LENGTH_SHORT).show();
            return; }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ArticleContract.ArticleEntry.COLUMN_NAME_SUBJECT, subject);
        contentValues.put(ArticleContract.ArticleEntry.COLUMN_NAME_ARTICLE, article);

        int rows = getContentResolver().update(CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(), contentValues, null, null);
        if (rows != 0) {
            Toast.makeText(getApplicationContext(),getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show(); }
    }
}