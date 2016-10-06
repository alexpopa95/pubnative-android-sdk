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

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.model.FlurryNativeAdModel;

import java.util.Map;

public class YahooNetworkAdapter extends PubnativeNetworkAdapter implements FlurryAdNativeListener {

    private static      String TAG                = YahooNetworkAdapter.class.getSimpleName();
    public static final String KEY_AD_SPACE_NAME  = "ad_space_name";
    public static final String KEY_FLURRY_API_KEY = "api_key";
    private Context mContext;

    public YahooNetworkAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeNetworkAdapter
    //==============================================================================================
    @Override
    public void request(Context context) {

        Log.v(TAG, "request");
        if (context != null && mData != null) {
            mContext = context;
            String apiKey = (String) mData.get(KEY_FLURRY_API_KEY);
            if (!TextUtils.isEmpty(apiKey)) {
                String adSpaceName = (String) mData.get(KEY_AD_SPACE_NAME);
                if (!TextUtils.isEmpty(adSpaceName)) {
                    createRequest(context, adSpaceName, apiKey);
                } else {
                    invokeFailed(new IllegalArgumentException("YahooNetworkAdapter - Error: Invalid ad_space_name provided."));
                }
            } else {
                invokeFailed(new IllegalArgumentException("YahooNetworkAdapter - Error: Invalid api_key provided."));
            }
        } else {
            invokeFailed(new IllegalArgumentException("YahooNetworkAdapter - Error: No context or adapter data provided."));
        }
    }

    //==============================================================================================
    // YahooNetworkAdapter
    //==============================================================================================
    protected void createRequest(Context context, String adSpaceName, String apiKey) {

        Log.v(TAG, "createRequest");
        // configure flurry
        FlurryAgent.setLogEnabled(true);
        // initialize flurry with new apiKey
        FlurryAgent.init(context, apiKey);
        // start/resume session
        if (!FlurryAgent.isSessionActive()) {
            FlurryAgent.onStartSession(context);
        }
        // Make request
        FlurryAdNative flurryAdNative = new FlurryAdNative(context, adSpaceName);
        flurryAdNative.setListener(this);
        flurryAdNative.fetchAd();
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

        Log.v(TAG, "onError: " + flurryAdErrorType.name() + " - " + errCode);
        endFlurrySession(mContext);
        if (flurryAdErrorType != null) {
            switch (flurryAdErrorType) {
                case FETCH: {
                    invokeLoaded(null);
                }
                break;
                default: {
                    invokeFailed(new Exception("YahooNetworkAdapter - Error: " + flurryAdErrorType.name() + " - " + errCode));
                }
                break;
            }
        } else {
            invokeFailed(new Exception("YahooNetworkAdapter - Error: Unknown"));
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
