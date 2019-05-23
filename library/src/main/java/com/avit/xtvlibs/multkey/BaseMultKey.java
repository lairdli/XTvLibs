package com.avit.xtvlibs.multkey;

import android.util.Log;
import android.view.KeyEvent;

/**
 * @author laird.li
 * @date 2018/11/6
 **/
public abstract class BaseMultKey implements IMultKey {
    protected String TAG ;

    //组合键序列
    protected int[] MULT_KEY ;
    //允许用户在多少时间间隔里输入按键
    protected int CHECK_NUM_ALLAW_MAX_DELAYED = 3000;

    //记录用户连续输入了多少个有效的键
    private  int check_num = 0;

    private  long lastEventTime = 0;//最后一次用户输入按键的时间

    BaseMultKey() {
        TAG =this.getClass().getSimpleName();
        initTrigger();
    }



    @Override
    public boolean handlerMultKey(int keyCode, KeyEvent event) {
        // 是否是有效按键输入
        boolean vaildKey = checkKey(keyCode, event.getEventTime());
        // 是否触发组合键
        if (vaildKey && checkMultKey()) {
            //执行触发
            onTrigger();
            //触发完成后清除掉原先的输入
            clearKeys();
            return true;

        }
        return false;
    }

    @Override
    public boolean checkKey(int keycode, long eventTime) {
        boolean check;
        int delayed;
        //转换为实际数值
        Log.i(TAG, "checkKey lastEventTime="+lastEventTime);
        Log.i(TAG, "checkKey num= "+ keycode +" , eventTime = "+eventTime);
        //首次按键
        if(lastEventTime==0){
            delayed = 0;
        }else{
            //非首次按键
            delayed = (int)(eventTime-lastEventTime);
        }
        check = checkKeyValid(keycode, delayed);

        lastEventTime = check?eventTime:0L;

        Log.i(TAG, "checkKey check key valid = "+check);
        return check;
    }
    /**
     * 传入用户输入的按键
     * @param num
     * @param delayed 两次按键之间的时间间隔
     * @return
     */
    private  boolean checkKeyValid(int num,int delayed){
        Log.i(TAG, "checkKey num= "+num+" , delayed = "+delayed);
        //如果超过最大时间间隔，则重置
        if(delayed>CHECK_NUM_ALLAW_MAX_DELAYED){
            check_num = 0;
            return false;
        }
        //如果输入的数刚好等于校验位置的数，则有效输入+1
        if(check_num<MULT_KEY.length&&MULT_KEY[check_num]==num){
            check_num++;
            return true;
        }else{
            check_num = 0;//如果输入错误的话，则重置掉原先输入的
        }
        return false;
    }
    @Override
    public void clearKeys() {
        lastEventTime = 0;
        check_num = 0;
    }

    @Override
    public boolean checkMultKey() {
        return check_num == MULT_KEY.length;
    }

}
