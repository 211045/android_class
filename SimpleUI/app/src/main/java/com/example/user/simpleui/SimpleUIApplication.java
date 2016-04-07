package com.example.user.simpleui;

import android.app.Application;

import com.facebook.FacebookSdk;
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

        //Parse.initialize(this);

        Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId("76ee57f8e5f8bd628cc9586e93d428d5")
                        .clientKey(null)
                        .server("http://parseserver-b3322-env.us-east-1.elasticbeanstalk.com/parse/")
                        .build()
        );

        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
