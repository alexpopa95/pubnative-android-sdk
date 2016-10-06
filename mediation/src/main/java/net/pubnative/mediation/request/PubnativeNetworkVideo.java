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
import net.pubnative.mediation.adapter.network.PubnativeNetworkVideoAdapter;
import net.pubnative.mediation.adapter.network.PubnativeNetworkVideoAdapter;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeNetworkVideo extends PubnativeNetworkWaterfall
        implements PubnativeNetworkVideoAdapter.LoadListener,
                   PubnativeNetworkVideoAdapter.AdListener {

    private static final String TAG = PubnativeNetworkVideo.class.getSimpleName();
    protected Listener                            mListener;
    protected Handler                             mHandler;
    protected boolean                             mIsLoading;
    protected boolean                             mIsShown;
    protected PubnativeNetworkVideoAdapter        mAdapter;
    protected long                                mStartTimestamp;

    /**
     * Interface for callbacks related to the video view behaviour
     */
    public interface Listener {

        /**
         * Called whenever the video finished loading an ad
         *
         * @param video video that finished the initialize
         */
        void onPubnativeNetworkVideoLoadFinish(PubnativeNetworkVideo video);

        /**
         * Called whenever the video failed loading an ad
         *
         * @param video video that failed the initialize
         * @param exception    exception with the description of the initialize error
         */
        void onPubnativeNetworkVideoLoadFail(PubnativeNetworkVideo video, Exception exception);

        /**
         * Called when the video was just shown on the screen
         *
         * @param video video that was shown in the screen
         */
        void onPubnativeNetworkVideoShow(PubnativeNetworkVideo video);

        /**
         * Called whenever the video finishes
         *
         * @param video video that has been stopped
         */
        void onPubnativeNetworkVideoStart(PubnativeNetworkVideo video);

        /**
         * Called whenever the video finishes
         *
         * @param video video that has been stopped
         */
        void onPubnativeNetworkVideoFinish(PubnativeNetworkVideo video);

        /**
         * Called whenever the video was clicked by the user
         *
         * @param video video that was clicked
         */
        void onPubnativeNetworkVideoClick(PubnativeNetworkVideo video);

        /**
         * Called when the impression is confirmed
         *
         * @param video video that was clicked
         */
        void onPubnativeNetworkVideoImpressionConfirmed(PubnativeNetworkVideo video);


        /**
         * Called whenever the video was removed from the screen
         *
         * @param video video that was hidden
         */
        void onPubnativeNetworkVideoHide(PubnativeNetworkVideo video);
    }

    //==============================================================================================
    // Public methods
    //==============================================================================================
    /**
     * Sets a callback listener for this video object
     *
     * @param listener valid PubnativeNetworkVideo.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Loads the video ads before being shown
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
            invokeLoadFail(PubnativeException.VIDEO_PARAMETERS_INVALID);
        } else if (mIsLoading) {
            invokeLoadFail(PubnativeException.VIDEO_LOADING);
        } else if (mIsShown) {
            invokeLoadFail(PubnativeException.VIDEO_SHOWN);
        } else {
            mHandler = new Handler(Looper.getMainLooper());
            initialize(context, appToken, placement);
        }
    }

    /**
     * Tells if the video is ready to be shown
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
     * This method will show the video if the ad is available
     */
    public synchronized void show() {

        Log.v(TAG, "show");
        if (mIsLoading) {
            Log.v(TAG, "show - the video currently loading, try again later");
        } else if (mIsShown) {
            Log.v(TAG, "show - the video ad is already shown");
        } else if (isReady()) {
            mAdapter.show();
        } else {
            Log.v(TAG, "show - the video is not loaded, please load before");
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

        mAdapter = hub.getVideoAdapter();
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
                    mListener.onPubnativeNetworkVideoLoadFinish(PubnativeNetworkVideo.this);
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
                    mListener.onPubnativeNetworkVideoLoadFail(PubnativeNetworkVideo.this, exception);
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
                    mListener.onPubnativeNetworkVideoShow(PubnativeNetworkVideo.this);
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
                    mListener.onPubnativeNetworkVideoImpressionConfirmed(PubnativeNetworkVideo.this);
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
                    mListener.onPubnativeNetworkVideoClick(PubnativeNetworkVideo.this);
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
                    mListener.onPubnativeNetworkVideoHide(PubnativeNetworkVideo.this);
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
                    mListener.onPubnativeNetworkVideoStart(PubnativeNetworkVideo.this);
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
                    mListener.onPubnativeNetworkVideoFinish(PubnativeNetworkVideo.this);
                }
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkVideoAdapter.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterLoadFinish(PubnativeNetworkVideoAdapter video) {

        Log.v(TAG, "onAdapterLoadFinish");

        video.setAdListener(this);

        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        mInsight.trackSuccededNetwork(mPlacement.currentPriority(), responseTime);
        invokeLoadFinish();
    }

    @Override
    public void onAdapterLoadFail(PubnativeNetworkVideoAdapter video, Exception exception) {

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

    // PubnativeNetworkVideoAdapter.AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterShow(PubnativeNetworkVideoAdapter video) {

        Log.v(TAG, "onAdapterShow");
        invokeShow();
    }

    @Override
    public void onAdapterImpressionConfirmed(PubnativeNetworkVideoAdapter video) {

        Log.v(TAG, "onAdapterImpressionConfirmed");
        invokeImpressionConfirmed();
    }

    @Override
    public void onAdapterClick(PubnativeNetworkVideoAdapter video) {

        Log.v(TAG, "onAdapterClick");
        invokeClick();
    }

    @Override
    public void onAdapterHide(PubnativeNetworkVideoAdapter video) {

        Log.v(TAG, "onAdapterHide");
        invokeHide();
    }

    @Override
    public void onAdapterVideoStart(PubnativeNetworkVideoAdapter interstitial) {

        Log.v(TAG, "onAdapterVideoStart");
        invokeVideoStart();
    }

    @Override
    public void onAdapterVideoFinish(PubnativeNetworkVideoAdapter interstitial) {

        Log.v(TAG, "onAdapterVideoFinish");
        invokeVideoFinish();
    }
}
