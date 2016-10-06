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
import android.view.ViewGroup;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public abstract class PubnativeNetworkFeedBannerAdapter extends PubnativeNetworkAdapter {

    private   static final String TAG           = PubnativeNetworkFeedBannerAdapter.class.getSimpleName();
    protected static final String KEY_APP_TOKEN = "apptoken";

    protected AdListener   mAdListener;
    protected LoadListener mLoadListener;

    /**
     * Interface for callbacks related to the feedBanner view behaviour
     */
    public interface LoadListener {

        /**
         * Called whenever the feedBanner finished loading an ad
         *
         * @param feedBanner feedBanner that finished the initialize
         */
        void onAdapterLoadFinish(PubnativeNetworkFeedBannerAdapter feedBanner);

        /**
         * Called whenever the feedBanner failed loading an ad
         *
         * @param feedBanner feedBanner that failed the initialize
         * @param exception    exception with the description of the initialize error
         */
        void onAdapterLoadFail(PubnativeNetworkFeedBannerAdapter feedBanner, Exception exception);
    }
    
    /**
     * Interface for callbacks related to the feedBanner view behaviour
     */
    public interface AdListener {

        /**
         * Called when the feedBanner was just shown on the screen
         *
         * @param feedBanner feedBanner that was shown in the screen
         */
        void onAdapterShow(PubnativeNetworkFeedBannerAdapter feedBanner);

        /**
         * Called when the feedBanner impression was confirmed
         *
         * @param feedBanner feedBanner which impression was confirmed
         */
        void onAdapterImpressionConfirmed(PubnativeNetworkFeedBannerAdapter feedBanner);

        /**
         * Called whenever the feedBanner was clicked by the user
         *
         * @param feedBanner feedBanner that was clicked
         */
        void onAdapterClick(PubnativeNetworkFeedBannerAdapter feedBanner);

        /**
         * Called whenever the feed banner was removed from the screen
         *
         * @param feedBanner feedBanner that was hidden
         */
        void onAdapterHide(PubnativeNetworkFeedBannerAdapter feedBanner);
    }

    //==============================================================================================
    // Overridable methods
    //==============================================================================================
    public void setLoadListener(PubnativeNetworkFeedBannerAdapter.LoadListener listener) {

        Log.v(TAG, "setLoadListener");
        mLoadListener = listener;
    }

    public void setAdListener(PubnativeNetworkFeedBannerAdapter.AdListener listener) {

        Log.v(TAG, "setAdListener");
        mAdListener = listener;
    }
    
    /**
     * Creates a new instance of PubnativeNetworkFeedBannerAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkFeedBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeNetworkAdapter
    //==============================================================================================
    @Override
    public void execute(Context context, int timeoutInMillis) {
        startTimeout(timeoutInMillis);
        load(context);
    }

    @Override
    protected void onTimeout() {
        invokeLoadFail(PubnativeException.ADAPTER_TIMEOUT);
    }

    //==============================================================================================
    // Abstract
    //==============================================================================================
    /**
     * Starts loading the feedBanner ad
     *
     * @param context valid Context
     *
     */
    public abstract void load(Context context);

    /**
     * Tells if the feedBanner is ready to be shown in the screen
     *
     * @return true if ready, false if not
     */
    public abstract boolean isReady();

    /**
     * Starts showing the feedBanner for the adapted network
     * @param container valid container for the feed banner
     */
    public abstract void show(ViewGroup container);

    /**
     * Destroys the current feedBanner for the adapted network
     */
    public abstract void destroy();

    /**
     * Hides the current feedBanner for the adapted network
     */
    public abstract void hide();

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        cancelTimeout();
        if (mLoadListener != null) {
            mLoadListener.onAdapterLoadFinish(this);
        }
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
