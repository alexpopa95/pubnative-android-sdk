// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
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
//

package net.pubnative.mediation.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class PubnativeDeviceUtils {

    private static String TAG = PubnativeDeviceUtils.class.getSimpleName();

    /**
     * Gets you the PackageInfo object based on the Context object passed in.
     *
     * @param context valid context object.
     *
     * @return PackageInfo object if context is valid, else null
     */
    public static PackageInfo getPackageInfo(Context context) {

        Log.v(TAG, "getPackageInfo");
        PackageInfo result = null;
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            Log.e(TAG, "getPackageInfo - Error:" + e);
        }
        return result;
    }

    /**
     * Checks if the current network is available and connected to internet
     *
     * @param context valid context
     *
     * @return true if it's available and connected
     */
    public static boolean isNetworkAvailable(Context context) {

        Log.v(TAG, "isNetworkAvailable");
        boolean result;
        Context appContext = context.getApplicationContext();
        final ConnectivityManager connectivityManager = ((ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager == null) {
            Log.e(TAG, "ERROR: Couldn't retrieve valid ConnectivityManager, please ensure that you added `ACCESS_NETWORK_STATE` permission to your Manifest file");
            result = false;
        } else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info == null) {
                Log.e(TAG, "ERROR: Couldn't retrieve valid NetworkInfo, please ensure that you added `ACCESS_NETWORK_STATE` permission to your Manifest file");
                result = false;
            } else {
                result = info.isConnectedOrConnecting();
            }
        }
        return result;
    }

    /**
     * Check size of the screen
     * @param context
     * @return true if screen large or extra large
     */
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
}
