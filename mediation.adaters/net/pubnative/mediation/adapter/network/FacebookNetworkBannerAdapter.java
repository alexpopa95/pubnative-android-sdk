package net.pubnative.mediation.adapter.network;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.ImpressionListener;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.HashMap;
import java.util.Map;

public class FacebookNetworkBannerAdapter extends PubnativeNetworkBannerAdapter
        implements AdListener,
                   ImpressionListener {

    public static final String TAG = FacebookNetworkBannerAdapter.class.getSimpleName();
    protected AdView  mBannerView;
    protected Context mContext;
    protected boolean mIsLoaded = false;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public FacebookNetworkBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    @Override
    public void load(Context context) {

        Log.d(TAG, "load");
        if (context == null && mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            createRequest(context);
        }
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mBannerView != null) {
            ViewGroup rootView = (ViewGroup) ((Activity) mContext).findViewById(android.R.id.content);
            RelativeLayout container = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.setLayoutParams(params);
            container.setGravity(Gravity.BOTTOM);
            rootView.addView(container);
            container.addView(mBannerView);
            invokeShow();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mBannerView != null) {
            mBannerView.destroy();
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mBannerView != null) {
            result = mIsLoaded;
        }
        return result;
    }

    @Override
    public void hide() {

        Log.v(TAG, "hide");
        if (mBannerView.getParent() != null) {
            ((ViewGroup) mBannerView.getParent()).removeAllViews();
        }
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    protected void createRequest(Context context) {

        Log.v(TAG, "createRequest");
        mContext = context;
        String placementId = (String) mData.get(FacebookNetworkRequestAdapter.KEY_PLACEMENT_ID);
        AdSize bannerSize = PubnativeDeviceUtils.isTablet(context) ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50;
        if (TextUtils.isEmpty(placementId)) {
            invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
        } else {
            mBannerView = new AdView(context, placementId, bannerSize);
            mBannerView.setAdListener(this);
            mBannerView.loadAd();
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onError(Ad ad, AdError adError) {

        Log.v(TAG, "onError: " + (adError != null ? (adError.getErrorCode() + " - " + adError.getErrorMessage()) : ""));
        if (adError == null) {
            invokeLoadFail(PubnativeException.ADAPTER_UNKNOWN_ERROR);
        } else {
            Map extras = new HashMap();
            extras.put("code", adError.getErrorCode());
            extras.put("message", adError.getErrorMessage());
            invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extras));
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {

        Log.v(TAG, "onAdLoaded");
        mIsLoaded = true;
        mBannerView.setImpressionListener(this);
        invokeLoadFinish();
    }

    @Override
    public void onAdClicked(Ad ad) {

        Log.v(TAG, "onAdClicked");
        invokeClick();
        destroy();
    }

    @Override
    public void onLoggingImpression(Ad ad) {

        Log.v(TAG, "onLoggingImpression");
        invokeImpressionConfirmed();
    }
}
