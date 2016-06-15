package com.icecodersteam.kiria.postpigeon;

import android.app.Application;

/**
 * Created by kiria on 13.06.2016.
 */
public class ThisApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        QueueSingleton.getInstance(this.getApplicationContext());
    }
}


