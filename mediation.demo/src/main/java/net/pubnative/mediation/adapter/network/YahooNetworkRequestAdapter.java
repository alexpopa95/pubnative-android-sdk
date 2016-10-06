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
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.mediation.adapter.model.FlurryNativeAdModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkRequestAdapter extends PubnativeNetworkRequestAdapter
        implements FlurryAdNativeListener {

    private static      String TAG                = YahooNetworkRequestAdapter.class.getSimpleName();
    public static final String KEY_AD_SPACE_NAME  = "ad_space_name";
    public static final String KEY_FLURRY_API_KEY = "api_key";
    protected Context mContext;

    /**
     * Creates a new instance of YahooNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkRequestAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeNetworkAdapter
    //==============================================================================================
    @Override
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context == null || mData == null) {
            invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String adSpaceName = (String) mData.get(KEY_AD_SPACE_NAME);
            String apiKey = (String) mData.get(KEY_FLURRY_API_KEY);
            if (TextUtils.isEmpty(adSpaceName) || TextUtils.isEmpty(apiKey)) {
                invokeFailed(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                mContext = context;
                createRequest(adSpaceName, apiKey);
            }
        }
    }

    //==============================================================================================
    // YahooNetworkAdapterHub
    //==============================================================================================
    protected void createRequest(String adSpaceName, String apiKey) {

        Log.v(TAG, "createRequest");
        // configure flurry
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(mContext, apiKey);
        // execute/resume session
        if (!FlurryAgent.isSessionActive()) {
            FlurryAgent.onStartSession(mContext);
        }
        // Make request
        FlurryAdNative flurryAdNative = new FlurryAdNative(mContext, adSpaceName);
        // Add targeting
        FlurryAdTargeting targeting = getTargeting();
        if (targeting != null) {
            flurryAdNative.setTargeting(targeting);
        }
        flurryAdNative.setListener(this);
        flurryAdNative.fetchAd();
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
    //==============================================================================================
    // Callbacks
    //==============================================================================================

    // FlurryAdNativeListener
    //----------------------------------------------------------------------------------------------
    protected void endFlurrySession(Context context) {

        Log.v(TAG, "endFlurrySession");
        FlurryAgent.onEndSession(context);
    }

    @Override
    public void onFetched(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onFetched");
        endFlurrySession(mContext);
        FlurryNativeAdModel nativeAdModel = new FlurryNativeAdModel(flurryAdNative);
        invokeLoaded(nativeAdModel);
    }

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int errCode) {

        Log.v(TAG, "onError: " + errCode);
        endFlurrySession(mContext);
        if (FlurryAdErrorType.FETCH == flurryAdErrorType) {
            invokeLoaded(null);
        } else {
            Map extra = new HashMap();
            extra.put("code", errCode);
            extra.put("type", flurryAdErrorType.name());
            invokeFailed(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extra));
        }
    }

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onShowFullscreen");
        // Do nothing for now.
    }

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onCloseFullscreen");
        // Do nothing for now.
    }

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onAppExit");
        // Do nothing for now.
    }

    @Override
    public void onClicked(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onClicked");
        // Do nothing for now.
    }

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onImpressionLogged");
        // Do nothing for now.
    }

    @Override
    public void onExpanded(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onExpanded");
    }

    @Override
    public void onCollapsed(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onCollapsed");
    }
}
