package net.pubnative.sdk.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import net.pubnative.api.core.utils.PNAPISystemUtils;
import net.pubnative.sdk.core.request.PNAdTargetingModel;
import net.pubnative.sdk.core.utils.PNAdvertisingId;
import net.pubnative.sdk.core.utils.PNDeviceUtils;

import java.util.Locale;

public class PNSettings {

    public static boolean            isTestModeEnabled;
    public static boolean            isCoppaModeEnabled;
    public static PNAdTargetingModel targeting;
    public static String             advertisingId;
    public static String             os;
    public static String             osVersion;
    public static String             deviceName;
    public static String             locale;
    public static String             sdkVersion;
    public static String             appBundleID;
    public static String             appVersion;
    public static Location           location;

    public interface Listener {
        void onInitFinish();
    }

    public static void init(Context context, final Listener listener) {

        os = "android";
        deviceName = Build.MODEL;
        osVersion = Build.VERSION.RELEASE;
        locale = Locale.getDefault().getLanguage();
        sdkVersion = net.pubnative.sdk.BuildConfig.VERSION_NAME + " (" + net.pubnative.sdk.BuildConfig.VERSION_CODE + ")";
        PackageInfo info = PNDeviceUtils.getPackageInfo(context);
        if (info != null) {
            appBundleID = info.packageName;
            appVersion = info.versionName;
        }

        if (isCoppaModeEnabled) {
            listener.onInitFinish();
        } else {

            // Here we put all the related fields that we cannot retrieve on coppa mode
            location = PNAPISystemUtils.getLocation(context);

            PNAdvertisingId advertisingId = new PNAdvertisingId();
            advertisingId.request(context, new PNAdvertisingId.Listener() {
                @Override
                public void onPubnativeAdvertisingIdFinish(String advertisingId) {
                    invokeFinish(advertisingId, listener);
                }
            });
        }
    }

    protected static void invokeFinish(String advertId, Listener listener) {
        advertisingId = advertId;
        if (listener != null) {
            listener.onInitFinish();
        }
    }
}
