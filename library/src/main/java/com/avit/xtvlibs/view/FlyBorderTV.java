package com.avit.xtvlibs.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.avit.xtvlibs.R;


/**
 * @author laird.li
 */
public class FlyBorderTV extends View {
    public final static String TAG = "FlyBorderView";

    private int borderWidth = 20;//焦点移动飞框的边框
    private int duration = 30;//动画持续时间

    public FlyBorderTV(Context context) {
        this(context, null);
    }

    public FlyBorderTV(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlyBorderTV(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        borderWidth = getContext().getResources().getDimensionPixelSize(R.dimen.h_10);
    }

    /**
     * @param newFocus 下一个选中项视图
     * @param scale    选中项视图的伸缩大小
     */
    public void attachToView(View newFocus, float scale) {
        int[] outLocation = new int[2];
        newFocus.getLocationInWindow(outLocation);
        Log.d(TAG, "attachToView: ------------------------------------begin----------------------------");
        Log.d(TAG, String.format("attachToView: [%d, %d]", outLocation[0], outLocation[1]));

        int width = newFocus.getWidth();
        int height = newFocus.getHeight();
        int x = (outLocation[0] % displayMetrics.widthPixels);
        int y = (outLocation[1] % displayMetrics.heightPixels);

        int widthInc = (int) (width * scale + 2 * borderWidth - getWidth());//当前选中项与下一个选中项的宽度偏移量
        int heightInc = (int) (height * scale + 2 * borderWidth - getHeight());//当前选中项与下一个选中项的高度偏移量

        int[] flyLocation = new int[2];
        getLocationInWindow(flyLocation);
        Log.d(TAG, String.format("attachToView: fly [%d, %d]", flyLocation[0], flyLocation[1]));


        float translateX = x - borderWidth - newFocus.getWidth() * (scale - 1 ) / 2;//飞框到达下一个选中项的X轴偏移量
        float translateY = y - borderWidth - newFocus.getHeight() * (scale - 1) / 2;//飞框到达下一个选中项的Y轴偏移量

        Log.d(TAG, String.format("attachToView: [%d, %d]->[%.2f, %.2f]", widthInc, heightInc, translateX, translateY));
        Log.d(TAG, "attachToView: ------------------------------------end------------------------------");

        startTotalAnim(widthInc, heightInc, translateX, translateY);//调用飞框 自适应和移动 动画效果
    }

    final DisplayMetrics displayMetrics = getContext().getApplicationContext().getResources().getDisplayMetrics();



    /**
     * 飞框 自适应和移动 动画效果
     *
     * @param widthInc   宽度偏移量
     * @param heightInc  高度偏移量
     * @param translateX X轴偏移量
     * @param translateY Y轴偏移量
     */
    private void startTotalAnim(final int widthInc, final int heightInc, float translateX, float translateY) {
        final int width = getWidth();//当前飞框的宽度
        final int height = getHeight();//当前飞框的高度
        ValueAnimator widthAndHeightChangeAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);//数值变化动画器，能获取平均变化的值
        if (widthInc != 0 || heightInc != 0) {//判断 减少绘制时间
            widthAndHeightChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setFlyBorderLayoutParams((int) (width + widthInc * Float.parseFloat(valueAnimator.getAnimatedValue().toString())),
                            (int) (height + heightInc * Float.parseFloat(valueAnimator.getAnimatedValue().toString())));//设置当前飞框的宽度和高度的自适应变化
                }
            });
        }

        ObjectAnimator translationX = ObjectAnimator.ofFloat(this, "translationX", translateX);//X轴移动的属性动画
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this, "translationY", translateY);//y轴移动的属性动画

        AnimatorSet set = new AnimatorSet();//动画集合
        set.play(widthAndHeightChangeAnimator).with(translationX).with(translationY);//动画一起实现
        set.setDuration(duration);
        set.setInterpolator(new LinearInterpolator());//设置动画插值器
        set.start();//开始动画
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private void setFlyBorderLayoutParams(int width, int height) {//设置焦点移动飞框的宽度和高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }

    private Rect findLocation(View view) {
        ViewGroup viewGroup = (ViewGroup) this.getParent();
        if (viewGroup != null && view != null) {
            Rect rect = new Rect();
            viewGroup.offsetDescendantRectToMyCoords(view, rect);//将一个在该视图的子视图坐标系中的Rect偏移到该视图的坐标系中
            return rect;
        }
        return null;
    }
}