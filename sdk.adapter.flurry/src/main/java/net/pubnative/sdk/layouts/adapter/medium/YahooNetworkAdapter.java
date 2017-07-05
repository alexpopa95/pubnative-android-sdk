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

package net.pubnative.sdk.layouts.adapter.medium;

import android.content.Context;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.sdk.core.adapter.Flurry;
import net.pubnative.sdk.layouts.adapter.PNLayoutFeedAdapter;
import net.pubnative.sdk.core.adapter.request.YahooNativeAdModel;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.layouts.PNMediumLayoutView;
import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.layouts.adapter.YahooLayoutAdModel;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkAdapter extends PNLayoutFeedAdapter implements FlurryAdNativeListener,
                                                                        PNAdModel.Listener {
    protected FlurryAdNative            mNativeAd;
    protected YahooLayoutAdModel        mWrapper;
    protected PNMediumLayoutRequestView mAdView;

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String adSpaceName = networkData.get(Flurry.KEY_AD_SPACE_NAME);
            String apiKey = networkData.get(Flurry.KEY_FLURRY_API_KEY);
            if (TextUtils.isEmpty(adSpaceName) || TextUtils.isEmpty(apiKey)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
            } else {
                mWrapper = null;
                mAdView = null;

                Flurry.init(context, apiKey);

                // Make request
                mNativeAd = new FlurryAdNative(context, adSpaceName);
                mNativeAd.setTargeting(Flurry.getTargeting());
                mNativeAd.setListener(this);
                mNativeAd.fetchAd();
            }
        }
    }

    @Override
    public PNMediumLayoutView getView(Context context) {
        if (mWrapper != null && mAdView == null) {
            mAdView = new PNMediumLayoutRequestView(context);
            mAdView.loadWithAd(context, mWrapper);
        }
        return mAdView;
    }


    @Override
    public void startTracking() {
        if (mWrapper != null) {
            mWrapper.startTracking(this.getView(mContext));
        }
    }

    @Override
    public void stopTracking() {
        if (mWrapper != null) {
            mWrapper.stopTracking();
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // FlurryAdNativeListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onFetched(FlurryAdNative flurryAdNative) {
        FlurryAgent.onEndSession(mContext);
        mWrapper = new YahooLayoutAdModel(mContext, flurryAdNative);
        mWrapper.fetchAssets(new YahooLayoutAdModel.LayoutListener() {
            @Override
            public void onFetchFinish(PNAdModel model) {
                mWrapper.setListener(YahooNetworkAdapter.this);
                invokeLoadSuccess();
            }

            @Override
            public void onFetchFail(PNAdModel model, Exception exception) {
                invokeLoadFail(exception);
            }
        });
    }

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int errCode) {
        FlurryAgent.onEndSession(mContext);
        invokeLoadFail(new Exception("Flurry error: " + flurryAdErrorType.name() + " - " + errCode));
    }

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onClicked(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onExpanded(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onCollapsed(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onPNAdImpression(PNAdModel model) {
        invokeImpression();
    }

    @Override
    public void onPNAdClick(PNAdModel model) {
        invokeClick();
    }
}
