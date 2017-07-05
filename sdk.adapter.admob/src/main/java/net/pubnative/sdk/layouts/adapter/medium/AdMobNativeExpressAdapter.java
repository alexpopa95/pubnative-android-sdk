package net.pubnative.sdk.layouts.adapter.medium;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import net.pubnative.sdk.core.adapter.AdMob;
import net.pubnative.sdk.layouts.adapter.PNLayoutFeedAdapter;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.layouts.PNMediumLayoutView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class AdMobNativeExpressAdapter extends PNLayoutFeedAdapter {

    protected NativeExpressAdView           mNativeAd;
    protected AdMobNativeExpressAdapterView mAdView;
    protected boolean                       mIsReady;
    protected boolean                       mIsImpressionConfirmed;

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String unitId = networkData.get(AdMob.KEY_UNIT_ID);
            if (TextUtils.isEmpty(unitId)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
            } else {

                mIsReady = false;
                mAdView = null;
                mIsImpressionConfirmed = false;
                mNativeAd = new NativeExpressAdView(mContext);
                mNativeAd.setAdSize(AdSize.MEDIUM_RECTANGLE);
                mNativeAd.setAdUnitId(unitId);
                mNativeAd.setAdListener(mAdListener);
                mNativeAd.loadAd(AdMob.getAdRequest(context));
            }
        }
    }

    @Override
    public PNMediumLayoutView getView(Context context) {

        if (mIsReady && mAdView == null) {
            mAdView = new AdMobNativeExpressAdapterView(mContext);
            mAdView.loadWithView(mNativeAd);
        }
        return mAdView;
    }

    @Override
    public void startTracking() {
        if(!mIsImpressionConfirmed) {
            invokeImpression();
        }
        mIsImpressionConfirmed = true;
    }

    @Override
    public void stopTracking() {
        // Do nothing, this ad self tracks itself
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
            mIsReady = true;
            invokeLoadSuccess();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            invokeClick();
        }
    };

    public class AdMobNativeExpressAdapterView extends PNMediumLayoutContainerView {

        public AdMobNativeExpressAdapterView(Context context) {
            super(context);
        }
    }
}
