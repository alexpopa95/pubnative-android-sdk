package net.pubnative.mediation.adapter.network;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class AdMobNetworkBannerAdapter extends PubnativeNetworkBannerAdapter {

    private static final String TAG = AdMobNetworkBannerAdapter.class.getSimpleName();
    protected AdView  mAdView;
    protected Context mContext;
    protected boolean mIsLoaded = false;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public AdMobNetworkBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            mIsLoaded = false;
            createRequest(context);
        }
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mAdView != null) {
            ViewGroup rootView = (ViewGroup) ((Activity) mContext).findViewById(android.R.id.content);
            RelativeLayout container = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.setLayoutParams(params);
            container.setGravity(Gravity.BOTTOM);
            rootView.addView(container);
            container.addView(mAdView);
            invokeShow();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mAdView != null) {
            result = mIsLoaded;
        }
        return result;
    }

    @Override
    public void hide() {

        Log.v(TAG, "hide");
        if (mAdView.getParent() != null) {
            ((ViewGroup) mAdView.getParent()).removeAllViews();
        }
    }
    //==============================================================================================
    // Private
    //==============================================================================================

    protected void createRequest(Context context) {

        Log.v(TAG, "createRequest");
        String unitId = (String) mData.get(AdMobNetworkRequestAdapter.ADMOB_UNIT_ID);
        if (TextUtils.isEmpty(unitId)) {
            invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
        } else {
            mContext = context;
            mAdView = new AdView(mContext);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            mAdView.setAdUnitId(unitId);
            mAdView.setAdListener(listener);
            mAdView.loadAd(getAdRequest());
        }
    }

    protected AdRequest getAdRequest() {

        AdRequest.Builder builder = new AdRequest.Builder();
        if (mTargeting != null) {
            if (mTargeting.age != null && mTargeting.age > 0) {
                int year = Calendar.getInstance().get(Calendar.YEAR) - mTargeting.age;
                builder.setBirthday(new GregorianCalendar(year, 1, 1).getTime());
            }
            if (TextUtils.isEmpty(mTargeting.gender)) {
                builder.setGender(AdRequest.GENDER_UNKNOWN);
            } else if ("male".equals(mTargeting.gender)) {
                builder.setGender(AdRequest.GENDER_MALE);
            } else if ("female".equals(mTargeting.gender)) {
                builder.setGender(AdRequest.GENDER_FEMALE);
            } else {
                builder.setGender(AdRequest.GENDER_UNKNOWN);
            }
        }
        return builder.build();
    }
    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    private com.google.android.gms.ads.AdListener listener = new com.google.android.gms.ads.AdListener() {

        @Override
        public void onAdClosed() {

            Log.v(TAG, "onAdClosed");
            destroy();
            super.onAdClosed();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {

            Map extra = new HashMap();
            extra.put("code", errorCode);
            invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extra));
        }

        @Override
        public void onAdLoaded() {

            Log.v(TAG, "onAdLoaded");
            super.onAdLoaded();
            mIsLoaded = true;
            invokeLoadFinish();
        }

        @Override
        public void onAdOpened() {

            Log.v(TAG, "onAdOpened");
            super.onAdOpened();
            invokeClick();
        }
    };
}
