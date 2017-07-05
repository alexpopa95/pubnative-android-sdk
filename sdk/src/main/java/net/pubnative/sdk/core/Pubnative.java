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

package net.pubnative.sdk.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.sdk.core.config.PNConfigManager;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.core.request.PNAdTargetingModel;
import net.pubnative.sdk.core.request.PNCacheManager;

import java.util.HashMap;
import java.util.Map;

public class Pubnative {

    private static final String TAG = Pubnative.class.getSimpleName();

    //==============================================================================================
    // Tracking data
    //==============================================================================================

    /**
     * Sets COPPA mode enabled
     *
     * @param enabled true enables coppa mode, false disables it
     */
    public static void setCoppaMode(boolean enabled) {
        PNSettings.isCoppaModeEnabled = enabled;
    }


    /**
     * Sets the targeting model for the request
     *
     * @param targeting targeting model with extended targeting config
     */
    public static void setTargeting(PNAdTargetingModel targeting) {
        PNSettings.targeting = targeting;
    }

    /**
     * Sets test mode enabled
     *
     * @param enabled true enables test mode, false disables it
     */
    public static void setTestMode(boolean enabled) {
        PNSettings.isTestModeEnabled = enabled;
    }

    //==============================================================================================
    // Tracking data
    //==============================================================================================

    /**
     * This method is used to initialise the mediation,
     * internally it caches items for improving request performance
     *
     * @param context  valid context.
     * @param appToken valid apptoken.
     */
    public static void init(Context context, String appToken) {

        if (context == null) {
            Log.w(TAG, "init - warning: invalid context");
        } else if (TextUtils.isEmpty(appToken)) {
            Log.w(TAG, "init - warning: invalid apptoken");
        } else {
            // fetch config
            PNConfigManager.getConfig(context, appToken, new PNConfigManager.Listener() {
                @Override
                public void onConfigLoaded(PNConfigModel configModel) {
                    // Do nothing
                }
            });
        }
    }
}