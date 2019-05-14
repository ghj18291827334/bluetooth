package com.allen.bluetoothtest;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by zhanglong on 2018/5/27.
 */
public class MListView extends ListView {
    boolean isMove = false;
    private boolean isScrollHead = true;
    private OnTouchListener onTouchListener= new OnTouchListener() {
        float initY = 0;
        boolean isFullScreen = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("zl", "onTouch_lv_" + event.getAction());
            int heightPixels = getResources().getDisplayMetrics().heightPixels;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initY = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isScrollHead) {
                        if (initY == 0) {
                            initY = event.getY();
                        }
                        Log.i("zl", "onTitleScroll" + (event.getY() - initY));
                        float height = event.getY() - initY;
                        if (height > heightPixels / 2) {
                            MBluetoothManage.getInstance().getAdapter().setTitleAddHeight(heightPixels);
                            isFullScreen = true;
                        } else {
                            MBluetoothManage.getInstance().getAdapter().setTitleAddHeight(height);
                            isFullScreen = false;
                        }
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    if (!isFullScreen) {
                        MBluetoothManage.getInstance().getAdapter().setTitleAddHeight(1);
                    }
                    break;

            }
            return false;
        }
    };

    public MListView(Context context) {
        super(context);
    }

    public MListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
     init();
    }

    public void init(){
        setOnTouchListener(onTouchListener);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstVisibleItem = 0;
            int visibleItemCount = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (firstVisibleItem == 0) {
                    Log.i("zl", "onTitleScroll");
                    isScrollHead = true;
                } else {
                    Log.i("zl", "onTitleScroll_NoFirst");
                    isScrollHead = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i("zl", "onScroll");
                this.firstVisibleItem = firstVisibleItem;
                this.visibleItemCount = visibleItemCount;
            }
        });

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("zl", "onInterceptTouchEvent_" + ev.getAction());
        if (onTouchListener==null)return super.onInterceptTouchEvent(ev);
        float tagY=0f;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = false;
                onTouchListener.onTouch(this,ev);
                tagY =  ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if ((ev.getY()-tagY)>1000.0f) {
                    Log.i("zl", "onInterceptTouchEvent_MOVE");
                    onTouchListener.onTouch(this,ev);
                    isMove = true;
                    return true;
                }else{
                    return super.onInterceptTouchEvent(ev);
                }
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    isMove = false;
                    onTouchListener.onTouch(this,ev);
                }

                break;
        }

        return super.onInterceptTouchEvent(ev);

    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }
}
