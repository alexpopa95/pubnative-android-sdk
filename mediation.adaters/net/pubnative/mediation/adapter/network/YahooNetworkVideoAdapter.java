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

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkVideoAdapter extends PubnativeNetworkVideoAdapter
        implements FlurryAdInterstitialListener {

    private static String TAG = YahooNetworkVideoAdapter.class.getSimpleName();

    private FlurryAdInterstitial mInterstitial;

    /**
     * Creates a new instance of YahooNetworkVideoAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkVideoAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String adSpaceName = (String) mData.get(YahooNetworkRequestAdapter.KEY_AD_SPACE_NAME);
            if (TextUtils.isEmpty(adSpaceName)) {
                invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                mInterstitial = new FlurryAdInterstitial(context, adSpaceName);
                mInterstitial.setListener(this);
                // Add targeting
                FlurryAdTargeting targeting = getTargeting();
                if (targeting != null) {
                    mInterstitial.setTargeting(targeting);
                }
                mInterstitial.fetchAd();
            }
        }
    }

    protected FlurryAdTargeting getTargeting() {

        FlurryAdTargeting result = null;
        if (mTargeting != null) {
            result = new FlurryAdTargeting();
            result.setAge(mTargeting.age);
            if (mTargeting.gender == null) {
                result.setGender(FlurryGender.UNKNOWN);
            } else if (mTargeting.gender.equals("female")) {
                result.setGender(FlurryGender.FEMALE);
            } else if (mTargeting.gender.equals("male")) {
                result.setGender(FlurryGender.MALE);
            } else {
                result.setGender(FlurryGender.UNKNOWN);
            }
            if (mTargeting.interests != null) {
                Map interests = new HashMap();
                interests.put("interest", TextUtils.join(",", mTargeting.interests));
                result.setKeywords(interests);
            }
        }
        return result;
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mInterstitial != null) {
            result = mInterstitial.isReady();
        }
        return result;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mInterstitial != null) {
            mInterstitial.displayAd();
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
    // FlurryAdInterstitialListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onFetched(FlurryAdInterstitial flurryAdInterstitial) {

        Log.v(TAG, "onFetched");
        invokeLoadFinish();
    }

    @Override
    public void onRendered(FlurryAdInterstitial flurryAdInterstitial) {

        Log.v(TAG, "onRendered");
        invokeShow();
    }

    @Override
    public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {

        Log.v(TAG, "onDisplay");
        invokeVideoStart();
        invokeImpressionConfirmed();
    }

    @Override
    public void onClose(FlurryAdInterstitial flurryAdInterstitial) {

        Log.v(TAG, "onClose");
        invokeHide();
    }

    @Override
    public void onAppExit(FlurryAdInterstitial flurryAdInterstitial) {

        Log.v(TAG, "onAppExit");
    }

    @Override
    public void onClicked(FlurryAdInterstitial flurryAdInterstitial) {

        Log.v(TAG, "onClicked");
        invokeClick();
    }

    @Override
    public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial) {

        Log.v(TAG, "onVideoCompleted");
        invokeVideoFinish();
    }

    @Override
    public void onError(FlurryAdInterstitial flurryAdInterstitial, FlurryAdErrorType flurryAdErrorType, int errorCode) {

        Log.v(TAG, "onError: " + errorCode);

        Map extras = new HashMap();
        extras.put("code", errorCode);
        extras.put("type", flurryAdErrorType.name());
        invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extras));

    }
}
