package com.example.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.example.myapplication.fragment.ControlFragment;



public class CountDownFinishReceiver extends BroadcastReceiver {
    private static final String TAG = "CountDownFinishReceiver";
    private static  Fragment me;
    public CountDownFinishReceiver() {
        this.me=ControlFragment.me;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((ControlFragment)me).setSetedCountTime(false);
    }
}