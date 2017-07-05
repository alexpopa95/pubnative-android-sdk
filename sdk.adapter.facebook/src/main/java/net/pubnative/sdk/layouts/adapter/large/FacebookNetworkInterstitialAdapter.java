package net.pubnative.sdk.layouts.adapter.large;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import net.pubnative.sdk.core.adapter.Facebook;
import net.pubnative.sdk.layouts.adapter.PNLayoutFullscreenAdapter;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Map;

public class FacebookNetworkInterstitialAdapter extends PNLayoutFullscreenAdapter implements InterstitialAdListener {

    protected InterstitialAd mInterstitialAd;

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
        } else {
            mContext = context;
            String placementId = networkData.get(Facebook.KEY_PLACEMENT_ID);
            if (TextUtils.isEmpty(placementId)) {
                invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
            } else {
                Facebook.init(context);
                mInterstitialAd = new InterstitialAd(mContext, placementId);
                mInterstitialAd.setAdListener(this);
                mInterstitialAd.loadAd();
            }
        }
    }

    @Override
    public void show() {
        mInterstitialAd.show();
    }

    @Override
    public void hide() {
        // Do nothing
    }

    //==============================================================================================
    // Callback
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onError(Ad ad, AdError adError) {
        String errorString = DEFAULT_ERROR;
        if (adError != null) {
            errorString = adError.getErrorCode() + " - " + adError.getErrorMessage();
        }
        invokeLoadFail(new Exception(errorString));
    }

    @Override
    public void onAdLoaded(Ad ad) {
        invokeLoadSuccess();
    }

    @Override
    public void onAdClicked(Ad ad) {
        invokeClick();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        invokeImpression();
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        invokeShow();
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        invokeHide();
    }
}
