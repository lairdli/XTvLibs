/*
 * Copyright (c) 2016. The Android Open Source Project
 * Created by idisfkj
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.avit.xtvlibs.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;


import com.avit.xtvlibs.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * 
 * @author laird
 * @date 2019/5/23 15:06
 *
 */
public class PickerViewTV<T extends Object> extends View {

	private static final String TAG = "PickerViewTV";
	private Paint mPaint;
//	private Paint mRecPaint;
	private Paint mBitmapPaint;
	private Rect mSrcRect, mDestRect;
	private Bitmap bitmap ;
	private List<T> dataList;
	private Timer mTimer;
	private int screenWidth;
	private int screenHeight;
	private int maxTextSize = 80;
	private int minTextSize = 20;
	private boolean isReady = false;

	private float moveLength = 0;
	private int maxAlpha = 255;
	private int minAlpha = 120;
	private int position = -1;

	private static final float MARGIN = 2.2f;

	private float mSelectTextSize;
	private int mSelectTextColor;

	private float mSelectItemHeight;
	private int mSelectItemFocusColor;
	private int mSelectItemNormalColor;

	private float mNormalTextSize;
	private int mNormalTextColor;

	private static final float DEFAULT_TEXT_SIZE = 14.0f;
	private static final float DEFAULT_SELECT_HEIGHT = 30.0f;

	private float eventY;
	private MyTimeTask myTimeTask;
	private OnSelectorListener mListener;

	private boolean hasFoucs = false;

	private Context mContext;

	public PickerViewTV(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public PickerViewTV(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initFromAttributes(context,attrs);
		init();

	}

	private void init() {
//		mRecPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBitmapPaint.setFilterBitmap(true);
		mBitmapPaint.setDither(true);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(mSelectTextColor);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setStyle(Paint.Style.FILL);
		dataList = new ArrayList<T>();
		mTimer = new Timer();
		this.setOnFocusChangeListener(onFocusChangeListener);
	}

	/**
	 * Basic data initialization
	 */
	@SuppressWarnings("ResourceType")
	private void initFromAttributes(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PickView);

		mSelectItemFocusColor = a.getColor(R.styleable.PickView_select_item_focus_color, Color.BLUE);
		mSelectItemNormalColor = a.getColor(R.styleable.PickView_select_item_normal_color, Color.GRAY);
		mSelectTextColor = a.getColor(R.styleable.PickView_select_text_color, Color.WHITE);
		mNormalTextColor = a.getColor(R.styleable.PickView_normal_text_color, Color.WHITE);

		mSelectTextSize = a.getDimensionPixelSize(R.styleable.PickView_select_text_size, dip2px(getContext(), DEFAULT_TEXT_SIZE));
		mNormalTextSize = a.getDimensionPixelSize(R.styleable.PickView_normal_text_size, dip2px(getContext(), DEFAULT_TEXT_SIZE));
		mSelectItemHeight= a.getDimensionPixelSize(R.styleable.PickView_select_item_height, dip2px(getContext(), DEFAULT_SELECT_HEIGHT));

		a.recycle();
	}

	/**
	 * set the default centered text
	 * 
	 * @param position
	 *            position in the data
	 */
	public void setSelected(int position) {
		this.position = position;
	}

	public void setData(List<T> list) {
		dataList = list;
		// if not set the default centered text,show the default centered text
		// in the data
		if (position == -1) {
			position = dataList.size() / 2;
		}
		invalidate();
	}

	// @Override
	// public boolean dispatchKeyEvent(KeyEvent event) {
	// Log.d("hailongqiu", "dispatchKeyEvent");
	// return super.dispatchKeyEvent(event);
	// }

	private boolean isKeyDown = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown");
		if (isKeyDownOrUp(keyCode)) {
			if (!isKeyDown) {
				doKeyDown(event);
				isKeyDown = true;
			}
			doKeyMove(event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyUp");
		if (isKeyDownOrUp(keyCode)) {
			if (isKeyDown) {
				doKeyUp(event);
				isKeyDown = false;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	private void doKeyDown(KeyEvent event) {
		if (myTimeTask != null) {
			myTimeTask.cancel();
			myTimeTask = null;
		}
	}

	private void doKeyMove(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			moveHeadToFoot();
		} else {
			moveFootTOHead();
		}
		invalidate();
	}

	private void doKeyUp(KeyEvent event) {
		completeSelector();
		if (Math.abs(moveLength) < 0.0001) {
			moveLength = 0;
			return;
		}
		if (myTimeTask != null) {
			myTimeTask.cancel();
			myTimeTask = null;
		}
		myTimeTask = new MyTimeTask(mHandler);
		mTimer.schedule(myTimeTask, 0, 100);
	}

	private boolean isKeyDownOrUp(int keyCode) {
		return (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) || (keyCode == KeyEvent.KEYCODE_DPAD_UP);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		screenWidth = getMeasuredWidth();
		screenHeight = getMeasuredHeight();
		maxTextSize = (int) (screenHeight / 16.0f);
		minTextSize = (int) (maxTextSize / 2.0f);
		
		
		isReady = true;
		invalidate();
	}

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocusd) {
			if(hasFocusd){
				hasFoucs = true;
			}else{
				hasFoucs = false;
			}
		}
	};



	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isReady&&position!=-1)
			onDrawView(canvas);
	}

	private void onDrawView(Canvas canvas) {
		moveLength = 0;
		float scal = getParabola((screenHeight / 6.0f), moveLength);
		float x = screenWidth / 2.0f;
		float y = screenHeight / 2.0f + moveLength;
//		float size = (maxTextSize - minTextSize) * scal + minTextSize;
		Paint.FontMetricsInt pfm = mPaint.getFontMetricsInt();
		float baseLine = y - (pfm.top + pfm.bottom) / 2;
		mPaint.setTextSize(mSelectTextSize);
		mPaint.setAlpha((int) ((maxAlpha - minAlpha) * scal + minAlpha));





		if(hasFoucs){
//			mRecPaint.setColor(0xff14A8FE);// 设置灰色
//			mRecPaint.setColor(mSelectItemFocusColor);// 设置灰色
//			bitmap= BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pickview_sel);
			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.pickview_sel)).getBitmap();
		}else{
//			mRecPaint.setColor(0xff6A7088);// 设置灰色
//			bitmap= BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pickview_unsel);
			bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.pickview_unsel)).getBitmap();
//			mRecPaint.setColor(mSelectItemNormalColor);// 设置灰色
		}
		int top = (int) (y-mSelectItemHeight);
		int bottom = (int) (y+mSelectItemHeight);
		mSrcRect = new Rect(0, top, screenWidth,bottom);
		mDestRect = new Rect(0, top, screenWidth, bottom);

//		mRecPaint.setStyle(Paint.Style.FILL);//设置填满
		Log.d(TAG,"screenWidth:"+screenWidth+"  y:"+y+" moveLength:"+moveLength);
//		canvas.drawRect(0, y-mSelectItemHeight, screenWidth, y+mSelectItemHeight, mRecPaint);// 长方形
		canvas.drawBitmap(bitmap, null,mDestRect, mBitmapPaint);// 长方形
//		canvas.drawBitmap(bitmap, 0,top, mBitmapPaint);// 长方形

		canvas.drawText(String.valueOf(dataList.get(position)), x, baseLine, mPaint);

		for (int i = 1; position - i >= 0; i++) {
			drawOtherView(canvas, i, -1);
		}
		for (int i = 1; position + i < dataList.size(); i++) {
			drawOtherView(canvas, i, 1);
		}
	}

	private void drawOtherView(Canvas canvas, int i, int direction) {
		float offsetY = (MARGIN * minTextSize * i + moveLength * direction);
		float scal = getParabola(screenHeight / 4.0f, offsetY);
		float x = screenWidth / 2.0f;
		float y = screenHeight / 2.0f + direction * offsetY;
//		float size = (maxTextSize - minTextSize) * scal + minTextSize;
		float alpha = (maxAlpha - minAlpha) * scal + minAlpha;
		Paint.FontMetricsInt pfm = mPaint.getFontMetricsInt();
		float baseLine = (float) (y - (pfm.top + pfm.bottom) / 2.0);
		mPaint.setTextSize(mNormalTextSize);
		mPaint.setAlpha((int) alpha);
		mPaint.setColor(mNormalTextColor);
		canvas.drawText(String.valueOf(dataList.get(position + direction * i)), x, baseLine, mPaint);
	}

	/**
	 * parabola
	 * 
	 * @param zero
	 * @param offsetY
	 * @return y = x^2
	 */
	private float getParabola(float zero, float offsetY) {
		float res = (float) (1 - Math.pow(offsetY / zero, 2));
		return res < 0 ? 0 : res;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			doDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			doMove(event);
			break;
		case MotionEvent.ACTION_UP:
			doUp(event);
			break;
		}
		return true;
	}

	private void doDown(MotionEvent event) {
		if (myTimeTask != null) {
			myTimeTask.cancel();
			myTimeTask = null;
		}
		eventY = event.getY();
	}

	private void doMove(MotionEvent event) {
		moveLength += event.getY() - eventY;
		if (moveLength > MARGIN * minTextSize / 2) {
			moveFootTOHead();
			moveLength = moveLength - MARGIN * minTextSize;
		} else if (moveLength < -MARGIN * minTextSize / 2) {
			moveHeadToFoot();
			moveLength = moveLength + MARGIN * minTextSize;
		}
		eventY = event.getY();
		invalidate();

	}

	private void moveHeadToFoot() {
//		T head = dataList.get(0);
//		dataList.remove(0);
//		dataList.add(head);
		if(position<dataList.size()-1){
			position++;
		}
	}

	private void moveFootTOHead() {
//		T foot = dataList.get(dataList.size() - 1);
//		dataList.remove(dataList.get(dataList.size() - 1));
//		dataList.add(0, foot);
		if(position>0){
			position--;
		}
	}

	private void doUp(MotionEvent event) {
		if (Math.abs(moveLength) < 0.0001) {
			moveLength = 0;
			return;
		}
		if (myTimeTask != null) {
			myTimeTask.cancel();
			myTimeTask = null;
		}
		myTimeTask = new MyTimeTask(mHandler);
		mTimer.schedule(myTimeTask, 0, 100);
	}

	public class MyTimeTask extends TimerTask {
		private Handler mHandler;

		public MyTimeTask(Handler handler) {
			mHandler = handler;
		}

		@Override
		public void run() {
			mHandler.sendMessage(mHandler.obtainMessage());
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Math.abs(moveLength) < MARGIN * minTextSize / 2) {
				moveLength = 0;
				if (myTimeTask != null) {
					myTimeTask.cancel();
					myTimeTask = null;
					completeSelector();
				}
			} else {
				if (moveLength < 0) {
					moveHeadToFoot();
				} else {
					moveFootTOHead();
				}
				moveLength = moveLength - moveLength / Math.abs(moveLength) * MARGIN * minTextSize / 2;
			}
			invalidate();
		}
	};

	private void completeSelector() {
		if (mListener != null)
			mListener.onSelector(String.valueOf(dataList.get(position)));
	}

	public void setOnSelectorListener(OnSelectorListener listener) {
		mListener = listener;
	}

	public interface OnSelectorListener {
		void onSelector(String text);
	}

	public  int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
