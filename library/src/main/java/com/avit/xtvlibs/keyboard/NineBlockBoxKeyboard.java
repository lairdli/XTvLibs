package com.avit.xtvlibs.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.avit.xtvlibs.R;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by laird on 2016/10/17.
 */

public class NineBlockBoxKeyboard extends LinearLayout implements View.OnClickListener{
    private  static Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");

    private TextView mSearchTextView;
    private PopupWindow mPopupWindow;
    private LinearLayout mPopupLinearLayout;
    private Context context;
    private HashMap<Button, LinearLayout> mButtonLinearLayoutHashMap = new HashMap<>();
    private InputFinishListener mListener;



    public NineBlockBoxKeyboard(Context context) {
        super(context);
        this.context = context;
    }

    public NineBlockBoxKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ninebox_keyboard, this, true);
        initView();
        this.context = context;

    }

    private void initView() {
        findViewById(R.id.key_clear).setOnClickListener(this);
        findViewById(R.id.key_back).setOnClickListener(this);
        findViewById(R.id.key_0).setOnClickListener(this);
        findViewById(R.id.key_1).setOnClickListener(this);
        findViewById(R.id.key_2).setOnClickListener(this);
        findViewById(R.id.key_3).setOnClickListener(this);
        findViewById(R.id.key_4).setOnClickListener(this);
        findViewById(R.id.key_5).setOnClickListener(this);
        findViewById(R.id.key_6).setOnClickListener(this);
        findViewById(R.id.key_7).setOnClickListener(this);
        findViewById(R.id.key_8).setOnClickListener(this);
        findViewById(R.id.key_9).setOnClickListener(this);
    }

    public void setSearchTextView(TextView searchTextView){
        this.mSearchTextView = searchTextView;
    }

    private void clearSearchTextView() {
        mSearchTextView.setText("");
    }

    private void deleteLastCharacterFromSearchTextView() {
        String text = mSearchTextView.getText().toString();
        if (text.length() >= 1) {
            mSearchTextView.setText(text.substring(0, text.length() - 1));
        }
    }

    private void appendSearchTextViewText(String input) {
        mSearchTextView.append(input);
    }

    private void showKeyboardPopupWindow(final Button btnParent) {
        final LinearLayout linearLayout = getPopupWindowLinearLayout(btnParent);

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(linearLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
        } else {
            mPopupWindow.setContentView(linearLayout);
        }

        linearLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int popupWidth = linearLayout.getMeasuredWidth();
        int popupHeight = linearLayout.getMeasuredHeight();

        int[] location = new int[2];
        btnParent.getLocationOnScreen(location);

        int linearLayoutX = location[0] + btnParent.getWidth() / 2 - popupWidth / 2;
        int linearLayoutY = location[1] + btnParent.getHeight() / 2 - popupHeight / 2;

        mPopupWindow.setFocusable(true); // 添加此属性才能获取焦点
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 添加此属性才能响应back按键
        mPopupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        mPopupWindow.showAtLocation(btnParent, Gravity.NO_GRAVITY, linearLayoutX, linearLayoutY);
    }

    private LinearLayout getPopupWindowLinearLayout(Button btnParent) {
        if (mButtonLinearLayoutHashMap.containsKey(btnParent)) {
            return mButtonLinearLayoutHashMap.get(btnParent);
        } else {
            LinearLayout linearLayout = createButtonLinearLayout(btnParent);
            mButtonLinearLayoutHashMap.put(btnParent, linearLayout);
            return linearLayout;
        }
    }

    private LinearLayout createButtonLinearLayout(Button btnParent) {
        mPopupLinearLayout = new LinearLayout(context);
        mPopupLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mPopupLinearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mPopupLinearLayout.setBackgroundResource(R.drawable.ninebox_keyboard_popupwindow_selector);

        for (int i = 0; i < btnParent.getText().length(); i++) {
            final String text = String.valueOf(btnParent.getText().charAt(i));
            Matcher m = CRLF.matcher(text);
            if(m.matches()){
                continue;
            }
            Button btn = new Button(context);
            btn.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            btn.setFocusable(true);
            btn.setFocusableInTouchMode(true);
            btn.setText(text);
            btn.setTextSize(30);
            btn.setTextColor(Color.WHITE);
            btn.setBackgroundResource(R.drawable.ninebox_keyboard_button_selector);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    appendSearchTextViewText(text);
                    dismissPopupWindow(mPopupLinearLayout);
                    mListener.onFinish(InputFinishListener.INPUT_NORMAL);
                }
            });
            mPopupLinearLayout.addView(btn);
        }
        return mPopupLinearLayout;
    }

    private boolean dismissPopupWindow(LinearLayout linearLayout) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            viewRequestFocus(linearLayout.getChildAt(0)); // reset焦点
            mPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    private void viewRequestFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    public void onContainerClick(View view){
        {
            if (view.getId() == R.id.key_clear) {
                clearSearchTextView();
            } else if (view.getId() == R.id.key_back) {
                deleteLastCharacterFromSearchTextView();
            } else if (view.getId() == R.id.key_1) {
                appendSearchTextViewText(((Button) view).getText().toString());
            } else if (view.getId() == R.id.key_0) {
                appendSearchTextViewText(((Button) view).getText().toString());
            } else {
                showKeyboardPopupWindow((Button) view);
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.key_clear) {
            clearSearchTextView();
            mSearchTextView.setText(R.string.search_tip);
            mListener.onFinish(InputFinishListener.INPUT_CLEAR);
        } else if (view.getId() == R.id.key_back) {
            if(!context.getString(R.string.search_tip).equals(mSearchTextView.getText().toString())){
                deleteLastCharacterFromSearchTextView();
            }
            if(TextUtils.isEmpty(mSearchTextView.getText().toString())){
                mSearchTextView.setText(R.string.search_tip);
            }

            mListener.onFinish(InputFinishListener.INPUT_BACK);
        } else if (view.getId() == R.id.key_1) {
            if(context.getString(R.string.search_tip).equals(mSearchTextView.getText().toString())){
                clearSearchTextView();
            }
            appendSearchTextViewText(((Button)view).getText().toString());
            mListener.onFinish(InputFinishListener.INPUT_NORMAL);
        } else if (view.getId() == R.id.key_0) {
            if(context.getString(R.string.search_tip).equals(mSearchTextView.getText().toString())){
                clearSearchTextView();
            }
            appendSearchTextViewText(((Button) view).getText().toString());
            mListener.onFinish(InputFinishListener.INPUT_NORMAL);
        } else {
            if(context.getString(R.string.search_tip).equals(mSearchTextView.getText().toString())){
                clearSearchTextView();
            }
            showKeyboardPopupWindow((Button) view);
        }

    }

    public void setInputFinishListener(InputFinishListener listener){
        mListener = listener;
    }

    public interface InputFinishListener {

        public static final  int INPUT_NORMAL = 0;
        public static final  int INPUT_CLEAR = INPUT_NORMAL+1;
        public static final  int INPUT_BACK = INPUT_CLEAR+1;
        void onFinish(int i);
    }
}
