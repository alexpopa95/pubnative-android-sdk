package net.pubnative.sdk.layouts.adapter.large;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import net.pubnative.sdk.core.adapter.AdMob;
import net.pubnative.sdk.core.utils.PNDeviceUtils;
import net.pubnative.sdk.layouts.adapter.PNLayoutFullscreenAdapter;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class AdMobNativeExpressAdapter extends PNLayoutFullscreenAdapter {

    protected NativeExpressAdView           mNativeAd;
    protected AdMobNativeExpressAdapterView mAdView;
    protected WindowManager                 mWindowManager;
    protected RelativeLayout                mFullScreenView;
    protected boolean                       mIsImpressionConfirmed;

    protected final int MAX_HEIGHT = 480;
    protected final int MAX_WIDTH = 320;
    protected final int MIN_HEIGHT = 480;
    protected final int MIN_WIDTH = 320;

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String unitId = networkData.get(AdMob.KEY_UNIT_ID);
            if (TextUtils.isEmpty(unitId)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
            } else {
                mIsImpressionConfirmed = false;
                mAdView = null;
                mNativeAd = new NativeExpressAdView(mContext);

                DisplayMetrics displayMetrics = new DisplayMetrics();

                if(context instanceof Activity) {
                    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int heightDP = PNDeviceUtils.convertPxToDp(displayMetrics.heightPixels, context);
                    heightDP = Math.min(Math.max(heightDP, MIN_HEIGHT), MAX_HEIGHT);
                    int widthDP = PNDeviceUtils.convertPxToDp(displayMetrics.widthPixels, context);
                    widthDP = Math.min(Math.max(widthDP, MIN_WIDTH), MAX_WIDTH);
                    mNativeAd.setAdSize(new AdSize(widthDP, heightDP));
                    mNativeAd.setAdUnitId(unitId);
                    mNativeAd.setAdListener(mAdListener);
                    mNativeAd.loadAd(AdMob.getAdRequest(context));
                } else {
                    invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
                }
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

    }

    @Override
    public void hide() {
        if (mFullScreenView != null) {
            mFullScreenView.removeAllViews();
        }
        if (mWindowManager != null) {
            mWindowManager.removeView(mFullScreenView);
            mWindowManager = null;
        }
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
            if (mNativeAd != null && mAdView == null) {
                mAdView = new AdMobNativeExpressAdapterView(mContext);
                mAdView.loadWithView(mNativeAd);
            }
            invokeLoadSuccess();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            invokeShow();
            if (!mIsImpressionConfirmed) {
                invokeImpression();
            }
            mIsImpressionConfirmed = true;
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

    public class AdMobNativeExpressAdapterView extends PNLargeLayoutContainerView {

        public AdMobNativeExpressAdapterView(Context context) {
            super(context);
        }
    }
}
