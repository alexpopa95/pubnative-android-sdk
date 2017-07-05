package net.pubnative.sdk.core.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.sdk.core.PNSettings;

import java.util.HashMap;
import java.util.Map;

public class Flurry {

    public static final    String KEY_AD_SPACE_NAME         = "ad_space_name";
    public static final    String KEY_FLURRY_API_KEY        = "api_key";
    protected static final String TARGETING_FLURRY_INTEREST = "interest";
    protected static final String TARGETING_GENDER_MALE     = "male";
    protected static final String TARGETING_GENDER_FEMALE   = "female";
    protected static final String TARGETING_SEPARATOR       = ",";

    public static void init(Context context, String key) {

        new FlurryAgent.Builder().withLogEnabled(true)
                                 .build(context, key);
        // execute/resume session
        if (!FlurryAgent.isSessionActive()) {
            FlurryAgent.onStartSession(context);
        }
    }

    public static FlurryAdTargeting getTargeting() {
        FlurryAdTargeting result = new FlurryAdTargeting();
        if (PNSettings.targeting != null) {

            if (PNSettings.targeting.age != null) {
                result.setAge(PNSettings.targeting.age);
            }

            if (PNSettings.targeting.gender == null) {
                result.setGender(FlurryGender.UNKNOWN);
            } else if (PNSettings.targeting.gender.equals(TARGETING_GENDER_FEMALE)) {
                result.setGender(FlurryGender.FEMALE);
            } else if (PNSettings.targeting.gender.equals(TARGETING_GENDER_MALE)) {
                result.setGender(FlurryGender.MALE);
            } else {
                result.setGender(FlurryGender.UNKNOWN);
            }

            if (PNSettings.targeting.interests != null) {
                Map interests = new HashMap();
                interests.put(TARGETING_FLURRY_INTEREST, TextUtils.join(TARGETING_SEPARATOR, PNSettings.targeting.interests));
                result.setKeywords(interests);
            }
        }
        result.setEnableTestAds(PNSettings.isTestModeEnabled);
        return result;
    }
}
