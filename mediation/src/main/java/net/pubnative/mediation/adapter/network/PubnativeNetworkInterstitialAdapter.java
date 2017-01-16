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
import android.util.Log;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.PubnativeNetworkInterstitial;

import java.util.Map;

public abstract class PubnativeNetworkInterstitialAdapter extends PubnativeNetworkAdapter {

    private   static final String TAG           = PubnativeNetworkInterstitialAdapter.class.getSimpleName();
    protected static final String KEY_APP_TOKEN = "apptoken";

    protected AdListener   mAdListener;
    protected LoadListener mLoadListener;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkInterstitialAdapter(Map data) {

        super(data);
    }

    /**
     * Interface for callbacks related to the interstitial view behaviour
     */
    public interface LoadListener {

        /**
         * Called whenever the interstitial finished loading an ad
         *
         * @param interstitial interstitial that finished the initialize
         */
        void onAdapterLoadFinish(PubnativeNetworkInterstitialAdapter interstitial);

        /**
         * Called whenever the interstitial failed loading an ad
         *
         * @param interstitial interstitial that failed the initialize
         * @param exception    exception with the description of the initialize error
         */
        void onAdapterLoadFail(PubnativeNetworkInterstitialAdapter interstitial, Exception exception);
    }

    /**
     * Interface for callbacks related to the interstitial view behaviour
     */
    public interface AdListener {

        /**
         * Called when the interstitial was just shown on the screen
         *
         * @param interstitial interstitial that was shown in the screen
         */
        void onAdapterShow(PubnativeNetworkInterstitialAdapter interstitial);

        /**
         * Called when the interstitial impression was confrimed
         *
         * @param interstitial interstitial which impression was confirmed
         */
        void onAdapterImpressionConfirmed(PubnativeNetworkInterstitialAdapter interstitial);

        /**
         * Called whenever the interstitial was clicked by the user
         *
         * @param interstitial interstitial that was clicked
         */
        void onAdapterClick(PubnativeNetworkInterstitialAdapter interstitial);

        /**
         * Called whenever the interstitial was removed from the screen
         *
         * @param interstitial interstitial that was hidden
         */
        void onAdapterHide(PubnativeNetworkInterstitialAdapter interstitial);
    }
    //==============================================================================================
    // Overridable methods
    //==============================================================================================

    public void setLoadListener(PubnativeNetworkInterstitialAdapter.LoadListener listener) {

        Log.v(TAG, "setLoadListener");
        mLoadListener = listener;
    }

    public void setAdListener(PubnativeNetworkInterstitialAdapter.AdListener listener) {

        Log.v(TAG, "setAdListener");
        mAdListener = listener;
    }

    //==============================================================================================
    // PubnativeNetworkAdapter
    //==============================================================================================
    @Override
    protected void onTimeout() {

        invokeLoadFail(PubnativeException.ADAPTER_TIMEOUT);
    }

    @Override
    public void execute(Context context, int timeoutInMillis) {

        startTimeout(timeoutInMillis);
        load(context);
    }
    //==============================================================================================
    // Abstract
    //==============================================================================================

    /**
     * Starts loading the interstitial ad
     *
     * @param context valid Context
     */
    public abstract void load(Context context);

    /**
     * Tells if the interstitial is ready to be shown in the screen
     *
     * @return true if ready, false if not
     */
    public abstract boolean isReady();

    /**
     * Starts showing the interstitial for the adapted network
     */
    public abstract void show();

    /**
     * Destroys the current interstitial for the adapted network
     */
    public abstract void destroy();

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        cancelTimeout();
        if (mLoadListener != null) {
            mLoadListener.onAdapterLoadFinish(this);
        }
        mLoadListener = null;
    }

    protected void invokeLoadFail(Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        cancelTimeout();
        if (mLoadListener != null) {
            mLoadListener.onAdapterLoadFail(this, exception);
        }
        mLoadListener = null;
    }

    protected void invokeShow() {

        Log.v(TAG, "invokeShow");
        if (mAdListener != null) {
            mAdListener.onAdapterShow(this);
        }
    }

    protected void invokeImpressionConfirmed() {

        Log.v(TAG, "invokeImpressionConfirmed");
        if (mAdListener != null) {
            mAdListener.onAdapterImpressionConfirmed(this);
        }
    }

    protected void invokeClick() {

        Log.v(TAG, "invokeClick");
        if (mAdListener != null) {
            mAdListener.onAdapterClick(this);
        }
    }

    protected void invokeHide() {

        Log.v(TAG, "invokeHide");
        if (mAdListener != null) {
            mAdListener.onAdapterHide(this);
        }
    }
}
