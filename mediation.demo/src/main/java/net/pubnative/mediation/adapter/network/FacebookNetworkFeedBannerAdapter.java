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
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.ImpressionListener;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class FacebookNetworkFeedBannerAdapter extends PubnativeNetworkFeedBannerAdapter
        implements AdListener, ImpressionListener {

    private static final String  TAG = FacebookNetworkFeedBannerAdapter.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    private AdView  mFeedBanner;
    private boolean mIsLoaded;

    /**
     * Creates a new instance of FacebookNetworkFeedBannerAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public FacebookNetworkFeedBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String placementId = (String) mData.get(FacebookNetworkRequestAdapter.KEY_PLACEMENT_ID);
            if (TextUtils.isEmpty(placementId)) {
                invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                mFeedBanner = new AdView(context, placementId, AdSize.RECTANGLE_HEIGHT_250);
                mFeedBanner.setAdListener(this);
                mFeedBanner.loadAd();
            }
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mFeedBanner != null) {
            result = mIsLoaded;
        }
        return result;
    }

    @Override
    public void show(ViewGroup container) {

        Log.v(TAG, "show");
        container.addView(mFeedBanner, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        invokeShow();
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mFeedBanner != null) {
            mFeedBanner.destroy();
        }
    }

    @Override
    public void hide() {
        if (mFeedBanner.getParent() != null) {
            ((ViewGroup) mFeedBanner.getParent()).removeView(mFeedBanner);
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onError(Ad ad, AdError adError) {

        Log.v(TAG, "onError: " + (adError != null ? (adError.getErrorCode() + " - " + adError.getErrorMessage()) : ""));
        if (adError == null) {
            invokeLoadFail(PubnativeException.ADAPTER_UNKNOWN_ERROR);
        } else {
            Map errorData = new HashMap();
            errorData.put("errorCode", adError.getErrorCode());
            errorData.put("message", adError.getErrorMessage());
            invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, errorData));
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {

        Log.v(TAG, "onAdLoaded");
        mIsLoaded = true;
        mFeedBanner.setImpressionListener(this);
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
