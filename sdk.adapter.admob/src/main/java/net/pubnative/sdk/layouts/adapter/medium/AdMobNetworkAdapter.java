package net.pubnative.sdk.layouts.adapter.medium;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;

import net.pubnative.sdk.core.adapter.AdMob;
import net.pubnative.sdk.layouts.adapter.PNLayoutFeedAdapter;
import net.pubnative.sdk.core.adapter.request.AdMobNativeAppInstallAdModel;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.layouts.PNMediumLayoutView;
import net.pubnative.sdk.core.request.PNAdModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class AdMobNetworkAdapter extends PNLayoutFeedAdapter implements PNAdModel.Listener,
                                                                        AdMobNativeAppInstallAdModel.LoadListener {

    protected AdMobNativeAppInstallAdModel mWrapper;
    protected PNMediumLayoutRequestView    mAdView;

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
    public PNMediumLayoutView getView(Context context) {
        if (mWrapper != null && mAdView == null) {
            mAdView = new PNMediumLayoutRequestView(context);
            mAdView.loadWithAd(context, mWrapper);
        }
        return mAdView;
    }

    @Override
    public void startTracking() {
        if (mWrapper != null) {
            mWrapper.setListener(this);
            mWrapper.startTracking(this.getView(mContext));
        }
    }

    @Override
    public void stopTracking() {
        if (mWrapper != null) {
            mWrapper.stopTracking();
            mWrapper.setListener(null);
        }
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
        invokeLoadSuccess();
    }

    @Override
    public void onLoadFail(Exception exception) {
        invokeLoadFail(exception);
    }
}
