package net.pubnative.sdk.demo;

import android.app.Application;

import net.pubnative.sdk.core.Pubnative;

public class MediationApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Pubnative.init(this, Settings.appToken);
        Pubnative.setTestMode(true);
    }
}