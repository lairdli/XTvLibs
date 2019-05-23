package com.avit.xtvlibs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
/**
 *
 *
 * @author laird
 * @date 2019/5/23 14:51
 *
 */
public class ListViewTV extends ListView {

    private boolean enableLastPosition = true;

    private int lastCheckPosition = -1;


    public void setLastCheckPosition(int last_position) {
        this.lastCheckPosition = last_position;
    }

    public ListViewTV(Context context) {
        this(context, null);
    }

    public ListViewTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 崩溃了.
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void init(Context context, AttributeSet attrs) {
        this.setChildrenDrawingOrderEnabled(true);
    }


    public void setDefualtSelect(int pos) {
        requestFocusFromTouch();
        setSelection(pos);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        //ListView 重新获取焦点时，让其重新选择上次选中的item,而不是就近选择。必须item处于选中状态getSelectedItemPosition（）才有效，不然一直返回-1，

        Log.d("ListViewTV", "lastCheckPosition: " + lastCheckPosition + "gainFocus:" + gainFocus);
        if (lastCheckPosition > 0) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
            if (gainFocus && enableLastPosition) {
                setSelection(lastCheckPosition);
            }
            return;
        }

        int lastSelectItem = getSelectedItemPosition();

        Log.d("ListViewTV", "lastSelectItem: " + lastSelectItem + "gainFocus:" + gainFocus);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && enableLastPosition) {
            setSelection(lastSelectItem);
        }
    }

    public void setEnableLastPosition(boolean enableLastPosition) {
        this.enableLastPosition = enableLastPosition;
    }
}
