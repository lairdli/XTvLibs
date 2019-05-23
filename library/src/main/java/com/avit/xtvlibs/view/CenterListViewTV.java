package com.avit.xtvlibs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 *
 * 
 * @author laird
 * @date 2019/5/23 15:08
 *
 */
public class CenterListViewTV extends ListView {
    private LinearLayout mFocus;
    private CenterListViewTV instance;
    private int deta_scroll_distance = 0;

    public CenterListViewTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        instance = this;
        instance.setOnKeyListener(mOnKeyListener);
    }

    public void setDetaScrollDistance(int deta_scroll_distance) {
        this.deta_scroll_distance = deta_scroll_distance;
    }

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
            // TODO 自动生成的方法存根
            switch (arg2.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (arg1 == KeyEvent.KEYCODE_DPAD_UP || (arg1 == KeyEvent.KEYCODE_DPAD_DOWN))
                        moveFocus(arg1, arg0);
                    break;

            }

            return false;
        }

        private void moveFocus(int direction, View focus) {
            int index = instance.getSelectedItemPosition();
            //丢失焦点的时候返回。
            if (index == -1) {
                return;
            }
            //获取下一个焦点
            int position;
            if (index < instance.getAdapter().getCount() - 1 && direction == KeyEvent.KEYCODE_DPAD_DOWN)
                position = index + 1 - getFirstVisiblePosition();
            else if (index > 0 && direction == KeyEvent.KEYCODE_DPAD_UP)
                position = index - 1 - getFirstVisiblePosition();
            else
                position = index - getFirstVisiblePosition();
            mFocus = (LinearLayout) instance.getChildAt(position);
            //焦点为空返回
            if (mFocus == null)
                return;
            int[] loc = new int[2];
            instance.getLocationOnScreen(loc);

            int[] screenLocation = new int[2];
            mFocus.getLocationOnScreen(screenLocation);

            //这里的高度需要有一个调整值，不知道为什么。
            int height = ((LinearLayout) mFocus).getHeight() + 2;

            if (screenLocation[1] < loc[1] + 8 * height) {
                instance.smoothScrollBy(instance.getScrollY() - height, 0);
            }
            if (screenLocation[1] > loc[1] + 8 * height) {
                instance.smoothScrollBy(instance.getScrollY() + height, 0);
            }

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        int index = instance.getSelectedItemPosition();
        //丢失焦点的时候返回。
        if (index == -1) {
            return false;
        }

        smoothScrollToPositionFromTop(index ,instance.getHeight() / 2 - deta_scroll_distance / 2, 500);

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);

        int index = instance.getSelectedItemPosition();
        //丢失焦点的时候返回。
        if (index == -1) {
            return false;
        }

        smoothScrollToPositionFromTop(index ,instance.getHeight() / 2 - deta_scroll_distance / 2, 500);
        return false;
    }
}
