// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.mediation.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    public static final String SHARED_PREFERENCES    = "pubnative_pref_file";
    public static final String SHARED_PLACEMENTS_KEY = "placements_key";
    public static final String SHARED_APP_TOKEN_KEY  = "app_token_key";

    private static SharedPreferences getSharedPreferences(Context context) {

        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {

        return getSharedPreferences(context).edit();
    }

    public static void removePlacement(Context context, String placementID) {

        List<String> placements = getPlacements(context);
        placements.remove(placementID);
        setPlacements(context, placements);
    }

    public static void addPlacement(Context context, String placementID) {

        List<String> placements = getPlacements(context);
        placements.add(placementID);
        setPlacements(context, placements);
    }

    public static List<String> getPlacements(Context context) {

        List<String> result = null;
        if (context != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            String json = sharedPreferences.getString(SHARED_PLACEMENTS_KEY, null);
            if (!TextUtils.isEmpty(json)) {
                result = new Gson().fromJson(json, List.class);
            } else {
                result = new ArrayList<>();
            }
        }
        return result;
    }

    public static void setPlacements(Context context, List<String> placements) {

        if (context != null) {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            if (placements == null || placements.size() == 0) {
                editor.remove(SHARED_PLACEMENTS_KEY);
            } else {
                editor.putString(SHARED_PLACEMENTS_KEY, new Gson().toJson(placements));
            }
            editor.apply();
        }
    }

    public static String getAppToken(Context context) {

        String result = null;
        if (context != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            result = sharedPreferences.getString(SHARED_APP_TOKEN_KEY, "");
        }
        return result;
    }

    public static void setAppToken(Context context, String appToken) {
        if (context != null && !TextUtils.isEmpty(appToken)) {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            if(TextUtils.isEmpty(appToken)) {
                editor.remove(SHARED_APP_TOKEN_KEY);
            } else {
                editor.putString(SHARED_APP_TOKEN_KEY, appToken).apply();
            }
        }
    }
}
