package net.pubnative.sdk.core.adapter.request;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;

import net.pubnative.sdk.core.adapter.AdMob;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class AdMobNetworkAdapter extends PNAdapter implements AdMobNativeAppInstallAdModel.LoadListener {

    private static final   String TAG         = AdMobNetworkAdapter.class.getSimpleName();

    protected AdMobNativeAppInstallAdModel mWrapper;

    /**
     * Creates a new instance of AdMobNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public AdMobNetworkAdapter(Map data) {

        super(data);
    }
    //==============================================================================================
    // PubnativeLibraryNetworkAdapter methods
    //==============================================================================================

    @Override
    protected void request(Context context) {
        if (context == null || mData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String unitId = (String) mData.get(AdMob.KEY_UNIT_ID);
            if (TextUtils.isEmpty(unitId)) {
                invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
            } else {
                createRequest(context, unitId);
            }
        }
    }
    //==============================================================================================
    // AdMobNetworkAdapterNetwork methods
    //==============================================================================================

    protected void createRequest(Context context, String unitId) {
        mWrapper = new AdMobNativeAppInstallAdModel(context, this);
        AdLoader adLoader = new AdLoader.Builder(context, unitId).forAppInstallAd(mWrapper)
                                                                 .withAdListener(mWrapper.getAdListener())
                                                                 .withNativeAdOptions(AdMob.getNativeAdOptions())
                                                                 .build();
        adLoader.loadAd(AdMob.getAdRequest(context));
    }

    @Override
    public void onLoadFinish() {
        mWrapper.setInsightModel(mInsight);
        invokeLoadFinish(mWrapper);
    }

    @Override
    public void onLoadFail(Exception exception) {
        invokeLoadFail(exception);
    }
}
