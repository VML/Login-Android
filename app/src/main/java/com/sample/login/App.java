package com.sample.login;

import android.app.Application;

import com.buddybuild.sdk.BuddyBuild;

/**
 * Created by tway on 5/3/17.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BuddyBuild.setup(this);
    }
}
