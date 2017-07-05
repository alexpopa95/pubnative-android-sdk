package net.pubnative.sdk.layouts.adapter.large;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;

import net.pubnative.sdk.core.adapter.AdMob;
import net.pubnative.sdk.core.adapter.request.AdMobNativeAppInstallAdModel;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.layouts.adapter.PNLayoutFullscreenAdapter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class AdMobNetworkAdapter extends PNLayoutFullscreenAdapter implements PNAdModel.Listener,
                                                                              AdMobNativeAppInstallAdModel.LoadListener {

    protected AdMobNativeAppInstallAdModel mWrapper;
    protected PNLargeLayoutRequestView     mAdView;
    protected WindowManager                mWindowManager;
    protected RelativeLayout               mFullScreenView;

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String unitId = networkData.get(AdMob.KEY_UNIT_ID);
            if (TextUtils.isEmpty(unitId)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
            } else {

                mAdView = null;
                mWrapper = new AdMobNativeAppInstallAdModel(context, this);
                AdLoader adLoader = new AdLoader.Builder(context, unitId).forAppInstallAd(mWrapper)
                                                                         .withAdListener(mWrapper.getAdListener())
                                                                         .withNativeAdOptions(AdMob.getNativeAdOptions())
                                                                         .build();
                adLoader.loadAd(AdMob.getAdRequest(context));
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
        mWrapper.startTracking(mAdView);
        invokeShow();

    }

    @Override
    public void hide() {
        mWrapper.stopTracking();
        if (mFullScreenView != null) {
            mFullScreenView.removeAllViews();
        }
        if (mWindowManager != null) {
            mWindowManager.removeView(mFullScreenView);
            mWindowManager = null;
        }
        invokeHide();
    }

    @Override
    public void onPNAdImpression(PNAdModel model) {
        invokeImpression();
    }

    @Override
    public void onPNAdClick(PNAdModel model) {
        invokeClick();
    }

    @Override
    public void onLoadFinish() {

        if (mWrapper != null && mAdView == null) {
            mAdView = new PNLargeLayoutRequestView(mContext);
            mAdView.loadWithAd(mContext, mWrapper);
        }
        invokeLoadSuccess();
    }

    @Override
    public void onLoadFail(Exception exception) {
        invokeLoadFail(exception);
    }
}
