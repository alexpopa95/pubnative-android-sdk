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

package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.mediation.config.model.PubnativeConfigAPIResponseModel;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeConfigRequestModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.task.PubnativeHttpTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PubnativeConfigManager {

    private static         String                            TAG                       = PubnativeConfigManager.class.getSimpleName();
    protected static final String                            SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String                            CONFIG_STRING_KEY         = "config";
    protected static final String                            APP_TOKEN_STRING_KEY      = "appToken";
    protected static final String                            TIMESTAMP_LONG_KEY        = "config.timestamp";
    protected static final String                            REFRESH_LONG_KEY          = "refresh";
    protected static final String                            CONFIG_DOWNLOAD_BASE_URL  = "http://ml.pubnative.net/ml/v1/config";
    protected static final String                            APP_TOKEN_KEY             = "?app_token=";
    protected static       List<PubnativeConfigRequestModel> sQueue                    = null;
    protected static       boolean                           sIdle                     = true;
    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface for callbacks when the requested config gets downloaded
     */
    public interface Listener {

        /**
         * Invoked when config manager returns a config.
         *
         * @param configModel PubnativeConfigModel object when cached/download config is available, else null.
         */
        void onConfigLoaded(PubnativeConfigModel configModel);
    }
    //==============================================================================================
    // PubnativeConfigManager
    //==============================================================================================

    // Singleton
    //----------------------------------------------------------------------------------------------
    private PubnativeConfigManager() {
        // do some initialization here may be.
    }
    // Public
    //----------------------------------------------------------------------------------------------

    /**
     * Gets a config asynchronously with listener callback, downloading a new one when outdated
     *
     * @param context  valid context object
     * @param appToken unique identification key provided by Pubnative for mediation sdk
     * @param listener listener to be used for tracking the config loaded callback
     */
    public synchronized static void getConfig(Context context, String appToken, Map<String, String> extras, PubnativeConfigManager.Listener listener) {

        Log.v(TAG, "getConfig: " + appToken);
        if (context != null && !TextUtils.isEmpty(appToken)) {
            if (listener != null) {
                PubnativeConfigRequestModel item = new PubnativeConfigRequestModel();
                item.context = context;
                item.listener = listener;
                item.parameters = extras;
                item.setAppToken(appToken);
                enqueueRequest(item);
                doNextConfigRequest();
            }
        } else {
            // ensuring null config is returned
            invokeLoaded(listener, null);
        }
    }

    /**
     * Completely resets all stored config data
     *
     * @param context valid context object
     */
    public static void clean(Context context) {

        Log.v(TAG, "clean");
        setStoredAppToken(context, null);
        setStoredTimestamp(context, null);
        setStoredRefresh(context, null);
        setStoredConfig(context, null);
    }

    // Private
    //----------------------------------------------------------------------------------------------
    protected static void doNextConfigRequest() {

        Log.v(TAG, "doNextConfigRequest");
        if (sIdle) {
            PubnativeConfigRequestModel item = dequeueRequest();
            if (item != null) {
                sIdle = false;
                getNextConfig(item);
            }
        }
    }

    private static void getNextConfig(PubnativeConfigRequestModel model) {

        Log.v(TAG, "getNextConfig: " + model.getAppToken());
        if (model.context != null && !TextUtils.isEmpty(model.getAppToken())) {
            // Downloads if needed
            if (configNeedsUpdate(model.context, model.getAppToken())) {
                downloadConfig(model);
            } else {
                serveStoredConfig(model.context, model.listener);
            }
        } else {
            // ensuring null config is returned
            invokeLoaded(model.listener, null);
        }
    }

    protected static void serveStoredConfig(Context context, PubnativeConfigManager.Listener listener) {

        Log.v(TAG, "serveStoredConfig");
        PubnativeConfigModel configModel = getStoredConfig(context);
        invokeLoaded(listener, configModel);
    }

    public static PubnativeConfigModel getStoredConfig(Context context) {

        Log.v(TAG, "getStoredConfig");
        PubnativeConfigModel currentConfig = null;
        String configString = getStoredConfigString(context);
        if (!TextUtils.isEmpty(configString)) {
            try {
                currentConfig = new Gson().fromJson(configString, PubnativeConfigModel.class);
            } catch (Exception e) {
                Log.e(TAG, "getStoredConfig - Error: " + e);
            }
        }
        // Ensure not returning an invalid getConfig
        if (currentConfig != null && currentConfig.isNullOrEmpty()) {
            currentConfig = null;
        }
        return currentConfig;
    }

    protected static void invokeLoaded(PubnativeConfigManager.Listener listener, PubnativeConfigModel configModel) {

        Log.v(TAG, "invokeLoaded");
        if (listener != null) {
            listener.onConfigLoaded(configModel);
        }
        sIdle = true;
        doNextConfigRequest();
    }

    protected static void updateConfig(Context context, String appToken, PubnativeConfigModel configModel) {

        Log.v(TAG, "updateConfig");
        if (context != null && !TextUtils.isEmpty(appToken)) {
            if (configModel != null && !configModel.isNullOrEmpty()) {
                setStoredConfig(context, configModel);
                setStoredAppToken(context, appToken);
                setStoredTimestamp(context, System.currentTimeMillis());
                if (configModel.globals.containsKey(PubnativeConfigModel.ConfigContract.REFRESH)) {
                    Double refresh = (Double) configModel.globals.get(PubnativeConfigModel.ConfigContract.REFRESH);
                    setStoredRefresh(context, refresh.longValue());
                }
            } else {
                clean(context);
            }
        } else {
            clean(context);
        }
    }

    protected synchronized static void downloadConfig(final PubnativeConfigRequestModel model) {

        Log.v(TAG, "downloadConfig");
        if (model.context != null && !TextUtils.isEmpty(model.getAppToken())) {

            Uri.Builder builder = Uri.parse(getConfigDownloadBaseUrl(model.context)).buildUpon();
            for (String key : model.parameters.keySet()) {
                String value = model.parameters.get(key);
                builder.appendQueryParameter(key, value);
            }
            String downloadURLString = builder.build().toString();
            PubnativeHttpTask http = new PubnativeHttpTask(model.context);
            http.setListener(new PubnativeHttpTask.Listener() {

                @Override
                public void onHttpTaskSuccess(PubnativeHttpTask task, String result) {

                    Log.v(TAG, "onHttpTaskSuccess");
                    processConfigDownloadResponse(model.context, model.getAppToken(), result);
                    serveStoredConfig(model.context, model.listener);
                }

                @Override
                public void onHttpTaskFailed(PubnativeHttpTask task, String errorMessage) {

                    Log.v(TAG, "onHttpTaskFailed: " + errorMessage);
                    serveStoredConfig(model.context, model.listener);
                }
            });
            http.execute(downloadURLString);
        } else {
            serveStoredConfig(model.context, model.listener);
        }
    }

    protected static void processConfigDownloadResponse(Context context, String appToken, String result) {

        Log.v(TAG, "processConfigDownloadResponse");
        if (!TextUtils.isEmpty(result)) {
            try {
                PubnativeConfigAPIResponseModel response = new Gson().fromJson(result, PubnativeConfigAPIResponseModel.class);
                if (PubnativeInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                    if (response.config != null && !response.config.isNullOrEmpty()) {
                        // Update delivery manager's tracking data
                        updateDeliveryManagerCache(context, response.config);
                        // Saving config string
                        updateConfig(context, appToken, response.config);
                    }
                } else {
                    Log.e(TAG, "downloadConfig - Error: " + response.error_message);
                }
            } catch (Exception e) {
                Log.e(TAG, "downloadConfig - Error: " + e);
            }
        }
    }

    protected static boolean configNeedsUpdate(Context context, String appToken) {

        Log.v(TAG, "configNeedsUpdate");
        boolean result = false;
        if (context != null) {
            String storedConfigString = getStoredConfigString(context);
            if (TextUtils.isEmpty(storedConfigString)) {
                // No stored config found - need update
                result = true;
            } else {
                String storedAppToken = getStoredAppToken(context);
                if (TextUtils.isEmpty(storedAppToken) || TextUtils.isEmpty(appToken) || !storedAppToken.equals(appToken)) {
                    // different (stored token != new token) OR invalid (stored/new token is empty/null) app_tokens found - Need update
                    result = true;
                } else {
                    Long refresh = getStoredRefresh(context);
                    Long storedTimestamp = getStoredTimestamp(context);
                    if (refresh == null || storedTimestamp == null) {
                        // Invalid/unset refresh or timestamp value - Need update
                        result = true;
                    } else {
                        Long currentTimestamp = System.currentTimeMillis();
                        Long elapsed = TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - storedTimestamp);
                        if (elapsed >= refresh) {
                            // Elapsed refresh time - Need update
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static void updateDeliveryManagerCache(Context context, PubnativeConfigModel downloadedConfig) {

        Log.v(TAG, "updateDeliveryManagerCache");
        if (downloadedConfig != null) {
            PubnativeConfigModel storedConfig = getStoredConfig(context);
            if (storedConfig != null) {
                Set<String> storePlacementIds = storedConfig.placements.keySet();
                for (String placementId : storePlacementIds) {
                    // check if new config contains that placement.
                    PubnativePlacementModel newPlacement = downloadedConfig.placements.get(placementId);
                    if (newPlacement != null) {
                        // compare the delivery rule of the new placement with the stored one.
                        PubnativePlacementModel storedPlacement = storedConfig.placements.get(placementId);
                        if (storedPlacement != null) {
                            if (storedPlacement.delivery_rule != null && newPlacement.delivery_rule != null) {
                                // check if impression cap (hour) changed
                                if (storedPlacement.delivery_rule.imp_cap_hour != newPlacement.delivery_rule.imp_cap_hour) {
                                    PubnativeDeliveryManager.resetHourlyImpressionCount(context, placementId);
                                }
                                // check if impression cap (day) changed
                                if (storedPlacement.delivery_rule.imp_cap_day != newPlacement.delivery_rule.imp_cap_day) {
                                    PubnativeDeliveryManager.resetDailyImpressionCount(context, placementId);
                                }
                                // check if pacing cap changed
                                if (storedPlacement.delivery_rule.pacing_cap_minute != newPlacement.delivery_rule.pacing_cap_minute
                                    || storedPlacement.delivery_rule.pacing_cap_hour != newPlacement.delivery_rule.pacing_cap_hour) {
                                    PubnativeDeliveryManager.resetPacingCalendar(placementId);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================
    protected static void enqueueRequest(PubnativeConfigRequestModel item) {

        Log.v(TAG, "enqueueRequest");
        if (item != null) {
            if (sQueue == null) {
                sQueue = new ArrayList<PubnativeConfigRequestModel>();
            }
            sQueue.add(item);
        }
    }

    protected static PubnativeConfigRequestModel dequeueRequest() {

        Log.v(TAG, "dequeueRequest");
        PubnativeConfigRequestModel result = null;
        if (sQueue != null && sQueue.size() > 0) {
            result = sQueue.remove(0);
        }
        return result;
    }
    //==============================================================================================
    // SHARED PREFERENCES
    //==============================================================================================

    // CONFIG URL
    //----------------------------------------------------------------------------------------------
    protected static String getConfigDownloadBaseUrl(Context context) {

        Log.v(TAG, "getConfigDownloadBaseUrl");
        String configDownloadBaseUrl = CONFIG_DOWNLOAD_BASE_URL;
        PubnativeConfigModel storedConfig = getStoredConfig(context);
        if (storedConfig != null && !storedConfig.isNullOrEmpty()) {
            String configUrl = (String) storedConfig.globals.get(PubnativeConfigModel.ConfigContract.CONFIG_URL);
            if (!TextUtils.isEmpty(configUrl)) {
                configDownloadBaseUrl = configUrl;
            }
        }
        return configDownloadBaseUrl;
    }

    // CONFIG
    //----------------------------------------------------------------------------------------------
    protected synchronized static String getStoredConfigString(Context context) {

        Log.v(TAG, "getStoredConfigString");
        return getStringSharedPreference(context, CONFIG_STRING_KEY);
    }

    protected synchronized static void setStoredConfig(Context context, PubnativeConfigModel config) {

        Log.v(TAG, "setStoredConfig");
        // ensuring the string "null" is not getting saved.
        String configString = (config != null) ? new Gson().toJson(config) : null;
        setStringSharedPreference(context, CONFIG_STRING_KEY, configString);
    }

    // APP_TOKEN
    //----------------------------------------------------------------------------------------------
    protected static String getStoredAppToken(Context context) {

        Log.v(TAG, "getStoredAppToken");
        return getStringSharedPreference(context, APP_TOKEN_STRING_KEY);
    }

    protected static void setStoredAppToken(Context context, String appToken) {

        Log.v(TAG, "getStoredAppToken");
        setStringSharedPreference(context, APP_TOKEN_STRING_KEY, appToken);
    }

    // TIMESTAMP
    //----------------------------------------------------------------------------------------------
    protected static Long getStoredTimestamp(Context context) {

        Log.v(TAG, "getStoredTimestamp");
        return getLongSharedPreference(context, TIMESTAMP_LONG_KEY);
    }

    protected static void setStoredTimestamp(Context context, Long timestamp) {

        Log.v(TAG, "getStoredTimestamp");
        setLongSharedPreference(context, TIMESTAMP_LONG_KEY, timestamp);
    }

    // REFRESH
    //----------------------------------------------------------------------------------------------
    protected static Long getStoredRefresh(Context context) {

        Log.v(TAG, "getStoredRefresh");
        return getLongSharedPreference(context, REFRESH_LONG_KEY);
    }

    protected static void setStoredRefresh(Context context, Long refresh) {

        Log.v(TAG, "setStoredRefresh");
        setLongSharedPreference(context, REFRESH_LONG_KEY, refresh);
    }

    // String
    //----------------------------------------------------------------------------------------------
    protected static String getStringSharedPreference(Context context, String key) {

        Log.v(TAG, "getStringSharedPreference");
        String result = null;
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences preferences = getSharedPreferences(context);
            if (preferences != null && preferences.contains((key))) {
                result = preferences.getString(key, null);
            }
        }
        return result;
    }

    protected static void setStringSharedPreference(Context context, String key, String value) {

        Log.v(TAG, "setStringSharedPreference");
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            if (editor != null) {
                if (TextUtils.isEmpty(value)) {
                    editor.remove(key);
                } else {
                    editor.putString(key, value);
                }
                editor.apply();
            }
        }
    }

    // Long
    //----------------------------------------------------------------------------------------------
    protected static Long getLongSharedPreference(Context context, String key) {

        Log.v(TAG, "getLongSharedPreference");
        Long result = null;
        SharedPreferences preferences = getSharedPreferences(context);
        if (context != null && preferences.contains(key)) {
            Long value = preferences.getLong(key, 0);
            if (value > 0) {
                result = value;
            }
        }
        return result;
    }

    protected static void setLongSharedPreference(Context context, String key, Long value) {

        Log.v(TAG, "setLongSharedPreference");
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            if (editor != null) {
                if (value == null) {
                    editor.remove(key);
                } else {
                    editor.putLong(key, value);
                }
                editor.apply();
            }
        }
    }

    // BASE SharedPreferences item
    //----------------------------------------------------------------------------------------------
    protected static SharedPreferences getSharedPreferences(Context context) {

        Log.v(TAG, "getSharedPreferences");
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
        }
        return result;
    }
}
