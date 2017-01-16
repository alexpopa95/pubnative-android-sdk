package net.pubnative.mediation.demo.activities;

import android.util.Log;
import android.view.View;

import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.request.PubnativeNetworkInterstitial;

public class InterstitialAdActivity extends StandardAdUnitActivity implements PubnativeNetworkInterstitial.Listener {

    private static final String TAG = InterstitialAdActivity.class.getSimpleName();

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        PubnativeNetworkInterstitial interstitial = new PubnativeNetworkInterstitial();
        interstitial.setListener(this);
        interstitial.load(this, Settings.getAppToken(this), mPlacementSpinner.getSelectedItem().toString());
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkInterstitial.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkInterstitialLoadFinish(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialLoadFinish");
        mLoaderContainer.setVisibility(View.GONE);
        interstitial.show();
        showToast("Interstitial ad loaded");
    }

    @Override
    public void onPubnativeNetworkInterstitialLoadFail(PubnativeNetworkInterstitial interstitial, Exception exception) {
        Log.v(TAG, "onPubnativeNetworkInterstitialLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
        showToast(exception.getMessage());
        showToast("Interstitial ad loading failed");
    }

    @Override
    public void onPubnativeNetworkInterstitialShow(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialShow");
        mLoaderContainer.setVisibility(View.GONE);
        showToast("Interstitial show");
    }

    @Override
    public void onPubnativeNetworkInterstitialImpressionConfirmed(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialImpressionConfirmed");
        showToast("Interstitial impression confirmed");
    }

    @Override
    public void onPubnativeNetworkInterstitialClick(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialClick");
        showToast("Interstitial click");
    }

    @Override
    public void onPubnativeNetworkInterstitialHide(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialHide");
        showToast("Interstitial hide");
    }
}
