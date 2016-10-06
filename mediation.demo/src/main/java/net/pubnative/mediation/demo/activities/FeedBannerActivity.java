package net.pubnative.mediation.demo.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.request.PubnativeNetworkFeedBanner;

public class FeedBannerActivity extends StandardAdUnitActivity implements PubnativeNetworkFeedBanner.Listener {

    private static final String TAG = FeedBannerActivity.class.getSimpleName();
    private RelativeLayout mFeedBannerView;
    private PubnativeNetworkFeedBanner mFeedBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.layout_infeed_banner, mBlankContainer);
        mFeedBannerView = (RelativeLayout) findViewById(R.id.infeed_banner_container);
    }

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        mFeedBanner = new PubnativeNetworkFeedBanner();
        mFeedBanner.setListener(this);
        mFeedBanner.load(this, Settings.getAppToken(this), mPlacementSpinner.getSelectedItem().toString());
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkFeedBanner.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkFeedBannerLoadFinish(PubnativeNetworkFeedBanner feedBanner) {
        Log.v(TAG, "onPubnativeNetworkFeedBannerLoadFinish");
        mLoaderContainer.setVisibility(View.GONE);
        feedBanner.show(mFeedBannerView);
        showToast("Feed Banner loaded");
    }

    @Override
    public void onPubnativeNetworkFeedBannerLoadFail(PubnativeNetworkFeedBanner feedBanner, Exception exception) {
        Log.v(TAG, "onPubnativeNetworkFeedBannerLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
        showToast(exception.getMessage());
        showToast("Feed Banner loading failed");
    }

    @Override
    public void onPubnativeNetworkFeedBannerShow(PubnativeNetworkFeedBanner feedBanner) {
        Log.v(TAG, "onPubnativeNetworkFeedBannerShow");
        mLoaderContainer.setVisibility(View.GONE);
        showToast("Feed Banner show");
    }

    @Override
    public void onPubnativeNetworkFeedBannerImpressionConfirmed(PubnativeNetworkFeedBanner feedBanner) {
        Log.v(TAG, "onPubnativeNetworkFeedBannerImpressionConfirmed");
        showToast("Feed Banner impression confirmed");
    }

    @Override
    public void onPubnativeNetworkFeedBannerClick(PubnativeNetworkFeedBanner feedBanner) {
        Log.v(TAG, "onPubnativeNetworkFeedBannerClick");
        showToast("Feed Banner clicked");
        mFeedBanner.hide();
    }

    @Override
    public void onPubnativeNetworkFeedBannerHide(PubnativeNetworkFeedBanner feedBanner) {
        Log.v(TAG, "onPubnativeNetworkFeedBannerHide");
        showToast("Feed Banner hide");
    }
}
