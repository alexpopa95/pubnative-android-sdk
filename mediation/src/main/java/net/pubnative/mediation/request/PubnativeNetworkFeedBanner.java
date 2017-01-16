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
import android.view.ViewGroup;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;
import net.pubnative.mediation.adapter.network.PubnativeNetworkFeedBannerAdapter;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeNetworkFeedBanner extends PubnativeNetworkWaterfall
        implements PubnativeNetworkFeedBannerAdapter.AdListener,
                   PubnativeNetworkFeedBannerAdapter.LoadListener {

    private static final String TAG = PubnativeNetworkFeedBanner.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    protected Listener                          mListener;
    protected Handler                           mHandler;
    protected boolean                           mIsLoading;
    protected boolean                           mIsShown;
    protected PubnativeNetworkFeedBannerAdapter mAdapter;
    protected long                              mStartTimestamp;

    /**
     * Interface for callbacks related to the feedBanner view behaviour
     */
    public interface Listener {

        /**
         * Called whenever the feedBanner finished loading an ad
         * w
         *
         * @param feedBanner feedBanner that finished the initialize
         */
        void onPubnativeNetworkFeedBannerLoadFinish(PubnativeNetworkFeedBanner feedBanner);

        /**
         * Called whenever the feedBanner failed loading an ad
         *
         * @param feedBanner feedBanner that failed the initialize
         * @param exception  exception with the description of the initialize error
         */
        void onPubnativeNetworkFeedBannerLoadFail(PubnativeNetworkFeedBanner feedBanner, Exception exception);

        /**
         * Called when the feedBanner was just shown on the screen
         *
         * @param feedBanner feedBanner that was shown in the screen
         */
        void onPubnativeNetworkFeedBannerShow(PubnativeNetworkFeedBanner feedBanner);

        /**
         * Called when the feedBanner impression was confrimed
         *
         * @param feedBanner feedBanner which impression was confirmed
         */
        void onPubnativeNetworkFeedBannerImpressionConfirmed(PubnativeNetworkFeedBanner feedBanner);

        /**
         * Called whenever the feedBanner was clicked by the user
         *
         * @param feedBanner feedBanner that was clicked
         */
        void onPubnativeNetworkFeedBannerClick(PubnativeNetworkFeedBanner feedBanner);

        /**
         * Called whenever the feedBanner was removed from the screen
         *
         * @param feedBanner feedBanner that was hidden
         */
        void onPubnativeNetworkFeedBannerHide(PubnativeNetworkFeedBanner feedBanner);
    }
    //==============================================================================================
    // Public methods
    //==============================================================================================

    /**
     * Sets a callback listener for this feedBanner object
     *
     * @param listener valid PubnativeNetworkFeedBanner.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Loads the feedBanner ads before being shown
     * @param context valid context
     * @param appToken valid app token string
     * @param placement valid placement string
     */
    public synchronized void load(Context context, String appToken, String placement) {

        Log.v(TAG, "initialize");
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (mListener == null) {
            Log.e(TAG, "initialize - Error: listener was not set, have you configured one using setListener()?");
        }
        if (context == null ||
            TextUtils.isEmpty(appToken) ||
            TextUtils.isEmpty(placement)) {
            invokeLoadFail(PubnativeException.FEED_BANNER_PARAMETERS_INVALID);
        } else if (mIsLoading) {
            invokeLoadFail(PubnativeException.FEED_BANNER_LOADING);
        } else if (mIsShown) {
            invokeLoadFail(PubnativeException.FEED_BANNER_SHOWN);
        } else {
            mIsLoading = true;
            initialize(context, appToken, placement);
        }
    }

    /**
     * Tells if the feedBanner is ready to be shown
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
     * This method will show the feedBanner if the ad is available
     *
     * @param container valid view group container for the banner
     */
    public synchronized void show(ViewGroup container) {

        Log.v(TAG, "show");
        if (container == null) {
            Log.e(TAG, "show - passed container argument cannot be null");
        } else if (mIsLoading) {
            Log.w(TAG, "show - the ad is loading");
        } else if (mIsShown) {
            Log.w(TAG, "show - the ad is already shown");
        } else if (isReady()) {
            mIsShown = true;
            mAdapter.show(container);
        } else {
            Log.w(TAG, "show - the ad is not loaded yet");
        }
    }

    /**
     * Destroy the current Feed banner
     */
    public void destroy() {

        Log.v(TAG, "destroy");
        mAdapter.destroy();
    }

    /**
     * Hides the current InFeed banner
     */
    public void hide() {

        Log.v(TAG, "hide");
        if (mIsShown) {
            mAdapter.hide();
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

        mAdapter = hub.getFeedBannerAdapter();
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

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsLoading = false;
                Log.v(TAG, "invokeLoadFinish");
                if (mListener != null) {
                    mListener.onPubnativeNetworkFeedBannerLoadFinish(PubnativeNetworkFeedBanner.this);
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
                    mListener.onPubnativeNetworkFeedBannerLoadFail(PubnativeNetworkFeedBanner.this, exception);
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
                    mListener.onPubnativeNetworkFeedBannerShow(PubnativeNetworkFeedBanner.this);
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
                    mListener.onPubnativeNetworkFeedBannerImpressionConfirmed(PubnativeNetworkFeedBanner.this);
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
                    mListener.onPubnativeNetworkFeedBannerClick(PubnativeNetworkFeedBanner.this);
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
                    mListener.onPubnativeNetworkFeedBannerHide(PubnativeNetworkFeedBanner.this);
                }
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkFeedBannerAdapter.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterLoadFinish(PubnativeNetworkFeedBannerAdapter feedBanner) {

        Log.v(TAG, "onAdapterLoadFinish");

        feedBanner.setAdListener(this);
        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        mInsight.trackSuccededNetwork(mPlacement.currentPriority(), responseTime);
        invokeLoadFinish();
    }

    @Override
    public void onAdapterLoadFail(PubnativeNetworkFeedBannerAdapter feedBanner, Exception exception) {

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

    // PubnativeNetworkFeedBannerAdapter.AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterShow(PubnativeNetworkFeedBannerAdapter feedBanner) {

        Log.v(TAG, "onAdapterShow");
        invokeShow();
    }

    @Override
    public void onAdapterImpressionConfirmed(PubnativeNetworkFeedBannerAdapter feedBanner) {

        Log.v(TAG, "onAdapterImpressionConfirmed");
        invokeImpressionConfirmed();
    }

    @Override
    public void onAdapterClick(PubnativeNetworkFeedBannerAdapter feedBanner) {

        Log.v(TAG, "onAdapterClick");
        invokeClick();
    }

    @Override
    public void onAdapterHide(PubnativeNetworkFeedBannerAdapter feedBanner) {

        Log.v(TAG, "onAdapterHide");
        invokeHide();
    }
}
