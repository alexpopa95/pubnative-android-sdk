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

package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;
import com.flurry.android.ads.FlurryAdNativeListener;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public class FlurryNativeAdModel extends PubnativeAdModel implements FlurryAdNativeListener {

    private static String TAG = FlurryNativeAdModel.class.getSimpleName();
    FlurryAdNative mFlurryAdNative;

    public FlurryNativeAdModel(FlurryAdNative flurryAdNative) {

        mFlurryAdNative = flurryAdNative;
    }

    //==============================================================================================
    // PubnativeAdModel methods
    //==============================================================================================
    // Fields
    //----------------------------------------------------------------------------------------------

    @Override
    public String getTitle() {

        Log.v(TAG, "getTitle");
        // The Ad headline, typically a single line. Type: STRING
        return getStringValueOfFirstAsset("headline");
    }

    @Override
    public String getDescription() {

        Log.v(TAG, "getDescription");
        // The call to action summary of the advertisement. Type: STRING
        return getStringValueOfFirstAsset("summary");
    }

    @Override
    public String getIconUrl() {

        Log.v(TAG, "getIconUrl");
        // secOrigImg: 	The secured original image, size: 627px x 627px. Optional asset, not present for the video ads
        // secImage:    The secured image, size: 82px x 82px. Optional asset, not present for the video ads.
        return getStringValueOfFirstAsset("secOrigImg", "secImage");
    }

    @Override
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");
        // secHqImage:  The secured high quality image, size: 1200px x 627px. Optional asset, not present for the video ads
        return getStringValueOfFirstAsset("secHqImage");
    }

    @Override
    public String getCallToAction() {

        Log.v(TAG, "getCallToAction");
        /**
         * Yahoo currently does not provide the short Call To Action (CTA)
         * asset or string at this time. Instead, you can create your own
         * CTA for each ad. For an ad that contains app install specific assets like
         * “appCategory” or “appRating”, the CTA could be ‘Install Now’.
         * For an ad that does not contain app specific assets, the CTA could be ‘Read More’.
         */
        String result = "Read More";
        if (getStringValueOfFirstAsset("appCategory") != null || getStringValueOfFirstAsset("appRating") != null) {
            result = "Install Now";
        }
        return result;
    }

    @Override
    public float getStarRating() {

        Log.v(TAG, "getStarRating");
        float result = 0;
        String appRating = getStringValueOfFirstAsset("appRating");
        if (appRating != null) {
            String[] parts = appRating.split("/");
            if (parts.length == 2) {
                try {
                    int ratingVal = Integer.parseInt(parts[0]);
                    int scaleVal = Integer.parseInt(parts[1]);
                    if (scaleVal != 0) {
                        result = (ratingVal / scaleVal) * 5;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getStarRating - Error while parsing star rating :" + e);
                }
            }
        }
        return result;
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {

        Log.v(TAG, "getAdvertisingDisclosureView");
        return null;
    }

    // Tracking
    //----------------------------------------------------------------------------------------------

    @Override
    public void startTracking(Context context, ViewGroup adView) {

        Log.v(TAG, "startTracking");
        mContext = context;
        if (mFlurryAdNative != null && adView != null) {
            mFlurryAdNative.setListener(this);
            mFlurryAdNative.setTrackingView(adView);
        }
    }

    @Override
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
        if (mFlurryAdNative != null) {
            mFlurryAdNative.setListener(null);
            mFlurryAdNative.removeTrackingView();
        }
    }

    //==============================================================================================
    // FlurryNativeAdModel methods
    //==============================================================================================
    protected String getStringValueOfFirstAsset(String... keys) {

        Log.v(TAG, "getStringValueOfFirstAsset");
        String result = null;
        if (mFlurryAdNative != null) {
            for (String key : keys) {
                FlurryAdNativeAsset asset = mFlurryAdNative.getAsset(key);
                if (asset != null) {
                    result = asset.getValue();
                    break;
                }
            }
        }
        return result;
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // FlurryAdNativeListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onFetched(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onFetched");
        // Do nothing
    }

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onShowFullscreen");
        // Do nothing
    }

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onCloseFullscreen");
        // Do nothing
    }

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onAppExit");
        // Do nothing
    }

    @Override
    public void onClicked(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onClicked");
        invokeOnAdClick();
    }

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onImpressionLogged");
        invokeOnAdImpressionConfirmed();
    }

    @Override
    public void onExpanded(FlurryAdNative flurryAdNative) {
        Log.v(TAG, "onExpanded");
    }

    @Override
    public void onCollapsed(FlurryAdNative flurryAdNative) {
        Log.v(TAG, "onCollapsed");
    }

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int i) {

        Log.v(TAG, "onError: " + flurryAdErrorType.name());
        // Do nothing
    }
}
