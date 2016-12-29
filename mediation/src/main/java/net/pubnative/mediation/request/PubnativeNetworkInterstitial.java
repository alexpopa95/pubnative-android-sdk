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

package net.pubnative.mediation.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;
import net.pubnative.mediation.adapter.network.PubnativeNetworkInterstitialAdapter;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeNetworkInterstitial extends PubnativeNetworkWaterfall
        implements PubnativeNetworkInterstitialAdapter.LoadListener,
                   PubnativeNetworkInterstitialAdapter.AdListener {

    private static final String TAG = PubnativeNetworkInterstitial.class.getSimpleName();
    protected Listener                            mListener;
    protected Handler                             mHandler;
    protected boolean                             mIsLoading;
    protected boolean                             mIsShown;
    protected PubnativeNetworkInterstitialAdapter mAdapter;
    protected long                                mStartTimestamp;

    /**
     * Interface for callbacks related to the interstitial view behaviour
     */
    public interface Listener {

        /**
         * Called whenever the interstitial finished loading an ad
         * w
         *
         * @param interstitial interstitial that finished the initialize
         */
        void onPubnativeNetworkInterstitialLoadFinish(PubnativeNetworkInterstitial interstitial);

        /**
         * Called whenever the interstitial failed loading an ad
         *
         * @param interstitial interstitial that failed the initialize
         * @param exception    exception with the description of the initialize error
         */
        void onPubnativeNetworkInterstitialLoadFail(PubnativeNetworkInterstitial interstitial, Exception exception);

        /**
         * Called when the interstitial was just shown on the screen
         *
         * @param interstitial interstitial that was shown in the screen
         */
        void onPubnativeNetworkInterstitialShow(PubnativeNetworkInterstitial interstitial);

        /**
         * Called when the interstitial impression was confrimed
         *
         * @param interstitial interstitial which impression was confirmed
         */
        void onPubnativeNetworkInterstitialImpressionConfirmed(PubnativeNetworkInterstitial interstitial);

        /**
         * Called whenever the interstitial was clicked by the user
         *
         * @param interstitial interstitial that was clicked
         */
        void onPubnativeNetworkInterstitialClick(PubnativeNetworkInterstitial interstitial);

        /**
         * Called whenever the interstitial was removed from the screen
         *
         * @param interstitial interstitial that was hidden
         */
        void onPubnativeNetworkInterstitialHide(PubnativeNetworkInterstitial interstitial);
    }
    //==============================================================================================
    // Public methods
    //==============================================================================================

    /**
     * Sets a callback listener for this interstitial object
     *
     * @param listener valid PubnativeNetworkInterstitial.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Loads the interstitial ads before being shown
     * @param context valid Context
     * @param appToken valid app token string
     * @param placement valid placement string
     */
    public synchronized void load(Context context, String appToken, String placement) {

        Log.v(TAG, "initialize");
        if (mListener == null) {
            Log.e(TAG, "initialize - Error: listener was not set, have you configured one using setListener()?");
        } else if (context == null ||
                   TextUtils.isEmpty(appToken) ||
                   TextUtils.isEmpty(placement)) {
            invokeLoadFail(PubnativeException.INTERSTITIAL_PARAMETERS_INVALID);
        } else if (mIsLoading) {
            invokeLoadFail(PubnativeException.INTERSTITIAL_LOADING);
        } else if (mIsShown) {
            invokeLoadFail(PubnativeException.INTERSTITIAL_SHOWN);
        } else {
            mHandler = new Handler(Looper.getMainLooper());
            initialize(context, appToken, placement);
        }
    }

    /**
     * Tells if the interstitial is ready to be shown
     *
     * @return true if ready, false if not
     */
    public synchronized boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mAdapter != null) {
            result = mAdapter.isReady();
        }
        return result;
    }

    /**
     * This method will show the interstitial if the ad is available
     */
    public synchronized void show() {

        Log.v(TAG, "show");
        if (mIsLoading) {
            Log.v(TAG, "show - the ad is loading, try again later");
        } else if (mIsShown) {
            Log.v(TAG, "show - the ad is already shown");
        } else if (isReady()) {
            mAdapter.show();
        } else {
            Log.v(TAG, "show - the ad is still not loaded");
        }
    }
    //==============================================================================================
    // PubnativeNetworkWaterfall methods
    //==============================================================================================

    @Override
    protected void onWaterfallLoadFinish(boolean pacingActive) {

        if (pacingActive && mAdapter == null) {
            invokeLoadFail(PubnativeException.PLACEMENT_PACING_CAP);
        } else if (pacingActive) {
            invokeLoadFinish();
        } else {
            getNextNetwork();
        }
    }

    @Override
    protected void onWaterfallError(Exception exception) {

        invokeLoadFail(exception);
    }

    @Override
    protected void onWaterfallNextNetwork(PubnativeNetworkHub hub, PubnativeNetworkModel network, Map extras, boolean isCached) {

        mAdapter = hub.getInterstitialAdapter();
        if (mAdapter == null) {
            mInsight.trackUnreachableNetwork(mPlacement.currentPriority(), 0, PubnativeException.ADAPTER_TYPE_NOT_IMPLEMENTED);
            getNextNetwork();
        } else {
            mStartTimestamp = System.currentTimeMillis();
            // Add ML extras for adapter
            mAdapter.setCachingEnable(isCached);
            mAdapter.setExtras(extras);
            mAdapter.setLoadListener(this);
            mAdapter.execute(mContext, network.timeout);
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsLoading = false;
                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialLoadFinish(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    protected void invokeLoadFail(final Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsLoading = false;
                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialLoadFail(PubnativeNetworkInterstitial.this, exception);
                }
                mListener = null;
            }
        });
    }

    protected void invokeShow() {

        Log.v(TAG, "invokeShow");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialShow(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    protected void invokeImpressionConfirmed() {

        Log.v(TAG, "invokeImpressionConfirmed");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialImpressionConfirmed(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    protected void invokeClick() {

        Log.v(TAG, "invokeClick");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialClick(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    protected void invokeHide() {

        Log.v(TAG, "invokeHide");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialHide(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkInterstitialAdapter.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterLoadFinish(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onAdapterLoadFinish");

        interstitial.setAdListener(this);

        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        mInsight.trackSuccededNetwork(mPlacement.currentPriority(), responseTime);
        invokeLoadFinish();
    }

    @Override
    public void onAdapterLoadFail(PubnativeNetworkInterstitialAdapter interstitial, Exception exception) {

        Log.v(TAG, "onAdapterLoadFail");
        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        if(!exception.getClass().isAssignableFrom(PubnativeException.class)
           || exception.equals(PubnativeException.ADAPTER_UNKNOWN_ERROR)) {
            mInsight.trackAttemptedNetwork(mPlacement.currentPriority(), responseTime, exception);
        } else {
            mInsight.trackUnreachableNetwork(mPlacement.currentPriority(), responseTime, exception);
        }
        getNextNetwork();
    }

    // PubnativeNetworkInterstitialAdapter.AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterShow(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onAdapterShow");
        invokeShow();
    }

    @Override
    public void onAdapterImpressionConfirmed(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onAdapterImpressionConfirmed");
        invokeImpressionConfirmed();
    }

    @Override
    public void onAdapterClick(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onAdapterClick");
        invokeClick();
    }

    @Override
    public void onAdapterHide(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onAdapterHide");
        invokeHide();
    }
}
