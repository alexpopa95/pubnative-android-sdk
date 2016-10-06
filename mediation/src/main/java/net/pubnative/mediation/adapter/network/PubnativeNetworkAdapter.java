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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.pubnative.mediation.request.model.PubnativeAdTargetingModel;

import java.util.Map;

public abstract class PubnativeNetworkAdapter {

    private static String TAG = PubnativeNetworkAdapter.class.getSimpleName();
    protected PubnativeNetworkAdapterRunnable mTimeoutRunnable;
    protected Map                             mData;
    protected Map<String, String>             mExtras;
    protected Handler                         mHandler;
    protected PubnativeAdTargetingModel       mTargeting;
    protected boolean                         mUseCaching = false;
    //==============================================================================================
    // Adapter Runnable
    //==============================================================================================

    protected class PubnativeNetworkAdapterRunnable implements Runnable {

        private final String TAG = PubnativeNetworkAdapterRunnable.class.getSimpleName();

        @Override
        public void run() {

            Log.v(TAG, "timeout");
            onTimeout();
        }
    }
    //==============================================================================================
    // PubnativeNetworkAdapter
    //==============================================================================================

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkAdapter(Map data) {

        mData = data;
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
     * This method sets the extras for the adapter request
     *
     * @param targeting valid targeting filed
     */
    public void setTargeting(PubnativeAdTargetingModel targeting) {

        Log.v(TAG, "setExtras");
        mTargeting = targeting;
    }

    public void setCachingEnable(boolean enable) {
        Log.v(TAG, "setCachingEnable");
        mUseCaching = enable;
    }

    public boolean isCachingEnable() {
        Log.v(TAG, "isCachingEnable");
        return mUseCaching;
    }

    /**
     * Starts this adapter process
     *
     * @param context         valid context
     * @param timeoutInMillis timeout in milliseconds, if 0, then no timeout is set
     */
    public abstract void execute(Context context, int timeoutInMillis);

    protected abstract void onTimeout();

    //==============================================================================================
    // Timeout helpers
    //==============================================================================================
    protected void startTimeout(int timeoutInMillis) {

        Log.v(TAG, "startTimeout");
        if (timeoutInMillis > 0) {
            mTimeoutRunnable = new PubnativeNetworkAdapterRunnable();
            mHandler = new Handler(Looper.getMainLooper());
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
}
