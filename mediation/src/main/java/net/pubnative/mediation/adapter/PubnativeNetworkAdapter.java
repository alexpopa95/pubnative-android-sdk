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

package net.pubnative.mediation.adapter;

import  android.content.Context;
import android.os.Handler;
import android.util.Log;

import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.Map;
import java.util.concurrent.TimeoutException;

public abstract class PubnativeNetworkAdapter {

    private static String TAG = PubnativeNetworkAdapter.class.getSimpleName();
    protected PubnativeNetworkAdapter.Listener mListener;
    protected PubnativeNetworkAdapterRunnable  mTimeoutRunnable;
    protected Map                              mData;
    protected Map<String, String>              mExtras;
    protected Handler                          mHandler;

    /**
     * Listener
     */
    public interface Listener {

        /**
         * Invoked when PubnativeNetworkAdapter starts the request with valid params.
         *
         * @param adapter Object used for requesting the ad.
         */
        void onPubnativeNetworkAdapterRequestStarted(PubnativeNetworkAdapter adapter);

        /**
         * Invoked when ad was received successfully from the network.
         *
         * @param adapter Object used for requesting the ad.
         * @param ad      Loaded ad object.
         */
        void onPubnativeNetworkAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad);

        /**
         * Invoked when ad request is failed or when networks gives no ad.
         *
         * @param adapter   Object used for requesting the ad.
         * @param exception Exception raised with proper message to indicate request failure.
         */
        void onPubnativeNetworkAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception);
    }

    //==============================================================================================
    // Adapter Runnable
    //==============================================================================================
    protected class PubnativeNetworkAdapterRunnable implements Runnable {

        private final String TAG = PubnativeNetworkAdapterRunnable.class.getSimpleName();
        private PubnativeNetworkAdapter mAdapter;

        public PubnativeNetworkAdapterRunnable(PubnativeNetworkAdapter adapter) {

            mAdapter = adapter;
        }

        @Override
        public void run() {

            Log.v(TAG, "timeout");
            // Invoke failed and avoid more callbacks by setting listener to null
            mAdapter.invokeFailed(new TimeoutException(PubnativeNetworkAdapter.this.getClass().getSimpleName() + ".doRequest - adapter timeout"));
        }
    }
    //==============================================================================================
    // PubnativeNetworkAdapter
    //==============================================================================================

    /**
     * Creates a new instance of PubnativeNetworkAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkAdapter(Map data) {

        mData = data;
    }

    /**
     * get extras map setted to the adapter when doing the request
     *
     * @return extras Map setted when doing the request
     */
    public Map<String, String> getExtras() {

        Log.v(TAG, "getExtras");
        return mExtras;
    }

    /**
     * This method sets the extras for the adapter request
     *
     * @param extras valid extras Map
     */
    public void setExtras(Map<String, String> extras) {

        Log.v(TAG, "setExtras");
        mExtras = extras;
    }

    /**
     * This method starts the adapter request setting up the configured timeout
     *
     * @param context         valid context
     * @param timeoutInMillis timeout in milliseconds. time to wait for an adapter to respond.
     * @param listener        lister to track the callbacks on adapter
     */
    public void doRequest(Context context, int timeoutInMillis, PubnativeNetworkAdapter.Listener listener) {

        Log.v(TAG, "doRequest");
        if (listener == null) {
            Log.e(TAG, "doRequest - context not specified, dropping the call");
        } else {
            mListener = listener;
            invokeStart();
            if (context == null) {
                invokeFailed(new IllegalArgumentException("PubnativeNetworkAdapter - Error: null context provided"));
            } else {
                startTimeout(timeoutInMillis);
                request(context);
            }
        }
    }

    public abstract void request(Context context);

    // Helpers
    //----------------------------------------------------------------------------------------------
    protected void startTimeout(int timeoutInMillis) {

        Log.v(TAG, "startTimeout");
        if (timeoutInMillis > 0) {
            mTimeoutRunnable = new PubnativeNetworkAdapterRunnable(this);
            mHandler = new Handler();
            mHandler.postDelayed(mTimeoutRunnable, timeoutInMillis);
        }
    }

    protected void cancelTimeout() {

        Log.v(TAG, "cancelTimeout");
        if (mHandler != null && mTimeoutRunnable != null) {
            mHandler.removeCallbacks(mTimeoutRunnable);
            mHandler = null;
        }
    }
    // Callback helpers
    //----------------------------------------------------------------------------------------------

    protected void invokeStart() {

        Log.v(TAG, "invokeStart");
        if (mListener != null) {
            mListener.onPubnativeNetworkAdapterRequestStarted(this);
        }
    }

    protected void invokeLoaded(PubnativeAdModel ad) {

        Log.v(TAG, "invokeLoaded");
        cancelTimeout();
        if (mListener != null) {
            mListener.onPubnativeNetworkAdapterRequestLoaded(this, ad);
        }
        mListener = null;
    }

    protected void invokeFailed(Exception exception) {

        Log.v(TAG, "invokeFailed: " + exception);
        cancelTimeout();
        if (mListener != null) {
            mListener.onPubnativeNetworkAdapterRequestFailed(this, exception);
        }
        mListener = null;
    }
}
