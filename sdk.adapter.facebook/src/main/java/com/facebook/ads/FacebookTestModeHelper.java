package com.facebook.ads;

import android.content.Context;
import android.content.SharedPreferences;

public class FacebookTestModeHelper {

    public static void updateHashId(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences("FBAdPrefs", 0);
        preferences.edit().putString("deviceIdHash", id).apply();
    }

}
