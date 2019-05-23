package com.avit.xtvlibs.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.avit.xtvlibs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * HorizontalScrollMenu
 * 
 * @author laird.li
 * @version creat: 2016.07.26
 * 
 */
public class HScrollMenuTV extends LinearLayout {
	private RadioGroup rg_items;
	private List<RadioButton> rb_items = new ArrayList<RadioButton>();
	private ViewPager vp_content;
	private Context mContext;
	private ColorStateList mColors;
	private int mItemMarginLeftRight ;
	private int mItemMarginTopBottom ;
	private int mPaddingLeftRight;
	private int mPaddingTopBottom ;
	private int mTextColor;
	private int mItemBackGround;
	private float mTextSize ;
	private boolean mFocusBindCheck;
	private boolean mEnableViewPage;

	private final  int DEFAULT_TEXT_SIZE = 18;
	private final  int DEFAULT_MARGIN_SIZE = 10;
	private final  int DEFAULT_PADDING_LEFT_SIZE = 20;
	private final  int DEFAULT_PADDING_TOP_SIZE = 10;

	private HorizontalScrollView hsv_menu;
	private List<String> mTitles = new ArrayList<String>(); // menu name
	private FragmentStatePagerAdapter mFragmentStatePagerAdapter;

    private OnTopBarFocusChange onTopBarFocusChange;
    private OnTopBarItemOnKeyListener onTopBarKeyListener;
	private OnTopBarItemOnClickListener onTopbarItemOnClickListener;
	private int check_positon = 0;
	private boolean isSetCheckEnable = false;

    /**
     * 顶部栏获得焦点
     */
    private boolean isTopFocused = false;

	private boolean mSwiped = true; // swipe or not

    /**
     * 页数 当前页
     */
    private int pageCount, pageCurrent;

	private OnFocusChangeListener mChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			if (hasFocus) {

				// 翻页
				int targetPage = Integer.parseInt(v.getTag().toString());
				if (pageCurrent != targetPage && !isTopFocused) {
					rb_items.get(pageCurrent).requestFocus();
					if (!mEnableViewPage) {
						if(isSetCheckEnable){
							pageCurrent = targetPage;
							isSetCheckEnable = false;
						}
						rb_items.get(pageCurrent).requestFocus();
						moveItemToCenter(rb_items.get(pageCurrent));
//						pageCurrent = targetPage;
					}
					return;
				}

				if (onTopBarFocusChange != null) {
					onTopBarFocusChange.onFocusChange(isTopFocused,
							targetPage);
				}
				if (mFocusBindCheck) {
					rb_items.get(pageCurrent).setChecked(true);
				}

				if (isTopFocused && mEnableViewPage) {
					vp_content.setCurrentItem(targetPage);
				}
				if(mEnableViewPage){
					isTopFocused = true;
				}



			}
		}
	};

    private OnKeyListener mOnKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    isTopFocused = false;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
					if(!mEnableViewPage){
						pageCurrent--;
					}

                    if (pageCurrent == 0) {
                        isTopFocused = false;
                    }
					if(pageCurrent < 0){
						pageCurrent = 0;
					}

                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
					if(!mEnableViewPage){
						pageCurrent++;
					}

                    if (pageCurrent == pageCount - 1) {
                        isTopFocused = false;
                    }
					if(pageCurrent >pageCount-1 ){
						pageCurrent = pageCount -1;
					}
                }
            }

			if(onTopBarKeyListener!=null){
				return  onTopBarKeyListener.onTopbarItemOnKey(v,keyCode,event);
			}
			return false;
        }
    };


	public HScrollMenuTV(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public HScrollMenuTV(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initFromAttributes(context,attrs);
	}


	public HScrollMenuTV(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initFromAttributes(context,attrs);
		mContext = context;
		View v = LayoutInflater.from(context).inflate(R.layout.horizontal_scroll_menu, this, true);
		rg_items = (RadioGroup) v.findViewById(R.id.rg_items);
		mColors = getResources().getColorStateList(mTextColor);
		vp_content = (ViewPager) v.findViewById(R.id.vp_content);
		hsv_menu = (HorizontalScrollView) v.findViewById(R.id.hsv_menu);


		//	mItemBackGround = R.drawable.bg_rb_checked;
	}

	/**
	 * Basic data initialization
	 */
	@SuppressWarnings("ResourceType")
	private void initFromAttributes(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollMenu);

		mItemMarginLeftRight = a.getDimensionPixelSize(R.styleable.HorizontalScrollMenu_item_margin_left_right, dip2px(getContext(), DEFAULT_MARGIN_SIZE));
		mItemMarginTopBottom = a.getDimensionPixelSize(R.styleable.HorizontalScrollMenu_padding_top_bottom, dip2px(getContext(), DEFAULT_MARGIN_SIZE));
		mPaddingLeftRight = a.getDimensionPixelSize(R.styleable.HorizontalScrollMenu_padding_left_right, dip2px(getContext(), DEFAULT_PADDING_LEFT_SIZE));
		mPaddingTopBottom = a.getDimensionPixelSize(R.styleable.HorizontalScrollMenu_padding_top_bottom,dip2px(getContext(), DEFAULT_PADDING_TOP_SIZE));
		mTextColor = a.getResourceId(R.styleable.HorizontalScrollMenu_text_background, R.drawable.default_textcolor_selector);

		mItemBackGround = a.getResourceId(R.styleable.HorizontalScrollMenu_item_background, R.drawable.default_item_selector);
		mTextSize = a.getDimensionPixelSize(R.styleable.HorizontalScrollMenu_text_size, dip2px(getContext(), DEFAULT_TEXT_SIZE));

		mFocusBindCheck = a.getBoolean(R.styleable.HorizontalScrollMenu_foucs_bind_checked,false);
		mEnableViewPage = a.getBoolean(R.styleable.HorizontalScrollMenu_foucs_bind_checked,true);

		a.recycle();
	}

	public  void setTitlesNoViewPage(List<String> mTitles){
		this.mTitles.clear();
		this.mTitles.addAll(mTitles);
		mEnableViewPage = false;
		initMenuItems(mTitles);
		vp_content.setVisibility(GONE);
	}


	public void setAdapter(FragmentStatePagerAdapter mAdapter) {
		if (null != mAdapter) {
			mFragmentStatePagerAdapter = mAdapter;
			initView(mFragmentStatePagerAdapter);
		}
	}
	/**
	 * initView
	 * 
	 * @param mFragmentAdapter
	 */
	private void initView(FragmentStatePagerAdapter mFragmentAdapter) {
		if (null == mFragmentAdapter) {
			return;
		}
		for (int i = 0; i < mFragmentAdapter.getCount(); i++) {
			mTitles.add((String) mFragmentAdapter.getPageTitle(i));
		}
		initMenuItems(mTitles);
		initContentViews(mFragmentAdapter);
	}
	/**
	 * initView
	 * 
	 * @param mFragmentAdapter
	 */
	private void initView(FragmentPagerAdapter mFragmentAdapter) {
		if (null == mFragmentAdapter) {
			return;
		}
		for (int i = 0; i < mFragmentAdapter.getCount(); i++) {
			mTitles.add((String) mFragmentAdapter.getPageTitle(i));
		}
		initMenuItems(mTitles);
		initContentViews(mFragmentAdapter);
	}

	/**
	 * notifyDataSetChanged
	 * 
	 * @param adapter
	 */
	public void notifyDataSetChanged(FragmentPagerAdapter adapter) {
		rg_items.removeAllViews();
		rb_items.clear();
		initView(adapter);
	}
	
	/**
	 * notifyDataSetChanged
	 * 
	 * @param mFragmentAdapter
	 */
	public void notifyDataSetChanged(FragmentStatePagerAdapter mFragmentAdapter) {
		rg_items.removeAllViews();
		rb_items.clear();
		mTitles.clear();
		initView(mFragmentAdapter);
	}

	/**
	 * initMenuItems
	 * 
	 * @param items
	 */
	private void initMenuItems(List<String> items) {
		if (null != items && 0 != items.size()) {
			rg_items.removeAllViews();
			rb_items.clear();
            pageCount = items.size();
			rg_items.setOnCheckedChangeListener(mItemListener);
			rg_items.setOnFocusChangeListener(mChangeListener);

			RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
					RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(mItemMarginLeftRight,mItemMarginTopBottom,mItemMarginLeftRight,mItemMarginTopBottom);
			for (int i =0;i<items.size();i++) {
				String str = items.get(i);
				RadioButton rb_item = (RadioButton) LayoutInflater.from(mContext)
						.inflate(R.layout.horizontal_scroll_menu_item, null);
				rb_item.setTextColor(mColors);
				rb_item.setText(str);
				rb_item.setTextSize(mTextSize);
				rb_item.setGravity(Gravity.CENTER);


				rb_item.setTag(i);
			//	rb_item.setId(1111+i);
				rb_item.setFocusable(true);
				if(mItemBackGround!=-1 && !mEnableViewPage){
					rb_item.setBackgroundResource(mItemBackGround);
//				btn.setText(text);
				}
                rb_item.setOnKeyListener(mOnKeyListener);
				rb_item.setOnFocusChangeListener(mChangeListener);
				rb_item.setPadding(mPaddingLeftRight, mPaddingTopBottom, mPaddingLeftRight, mPaddingTopBottom);

				rb_item.setLayoutParams(layoutParams);

				rg_items.addView(rb_item);
				rb_items.add(rb_item);
			}
			rb_items.get(check_positon).setChecked(true);
			rb_items.get(check_positon).requestFocus();
			pageCurrent = check_positon;
		}

	}
	
	/**
	 * initContentViews
	 * 
	 * @param mFragmentStatePagerAdapter
	 */
	private void initContentViews(FragmentStatePagerAdapter mFragmentStatePagerAdapter) {
		if (null == mFragmentStatePagerAdapter) {
			return;
		}
		vp_content.setAdapter(mFragmentStatePagerAdapter);
		vp_content.setOnPageChangeListener(mPageListener);
	}

	/**
	 * initContentViews
	 * 
	 * @param mFragmentPagerAdapter
	 */
	private void initContentViews(FragmentPagerAdapter mFragmentPagerAdapter) {
		if (null == mFragmentPagerAdapter) {
			return;
		}
		vp_content.setAdapter(mFragmentPagerAdapter);
		vp_content.setOnPageChangeListener(mPageListener);

	}

	/**
	 * setCheckedBackground
	 * 
	 * @param resId
	 */
	public void setCheckedBackground(int resId) {
		mItemBackGround = resId;
	}

	public void setCheckedItemPosition(int postion) {
		isSetCheckEnable = true;
		check_positon = postion;
	}

	public void setCheckedItem(int postion) {
		pageCurrent = postion;
//		isSetCheckEnable = true;
		rb_items.get(postion).setChecked(true);
	}
	
	

	/**
	 * OnCheckedChangeListener
	 */
	private OnCheckedChangeListener mItemListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			RadioButton btn = (RadioButton) group.findViewById(checkedId);
			if(mEnableViewPage) {
				setMenuItemsNullBackground();
				if (mItemBackGround != -1) {
					btn.setBackgroundResource(mItemBackGround);
//				btn.setText(text);
				}
			}


			int position = 0;
			for (int i = 0; i < rb_items.size(); i++) {
				if (rb_items.get(i) == btn) {
					position = i;
				}
			}
			if(onTopbarItemOnClickListener !=null){
				onTopbarItemOnClickListener.onTopbarItemOnClick(position);
			}
			if(mEnableViewPage){
				vp_content.setCurrentItem(position, mSwiped);
			}
			moveItemToCenter(btn);
		}

	};

	/**
	 * OnPageChangeListener
	 */
	private OnPageChangeListener mPageListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int postion) {
			// TODO Auto-generated method stub
			rb_items.get(postion).setChecked(true);
            pageCurrent = postion;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * moveItemToCenter
	 * 
	 * @param rb
	 */
	private void moveItemToCenter(RadioButton rb) {
		int screenWidth = getWidth();//dm.widthPixels;
		int[] locations = new int[2];
		rb.getLocationInWindow(locations);
		int rbWidth = rb.getWidth();
		
		int[] hLocations = new int[2];
		hsv_menu.getLocationInWindow(hLocations);
 		hsv_menu.smoothScrollBy((locations[0] - hLocations[0] + rbWidth / 2 - screenWidth / 2), 0);
	}
	
	/**
	 * @param visibility  One of VISIBLE, INVISIBLE, or GONE. 
	 */
	public void setMenuVisibility(int visibility){
		hsv_menu.setVisibility(visibility);
	}

	/**
	 * setMenuItemsNullBackground
	 */
	private void setMenuItemsNullBackground() {
		if (null == rg_items) {
            return;
        }

		for (int i = 0; i < rg_items.getChildCount(); i++) {
			View v = rg_items.getChildAt(i);
			v.setBackgroundResource(android.R.color.transparent);
		}
	}

	/**
	 * 视图页的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	static class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mViews;

		public MyViewPagerAdapter(List<View> views) {
			// TODO Auto-generated constructor stub
			mViews = views;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(mViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(mViews.get(position));
			return mViews.get(position);
		}
	}


    public interface OnTopBarFocusChange {
        public void onFocusChange(boolean hasFocus, int postion);
    }

    public void setOnTopBarFocusChangeListener(OnTopBarFocusChange listener) {
        this.onTopBarFocusChange = listener;
    }

	public interface OnTopBarItemOnClickListener{
		public void onTopbarItemOnClick(int postion);
	}

	public interface OnTopBarItemOnKeyListener{
		public boolean onTopbarItemOnKey(View v, int keyCode, KeyEvent event);
	}

	public void setOnTopBarItemOnKeyListener(OnTopBarItemOnKeyListener listener) {
		this.onTopBarKeyListener = listener;
	}

	public void setOnTopbarItemOnClickListener(OnTopBarItemOnClickListener listener) {
		this.onTopbarItemOnClickListener = listener;
	}

	public  int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
