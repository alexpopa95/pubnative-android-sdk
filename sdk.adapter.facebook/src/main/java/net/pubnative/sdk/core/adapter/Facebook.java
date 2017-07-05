package net.pubnative.sdk.core.adapter;

import android.content.Context;

import com.facebook.ads.AdSettings;
import com.facebook.ads.FacebookTestModeHelper;

import net.pubnative.api.core.utils.PNAPICrypto;
import net.pubnative.sdk.core.PNSettings;

public class Facebook {

    protected static final String MEDIATION_SERVICE_NAME = "Pubnative ML";
    public static final    String KEY_PLACEMENT_ID       = "placement_id";
    public static final    int    ERROR_NO_FILL_1203     = 1203;

    public static void init(Context context) {
        
        AdSettings.setMediationService(MEDIATION_SERVICE_NAME);
        AdSettings.setIsChildDirected(PNSettings.isCoppaModeEnabled);
        if (PNSettings.isTestModeEnabled) {
            AdSettings.addTestDevice(PNAPICrypto.md5(PNSettings.advertisingId));
            FacebookTestModeHelper.updateHashId(context, PNAPICrypto.md5(PNSettings.advertisingId));
        }
    }
}
