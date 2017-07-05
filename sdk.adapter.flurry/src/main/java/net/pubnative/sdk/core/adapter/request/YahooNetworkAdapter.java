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

package net.pubnative.sdk.core.adapter.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.android.ads.FlurryAdTargeting;

import net.pubnative.sdk.core.adapter.Flurry;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Map;

public class YahooNetworkAdapter extends PNAdapter
        implements FlurryAdNativeListener {

    private static String TAG = YahooNetworkAdapter.class.getSimpleName();

    protected Context mContext;

    /**
     * Creates a new instance of YahooNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeLibraryNetworkAdapter
    //==============================================================================================
    @Override
    protected void request(Context context) {
        if (context == null || mData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String adSpaceName = (String) mData.get(Flurry.KEY_AD_SPACE_NAME);
            String apiKey = (String) mData.get(Flurry.KEY_FLURRY_API_KEY);
            if (TextUtils.isEmpty(adSpaceName) || TextUtils.isEmpty(apiKey)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
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
        // configure flurry
        // execute/resume session
        Flurry.init(mContext, apiKey);
        // Make request
        FlurryAdNative flurry = new FlurryAdNative(mContext, adSpaceName);
        flurry.setTargeting(Flurry.getTargeting());
        flurry.setListener(this);
        flurry.fetchAd();
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================

    // FlurryAdNativeListener
    //----------------------------------------------------------------------------------------------
    protected void endFlurrySession(Context context) {
        FlurryAgent.onEndSession(context);
    }

    @Override
    public void onFetched(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onFetched");
        endFlurrySession(mContext);
        YahooNativeAdModel nativeAdModel = new YahooNativeAdModel(mContext, flurryAdNative);
        nativeAdModel.setInsightModel(mInsight);
        invokeLoadFinish(nativeAdModel);
    }

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int errCode) {
        endFlurrySession(mContext);
        if (FlurryAdErrorType.FETCH == flurryAdErrorType) {
            invokeLoadFinish(null);
        } else {
            invokeLoadFail(new Exception(flurryAdErrorType.name() + " - " + errCode));
        }
    }

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative) {}

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative) {}

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative) {}

    @Override
    public void onClicked(FlurryAdNative flurryAdNative) {}

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative) {}

    @Override
    public void onExpanded(FlurryAdNative flurryAdNative) {}

    @Override
    public void onCollapsed(FlurryAdNative flurryAdNative) {}
}
