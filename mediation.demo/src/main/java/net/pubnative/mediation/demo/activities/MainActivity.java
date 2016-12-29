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

package net.pubnative.mediation.demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.demo.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String  TAG       = MainActivity.class.getSimpleName();
    private static final String  APP_TOKEN = "7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c";
    private              boolean mIsDefaultsSet   = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDefaults();
    }

    public void onSettingsClick(View v) {

        Log.v(TAG, "onSettingsClick");
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void onNativeClick(View view) {

        Log.v(TAG, "onNativeClick");
        // Launch native activity
        Intent intent = new Intent(this, NativeAdActivity.class);
        startActivity(intent);
    }

    public void onInterstitialClick(View view) {

        Log.v(TAG, "onInterstitialClick");
        // Launch interstitial activity
        Intent intent = new Intent(this, InterstitialAdActivity.class);
        startActivity(intent);
    }

    public void onFeedBannerClick(View view) {

        Log.v(TAG, "onFeedBannerClick");
        // Launch Feed Banner activity
        Intent intent = new Intent(this, FeedBannerActivity.class);
        startActivity(intent);
    }

    public void onBannerClick(View view) {

        Log.v(TAG, "onBannerClick");
        // Launch banner activity
        Intent intent = new Intent(this, BannerAdActivity.class);
        startActivity(intent);
    }

    public void onVideoClick(View view) {

        Log.v(TAG, "onVideoClick");
        // Launch video activity
        Intent intent = new Intent(this, VideoAdActivity.class);
        startActivity(intent);
    }

    public void onFeedVideoClick(View view) {

        Log.v(TAG, "onFeedVideoClick");
        // Launch feed video activity
        Intent intent = new Intent(this, FeedVideoActivity.class);
        startActivity(intent);
    }

    protected void setDefaults() {

        Log.v(TAG, "setDefaults");
        if(!mIsDefaultsSet) {
            mIsDefaultsSet = true;
            // App token
            Settings.setAppToken(this, APP_TOKEN);
            // Placements
            List<String> placements = new ArrayList<>();
            placements.add("facebook_only");
            placements.add("pubnative_only");
            placements.add("yahoo_only");
            placements.add("yahoo_interstitial");
            placements.add("admob_only");
            placements.add("waterfall");
            placements.add("imp_day_cap_10");
            placements.add("imp_hour_cap_10");
            placements.add("pacing_cap_hour_1");
            placements.add("pacing_cap_min_1");
            placements.add("disabled");
            Settings.setPlacements(this, placements);
        }
    }
}
