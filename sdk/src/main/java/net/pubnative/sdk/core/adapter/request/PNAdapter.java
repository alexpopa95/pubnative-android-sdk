// The MIT License (MIT)
//
// Copyright (c) 2017 PubNative GmbH
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

package net.pubnative.sdk.core.adapter.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.core.insights.model.PNInsightModel;
import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.core.request.PNAdTargetingModel;

import java.util.Map;

public abstract class PNAdapter {

    private static String TAG = PNAdapter.class.getSimpleName();

    protected static final String DEFAULT_ERROR = "Unknown";

    /**
     * LoadListener
     */
    public interface LoadListener {

        /**
         * Invoked when ad was received successfully from the network.
         *
         * @param adapter Object used for requesting the ad.
         * @param ad      Loaded ad object.
         */
        void onAdapterLoadFinish(PNAdapter adapter, PNAdModel ad);

        /**
         * Invoked when ad request is failed or when networks gives no ad.
         *
         * @param adapter   Object used for requesting the ad.
         * @param exception Exception raised with proper message to indicate request failure.
         */
        void onAdapterLoadFail(PNAdapter adapter, Exception exception);
    }

    protected PubnativeNetworkAdapterRunnable mTimeoutRunnable;
    protected Context                         mContext;
    protected Map                             mData;
    protected Map<String, String>             mExtras;
    protected Handler                         mHandler;
    protected PNInsightModel                  mInsight;
    protected String                          mNetworkName;
    protected boolean                         mIsParallelRequest;
    protected boolean                         mIsCPICacheEnabled;
    protected long                            mRequestStartTimeStamp;
    protected LoadListener                    mLoadListener;

    //==============================================================================================
    // Adapter Runnable
    //==============================================================================================

    protected class PubnativeNetworkAdapterRunnable implements Runnable {

        private final String TAG = PubnativeNetworkAdapterRunnable.class.getSimpleName();

        @Override
        public void run() {
            onTimeout();
        }
    }

    //==============================================================================================
    // PubnativeLibraryNetworkAdapter
    //==============================================================================================

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PNAdapter(Map data) {
        mData = data;
    }

    /**
     * Sets loadListener for this request
     *
     * @param listener valid loadListener
     */
    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    /**
     * This method sets the extras for the adapter request
     *
     * @param extras valid extras Map
     */
    public void setExtras(Map<String, String> extras) {
        mExtras = extras;
    }

    /**
     * This method sets CPI fetchAssets value available for networks
     *
     * @param enabled true if enabled, false if disabled
     */
    public void setCPICacheEnabled(boolean enabled) {
        mIsCPICacheEnabled = enabled;
    }

    /**
     * This method sets parallel request mode enabled or disabled
     *
     * @param enabled true if enabled, false if disabled
     */
    public void setParallelRequestMode(boolean enabled) {
        mIsParallelRequest = enabled;
    }

    /**
     * This method retrieves parallel request mode set
     *
     * @return true if set, false if not
     */
    public boolean isParallelRequest() {
        return mIsParallelRequest;
    }

    /**
     * sets insight to track request when time out occur for this request.
     *
     * @param insight valid insight for track this request.
     */
    public void setInsight(PNInsightModel insight) {
        mInsight = insight;
    }

    /**
     * this function returns time taken to complete this request.
     *
     * @return valid time in milliseconds.
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - mRequestStartTimeStamp;
    }

    /**
     * Starts this adapter process
     *
     * @param context         valid context
     * @param timeoutInMillis timeout in milliseconds, if 0, then no timeout is set
     */
    public void execute(Context context, int timeoutInMillis) {

        mRequestStartTimeStamp = System.currentTimeMillis();
        mContext = context;
        startTimeout(timeoutInMillis);
        request(context);
    }

    //==============================================================================================
    // Timeout helpers
    //==============================================================================================
    protected void startTimeout(int timeoutInMillis) {
        if (timeoutInMillis > 0) {
            mTimeoutRunnable = new PubnativeNetworkAdapterRunnable();
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(mTimeoutRunnable, timeoutInMillis);
        }
    }

    protected void cancelTimeout() {
        if (mHandler != null && mTimeoutRunnable != null) {
            mHandler.removeCallbacks(mTimeoutRunnable);
            mHandler = null;
        }
    }

    protected void onTimeout() {
        invokeLoadFail(PNException.ADAPTER_TIMEOUT);
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish(PNAdModel ad) {
        cancelTimeout();
        LoadListener listener = mLoadListener;
        mLoadListener = null;
        if (listener != null) {
            listener.onAdapterLoadFinish(this, ad);
        } else if (mInsight != null) {
            mInsight.sendRescueInsight(mNetworkName, getElapsedTime());
        }

    }

    protected void invokeLoadFail(Exception exception) {
        cancelTimeout();
        LoadListener listener = mLoadListener;
        mLoadListener = null;
        if (listener != null) {
            listener.onAdapterLoadFail(this, exception);
        }
    }

    //==============================================================================================
    // Abstract methods
    //==============================================================================================
    protected abstract void request(Context context);
}
