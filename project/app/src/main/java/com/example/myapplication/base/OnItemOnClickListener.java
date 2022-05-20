package com.example.myapplication.base;

import android.view.View;


public interface OnItemOnClickListener{
    void onItemOnClick(View view,int pos,int id);
    void onItemLongOnClick(View view ,int pos,int id);
}