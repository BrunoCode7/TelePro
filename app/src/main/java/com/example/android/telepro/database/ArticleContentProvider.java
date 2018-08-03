package com.example.android.telepro.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.telepro.database.ArticleContract.ArticleEntry.COLUMN_TIMESTAMP;
import static com.example.android.telepro.database.ArticleContract.ArticleEntry.CONTENT_URI;
import static com.example.android.telepro.database.ArticleContract.ArticleEntry.TABLE_NAME;

/**
 * Created by Baraa Hesham on 7/22/2018.
 */
public class ArticleContentProvider extends ContentProvider {
    private ArticleDbHelper mArticleDbHelper;
    public static final int ARTICLES = 100;
    public static final int ARTICLE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ArticleContract.AUTHORITY, ArticleContract.PATH_ARTICLES, ARTICLES);
        matcher.addURI(ArticleContract.AUTHORITY, ArticleContract.PATH_ARTICLE, ARTICLE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mArticleDbHelper = new ArticleDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final SQLiteDatabase database = mArticleDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor mCursor;
        switch (match) {

            case ARTICLES:
                mCursor = database.query(TABLE_NAME, null, null, null,
                        null, null, COLUMN_TIMESTAMP);
                break;

            case ARTICLE_WITH_ID:

                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                mCursor = database.query(TABLE_NAME, null, mSelection, mSelectionArgs,
                        null, null, COLUMN_TIMESTAMP);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        mCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return mCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mArticleDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case ARTICLES:
                long id = db.insert(TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionsArgs) {
        final SQLiteDatabase database = mArticleDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int selectedRow;
        switch (match) {

            case ARTICLES:
                selectedRow = database.delete(TABLE_NAME, null, null);
                break;

            case ARTICLE_WITH_ID:

                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                selectedRow = database.delete(TABLE_NAME, mSelection, mSelectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if (selectedRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return selectedRow;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase database = mArticleDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int selectedRow;
        switch (match) {

            case ARTICLES:
                selectedRow = database.update(TABLE_NAME, contentValues, null, null);
                break;

            case ARTICLE_WITH_ID:

                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                selectedRow = database.update(TABLE_NAME, contentValues, mSelection, mSelectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if (selectedRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return selectedRow;
    }
}
