package com.indexablelistview.widgte;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * Created by Jumy on 16/1/5.
 * 索引条 include previewTextView && sections
 */
public class IndexScroller {
    private float mIndexBarWidth; // 索引条宽度
    private float mIndexBarMargin; // 索引条外边距
    private float mPreviewPadding; //在中心显示的预览文本的文本显示边距
    private float mDensity; // 密度
    private float mScaledDensity; // 缩放密度
    private float mAlphaRate; // 透明度 -- 用于显示和隐藏索引条 [0,1]
    private int mState = STATE_HIDDEN; // 状态
    private int mListViewWidth; // ListView宽度
    private int mListViewHeight; // ListView高度
    private int mCurrentSection = -1; // 当前 索引位置
    private boolean mIsIndexing = false; // 是否正在索引
    private ListView mListView = null;
    private SectionIndexer mIndexer = null;
    private String[] mSections = null;
    private RectF mIndexBarRect;

    // 4种状态（已隐藏、正在显示、已显示、正在隐藏）
    private static final int STATE_HIDDEN = 0;
    private static final int STATE_SHOWING = 1;
    private static final int STATE_SHOWN = 2;
    private static final int STATE_HIDING = 3;

    public IndexScroller(Context context, ListView mListView) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        this.mListView = mListView;
        setAdapter(mListView.getAdapter());

        mIndexBarWidth = 20 * mDensity;
        mIndexBarMargin = 10 * mDensity;
        mPreviewPadding = 5 * mDensity;
    }


    public void setAdapter(Adapter adapter) {
        if (adapter instanceof SectionIndexer) {
            mIndexer = (SectionIndexer) adapter;
            mSections = (String[]) mIndexer.getSections();
        }
    }

    /**
     * 绘制索引条和预览文本
     * @param canvas 画布
     */
    public void draw(Canvas canvas) {
        if (mState == STATE_HIDDEN) {
            return;
        }
        //background--indexBar
        Paint indexBarPaint = new Paint();
        indexBarPaint.setColor(Color.BLACK);
        indexBarPaint.setAlpha((int) (64 * mAlphaRate));
        indexBarPaint.setAntiAlias(true);
        canvas.drawRoundRect(mIndexBarRect, 5 * mDensity, 5 * mDensity, indexBarPaint);
        //获取索引数据，绘制索引条
        if (mSections != null && mSections.length > 0) {
            //Preview is shown when mCurrentSection is set
            if (mCurrentSection >= 0) {
                Paint previewPaint = new Paint();
                previewPaint.setColor(Color.BLACK);
                previewPaint.setAlpha(96);
                previewPaint.setAntiAlias(true);
                previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

                Paint previewTextPaint = new Paint();
                previewTextPaint.setColor(Color.WHITE);
                previewTextPaint.setAntiAlias(true);
                previewTextPaint.setTextSize(50 * mScaledDensity);

                float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
                float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
                RectF previewRect = new RectF((mListViewWidth - previewSize) / 2,
                        (mListViewHeight - previewSize) / 2,
                        (mListViewWidth - previewSize) / 2 + previewSize,
                        (mListViewHeight - previewSize) / 2 + previewSize);

                canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
                canvas.drawText(mSections[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1,
                        previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1, previewTextPaint);
            }
            //Sections shown
            Paint indexPaint = new Paint();
            indexPaint.setColor(Color.WHITE);
            indexPaint.setAlpha((int) (255 * mAlphaRate));
            indexPaint.setAntiAlias(true);
            indexPaint.setTextSize(12 * mScaledDensity);

            float sectionHeight = (mIndexBarRect.height() - 2 * mIndexBarMargin) / mSections.length;
            float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
            for (int i = 0; i < mSections.length; i++) {
                float paddingLeft = (mIndexBarWidth - indexPaint.measureText(mSections[i])) / 2;
                canvas.drawText(mSections[i], mIndexBarRect.left + paddingLeft
                        , mIndexBarRect.top + mIndexBarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // If down event occurs inside index bar region, start indexing
                if (mState != STATE_HIDDEN && contains(event.getX(), event.getY())) {
                    setState(STATE_SHOWN);

                    // It demonstrates that the motion event started from index bar
                    mIsIndexing = true;
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(event.getY());
                    mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsIndexing) {
                    // If this event moves inside index bar
                    if (contains(event.getX(), event.getY())) {
                        // Determine which section the point is in, and move the list to that section
                        mCurrentSection = getSectionByPoint(event.getY());
                        mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing) {
                    mIsIndexing = false;
                    mCurrentSection = -1;
                }
                if (mState == STATE_SHOWN)
                    setState(STATE_HIDING);
                break;
        }
        return false;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;
        mIndexBarRect = new RectF(w - mIndexBarMargin - mIndexBarWidth
                , mIndexBarMargin
                , w - mIndexBarMargin
                , h - mIndexBarMargin);
    }

    public void show() {
        if (mState == STATE_HIDDEN)
            setState(STATE_SHOWING);
        else if (mState == STATE_HIDING)
            setState(STATE_HIDING);
    }

    public void hide() {
        if (mState == STATE_SHOWN)
            setState(STATE_HIDING);
    }

    private void setState(int state) {
        if (state < STATE_HIDDEN || state > STATE_HIDING)
            return;

        mState = state;
        switch (mState) {
            case STATE_HIDDEN:
                // Cancel any fade effect
                mHandler.removeMessages(0);
                break;
            case STATE_SHOWING:
                // Start to fade in
                mAlphaRate = 0;
                fade(0);
                break;
            case STATE_SHOWN:
                // Cancel any fade effect
                mHandler.removeMessages(0);
                break;
            case STATE_HIDING:
                // Start to fade out after three seconds
                mAlphaRate = 1;
                fade(3000);
                break;
        }
    }

    public boolean contains(float x, float y) {
        // Determine if the point is in index bar region, which includes the right margin of the bar
        return (x >= mIndexBarRect.left && y >= mIndexBarRect.top && y <= mIndexBarRect.top + mIndexBarRect.height());
    }

    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.length == 0)
            return 0;
        if (y < mIndexBarRect.top + mIndexBarMargin)
            return 0;
        if (y >= mIndexBarRect.top + mIndexBarRect.height() - mIndexBarMargin)
            return mSections.length - 1;
        return (int) ((y - mIndexBarRect.top - mIndexBarMargin) / ((mIndexBarRect.height() - 2 * mIndexBarMargin) / mSections.length));
    }

    private void fade(long delay) {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (mState) {
                case STATE_SHOWING:
                    // Fade in effect
                    mAlphaRate += (1 - mAlphaRate) * 0.2;
                    if (mAlphaRate > 0.9) {
                        mAlphaRate = 1;
                        setState(STATE_SHOWN);
                    }

                    mListView.invalidate();
                    fade(10);
                    break;
                case STATE_SHOWN:
                    // If no action, hide automatically
                    setState(STATE_HIDING);
                    break;
                case STATE_HIDING:
                    // Fade out effect
                    mAlphaRate -= mAlphaRate * 0.2;
                    if (mAlphaRate < 0.1) {
                        mAlphaRate = 0;
                        setState(STATE_HIDDEN);
                    }

                    mListView.invalidate();
                    fade(10);
                    break;
            }
        }

    };
}
