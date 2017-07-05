package net.pubnative.sdk.layouts.adapter.large;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.sdk.core.adapter.Flurry;
import net.pubnative.sdk.layouts.adapter.PNLayoutFullscreenAdapter;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.layouts.adapter.YahooLayoutAdModel;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkAdapter extends PNLayoutFullscreenAdapter implements FlurryAdNativeListener, PNAdModel.Listener {

    protected static final String KEY_AD_SPACE_NAME         = "ad_space_name";
    protected static final String KEY_FLURRY_API_KEY        = "api_key";
    protected static final String TARGETING_FLURRY_INTEREST = "interest";
    protected static final String TARGETING_GENDER_MALE     = "male";
    protected static final String TARGETING_GENDER_FEMALE   = "female";
    protected static final String TARGETING_SEPARATOR       = ",";

    protected FlurryAdNative           mNativeAd;
    protected YahooLayoutAdModel       mWrapper;
    protected PNLargeLayoutRequestView mAdView;
    protected WindowManager            mWindowManager;
    protected RelativeLayout           mFullScreenView;

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

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String adSpaceName = networkData.get(KEY_AD_SPACE_NAME);
            String apiKey = networkData.get(KEY_FLURRY_API_KEY);
            if (TextUtils.isEmpty(adSpaceName) || TextUtils.isEmpty(apiKey)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
            } else {
                mWrapper = null;
                mAdView = null;

                Flurry.init(context, apiKey);

                // Make request
                mNativeAd = new FlurryAdNative(context, adSpaceName);
                mNativeAd.setTargeting(Flurry.getTargeting());
                mNativeAd.setListener(this);
                mNativeAd.fetchAd();
            }
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // FlurryAdNativeListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onFetched(FlurryAdNative flurryAdNative) {
        FlurryAgent.onEndSession(mContext);
        mWrapper = new YahooLayoutAdModel(mContext, flurryAdNative);
        mWrapper.fetchAssets(new YahooLayoutAdModel.LayoutListener() {
            @Override
            public void onFetchFinish(PNAdModel model) {
                mWrapper.setListener(YahooNetworkAdapter.this);
                mAdView = new PNLargeLayoutRequestView(mContext);
                mAdView.loadWithAd(mContext, mWrapper);
                invokeLoadSuccess();
            }

            @Override
            public void onFetchFail(PNAdModel model, Exception exception) {
                invokeLoadFail(exception);
            }
        });


    }

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int errCode) {
        FlurryAgent.onEndSession(mContext);
        invokeLoadFail(new Exception("Flurry error: " + flurryAdErrorType.name() + " - " + errCode));
    }

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onClicked(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onExpanded(FlurryAdNative flurryAdNative) {
    }

    @Override
    public void onCollapsed(FlurryAdNative flurryAdNative) {
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
