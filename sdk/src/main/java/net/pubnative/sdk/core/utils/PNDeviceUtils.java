// The MIT License (MIT)
//
// Copyright (c) 2017 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package net.pubnative.sdk.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;

import net.pubnative.sdk.core.PNSettings;

public class PNDeviceUtils {

    private static String TAG = PNDeviceUtils.class.getSimpleName();

    public enum ConnectionType {
        UNKNOWN,
        CELLULAR,
        WIFI
    }

    /**
     * Gets you the PackageInfo object based on the Context object passed in.
     *
     * @param context valid context object.
     * @return PackageInfo object if context is valid, else null
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo result = null;
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            Log.e(TAG, "getPackageInfo - Error:" + e);
        }
        return result;
    }

    protected static NetworkInfo getActiveNetworkInfo(Context context) {

        NetworkInfo result = null;
        Context appContext = context.getApplicationContext();
        ConnectivityManager manager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            Log.w(TAG, "ERROR: Couldn't retrieve valid ConnectivityManager, please ensure that you added `ACCESS_NETWORK_STATE` permission to your Manifest file");
        } else {
            result = manager.getActiveNetworkInfo();
        }
        return result;
    }

    /**
     * Checks if the current network is available and connected to internet
     *
     * @param context valid context
     * @return true if it's available and connected
     */
    public static boolean isNetworkAvailable(Context context) {

        boolean result = false;
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info == null) {
            Log.w(TAG, "ERROR: Couldn't retrieve valid NetworkInfo, please ensure that you added `ACCESS_NETWORK_STATE` permission to your Manifest file");
        } else {
            result = info.isConnectedOrConnecting();
        }
        return result;
    }

    /**
     * Extracts the specific connection type to which this device is connected from WIFI or CELLULLAR
     *
     * @param context valid context
     * @return connection type
     */
    public static ConnectionType getConnectionType(Context context) {

        ConnectionType result = ConnectionType.UNKNOWN;
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info != null && info.isConnected()) {
            result = ConnectionType.CELLULAR;
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                result = ConnectionType.WIFI;
            }
        }
        return result;
    }

    /**
     * Check size of the screen
     *
     * @param context valid context
     * @return true if screen large or extra large
     */
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(float dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param px      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertPxToDp(float px, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
    }
}
