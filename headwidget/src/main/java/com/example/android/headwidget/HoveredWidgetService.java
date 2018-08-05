package com.example.android.headwidget;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Baraa Hesham on 7/24/2018.
 */
public class HoveredWidgetService extends Service implements View.OnClickListener {
    private WindowManager mWindowManager;
    private View mHoveredWidgetView, collapsedView, expandedView;
    private Point szWindow = new Point();
    private TextView textView;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private SharedPreferences sharedPreferences;
    public static final String ARTICLE_STRING_KEY="widget_string";
    ScrollView scrollView;


    public HoveredWidgetService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        getWindowManagerDefaultDisplay();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        addFloatingWidgetView(inflater);
        mHoveredWidgetView.findViewById(R.id.close_floating_view).setOnClickListener(this);
        mHoveredWidgetView.findViewById(R.id.close_expanded_view).setOnClickListener(this);
        implementTouchListenerToFloatingWidgetView();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String article = (String) intent.getExtras().get(ARTICLE_STRING_KEY);


            textView.setText(article);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /*  Add Floating Widget View to Window Manager  */
    private void addFloatingWidgetView(LayoutInflater inflater) {
        WindowManager.LayoutParams params;
        mHoveredWidgetView = inflater.inflate(R.layout.hovered_widget_layout, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        mWindowManager.addView(mHoveredWidgetView, params);
        collapsedView = mHoveredWidgetView.findViewById(R.id.collapse_view);
        expandedView = mHoveredWidgetView.findViewById(R.id.expanded_container);
        textView = mHoveredWidgetView.findViewById(R.id.floating_widget_detail_label);
        scrollView=mHoveredWidgetView.findViewById(R.id.hovered_scroll);

    }

    private void getWindowManagerDefaultDisplay() {
        mWindowManager.getDefaultDisplay().getSize(szWindow);
    }

    private void implementTouchListenerToFloatingWidgetView() {
        mHoveredWidgetView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {

            long time_start = 0, time_end = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mHoveredWidgetView.getLayoutParams();
                int xAxis = (int) event.getRawX();
                int yAxis = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        x_init_cord = xAxis;
                        y_init_cord = yAxis;
                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        return true;
                    case MotionEvent.ACTION_UP:


                        int x_diff = xAxis - x_init_cord;
                        int y_diff = yAxis - y_init_cord;
                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis();
                            if ((time_end - time_start) < 300)
                                onFloatingWidgetClick();

                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int barHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (mHoveredWidgetView.getHeight() + barHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (mHoveredWidgetView.getHeight() + barHeight);
                        }

                        layoutParams.y = y_cord_Destination;


                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = xAxis - x_init_cord;
                        int y_diff_move = yAxis - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;
                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        mWindowManager.updateViewLayout(mHoveredWidgetView, layoutParams);
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.close_floating_view) {
            stopSelf();

        } else if (i == R.id.close_expanded_view) {
//            textView.stopMarquee();
            collapsedView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.GONE);
        }
    }

    private boolean isViewCollapsed() {
        return mHoveredWidgetView == null || mHoveredWidgetView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }

    private void onFloatingWidgetClick() {
        if (isViewCollapsed()) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            final int speed = sharedPreferences.getInt("seek_bar_key_hovered", 10);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(scrollView,"scrollY", 0, scrollView.getBottom());
                    objectAnimator.setDuration(500000/speed);
                    objectAnimator.start();
                }
            });
//            textView.startMarquee();
            collapsedView.setVisibility(View.GONE);
            expandedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHoveredWidgetView != null)
            mWindowManager.removeView(mHoveredWidgetView);
    }
}