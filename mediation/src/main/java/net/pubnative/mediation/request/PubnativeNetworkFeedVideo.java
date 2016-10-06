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
import net.pubnative.mediation.adapter.network.PubnativeNetworkFeedVideoAdapter;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeNetworkFeedVideo extends PubnativeNetworkWaterfall
        implements PubnativeNetworkFeedVideoAdapter.LoadListener,
                   PubnativeNetworkFeedVideoAdapter.AdListener {

    private static final String TAG = PubnativeNetworkFeedVideo.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    protected Listener                            mListener;
    protected Handler                             mHandler;
    protected boolean                             mIsLoading;
    protected boolean                             mIsShown;
    protected PubnativeNetworkFeedVideoAdapter    mAdapter;
    protected long                                mStartTimestamp;

    /**
     * Interface for callbacks related to the feedVideo view behaviour
     */
    public interface Listener {

        /**
         * Called whenever the feedVideo finished loading an ad
         *
         * @param feedVideo feedVideo that finished the initialize
         */
        void onPubnativeNetworkFeedVideoLoadFinish(PubnativeNetworkFeedVideo feedVideo);

        /**
         * Called whenever the feedVideo failed loading an ad
         *
         * @param feedVideo feedVideo that failed the initialize
         * @param exception exception with the description of the initialize error
         */
        void onPubnativeNetworkFeedVideoLoadFail(PubnativeNetworkFeedVideo feedVideo, Exception exception);

        /**
         * Called when the feedVideo was just shown on the screen
         *
         * @param feedVideo feedVideo that was shown in the screen
         */
        void onPubnativeNetworkFeedVideoShow(PubnativeNetworkFeedVideo feedVideo);

        /**
         * Called whenever the feedVideo finishes
         *
         * @param feedVideo feedVideo that has been stopped
         */
        void onPubnativeNetworkFeedVideoFinish(PubnativeNetworkFeedVideo feedVideo);

        /**
         * Called whenever the feedVideo starts
         *
         * @param feedVideo feedVideo that has been stopped
         */
        void onPubnativeNetworkFeedVideoStart(PubnativeNetworkFeedVideo feedVideo);

        /**
         * Called when impression is confirmed
         *
         * @param feedVideo feedVideo which impression was confirmed
         */
        void onPubnativeNetworkFeedVideoImpressionConfirmed(PubnativeNetworkFeedVideo feedVideo);

        /**
         * Called whenever the feedVideo was clicked by the user
         *
         * @param feedVideo feedVideo that was clicked
         */
        void onPubnativeNetworkFeedVideoClick(PubnativeNetworkFeedVideo feedVideo);

        /**
         * Called whenever the feedVideo was removed from the screen
         *
         * @param feedVideo feedVideo that was hidden
         */
        void onPubnativeNetworkFeedVideoHide(PubnativeNetworkFeedVideo feedVideo);
    }
    //==============================================================================================
    // Public methods
    //==============================================================================================

    /**
     * Sets a callback listener for this feedVideo object
     *
     * @param listener valid PubnativeNetworkFeedVideo.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Loads the feedVideo ads before being shown
     * @param context   valid Context
     * @param appToken  valid app token string
     * @param placement valid placement string
     */
    public synchronized void load(Context context, String appToken, String placement) {

        Log.v(TAG, "initialize");
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (mListener == null) {
            Log.e(TAG, "initialize - Error: listener was not set, have you configured one using setListener()?");
        } else if (context == null
                   || TextUtils.isEmpty(appToken)
                   || TextUtils.isEmpty(placement)) {
            invokeLoadFail(PubnativeException.FEED_VIDEO_PARAMETERS_INVALID);
        } else if (mIsLoading) {
            invokeLoadFail(PubnativeException.FEED_VIDEO_LOADING);
        } else if (mIsShown) {
            invokeLoadFail(PubnativeException.FEED_VIDEO_SHOWN);
        } else {
            mIsLoading = true;
            initialize(context, appToken, placement);
        }
    }

    /**
     * Tells if the feedVideo is ready to be shown
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
     * This method will show the feedVideo if the ad is available
     */
    public synchronized void show(ViewGroup container) {

        Log.v(TAG, "show");
        if (container == null) {
            Log.e(TAG, "show - passed container argument cannot be null");
        } else if (mIsLoading) {
            Log.w(TAG, "show - the feed video is loading");
        } else if (mIsShown) {
            Log.w(TAG, "show - the feed video is already shown");
        } else if (isReady()) {
            mIsShown = true;
            mAdapter.show(container);
        } else {
            Log.w(TAG, "show - the feed video is not loaded yet");
        }
    }

    /**
     * Hides the current InFeed video
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

        mAdapter = hub.getFeedVideoAdapter();
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
                    mListener.onPubnativeNetworkFeedVideoLoadFinish(PubnativeNetworkFeedVideo.this);
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
                    mListener.onPubnativeNetworkFeedVideoLoadFail(PubnativeNetworkFeedVideo.this, exception);
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
                    mListener.onPubnativeNetworkFeedVideoShow(PubnativeNetworkFeedVideo.this);
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
                    mListener.onPubnativeNetworkFeedVideoImpressionConfirmed(PubnativeNetworkFeedVideo.this);
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
                    mListener.onPubnativeNetworkFeedVideoClick(PubnativeNetworkFeedVideo.this);
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
                    mListener.onPubnativeNetworkFeedVideoHide(PubnativeNetworkFeedVideo.this);
                }
            }
        });
    }

    protected void invokeVideoFinish() {

        Log.v(TAG, "invokeVideoFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkFeedVideoFinish(PubnativeNetworkFeedVideo.this);
                }
            }
        });
    }

    protected void invokeVideoStart() {

        Log.v(TAG, "invokeVideoStart");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkFeedVideoStart(PubnativeNetworkFeedVideo.this);
                }
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkFeedVideoAdapter.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterLoadFinish(PubnativeNetworkFeedVideoAdapter feedVideo) {

        Log.v(TAG, "onAdapterLoadFinish");

        feedVideo.setAdListener(this);
        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        mInsight.trackSuccededNetwork(mPlacement.currentPriority(), responseTime);
        invokeLoadFinish();
    }

    @Override
    public void onAdapterLoadFail(PubnativeNetworkFeedVideoAdapter feedVideo, Exception exception) {

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

    //==============================================================================================
    // PubnativeNetworkFeedVideoAdapter.AdListener
    //==============================================================================================
    @Override
    public void onAdapterShow(PubnativeNetworkFeedVideoAdapter feedVideo) {

        Log.v(TAG, "onAdapterShow");
        invokeShow();
    }

    @Override
    public void onAdapterImpressionConfirmed(PubnativeNetworkFeedVideoAdapter feedVideo) {

        Log.v(TAG, "onAdapterImpressionConfirmed");
        invokeImpressionConfirmed();
    }

    @Override
    public void onAdapterClick(PubnativeNetworkFeedVideoAdapter feedVideo) {

        Log.v(TAG, "onAdapterClick");
        invokeClick();
    }

    @Override
    public void onAdapterHide(PubnativeNetworkFeedVideoAdapter feedVideo) {

        Log.v(TAG, "onAdapterHide");
        invokeHide();
    }

    @Override
    public void onAdapterVideoStart(PubnativeNetworkFeedVideoAdapter feedVideo) {
        Log.v(TAG, "onAdapterVideoStart");
        invokeVideoStart();
    }

    @Override
    public void onAdapterVideoFinish(PubnativeNetworkFeedVideoAdapter feedVideo) {
        Log.v(TAG, "onAdapterVideoFinish");
        invokeVideoFinish();
    }
}
