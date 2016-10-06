package net.pubnative.mediation.demo.activities;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.request.PubnativeNetworkBanner;

public class BannerAdActivity extends StandardAdUnitActivity implements PubnativeNetworkBanner.Listener {

    private static final String TAG = "BannerAdActivity";

    private PubnativeNetworkBanner mBanner;

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        if(mBanner != null) {
            mBanner.destroy();
        }
        mBanner = new PubnativeNetworkBanner();
        mBanner.setListener(this);
        mBanner.load(this, Settings.getAppToken(this), mPlacementSpinner.getSelectedItem().toString());
    }

    @Override
    public void onPubnativeNetworkBannerLoadFinish(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkBannerLoadFinish");
        if(mBanner == banner) {
            mLoaderContainer.setVisibility(View.GONE);
            banner.show();
            Toast.makeText(this, "Banner ad loaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPubnativeNetworkBannerLoadFail(PubnativeNetworkBanner banner, Exception exception) {
        Log.v(TAG, "onPubnativeNetworkBannerLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
        Toast.makeText(this, "Banner ad loading failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPubnativeNetworkBannerShow(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkBannerShow");
        mLoaderContainer.setVisibility(View.GONE);
        Toast.makeText(this, "Banner show", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPubnativeNetworkBannerImpressionConfirmed(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkBannerImpressionConfirmed");
        Toast.makeText(this, "Banner impression confirmed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPubnativeNetworkBannerClick(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkBannerClick");
        Toast.makeText(this, "Banner click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPubnativeNetworkBannerHide(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkBannerHide");
        Toast.makeText(this, "Banner hide", Toast.LENGTH_SHORT).show();
    }
}
