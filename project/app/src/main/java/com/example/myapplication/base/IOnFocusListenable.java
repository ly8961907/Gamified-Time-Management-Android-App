package com.example.myapplication.base;

import android.view.KeyEvent;



public interface IOnFocusListenable {
    public void onWindowFocusChanged(int top);
    public boolean onKeyDown(int keyCode, KeyEvent event);
}