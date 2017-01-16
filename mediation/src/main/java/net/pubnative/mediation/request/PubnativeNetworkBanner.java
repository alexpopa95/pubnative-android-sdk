package net.pubnative.mediation.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;
import net.pubnative.mediation.adapter.network.PubnativeNetworkBannerAdapter;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeNetworkBanner extends PubnativeNetworkWaterfall
        implements PubnativeNetworkBannerAdapter.AdListener,
                   PubnativeNetworkBannerAdapter.LoadListener {

    public static final String TAG = PubnativeNetworkBanner.class.getSimpleName();
    protected Listener                      mListener;
    protected Handler                       mHandler;
    protected boolean                       mIsShown;
    protected boolean                       mIsLoading;
    protected PubnativeNetworkBannerAdapter mAdapter;
    protected long                          mStartTimestamp;

    public interface Listener {

        /**
         * Called whenever the banner finished loading an ad
         * w
         *
         * @param banner banner that finished the initialize
         */
        void onPubnativeNetworkBannerLoadFinish(PubnativeNetworkBanner banner);

        /**
         * Called whenever the banner failed loading an ad
         *
         * @param banner    banner that failed the initialize
         * @param exception exception with the description of the initialize error
         */
        void onPubnativeNetworkBannerLoadFail(PubnativeNetworkBanner banner, Exception exception);

        /**
         * Called when the banner was just shown on the screen
         *
         * @param banner banner that was shown in the screen
         */
        void onPubnativeNetworkBannerShow(PubnativeNetworkBanner banner);

        /**
         * Called when the banner impression was confrimed
         *
         * @param banner banner which impression was confirmed
         */
        void onPubnativeNetworkBannerImpressionConfirmed(PubnativeNetworkBanner banner);

        /**
         * Called whenever the banner was clicked by the user
         *
         * @param banner banner that was clicked
         */
        void onPubnativeNetworkBannerClick(PubnativeNetworkBanner banner);

        /**
         * Called whenever the banner was removed from the screen
         *
         * @param banner banner that was hidden
         */
        void onPubnativeNetworkBannerHide(PubnativeNetworkBanner banner);
    }

    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Loads the interstitial ads before being shown
     */
    public synchronized void load(Context context, String appToken, String placement) {

        Log.v(TAG, "initialize");
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (mListener == null) {
            Log.w(TAG, "initialize - Warning: listener was not set, have you configured one using setListener()?");
        }
        if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placement)) {
            invokeLoadFail(PubnativeException.BANNER_PARAMETERS_INVALID);
        } else if (mIsLoading) {
            invokeLoadFail(PubnativeException.BANNER_LOADING);
        } else if (mIsShown) {
            invokeLoadFail(PubnativeException.BANNER_SHOWN);
        } else {
            initialize(context, appToken, placement);
        }
    }

    /**
     * This method will show the banner if the ad is available
     */
    public synchronized void show() {

        Log.v(TAG, "show");
        if (mIsLoading) {
            Log.w(TAG, "The ad is still loading, shown cannot be completed, dropping call");
        } else if (mIsShown) {
            Log.w(TAG, "The ad is already shown, dropping call");
        } else if (isReady()) {
            mAdapter.show();
        } else {
            Log.w(TAG, "The ad is still not loaded");
        }
    }

    /**
     * Tells if the banner is ready to be shown
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
     * Destroy the current banner
     */
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mAdapter != null) {
            mAdapter.destroy();
        }
    }

    /**
     * Hides the current banner
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

        mAdapter = hub.getBannerAdapter();
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
                    mListener.onPubnativeNetworkBannerLoadFinish(PubnativeNetworkBanner.this);
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
                    mListener.onPubnativeNetworkBannerLoadFail(PubnativeNetworkBanner.this, exception);
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
                    mListener.onPubnativeNetworkBannerShow(PubnativeNetworkBanner.this);
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
                    mListener.onPubnativeNetworkBannerImpressionConfirmed(PubnativeNetworkBanner.this);
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
                    mListener.onPubnativeNetworkBannerClick(PubnativeNetworkBanner.this);
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
                    mListener.onPubnativeNetworkBannerHide(PubnativeNetworkBanner.this);
                }
            }
        });
    }
    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkBannerAdapter.AdListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onAdapterShow(PubnativeNetworkBannerAdapter banner) {

        Log.v(TAG, "onPubnativeBannerShowLoadFail");
        invokeShow();
    }

    @Override
    public void onAdapterImpressionConfirmed(PubnativeNetworkBannerAdapter banner) {

        Log.v(TAG, "onPubnativeBannerImpressionConfirmed");
        invokeImpressionConfirmed();
    }

    @Override
    public void onAdapterClick(PubnativeNetworkBannerAdapter banner) {

        Log.v(TAG, "onPubnativeBannerClick");
        invokeClick();
    }

    @Override
    public void onAdapterHide(PubnativeNetworkBannerAdapter banner) {

        Log.v(TAG, "onPubnativeBannerHide");
        invokeHide();
    }
    // PubnativeNetworkBannerAdapter.LoadListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onAdapterLoadFinish(PubnativeNetworkBannerAdapter banner) {

        Log.v(TAG, "onPubnativeBannerLoadFinish");

        mIsLoading = false;

        banner.setAdListener(this);

        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        mInsight.trackSuccededNetwork(mPlacement.currentPriority(), responseTime);
        invokeLoadFinish();
    }

    @Override
    public void onAdapterLoadFail(PubnativeNetworkBannerAdapter banner, Exception exception) {

        Log.v(TAG, "onPubnativeBannerLoadFail");
        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        if(!exception.getClass().isAssignableFrom(PubnativeException.class)
           || exception.equals(PubnativeException.ADAPTER_UNKNOWN_ERROR)) {
            mInsight.trackAttemptedNetwork(mPlacement.currentPriority(), responseTime, exception);
        } else {
            mInsight.trackUnreachableNetwork(mPlacement.currentPriority(), responseTime, exception);
        }
        getNextNetwork();
    }
}
