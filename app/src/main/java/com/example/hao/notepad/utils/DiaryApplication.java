package com.example.hao.notepad.utils;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by developerHaoz on 2017/5/9.
 * Updated by 梁 on 2018/5/5
 */

public class DiaryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
      }
        LeakCanary.install(this);
    }
}
