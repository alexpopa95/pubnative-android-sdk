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

package net.pubnative.sdk.layouts.adapter.small;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import net.pubnative.sdk.core.adapter.Facebook;
import net.pubnative.sdk.layouts.adapter.PNLayoutFeedAdapter;
import net.pubnative.sdk.core.adapter.request.FacebookNativeAdModel;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.layouts.PNSmallLayoutView;
import net.pubnative.sdk.core.request.PNAdModel;

import java.util.Map;

public class FacebookNetworkAdapter extends PNLayoutFeedAdapter implements AdListener, PNAdModel.Listener {
    protected PNAdModel                mWrapper;
    protected NativeAd                 mNativeAd;
    protected PNSmallLayoutRequestView mAdView;

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
        } else {
            mContext = context;
            String placementId = networkData.get(Facebook.KEY_PLACEMENT_ID);
            if (TextUtils.isEmpty(placementId)) {
                invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
            } else {
                mAdView = null;
                mWrapper = null;

                Facebook.init(context);
                mNativeAd = new NativeAd(context, placementId);
                mNativeAd.setAdListener(this);
                mNativeAd.loadAd();
            }
        }
    }

    @Override
    public PNSmallLayoutView getView(Context context) {
        if (mWrapper != null && mAdView == null) {
            mAdView = new PNSmallLayoutRequestView(mContext);
            mAdView.loadWithAd(mContext, mWrapper);
        }
        return mAdView;
    }


    @Override
    public void startTracking() {
        if (mWrapper != null) {
            mWrapper.setListener(this);
            mWrapper.startTracking(getView(mContext));
        }
    }

    @Override
    public void stopTracking() {
        if (mWrapper != null) {
            mWrapper.stopTracking();
            mWrapper.setListener(null);
        }
    }

    //==============================================================================================
    // Callback
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onError(Ad ad, AdError adError) {

        String errorString = DEFAULT_ERROR;
        if (adError != null) {
            errorString = adError.getErrorCode() + " - " + adError.getErrorMessage();
        }
        if (adError == null) {
            invokeLoadFail(new Exception(errorString));
        } else {

            switch (adError.getErrorCode()) {
                case AdError.NO_FILL_ERROR_CODE:
                case AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE:
                case Facebook.ERROR_NO_FILL_1203:
                    invokeLoadFail(new Exception("Facebook error: no-fill"));
                    break;
                default:
                    invokeLoadFail(new Exception(errorString));
                    break;
            }
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        mWrapper = new FacebookNativeAdModel(mContext, (NativeAd) ad);
        invokeLoadSuccess();
    }

    @Override
    public void onAdClicked(Ad ad) {
        // This is overwritten by FacebookNativeAdModel that sets itself as listener
        // so all the callbacks are coming through PNAdModel tracking listener callbacks
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        // This is overwritten by FacebookNativeAdModel that sets itself as listener
        // so all the callbacks are coming through PNAdModel tracking listener callbacks
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
