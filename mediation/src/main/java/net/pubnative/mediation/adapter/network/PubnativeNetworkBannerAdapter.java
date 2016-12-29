package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.util.Log;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public abstract class PubnativeNetworkBannerAdapter extends PubnativeNetworkAdapter {

    private   static final String TAG           = PubnativeNetworkBannerAdapter.class.getSimpleName();
    protected static final String KEY_APP_TOKEN = "apptoken";

    protected LoadListener mLoadListener;
    protected AdListener mAdListener;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkBannerAdapter(Map data) {

        super(data);
    }

    public interface LoadListener {

        /**
         * Called whenever the banner finished loading an ad
         *
         * @param banner banner that finished the initialize
         */
        void onAdapterLoadFinish(PubnativeNetworkBannerAdapter banner);

        /**
         * Called whenever the banner failed loading an ad
         *
         * @param banner banner that failed the initialize
         * @param exception    exception with the description of the initialize error
         */
        void onAdapterLoadFail(PubnativeNetworkBannerAdapter banner, Exception exception);

    }

    public interface AdListener {

        /**
         * Called when the banner was just shown on the screen
         *
         * @param banner banner that was shown in the screen
         */
        void onAdapterShow(PubnativeNetworkBannerAdapter banner);

        /**
         * Called when the banner impression was confrimed
         *
         * @param banner banner which impression was confirmed
         */
        void onAdapterImpressionConfirmed(PubnativeNetworkBannerAdapter banner);

        /**
         * Called whenever the banner was clicked by the user
         *
         * @param banner banner that was clicked
         */
        void onAdapterClick(PubnativeNetworkBannerAdapter banner);

        /**
         * Called whenever the banner was removed from the screen
         *
         * @param banner banner that was hidden
         */
        void onAdapterHide(PubnativeNetworkBannerAdapter banner);

    }

    //==============================================================================================
    // Overridable methods
    //==============================================================================================

    public void setLoadListener(LoadListener loadListener) {

        Log.v(TAG, "setLoadListener");
        mLoadListener = loadListener;
    }

    public void setAdListener(AdListener adListener) {

        Log.v(TAG, "setAdListener");
        mAdListener = adListener;
    }

    //==============================================================================================
    // PubnativeNetworkAdapter
    //==============================================================================================

    @Override
    public void execute(Context context, int timeoutInMillis) {
        startTimeout(timeoutInMillis);
        load(context);
    }

    @Override
    protected void onTimeout() {
        invokeLoadFail(PubnativeException.ADAPTER_TIMEOUT);
    }

    //==============================================================================================
    // Abstract
    //==============================================================================================

    /**
     * Starts loading the interstitial ad
     *
     * @param context valid Context
     */
    public abstract void load(Context context);

    /**
     * Starts showing the interstitial for the adapted network
     */
    public abstract void show();

    /**
     * Destroys the current interstitial for the adapted network
     */
    public abstract void destroy();

    /**
     * Tells if the interstitial is ready to be shown in the screen
     *
     * @return true if it's ready, false if it's not
     */
    public abstract boolean isReady();

    public abstract void hide();

    //==============================================================================================
    // Callback helpers
    //==============================================================================================

    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        if (mLoadListener != null) {
            mLoadListener.onAdapterLoadFinish(this);
        }
        mLoadListener = null;
    }

    protected void invokeLoadFail(Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        if (mLoadListener != null) {
            mLoadListener.onAdapterLoadFail(this, exception);
        }
        mLoadListener = null;
    }

    protected void invokeShow() {

        Log.v(TAG, "invokeShow");
        if (mAdListener != null) {
            mAdListener.onAdapterShow(this);
        }
    }

    protected void invokeImpressionConfirmed() {

        Log.v(TAG, "invokeImpressionConfirmed");
        if (mAdListener != null) {
            mAdListener.onAdapterImpressionConfirmed(this);
        }
    }

    protected void invokeClick() {

        Log.v(TAG, "invokeClick");
        if (mAdListener != null) {
            mAdListener.onAdapterClick(this);
        }
    }

    protected void invokeHide() {

        Log.v(TAG, "invokeHide");
        if (mAdListener != null) {
            mAdListener.onAdapterHide(this);
        }
    }
}
