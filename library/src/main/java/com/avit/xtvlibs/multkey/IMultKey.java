package com.avit.xtvlibs.multkey;

/**
 * @author laird.li
 * @date 2018/11/6
 **/

import android.view.KeyEvent;

/**
 * 组合按键触发功能的接口
 *
 */
public interface IMultKey {
    /**
     * 初始化
     * 键值，键值间隔时间
     * @return
     */
    void initTrigger();
    /**
     * 检查输入的按键是否是对应组合键某个位置
     * @param keycode
     * @param eventTime
     * @return
     */
    boolean checkKey(int keycode, long eventTime);
    /**
     * 检查组合键是否已经输入完成
     * @return
     */
    boolean checkMultKey();
    /**
     * 清除所有记录的键
     */
    void clearKeys();
    /**
     * 组合键触发事件
     */
    void onTrigger();

    boolean handlerMultKey(int keyCode, KeyEvent event);
}
