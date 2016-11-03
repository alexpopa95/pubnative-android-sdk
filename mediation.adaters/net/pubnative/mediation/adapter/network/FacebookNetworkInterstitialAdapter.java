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

package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.ImpressionListener;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class FacebookNetworkInterstitialAdapter extends PubnativeNetworkInterstitialAdapter
        implements InterstitialAdListener,
                   ImpressionListener {

    private static final String TAG = FacebookNetworkInterstitialAdapter.class.getSimpleName();
    private InterstitialAd mInterstitial;

    /**
     * Creates a new instance of FacebookNetworkInterstitialAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public FacebookNetworkInterstitialAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context != null && mData != null) {
            String placementId = (String) mData.get(FacebookNetworkRequestAdapter.KEY_PLACEMENT_ID);
            if (!TextUtils.isEmpty(placementId)) {
                mInterstitial = new InterstitialAd(context, placementId);
                mInterstitial.setAdListener(this);
                mInterstitial.loadAd();
            } else {
                invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
            }
        } else {
            invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mInterstitial != null) {
            result = mInterstitial.isAdLoaded();
        }
        return result;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // InterstitialAdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onInterstitialDisplayed(Ad ad) {

        Log.v(TAG, "onInterstitialDisplayed");
        invokeShow();
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {

        Log.v(TAG, "onInterstitialDismissed");
        invokeHide();
    }

    @Override
    public void onError(Ad ad, AdError adError) {

        Log.v(TAG, "onError: " + (adError != null ? (adError.getErrorCode() + " - " + adError.getErrorMessage()) : ""));
        if (adError == null) {
            invokeLoadFail(PubnativeException.ADAPTER_UNKNOWN_ERROR);
        } else {
            Map extra = new HashMap();
            extra.put("code", adError.getErrorCode());
            extra.put("message", adError.getErrorMessage());
            invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extra));
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {

        Log.v(TAG, "onAdLoaded");
        mInterstitial.setImpressionListener(this);
        invokeLoadFinish();
    }

    @Override
    public void onAdClicked(Ad ad) {

        Log.v(TAG, "onAdClicked");
        invokeClick();
    }

    @Override
    public void onLoggingImpression(Ad ad) {

        Log.v(TAG, "onLoggingImpression");
        invokeImpressionConfirmed();
    }
}
