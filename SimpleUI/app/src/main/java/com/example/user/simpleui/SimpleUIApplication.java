package com.example.user.simpleui;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by user on 2016/3/21.
 */
public class SimpleUIApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //另開class設定parse連線
        Parse.enableLocalDatastore(this);

        Parse.initialize(this);
    }
}
