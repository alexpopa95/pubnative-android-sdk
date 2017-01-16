package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;

import net.pubnative.mediation.adapter.model.AdMobNativeAppInstallAdModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class AdMobNetworkRequestAdapter extends PubnativeNetworkRequestAdapter implements NativeAppInstallAd.OnAppInstallAdLoadedListener {

    public static final    String TAG           = AdMobNetworkRequestAdapter.class.getSimpleName();
    protected static final String ADMOB_UNIT_ID = "unit_id";

    /**
     * Creates a new instance of AdMobNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public AdMobNetworkRequestAdapter(Map data) {

        super(data);
    }
    //==============================================================================================
    // PubnativeNetworkAdapter methods
    //==============================================================================================

    @Override
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context == null || mData == null) {
            invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String unitId = (String) mData.get(ADMOB_UNIT_ID);
            if (TextUtils.isEmpty(unitId)) {
                invokeFailed(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                createRequest(context, unitId);
            }
        }
    }
    //==============================================================================================
    // AdMobNetworkAdapter methods
    //==============================================================================================

    protected void createRequest(Context context, String unitId) {

        Log.v(TAG, "createRequest");
        AdLoader adLoader = new AdLoader.Builder(context, unitId)
                .forAppInstallAd(this)
                .withAdListener(new NativeAdListener())
                .build();
        adLoader.loadAd(getAdRequest());
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
    protected class NativeAdListener extends com.google.android.gms.ads.AdListener {

        @Override
        public void onAdFailedToLoad(int errorCode) {

            Log.v(TAG, "onAdFailedToLoad");
            Map extra = new HashMap();
            extra.put("code", errorCode);
            invokeFailed(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extra));
        }
    }

    // NativeAppInstallAd.OnAppInstallAdLoadedListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {

        Log.v(TAG, "onAppInstallAdLoaded");
        AdMobNativeAppInstallAdModel wrapper = new AdMobNativeAppInstallAdModel(nativeAppInstallAd);
        invokeLoaded(wrapper);
    }
}
