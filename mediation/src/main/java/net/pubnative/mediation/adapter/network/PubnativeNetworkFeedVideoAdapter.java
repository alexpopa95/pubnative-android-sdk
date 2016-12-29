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

public abstract class PubnativeNetworkFeedVideoAdapter extends PubnativeNetworkAdapter {

    private   static final String TAG           = PubnativeNetworkFeedVideoAdapter.class.getSimpleName();
    protected static final String KEY_APP_TOKEN = "apptoken";

    protected AdListener   mAdListener;
    protected LoadListener mLoadListener;

    /**
     * Interface for callbacks related to the feedVideo view behaviour
     */
    public interface LoadListener {

        /**
         * Called whenever the feedVideo finished loading an ad
         *
         * @param feedVideo feedVideo that finished the initialize
         */
        void onAdapterLoadFinish(PubnativeNetworkFeedVideoAdapter feedVideo);

        /**
         * Called whenever the feedVideo failed loading an ad
         *
         * @param feedVideo   feedVideo that failed the initialize
         * @param exception    exception with the description of the initialize error
         */
        void onAdapterLoadFail(PubnativeNetworkFeedVideoAdapter feedVideo, Exception exception);
    }

    /**
     * Interface for callbacks related to the feedVideo view behaviour
     */
    public interface AdListener {

        /**
         * Called when the feedVideo was just shown on the screen
         *
         * @param feedVideo feedVideo that was shown in the screen
         */
        void onAdapterShow(PubnativeNetworkFeedVideoAdapter feedVideo);

        /**
         * Called when the feedVideo impression was confirmed
         *
         * @param feedVideo feedVideo which impression was confirmed
         */
        void onAdapterImpressionConfirmed(PubnativeNetworkFeedVideoAdapter feedVideo);

        /**
         * Called whenever the feedVideo was clicked by the user
         *
         * @param feedVideo feedVideo that was clicked
         */
        void onAdapterClick(PubnativeNetworkFeedVideoAdapter feedVideo);

        /**
         * Called whenever the feed video was removed from the screen
         *
         * @param feedVideo feedVideo that was hidden
         */
        void onAdapterHide(PubnativeNetworkFeedVideoAdapter feedVideo);

        /**
         * Called whenever the feedVideo was removed from the screen
         *
         * @param feedVideo feedVideo that was hidden
         */
        void onAdapterVideoStart(PubnativeNetworkFeedVideoAdapter feedVideo);

        /**
         * Called whenever the feedVideo was removed from the screen
         *
         * @param feedVideo feedVideo that was hidden
         */
        void onAdapterVideoFinish(PubnativeNetworkFeedVideoAdapter feedVideo);
    }

    //==============================================================================================
    // Overridable methods
    //==============================================================================================
    public void setLoadListener(PubnativeNetworkFeedVideoAdapter.LoadListener listener) {

        Log.v(TAG, "setLoadListener");
        mLoadListener = listener;
    }

    public void setAdListener(PubnativeNetworkFeedVideoAdapter.AdListener listener) {

        Log.v(TAG, "setAdListener");
        mAdListener = listener;
    }

    /**
     * Creates a new instance of PubnativeNetworkFeedBannerAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkFeedVideoAdapter(Map data) {

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
     * Starts loading the feedVideo ad
     *
     * @param context valid Context
     *
     */
    public abstract void load(Context context);

    /**
     * Tells if the feedVideo is ready to be shown in the screen
     *
     * @return true if ready, false if not
     */
    public abstract boolean isReady();

    /**
     * Starts showing the feedVideo for the adapted network
     * @param container valid container for the feed video
     */
    public abstract void show(ViewGroup container);

    /**
     * Destroys the current feedVideo for the adapted network
     */
    public abstract void destroy();

    /**
     * Hides the current feedVideo for the adapted network
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

    protected void invokeVideoStart() {

        Log.v(TAG, "invokeVideoStart");
        if (mAdListener != null) {
            mAdListener.onAdapterVideoStart(this);
        }
    }

    protected void invokeVideoFinish() {

        Log.v(TAG, "invokeVideoFinish");
        if (mAdListener != null) {
            mAdListener.onAdapterVideoFinish(this);
        }
    }
}
