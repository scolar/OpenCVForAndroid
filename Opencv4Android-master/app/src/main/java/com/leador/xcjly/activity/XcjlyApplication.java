package com.leador.xcjly.activity;

import android.app.Application;
import android.content.Context;

/**
 * Created by a on 2017/2/21.
 */

public class XcjlyApplication extends Application{

    protected Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
