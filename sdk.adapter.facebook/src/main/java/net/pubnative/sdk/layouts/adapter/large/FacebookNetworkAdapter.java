package net.pubnative.sdk.layouts.adapter.large;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import net.pubnative.sdk.core.adapter.Facebook;
import net.pubnative.sdk.layouts.adapter.PNLayoutFullscreenAdapter;
import net.pubnative.sdk.core.adapter.request.FacebookNativeAdModel;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.core.request.PNAdModel;

import java.util.Map;

public class FacebookNetworkAdapter extends PNLayoutFullscreenAdapter implements AdListener, PNAdModel.Listener {

    protected NativeAd                 mNativeAd;
    protected PNAdModel                mWrapper;
    protected PNLargeLayoutRequestView mAdView;
    protected WindowManager            mWindowManager;
    protected RelativeLayout           mFullScreenView;

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
                mAdView = null;
                mWrapper = null;
                mNativeAd = new NativeAd(context, placementId);
                mNativeAd.setAdListener(this);
                mNativeAd.loadAd();
            }
        }
    }

    @Override
    public void show() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        mFullScreenView = new RelativeLayout(mContext) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    hide();
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }
        };

        mFullScreenView.addView(mAdView);
        mWindowManager.addView(mFullScreenView, params);
        mWrapper.setListener(this);
        mWrapper.startTracking(mFullScreenView);
        invokeShow();
    }

    @Override
    public void hide() {
        mWrapper.stopTracking();
        mWrapper.setListener(null);
        mFullScreenView.removeAllViews();
        mWindowManager.removeView(mFullScreenView);
        mWindowManager = null;
        invokeHide();
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

        mWrapper = new FacebookNativeAdModel(mContext, mNativeAd);
        mAdView = new PNLargeLayoutRequestView(mContext);
        mAdView.loadWithAd(mContext, mWrapper);
        invokeLoadSuccess();
    }

    @Override
    public void onAdClicked(Ad ad) {
        // This is overwritten by FacebookNativeAdModel that sets itself as listener
        // so all the callbacks are coming through PNAdModel tracking listener callbacks
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        // This is overwritten by FacebookNativeAdModel that sets itself as listener
        // so all the callbacks are coming through PNAdModel tracking listener callbacks
    }

    @Override
    public void onPNAdImpression(PNAdModel model) {
        invokeImpression();
    }

    @Override
    public void onPNAdClick(PNAdModel model) {
        invokeClick();
    }
}
