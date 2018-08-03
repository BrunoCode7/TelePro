package com.example.android.telepro.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Baraa Hesham on 7/22/2018.
 */
public class ArticleDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Articles.db";
    public static final int DATABASE_VERSION = 1;

    public ArticleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ARTICLE_TABLE = "CREATE TABLE " +
                ArticleContract.ArticleEntry.TABLE_NAME + " (" +
                ArticleContract.ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArticleContract.ArticleEntry.COLUMN_NAME_SUBJECT + " TEXT NOT NULL, " +
                ArticleContract.ArticleEntry.COLUMN_NAME_ARTICLE + " TEXT NOT NULL," +
                ArticleContract.ArticleEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArticleContract.ArticleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
