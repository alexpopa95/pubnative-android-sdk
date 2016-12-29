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

package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.library.request.model.PubnativeAdModel;

public class PubnativeLibraryAdModel extends net.pubnative.mediation.request.model.PubnativeAdModel
        implements PubnativeAdModel.Listener {

    private static String                                               TAG      = PubnativeLibraryAdModel.class.getSimpleName();
    protected      net.pubnative.library.request.model.PubnativeAdModel mAdModel = null;

    public PubnativeLibraryAdModel(PubnativeAdModel model) {

        mAdModel = model;
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
        if (mAdModel != null) {
            result = mAdModel.getTitle();
        }
        return result;
    }

    @Override
    public String getDescription() {

        Log.v(TAG, "getDescription");
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getDescription();
        }
        return result;
    }

    @Override
    public String getIconUrl() {

        Log.v(TAG, "getIconUrl");
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getIconUrl();
        }
        return result;
    }

    @Override
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getBannerUrl();
        }
        return result;
    }

    @Override
    public String getCallToAction() {

        Log.v(TAG, "getCallToAction");
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getCtaText();
        }
        return result;
    }

    @Override
    public float getStarRating() {

        Log.v(TAG, "getStarRating");
        float result = 0F;
        if(mAdModel != null) {
            result = mAdModel.getRating();
        }
        return result;
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {

        Log.v(TAG, "getAdvertisingDisclosureView");
        return null;
    }

    //----------------------------------------------------------------------------------------------
    // Tracking
    //----------------------------------------------------------------------------------------------

    @Override
    public void startTracking(Context context, ViewGroup adView) {

        Log.v(TAG, "startTracking");
        if (mAdModel != null && context != null && adView != null) {
            mContext = context;
            mAdModel.startTracking(adView, this);
        }
    }

    @Override
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
        mAdModel.stopTracking();
        // Do nothing
    }

    public void setLinkCaching(boolean enable) {

        mAdModel.setUseClickCaching(enable);
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeAdModel.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view) {

        Log.v(TAG, "onPubnativeAdModelImpression");
        invokeOnAdImpressionConfirmed();
    }

    @Override
    public void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view) {

        Log.v(TAG, "onPubnativeAdModelClick");
        invokeOnAdClick();
    }

    @Override
    public void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel) {

        Log.v(TAG, "onPubnativeAdModelOpenOffer");
    }
}
