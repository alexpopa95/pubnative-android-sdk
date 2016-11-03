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
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import net.pubnative.mediation.adapter.model.FacebookNativeAdModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class FacebookNetworkRequestAdapter extends PubnativeNetworkRequestAdapter
        implements AdListener {

    private static         String   TAG                         = FacebookNetworkRequestAdapter.class.getSimpleName();
    protected static final String   KEY_PLACEMENT_ID            = "placement_id";
    protected static final int      FACEBOOK_ERROR_NO_FILL_1203 = 1203;
    protected              NativeAd mNativeAd                   = null;

    /**
     * Creates a new instance of FacebookNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public FacebookNetworkRequestAdapter(Map data) {

        super(data);
    }
    //==============================================================================================
    // PubnativeNetworkAdapter methods
    //==============================================================================================

    @Override
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context != null && mData != null) {
            String placementId = (String) mData.get(KEY_PLACEMENT_ID);
            if (!TextUtils.isEmpty(placementId)) {
                createRequest(context, placementId);
            } else {
                invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
            }
        } else {
            invokeFailed(PubnativeException.ADAPTER_MISSING_DATA);
        }
    }

    //==============================================================================================
    // FacebookNetworkAdapter methods
    //==============================================================================================
    protected void createRequest(Context context, String placementId) {

        Log.v(TAG, "createRequest");
        mNativeAd = new NativeAd(context, placementId);
        mNativeAd.setAdListener(this);
        mNativeAd.loadAd();
    }

    //==============================================================================================
    // Callback
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onError(Ad ad, AdError adError) {

        Log.v(TAG, "onError: " + (adError != null ? (adError.getErrorCode() + " - " + adError.getErrorMessage()) : ""));
        if (adError == null) {
            invokeFailed(PubnativeException.ADAPTER_UNKNOWN_ERROR);
        } else {
            switch (adError.getErrorCode()) {
                case AdError.NO_FILL_ERROR_CODE:
                case AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE:
                case FACEBOOK_ERROR_NO_FILL_1203:
                    invokeLoaded(null);
                break;
                default:
                    Map extra = new HashMap();
                    extra.put("code", adError.getErrorCode());
                    extra.put("message", adError.getErrorMessage());
                    invokeFailed(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extra));
                break;
            }
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {

        Log.v(TAG, "onAdLoaded");
        if (ad == mNativeAd) {
            FacebookNativeAdModel wrapModel = new FacebookNativeAdModel((NativeAd) ad);
            invokeLoaded(wrapModel);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {

        Log.v(TAG, "onAdClicked");
        // Do nothing, just ignore thiss
    }
}
