package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class AdMobNetworkInterstitialAdapter extends PubnativeNetworkInterstitialAdapter {

    private static final   String TAG           = AdMobNetworkInterstitialAdapter.class.getSimpleName();
    protected static final String ADMOB_UNIT_ID = "unit_id";
    protected InterstitialAd mInterstitial;

    /**
     * Creates a new instance of AdMobNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public AdMobNetworkInterstitialAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            createRequest(context);
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mInterstitial != null) {
            result = mInterstitial.isLoaded();
        }
        return result;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        mInterstitial.show();
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        mInterstitial = null;
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    protected void createRequest(Context context) {

        Log.v(TAG, "createRequest");
        String unitID = (String) mData.get(ADMOB_UNIT_ID);
        if (TextUtils.isEmpty(unitID)) {
            invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
        } else {
            mInterstitial = new InterstitialAd(context);
            mInterstitial.setAdUnitId(unitID);
            mInterstitial.setAdListener(new InterstitialAdListener());
            mInterstitial.loadAd(getAdRequest());
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
    protected class InterstitialAdListener extends com.google.android.gms.ads.AdListener {

        @Override
        public void onAdClosed() {

            Log.v(TAG, "onAdClosed");
            invokeHide();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {

            Log.v(TAG, "onAdFailedToLoad");
            Map extra = new HashMap();
            extra.put("code", errorCode);
            invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extra));
        }

        @Override
        public void onAdLeftApplication() {

            Log.v(TAG, "onAdLeftApplication");
            invokeClick();
        }

        @Override
        public void onAdOpened() {

            Log.v(TAG, "onAdOpened");
            invokeShow();
        }

        @Override
        public void onAdLoaded() {

            Log.v(TAG, "onAdLoaded");
            invokeLoadFinish();
        }
    }
}
