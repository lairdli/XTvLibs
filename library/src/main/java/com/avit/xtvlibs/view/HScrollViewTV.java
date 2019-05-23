package com.avit.xtvlibs.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

import java.lang.reflect.Field;

/**
 *
 * 
 * @author laird
 * @date 2019/5/23 15:08
 *
 */
public class HScrollViewTV extends HorizontalScrollView {
    private FixedSpeedScroller mScroller;
    private float mSelectedItemOffsetStart =10;
    private float mSelectedItemOffsetEnd = 10;
    private boolean mIsSelectedCentered = false;
    private OnArrowScrollListneter mOnArrowScrollListneter;
    
    public HScrollViewTV(Context context) {
        this(context, null);
    }

    public HScrollViewTV(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HScrollViewTV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initScroller(getContext());

    }

    private void initScroller(Context context) {
        if(null != mScroller) {
            return;
        }
        try {
            mScroller = new FixedSpeedScroller(context, new AccelerateInterpolator());
            Field field = this.getClass().getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(this, mScroller);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }
    
    /**
     * 设置滚动时长
     * @param duration
     */
    public void setScrollerDuration(int duration){
        initScroller(getContext());
        if(null != mScroller) {
            mScroller.setScrollDuration(duration);
        }
    }

    /**
     * 获取滚动时长
     * @return
     */
    public int getScrollerDuration() {
        initScroller(getContext());
        if(null != mScroller) {
            return mScroller.getScrollDuration();
        }
        return 0;
    }

    public void setSelectedCentered(boolean selectedCentered) {
        mIsSelectedCentered = selectedCentered;
    }

    public boolean isSelectedCentered() {
        return mIsSelectedCentered;
    }

    public void setSelectedItemOffsetStart(float selectedItemOffsetStart) {
        mSelectedItemOffsetStart = selectedItemOffsetStart;
    }

    public float getSelectedItemOffsetStart() {
        return mSelectedItemOffsetStart;
    }

    public void setSelectedItemOffsetEnd(float selectedItemOffsetEnd) {
        mSelectedItemOffsetEnd = selectedItemOffsetEnd;
    }

    public float getSelectedItemOffsetEnd() {
        return mSelectedItemOffsetEnd;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        if (getChildCount() == 0) {
            return 0;
        }

        int width = getWidth();
        int screenLeft = getScrollX();
        int screenRight = screenLeft + width;

        if(mIsSelectedCentered && null != rect && !rect.isEmpty()) {
            mSelectedItemOffsetStart = width - getPaddingLeft() - getPaddingRight() - rect.width();
            mSelectedItemOffsetStart /= 2f;
            mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
        }

        if (rect.left > 0) {
            screenLeft += mSelectedItemOffsetStart;
        }
        if (rect.right < getChildAt(0).getWidth()) {
            screenRight -= mSelectedItemOffsetEnd;
        }

        int scrollXDelta = 0;
        if (rect.right > screenRight && rect.left > screenLeft) {
            if (rect.width() > width) {
                scrollXDelta += (rect.left - screenLeft);
            } else {
                scrollXDelta += (rect.right - screenRight);
            }
            int right = getChildAt(0).getRight();
            int distanceToRight = right - screenRight;
            scrollXDelta = Math.min(scrollXDelta, distanceToRight);

        } else if (rect.left < screenLeft && rect.right < screenRight) {
            if (rect.width() > width) {
                scrollXDelta -= (screenRight - rect.right);
            } else {
                scrollXDelta -= (screenLeft - rect.left);
            }
            scrollXDelta = Math.max(scrollXDelta, -getScrollX());
        }
        return scrollXDelta;
    }

    /**
     * 固定速度的Scroller
     * */
    public static class FixedSpeedScroller extends OverScroller {
        private int mDuration = 600;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setScrollDuration(int time) {
            mDuration = time;
        }

        public int getScrollDuration() {
            return mDuration;
        }
    }

    @Override
    public boolean arrowScroll(int direction) {
        if(null != mOnArrowScrollListneter){
            if(mOnArrowScrollListneter.arrowScroll(direction, this)) {
                return true;
            }
        }
        return super.arrowScroll(direction);
    }

    public void setOnArrowScrollListneter(OnArrowScrollListneter listneter){
        this.mOnArrowScrollListneter = listneter;
    }

    public interface OnArrowScrollListneter{
        public boolean arrowScroll(int direction, HScrollViewTV scrollView);
    }
}
