package net.pubnative.sdk.core.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;

import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.request.PNAdTargetingModel;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AdMob {

    public static final String KEY_UNIT_ID = "unit_id";
    public static final String MALE        = "male";
    public static final String FEMALE      = "female";

    public static AdRequest getAdRequest(Context context) {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (PNSettings.targeting!= null) {
            if (PNSettings.targeting.age != null && PNSettings.targeting.age > 0) {
                int year = Calendar.getInstance().get(Calendar.YEAR) - PNSettings.targeting.age;
                builder.setBirthday(new GregorianCalendar(year, 1, 1).getTime());
            }
            if (TextUtils.isEmpty(PNSettings.targeting.gender)) {
                builder.setGender(AdRequest.GENDER_UNKNOWN);
            } else if (MALE.equals(PNSettings.targeting.gender)) {
                builder.setGender(AdRequest.GENDER_MALE);
            } else if (FEMALE.equals(PNSettings.targeting.gender)) {
                builder.setGender(AdRequest.GENDER_FEMALE);
            } else {
                builder.setGender(AdRequest.GENDER_UNKNOWN);
            }
        }
        builder.tagForChildDirectedTreatment(PNSettings.isCoppaModeEnabled);

        if (PNSettings.isTestModeEnabled) {
            String androidId = android.provider.Settings.Secure.getString(context.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            try {
                MessageDigest md5Hash = MessageDigest.getInstance("MD5");
                md5Hash.update(androidId.getBytes());
                builder.addTestDevice(String.format(Locale.US, "%032X", new Object[]{new BigInteger(1, md5Hash.digest())}));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return builder.build();
    }

    public static NativeAdOptions getNativeAdOptions() {

        return new NativeAdOptions.Builder().setReturnUrlsForImageAssets(true)
                                            .build();
    }
}
