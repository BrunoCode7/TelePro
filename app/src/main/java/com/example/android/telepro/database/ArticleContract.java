package com.example.android.telepro.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Baraa Hesham on 7/22/2018.
 */
public class ArticleContract {

    public static final String AUTHORITY = "com.example.android.telepro";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_ARTICLES = "articles";
    public static final String PATH_ARTICLE = "articles/#";

    private ArticleContract() {
    }

    public static class ArticleEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLES).build();
        public static final String TABLE_NAME = "articles";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_ARTICLE = "article";
        public static final String COLUMN_TIMESTAMP = "timestamp";


    }
}
