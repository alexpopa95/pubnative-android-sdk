package net.pubnative.sdk.layouts;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.sdk.core.exceptions.PNException;

public abstract class PNLayout extends PNLayoutWaterfall {

    private static final String TAG = PNLayout.class.getSimpleName();

    //==============================================================================================
    // Listener
    //==============================================================================================
    public interface LoadListener {

        void onPNLayoutLoadFinish(PNLayout layout);

        void onPNLayoutLoadFail(PNLayout layout, Exception exception);

    }

    public interface TrackListener {

        void onPNLayoutTrackImpression(PNLayout layout);

        void onPNLayoutTrackClick(PNLayout layout);
    }

    protected LoadListener  mLoadListener;
    protected TrackListener mTrackListener;

    //==============================================================================================
    // Public methods
    //==============================================================================================
    public void load(Context context, String appToken, String placement) {
        if (mLoadListener == null) {
            Log.w(TAG, "load - Error: listener was not set, have you configured one using setLoadListener()?");
        }
        if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placement)) {
            Log.w(TAG, "Error: placement was null or emtpy and required");
            invokeFail(PNException.REQUEST_PARAMETERS_INVALID);
        } else {
            initialize(context, appToken, placement);
        }
    }

    //==============================================================================================
    // HELPERS
    //==============================================================================================
    protected void invokeFinish() {
        if (mLoadListener != null) {
            mLoadListener.onPNLayoutLoadFinish(this);
        }
    }

    protected void invokeFail(Exception exception) {
        if (mLoadListener != null) {
            mLoadListener.onPNLayoutLoadFail(this, exception);
        }
    }

    protected void invokeImpression() {
        if (mInsight != null) {
            mInsight.sendImpressionInsight();
        }
        if (mTrackListener != null) {
            mTrackListener.onPNLayoutTrackImpression(this);
        }
    }

    protected void invokeClick() {
        if (mInsight != null) {
            mInsight.sendClickInsight();
        }
        if (mTrackListener != null) {
            mTrackListener.onPNLayoutTrackClick(this);
        }
    }

    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    public void setTrackListener(TrackListener listener) {
        mTrackListener = listener;
    }
}
