package net.pubnative.mediation.demo.activities;

import android.util.Log;
import android.view.View;

import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.request.PubnativeNetworkVideo;

public class VideoAdActivity extends StandardAdUnitActivity implements PubnativeNetworkVideo.Listener {

    private static final String TAG = VideoAdActivity.class.getSimpleName();

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        PubnativeNetworkVideo video = new PubnativeNetworkVideo();
        video.setListener(this);
        video.load(this, Settings.getAppToken(this), mPlacementSpinner.getSelectedItem().toString());
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkVideo.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkVideoLoadFinish(PubnativeNetworkVideo video) {
        Log.v(TAG, "onPubnativeNetworkVideoLoadFinish");
        mLoaderContainer.setVisibility(View.GONE);
        video.show();
        showToast("video ad loaded");
    }

    @Override
    public void onPubnativeNetworkVideoLoadFail(PubnativeNetworkVideo video, Exception exception) {
        Log.v(TAG, "onPubnativeNetworkVideoLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
        showToast(exception.getMessage());
        showToast("video ad loading failed");
    }

    @Override
    public void onPubnativeNetworkVideoShow(PubnativeNetworkVideo video) {
        Log.v(TAG, "onPubnativeNetworkVideoShow");
        mLoaderContainer.setVisibility(View.GONE);
        showToast("video show");
    }

    @Override
    public void onPubnativeNetworkVideoStart(PubnativeNetworkVideo video) {
        Log.v(TAG, "onPubnativeNetworkVideoStart");
        showToast("video started");
    }

    @Override
    public void onPubnativeNetworkVideoFinish(PubnativeNetworkVideo video) {
        Log.v(TAG, "onPubnativeNetworkVideoFinish");
        showToast("video finished");
    }

    @Override
    public void onPubnativeNetworkVideoClick(PubnativeNetworkVideo video) {
        Log.v(TAG, "onPubnativeNetworkVideoClick");
        showToast("video click");
    }

    @Override
    public void onPubnativeNetworkVideoImpressionConfirmed(PubnativeNetworkVideo video) {
        Log.v(TAG, "onPubnativeNetworkVideoImpressionConfirmed");
        showToast("impression confirmed");
    }

    @Override
    public void onPubnativeNetworkVideoHide(PubnativeNetworkVideo video) {
        Log.v(TAG, "onPubnativeNetworkVideoHide");
        showToast("video hide");
    }
}
