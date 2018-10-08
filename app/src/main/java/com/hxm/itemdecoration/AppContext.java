package com.hxm.itemdecoration;

import android.app.Application;

/**
 * Created by hxm on 2018/10/8
 * 描述：
 */
public class AppContext extends Application {
    public static AppContext instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
