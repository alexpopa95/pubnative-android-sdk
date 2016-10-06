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
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.library.feed.banner.PubnativeFeedBanner;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeLibraryNetworkFeedBannerAdapter
        extends PubnativeNetworkFeedBannerAdapter implements PubnativeFeedBanner.Listener {

    private static String TAG = PubnativeLibraryNetworkFeedBannerAdapter.class.getSimpleName();

    protected PubnativeFeedBanner mFeedBanner;

    /**
     * Creates a new instance of PubnativeLibraryNetworkFeedBannerAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeLibraryNetworkFeedBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeLibraryNetworkFeedBannerAdapter
    //==============================================================================================
    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String appToken = (String) mData.get(KEY_APP_TOKEN);
            if (TextUtils.isEmpty(appToken)) {
                invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                mFeedBanner = new PubnativeFeedBanner();
                mFeedBanner.setListener(this);
                mFeedBanner.load(context, appToken);
            }
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mFeedBanner != null) {
            result = mFeedBanner.isReady();
        }
        return result;
    }

    @Override
    public void show(ViewGroup container) {

        Log.v(TAG, "show");
        if (mFeedBanner != null) {
            View adView = mFeedBanner.getView();
            ViewGroup parent = (ViewGroup)adView.getParent();
            if(parent != null) {
                parent.removeView(adView);
            }
            container.addView(adView);
            invokeShow();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
    }

    @Override
    public void hide() {

        Log.v(TAG, "hide");
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // PubnativeFeedBanner.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeFeedBannerLoadFinish(PubnativeFeedBanner feedBanner) {

        Log.v(TAG, "onPubnativeFeedBannerLoadFinish");
        invokeLoadFinish();
    }

    @Override
    public void onPubnativeFeedBannerLoadFailed(PubnativeFeedBanner feedBanner, Exception exception) {

        Log.v(TAG, "onPubnativeFeedBannerLoadFailed");
        invokeLoadFail(exception);
    }

    @Override
    public void onPubnativeFeedBannerImpressionConfirmed(PubnativeFeedBanner feedBanner) {

        Log.v(TAG, "onPubnativeFeedBannerImpressionConfirmed");
        invokeImpressionConfirmed();
    }

    @Override
    public void onPubnativeFeedBannerClick(PubnativeFeedBanner feedBanner) {

        Log.v(TAG, "onPubnativeFeedBannerClick");
        invokeClick();
    }
}
