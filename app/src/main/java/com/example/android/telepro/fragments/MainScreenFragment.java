package com.example.android.telepro.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.headwidget.HoveredWidgetService;
import com.example.android.telepro.ArticleScreen;
import com.example.android.telepro.FullscreenTeleprompter;
import com.example.android.telepro.R;
import com.example.android.telepro.database.ArticleContract;
import com.example.android.telepro.database.ArticleDbHelper;
import com.example.android.telepro.utilities.ArticleCursorAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.headwidget.HoveredWidgetService.ARTICLE_STRING_KEY;
import static com.example.android.telepro.database.ArticleContract.ArticleEntry.CONTENT_URI;

public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ArticleCursorAdapter.clickListener, ArticleCursorAdapter.clickListenerDelete,
        ArticleCursorAdapter.clickListenerStart, ArticleCursorAdapter.clickListenerStartWidget {

    public final static int ARTICLES_LOADER_ID = 0;
    @BindView(R.id.articles_recycler)
    RecyclerView recyclerView;
    ArticleCursorAdapter adapter;
    private Unbinder unbinder;
    SQLiteDatabase database;
    public static String ARTICLE_KEY = "article";
    public static String SUBJECT_KEY = "subject";
    public static String ARTICLE_ID = "id";
    public static final String ARTICLE_KEY_0 = "article0";
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    @BindView(R.id.adView)
    AdView mAdView;
    Bundle bundle;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArticleCursorAdapter(getContext(), this, this, this, this);
        getActivity().getSupportLoaderManager().initLoader(ARTICLES_LOADER_ID, null, this);
        ArticleDbHelper dbHelper = new ArticleDbHelper(getContext());
        database = dbHelper.getWritableDatabase();
        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544/6300978111");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        if (isOnline()){
            mAdView.setVisibility(View.VISIBLE);
        }
        return view;
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(ARTICLES_LOADER_ID, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {
            Cursor articleData = null;

            @Override
            protected void onStartLoading() {
                if (articleData != null) {
                    deliverResult(articleData);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContext().getContentResolver().query(CONTENT_URI,
                            null, null, null, ArticleContract.ArticleEntry.COLUMN_TIMESTAMP);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable Cursor data) {
                articleData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    @Override
    public void clickDelete(int itemId) {
        Toast.makeText(getContext(), "Article deleted", Toast.LENGTH_SHORT).show();
        getActivity().getContentResolver().delete(CONTENT_URI.buildUpon().appendPath(String.valueOf(itemId)).build(), null, null);
        getLoaderManager().restartLoader(ARTICLES_LOADER_ID, null, this);
    }

    @Override
    public void clickStart(String article) {
        Intent intent = new Intent(getContext(), FullscreenTeleprompter.class);
        intent.putExtra(ARTICLE_KEY_0, article);
        startActivity(intent);
    }

    @Override
    public void onListItemClick(String article, String subject, int id) {
        Intent intent = new Intent(getContext(), ArticleScreen.class);
        intent.putExtra(ARTICLE_KEY, article);
        intent.putExtra(SUBJECT_KEY, subject);
        intent.putExtra(ARTICLE_ID, id);
        startActivity(intent);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(R.id.articles_recycler));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Article");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    @Override
    public void clickStartWidget(String article) {
        bundle = new Bundle();
        bundle.putString(ARTICLE_STRING_KEY, article);
        createFloatingWidget(bundle);
    }

    public void createFloatingWidget(Bundle aBundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getContext().getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        } else
            startFloatingWidgetService(aBundle);
    }

    private void startFloatingWidgetService(Bundle aBundle) {
        Intent intent = new Intent(getContext(), HoveredWidgetService.class);
        intent.putExtras(aBundle);
        getActivity().startService(intent);
        getActivity().finish();
    }
}
