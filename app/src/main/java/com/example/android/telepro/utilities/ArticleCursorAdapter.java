package com.example.android.telepro.utilities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.telepro.AppMainScreen;
import com.example.android.telepro.R;
import com.example.android.telepro.database.ArticleContract;
import com.example.android.telepro.fragments.MainScreenFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.telepro.database.ArticleContract.ArticleEntry.CONTENT_URI;
import static com.example.android.telepro.fragments.MainScreenFragment.ARTICLES_LOADER_ID;

/**
 * Created by Baraa Hesham on 7/22/2018.
 */
public class ArticleCursorAdapter extends RecyclerView.Adapter<ArticleCursorAdapter.ArticleCursorViewHolder> {
    public Cursor mCursor;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private final clickListener clickListener;
    private final clickListenerDelete clickListenerDelete;
    private final clickListenerStart clickListenerStart;
    private final clickListenerStartWidget clickListenerStartWidget;


    public interface clickListener {
        void onListItemClick(String article, String subject, int id);
    }

    public interface clickListenerDelete {
        void clickDelete(int itemId);
    }

    public interface clickListenerStart {
        void clickStart(String article);
    }

    public interface clickListenerStartWidget {
        void clickStartWidget(String article);
    }

    public ArticleCursorAdapter(Context context, clickListener clickListener, clickListenerStart clickListenerStart,
                                clickListenerDelete clickListenerDelete, clickListenerStartWidget clickListenerStartWidget) {
        this.mContext = context;
        this.clickListener = clickListener;
        layoutInflater = LayoutInflater.from(mContext);
        this.clickListenerDelete = clickListenerDelete;
        this.clickListenerStart = clickListenerStart;
        this.clickListenerStartWidget = clickListenerStartWidget;

    }

    @NonNull
    @Override
    public ArticleCursorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.article_list_item, null);
        ArticleCursorViewHolder viewHolder = new ArticleCursorViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleCursorViewHolder holder, int position) {
        int subjectNumber = mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_NAME_SUBJECT);
        int idNumber = mCursor.getColumnIndex(ArticleContract.ArticleEntry._ID);
        int articleNumber = mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_NAME_ARTICLE);
        int timeNumber = mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_TIMESTAMP);
        mCursor.moveToPosition(position);
        final String subject = mCursor.getString(subjectNumber);
        String time = mCursor.getString(timeNumber);
        final String article = mCursor.getString(articleNumber);
        final int id = mCursor.getInt(idNumber);

        holder.articleSubject.setText(subject);
        holder.articleDate.setText(time);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListenerDelete.clickDelete(id);
            }
        });
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onListItemClick(article, subject, id);
            }
        });
        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListenerStart.clickStart(article);
            }
        });
        holder.startWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListenerStartWidget.clickStartWidget(article);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    class ArticleCursorViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.article_subject)
        TextView articleSubject;
        @BindView(R.id.article_date)
        TextView articleDate;
        @BindView(R.id.delete)
        Button delete;
        @BindView(R.id.start)
        Button start;
        @BindView(R.id.list_item)
        RelativeLayout relativeLayout;
        @BindView(R.id.play_widget)
        Button startWidget;

        public ArticleCursorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}

