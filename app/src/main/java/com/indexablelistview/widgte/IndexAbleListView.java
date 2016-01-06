package com.indexablelistview.widgte;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Jumy on 16/1/5.
 * 自定义索引列表
 */
public class IndexAbleListView extends ListView {
    private boolean mIsFastScrollEnabled = false;
    private IndexScroller mScroller = null;
    private GestureDetector mGestureDetector = null;


    @Override
    public boolean isFastScrollEnabled() {
        return mIsFastScrollEnabled;
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
        mIsFastScrollEnabled = enabled;
        if (mIsFastScrollEnabled) {
            if (mScroller == null) {
                mScroller = new IndexScroller(getContext(), this);
            }
        } else {
            if (mScroller != null) {
                mScroller.hide();
                mScroller = null;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mScroller != null) {
            mScroller.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //Intercept ListView's touch event
        if (mScroller != null && mScroller.onTouchEvent(ev)) {
            return true;
        }
        if (mGestureDetector == null) {
            //Create a new GestureDetector -- 手势探测器
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (mScroller != null) {
                        mScroller.show();
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }
        mGestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScroller.contains(ev.getX(), ev.getY())) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (mScroller != null){
            mScroller.setAdapter(adapter);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mScroller != null){
            mScroller.onSizeChanged(w,h,oldw,oldh);
        }
    }

    public IndexAbleListView(Context context) {
        super(context);
    }

    public IndexAbleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexAbleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
