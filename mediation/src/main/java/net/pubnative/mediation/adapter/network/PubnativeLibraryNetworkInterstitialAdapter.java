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

import net.pubnative.library.interstitial.PubnativeInterstitial;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeLibraryNetworkInterstitialAdapter
        extends PubnativeNetworkInterstitialAdapter implements PubnativeInterstitial.Listener {

    private static String TAG = PubnativeLibraryNetworkInterstitialAdapter.class.getSimpleName();

    protected PubnativeInterstitial mInterstitial;

    /**
     * Creates a new instance of PubnativeLibraryNetworkInterstitialAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeLibraryNetworkInterstitialAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeLibraryNetworkInterstitialAdapter
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
                mInterstitial = new PubnativeInterstitial();
                mInterstitial.setListener(this);
                mInterstitial.load(context, appToken);
            }
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mInterstitial != null) {
            result = mInterstitial.isReady();
        }
        return result;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // PubnativeInterstitial.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeInterstitialLoadFinish(PubnativeInterstitial interstitial) {

        Log.v(TAG, "onPubnativeInterstitialLoadFinish");
        invokeLoadFinish();
    }

    @Override
    public void onPubnativeInterstitialLoadFail(PubnativeInterstitial interstitial, Exception exception) {

        Log.v(TAG, "onPubnativeInterstitialLoadFail", exception);
        invokeLoadFail(exception);
    }

    @Override
    public void onPubnativeInterstitialShow(PubnativeInterstitial interstitial) {

        Log.v(TAG, "onPubnativeInterstitialShow");
        invokeShow();
    }

    @Override
    public void onPubnativeInterstitialImpressionConfirmed(PubnativeInterstitial interstitial) {

        Log.v(TAG, "onPubnativeInterstitialImpressionConfirmed");
        invokeImpressionConfirmed();
    }

    @Override
    public void onPubnativeInterstitialClick(PubnativeInterstitial interstitial) {

        Log.v(TAG, "onPubnativeInterstitialClick");
        invokeClick();
    }

    @Override
    public void onPubnativeInterstitialHide(PubnativeInterstitial interstitial) {

        Log.v(TAG, "onPubnativeInterstitialHide");
        invokeHide();
    }
}
