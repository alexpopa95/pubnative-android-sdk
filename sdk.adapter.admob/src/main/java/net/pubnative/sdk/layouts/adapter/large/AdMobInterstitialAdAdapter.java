package net.pubnative.sdk.layouts.adapter.large;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import net.pubnative.sdk.core.adapter.AdMob;
import net.pubnative.sdk.layouts.adapter.PNLayoutFullscreenAdapter;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class AdMobInterstitialAdAdapter extends PNLayoutFullscreenAdapter {

    protected InterstitialAd mInterstitialAd;

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String unitId = networkData.get(AdMob.KEY_UNIT_ID);
            if (TextUtils.isEmpty(unitId)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
            } else {
                mInterstitialAd = new InterstitialAd(mContext);
                mInterstitialAd.setAdUnitId(unitId);
                mInterstitialAd.setAdListener(mAdListener);
                mInterstitialAd.loadAd(AdMob.getAdRequest(context));
            }
        }
    }

    @Override
    public void show() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void hide() {
        // Do nothing
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    protected AdListener mAdListener = new AdListener() {
        @Override
        public void onAdFailedToLoad(int errorCode) {
            invokeLoadFail(new Exception("AdMob error: " + errorCode));
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            invokeLoadSuccess();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            invokeShow();
            invokeImpression();
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            invokeHide();
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            invokeClick();
        }
    };
}
