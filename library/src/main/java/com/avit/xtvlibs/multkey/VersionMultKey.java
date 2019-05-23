package com.avit.xtvlibs.multkey;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;

import com.avit.xutils.AppUtils;
import com.avit.xutils.DisplayUtils;
import com.avit.xutils.SPUtils;


/**
 * @author laird.li
 * @date 2018/11/6
 **/
public class VersionMultKey extends BaseMultKey {

    private Context context;

    public VersionMultKey(Context context) {
        this.context = context;
    }

    @Override
    public void initTrigger() {
        //9659996599
        MULT_KEY = new int[]{KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_VOLUME_UP};
        CHECK_NUM_ALLAW_MAX_DELAYED = 3000;
    }

    @Override
    public void onTrigger() {
        //enter factory
        Log.i(TAG, "onTrigger");
        showVersionInfo();
    }

    private void showVersionInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("AppInfo");
        builder.setMessage(getVersionInfo());
        builder.create().show();
    }

    private String getVersionInfo() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(AppUtils.BUILD_INFO).append("\n");
        stringBuffer.append(AppUtils.getVersionInfo(context)).append("\n");
        stringBuffer.append(SPUtils.getInstance().getString(SPUtils.SP_KEY_VERSION,"\n")).append("\n");
        stringBuffer.append(AppUtils.getSystemInfo()).append("\n");
        stringBuffer.append(DisplayUtils.logScreenInfo((Activity) context));
        return stringBuffer.toString();
    }


}
