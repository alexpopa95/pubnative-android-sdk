package net.pubnative.sdk.layouts.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.core.insights.model.PNInsightModel;
import net.pubnative.sdk.core.request.PNAdTargetingModel;

import java.util.Map;

public abstract class PNLayoutAdapter {

    private static final   String TAG           = PNLayoutAdapter.class.getSimpleName();
    protected static final String DEFAULT_ERROR = "unknown";

    protected Context                         mContext;
    protected PubnativeNetworkAdapterRunnable mTimeoutRunnable;
    protected LoadListener                    mLoadListener;
    protected TrackListener                   mTrackListener;
    protected Map<String, String>             mNetworkData;
    protected Handler                         mHandler;
    protected PNInsightModel                  mInsight;
    protected boolean                         mIsCPICacheEnabled;
    protected long                            mRequestStartTimeStamp;

    //==============================================================================================
    // Adapter Runnable
    //==============================================================================================

    protected class PubnativeNetworkAdapterRunnable implements Runnable {

        private final String TAG = getClass().getSimpleName();

        @Override
        public void run() {
            onTimeout();
        }
    }

    //==============================================================================================
    // LISTENER
    //==============================================================================================
    public interface LoadListener {
        void onAdapterLoadFinished(PNLayoutAdapter adapter);

        void onAdapterLoadFail(PNLayoutAdapter adapter, Exception exception);
    }

    public interface TrackListener {
        void onAdapterTrackImpression();

        void onAdapterTrackClick();
    }

    /**
     * Sets callback listener for network load process
     *
     * @param listener valid load listener
     */
    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    /**
     * Sets callback listener for view interactions
     *
     * @param listener valid view listener
     */
    public void setTrackListener(TrackListener listener) {
        mTrackListener = listener;
    }

    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * sets insight to track request when time out occur for this request.
     *
     * @param insight valid insight for track this request.
     */
    public void setInsight(PNInsightModel insight) {
        mInsight = insight;
    }

    /**
     * Sets network data to configure the request and internal tracking data
     *
     * @param data valid map with data containing the network configuration
     */
    public void setNetworkData(Map<String, String> data) {
        mNetworkData = data;
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
     * this function returns time taken to complete this request.
     *
     * @return valid time in milliseconds.
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - mRequestStartTimeStamp;
    }

    //==============================================================================================
    // HELPERS
    //==============================================================================================

    protected void invokeLoadSuccess() {
        cancelTimeout();
        LoadListener listener = mLoadListener;
        mLoadListener = null;
        if (listener != null) {
            listener.onAdapterLoadFinished(this);
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

    protected void invokeImpression() {
        if (mTrackListener != null) {
            mTrackListener.onAdapterTrackImpression();
        }
    }

    protected void invokeClick() {
        if (mTrackListener != null) {
            mTrackListener.onAdapterTrackClick();
        }
    }

    //==============================================================================================
    // STANDARD METHODS
    //==============================================================================================

    public void execute(Context context, int timeoutInMillis) {
        mContext = context;
        mRequestStartTimeStamp = System.currentTimeMillis();
        startTimeout(timeoutInMillis);
        request(context, mNetworkData);
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
    // ABSTRACT METHODS
    //==============================================================================================

    protected abstract void request(Context context, Map<String, String> networkData);
}
