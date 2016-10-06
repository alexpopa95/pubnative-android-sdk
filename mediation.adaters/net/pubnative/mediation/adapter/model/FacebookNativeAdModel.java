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

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.ImpressionListener;
import com.facebook.ads.NativeAd;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public class FacebookNativeAdModel extends PubnativeAdModel implements ImpressionListener,
                                                                       AdListener {

    private static String TAG = FacebookNativeAdModel.class.getSimpleName();
    protected NativeAd mNativeAd;

    public FacebookNativeAdModel(NativeAd nativeAd) {

        if (nativeAd != null) {
            mNativeAd = nativeAd;
            mNativeAd.setAdListener(this);
            mNativeAd.setImpressionListener(this);
        }
    }

    //==============================================================================================
    // PubnativeAdModel methods
    //==============================================================================================

    // Fields
    //----------------------------------------------------------------------------------------------
    @Override
    public String getTitle() {

        Log.v(TAG, "getTitle");
        String result = null;
        if (mNativeAd != null) {
            result = mNativeAd.getAdTitle();
        }
        return result;
    }

    @Override
    public String getDescription() {

        Log.v(TAG, "getDescription");
        String result = null;
        if (mNativeAd != null) {
            result = mNativeAd.getAdBody();
        }
        return result;
    }

    @Override
    public String getIconUrl() {

        Log.v(TAG, "getIconUrl");
        String iconUrl = null;
        if (mNativeAd != null && mNativeAd.getAdIcon() != null) {
            iconUrl = mNativeAd.getAdIcon().getUrl();
        }
        return iconUrl;
    }

    @Override
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");
        String bannerUrl = null;
        if (mNativeAd != null && mNativeAd.getAdCoverImage() != null) {
            bannerUrl = mNativeAd.getAdCoverImage().getUrl();
        }
        return bannerUrl;
    }

    @Override
    public String getCallToAction() {

        Log.v(TAG, "getCallToAction");
        String result = null;
        if (mNativeAd != null) {
            result = mNativeAd.getAdCallToAction();
        }
        return result;
    }

    @Override
    public float getStarRating() {

        Log.v(TAG, "getStarRating");
        float starRating = 0;
        if (mNativeAd != null) {
            NativeAd.Rating rating = mNativeAd.getAdStarRating();
            if (rating != null) {
                double ratingScale = rating.getScale();
                double ratingValue = rating.getValue();
                starRating = (float) ((ratingValue / ratingScale) * 5);
            }
        }
        return starRating;
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {

        Log.v(TAG, "getAdvertisingDisclosureView");
        View result = null;
        if (context != null && mNativeAd != null) {
            this.mContext = context;
            return new AdChoicesView(this.mContext, mNativeAd);
        }
        return result;
    }
    // Tracking
    //----------------------------------------------------------------------------------------------

    @Override
    public void startTracking(Context context, View adView) {

        Log.v(TAG, "startTracking");
        if (context != null && mNativeAd != null && adView != null) {
            this.mContext = context;
            mNativeAd.registerViewForInteraction(adView);
        }
    }

    @Override
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
        if (mNativeAd != null) {
            mNativeAd.unregisterView();
        }
    }
    //==============================================================================================
    // Callbacks
    //==============================================================================================

    // ImpressionListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onLoggingImpression(Ad ad) {

        Log.v(TAG, "onLoggingImpression");
        invokeOnAdImpressionConfirmed();
    }

    // AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onError(Ad ad, AdError adError) {

        Log.v(TAG, "onError");
        // Do nothing
    }

    @Override
    public void onAdLoaded(Ad ad) {

        Log.v(TAG, "onAdLoaded");
        // Do nothing
    }

    @Override
    public void onAdClicked(Ad ad) {

        Log.v(TAG, "onAdClicked");
        invokeOnAdClick();
    }
}
