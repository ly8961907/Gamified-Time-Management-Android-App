package com.example.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.util.Log;

import com.example.myapplication.R;
import com.example.myapplication.bean.AppInfo;
import com.example.myapplication.fragment.ControlFragment;



public class NotifyUserReceiver extends BroadcastReceiver {
    private String TAG = "NotifyUserReceiver";
    private static Fragment me;
    public NotifyUserReceiver() {
        this.me=ControlFragment.me;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isNotifyOpenApp =  intent.getBooleanExtra("isOpenAAPP",false);
        boolean isCountStart = ((ControlFragment)me).getIsSetedCountTime();
        boolean isClickStart = ((ControlFragment)me).isClickStart();
        Log.d(TAG,"是否在计时:"+isCountStart);
        Log.d(TAG,"是否点击开启:"+isClickStart);
        if (isNotifyOpenApp && isCountStart){
            //显示通知
            AppInfo app = (AppInfo) intent.getSerializableExtra("appOfOpen");
            Log.d(TAG,"App:"+app.getAppName()+" 系统："+app.isSystemApp()+"自己:"+app.isMyApp());

            if ((!app.isMyApp()) && (!app.isSystemApp()) && (app.getAppName() != null) ){
                Log.d(TAG,"调用Home");
                ((ControlFragment)me).closeApp(app.getAppPackage());
            }
            Log.d(TAG,"appName:"+app.getAppName());
        }else if (isClickStart){
            ((ControlFragment)me).startCountService();
            ((ControlFragment)me).setClickStart(false);
        }
    }
}