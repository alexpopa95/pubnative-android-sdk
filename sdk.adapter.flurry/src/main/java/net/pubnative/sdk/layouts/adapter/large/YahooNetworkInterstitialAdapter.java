package net.pubnative.sdk.layouts.adapter.large;

import android.content.Context;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.sdk.core.adapter.Flurry;
import net.pubnative.sdk.layouts.adapter.PNLayoutFullscreenAdapter;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkInterstitialAdapter extends PNLayoutFullscreenAdapter implements FlurryAdInterstitialListener {

    protected FlurryAdInterstitial mInterstitialAd;

    @Override
    protected void request(Context context, Map<String, String> networkData) {

        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String adSpaceName = networkData.get(Flurry.KEY_AD_SPACE_NAME);
            String apiKey = networkData.get(Flurry.KEY_FLURRY_API_KEY);
            if (TextUtils.isEmpty(adSpaceName) || TextUtils.isEmpty(apiKey)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
            } else {

                new FlurryAgent.Builder().withLogEnabled(true).build(context, apiKey);
                // execute/resume session
                if (!FlurryAgent.isSessionActive()) {
                    FlurryAgent.onStartSession(context);
                }

                mInterstitialAd = new FlurryAdInterstitial(context, adSpaceName);
                mInterstitialAd.setTargeting(Flurry.getTargeting());
                mInterstitialAd.setListener(this);
                mInterstitialAd.fetchAd();
            }
        }
    }

    @Override
    public void show() {
        mInterstitialAd.displayAd();
    }

    @Override
    public void hide() {
        // Do nothing
    }

    //==============================================================================================
    // FlurryAdInterstitialListener
    //==============================================================================================
    @Override
    public void onFetched(FlurryAdInterstitial flurryAdInterstitial) {
        FlurryAgent.onEndSession(mContext);
        invokeLoadSuccess();
    }

    @Override
    public void onRendered(FlurryAdInterstitial flurryAdInterstitial) {
        invokeShow();
    }

    @Override
    public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {
        invokeImpression();
    }

    @Override
    public void onClose(FlurryAdInterstitial flurryAdInterstitial) {
        invokeHide();
    }

    @Override
    public void onAppExit(FlurryAdInterstitial flurryAdInterstitial) {

    }

    @Override
    public void onClicked(FlurryAdInterstitial flurryAdInterstitial) {
        invokeClick();
    }

    @Override
    public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial) {

    }

    @Override
    public void onError(FlurryAdInterstitial flurryAdInterstitial, FlurryAdErrorType flurryAdErrorType, int errCode) {
        FlurryAgent.onEndSession(mContext);
        invokeLoadFail(new Exception("Flurry error: " + flurryAdErrorType.name() + " - " + errCode));
    }
}
