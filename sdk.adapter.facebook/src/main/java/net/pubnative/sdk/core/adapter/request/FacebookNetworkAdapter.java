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

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import net.pubnative.sdk.core.adapter.Facebook;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Map;

public class FacebookNetworkAdapter extends PNAdapter
        implements AdListener {

    private static String TAG = FacebookNetworkAdapter.class.getSimpleName();

    protected NativeAd mNativeAd;

    /**
     * Creates a new instance of FacebookNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public FacebookNetworkAdapter(Map data) {

        super(data);
    }
    //==============================================================================================
    // PubnativeLibraryNetworkAdapter methods
    //==============================================================================================

    @Override
    protected void request(Context context) {
        if (context == null || mData == null) {
            invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
        } else {
            String placementId = (String) mData.get(Facebook.KEY_PLACEMENT_ID);
            if (TextUtils.isEmpty(placementId)) {
                invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
            } else {

                createRequest(context, placementId);
            }
        }
    }

    //==============================================================================================
    // FacebookNetworkAdapterNetwork methods
    //==============================================================================================
    protected void createRequest(Context context, String placementId) {
        Facebook.init(context);
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
                    invokeLoadFinish(null);
                    break;
                default:
                    invokeLoadFail(new Exception(errorString));
                    break;
            }
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (ad == mNativeAd) {
            FacebookNativeAdModel wrapModel = new FacebookNativeAdModel(mContext, (NativeAd) ad);
            wrapModel.setInsightModel(mInsight);
            invokeLoadFinish(wrapModel);
        }
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
}
