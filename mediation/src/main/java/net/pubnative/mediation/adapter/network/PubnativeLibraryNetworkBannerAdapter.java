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

import net.pubnative.library.banner.PubnativeBanner;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeLibraryNetworkBannerAdapter extends PubnativeNetworkBannerAdapter implements
                                                                                        PubnativeBanner.Listener {

    private static String TAG = PubnativeLibraryNetworkBannerAdapter.class.getSimpleName();

    protected PubnativeBanner mBanner;

    /**
     * Creates a new instance of PubnativeLibraryNetworkBannerAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeLibraryNetworkBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeLibraryNetworkBannerAdapter
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
                mBanner = new PubnativeBanner();
                mBanner.setListener(this);
                mBanner.load(context, appToken, PubnativeBanner.Size.BANNER_50, PubnativeBanner.Position.BOTTOM);
            }
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mBanner != null) {
            result = mBanner.isReady();
        }
        return result;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mBanner != null) {
            mBanner.show();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mBanner != null) {
            mBanner.destroy();
        }
    }

    @Override
    public void hide() {

        Log.v(TAG, "hide");
        if (mBanner != null) {
            mBanner.destroy();
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // PubnativeFeedBanner.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeBannerLoadFinish(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerLoadFinish");
        invokeLoadFinish();
    }

    @Override
    public void onPubnativeBannerLoadFail(PubnativeBanner banner, Exception exception) {
        Log.v(TAG, "onPubnativeBannerLoadFail", exception);
        invokeLoadFail(exception);
    }

    @Override
    public void onPubnativeBannerShow(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerShow");
        invokeShow();
    }

    @Override
    public void onPubnativeBannerImpressionConfirmed(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerImpressionConfirmed");
        invokeImpressionConfirmed();
    }

    @Override
    public void onPubnativeBannerClick(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerClick");
        invokeClick();
    }

    @Override
    public void onPubnativeBannerHide(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerHide");
        invokeHide();
    }
}
