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

package net.pubnative.sdk.core.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.adapter.request.PubnativeLibraryCPICache;
import net.pubnative.sdk.core.config.model.PNConfigAPIResponseModel;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.core.config.model.PNConfigRequestModel;
import net.pubnative.sdk.core.config.model.PNPlacementModel;
import net.pubnative.sdk.core.insights.model.PNInsightsAPIResponseModel;
import net.pubnative.sdk.core.network.PNHttpRequest;
import net.pubnative.sdk.core.utils.PNAdvertisingId;
import net.pubnative.sdk.core.utils.PNDeviceUtils;
import net.pubnative.sdk.core.utils.PNStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PNConfigManager {

    private static String TAG = PNConfigManager.class.getSimpleName();

    protected static final String SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String CONFIG_STRING_KEY         = "config";
    protected static final String APP_TOKEN_STRING_KEY      = "appToken";
    protected static final String TIMESTAMP_LONG_KEY        = "config.timestamp";
    protected static final String REFRESH_LONG_KEY          = "refresh";

    protected static List<PNConfigRequestModel> sQueue       = null;
    protected static boolean                    sIdle        = true;

    //==============================================================================================
    // LoadListener
    //==============================================================================================

    /**
     * Interface for callbacks when the requested config gets downloaded
     */
    public interface Listener {

        /**
         * Invoked when config manager returns a config.
         *
         * @param configModel PNConfigModel object when cached/download config is available, else null.
         */
        void onConfigLoaded(PNConfigModel configModel);
    }

    //==============================================================================================
    // PNConfigManager
    //==============================================================================================
    // Singleton
    //----------------------------------------------------------------------------------------------

    private PNConfigManager() {
        // do some initialization here may be.
    }

    //----------------------------------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------------------------------

    /**
     * Gets a config asynchronously with listener callback, downloading a new one when outdated
     *
     * @param context  valid context object
     * @param appToken unique identification key provided by Pubnative for mediation sdk
     * @param listener listener to be used for tracking the config loaded callback
     */
    public synchronized static void getConfig(Context context, String appToken, PNConfigManager.Listener listener) {
        if (listener == null) {
            Log.e(TAG, "getConfig - Error: listener is null, dropping this call");
        } else if (context == null) {
            // ensuring null config is returned
            Log.e(TAG, "getConfig - Error: context is null");
            invokeLoaded(null, listener);
        } else if (TextUtils.isEmpty(appToken)) {
            Log.e(TAG, "getConfig - Error: app token is null");
            invokeLoaded(null, listener);
        } else {
            PNConfigRequestModel item = new PNConfigRequestModel();
            item.context = context;
            item.appToken = appToken;
            item.listener = listener;
            enqueueRequest(item);
            doNextConfigRequest();
        }
    }

    /**
     * Completely resets all stored config data
     *
     * @param context valid context object
     */
    public static void clean(Context context) {
        setStoredAppToken(context, null);
        setStoredTimestamp(context, null);
        setStoredRefresh(context, null);
        setStoredConfig(context, null);
    }

    //----------------------------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------------------------

    protected static void doNextConfigRequest() {
        if (sIdle) {
            final PNConfigRequestModel item = dequeueRequest();
            if (item != null) {
                sIdle = false;

                if(PNSettings.os == null) {
                    PNSettings.init(item.context, new PNSettings.Listener() {
                        @Override
                        public void onInitFinish() {
                            getNextConfig(item);
                        }
                    });
                } else {
                    getNextConfig(item);
                }
            }
        }
    }

    protected static void getNextConfig(final PNConfigRequestModel requestModel) {
        if (configNeedsUpdate(requestModel)) {

            // Reload settings on every new download, to update location
            PNSettings.init(requestModel.context, new PNSettings.Listener() {
                @Override
                public void onInitFinish() {
                    downloadConfig(requestModel);
                }
            });

        } else {
            serveStoredConfig(requestModel);
        }
    }

    protected static void serveStoredConfig(final PNConfigRequestModel request) {
        String storedAppToken = getStoredAppToken(request.context);
        if (storedAppToken != null && storedAppToken.equals(request.appToken)) {
            final PNConfigModel config = getStoredConfig(request.context);

            // Ensure that the CPICache is fill before allowing any request
            PubnativeLibraryCPICache.init(request.context, request.appToken, config,
                                          new PubnativeLibraryCPICache.Listener() {
                                              @Override
                                              public void onPubnativeCpiCacheLoadFinish() {
                                                  invokeLoaded(config, request.listener);
                                              }
                                          });
        } else {
            invokeLoaded(null, request.listener);
        }
    }

    protected static PNConfigModel getStoredConfig(Context context) {
        PNConfigModel currentConfig = null;
        String configString = getStoredConfigString(context);
        if (!TextUtils.isEmpty(configString)) {
            try {
                currentConfig = PNStringUtils.convertStringToObject(configString, PNConfigModel.class); // Config only one
            } catch (Exception e) {
                Log.e(TAG, "getStoredConfig - Error: " + e);
            }
        }
        // Ensure not returning an invalid getConfig
        if (currentConfig == null || currentConfig.isEmpty()) {
            currentConfig = null;
        }
        return currentConfig;
    }

    protected static void updateConfig(Context context, String appToken, PNConfigModel configModel) {
        if (context != null) {
            if (TextUtils.isEmpty(appToken) || configModel == null || configModel.isEmpty()) {
                clean(context);
            } else {
                setStoredConfig(context, configModel);
                setStoredAppToken(context, appToken);
                setStoredTimestamp(context, System.currentTimeMillis());
                if (configModel.globals.containsKey(PNConfigModel.GLOBAL.REFRESH)) {
                    Double refresh = (Double) configModel.globals.get(PNConfigModel.GLOBAL.REFRESH);
                    setStoredRefresh(context, refresh.longValue());
                }
            }
        }
    }

    protected synchronized static void downloadConfig(final PNConfigRequestModel requestModel) {

        PNHttpRequest http = new PNHttpRequest();
        http.start(requestModel.context, getConfigDownloadUrl(requestModel), new PNHttpRequest.Listener() {

            @Override
            public void onPNHttpRequestFinish(PNHttpRequest request, String result) {
                processConfigDownloadResponse(requestModel, result);
                serveStoredConfig(requestModel);
            }

            @Override
            public void onPNHttpRequestFail(PNHttpRequest request, Exception exception) {
                serveStoredConfig(requestModel);
            }
        });
    }

    protected static void processConfigDownloadResponse(PNConfigRequestModel request, String result) {
        if (TextUtils.isEmpty(result)) {
            // In case of server error problem, we serve the stored config
            Log.w(TAG, "downloadConfig - Error, empty response");
            serveStoredConfig(request);
        } else {
            try {
                PNConfigAPIResponseModel response = new Gson().fromJson(result, PNConfigAPIResponseModel.class);
                if (PNInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                    // Update delivery manager's tracking data
                    updateDeliveryManagerCache(request.context, response.config);
                    // Saving config string
                    updateConfig(request.context, request.appToken, response.config);
                } else {
                    Log.w(TAG, "downloadConfig - Error: " + response.error_message);
                    serveStoredConfig(request);
                }
            } catch (Exception e) {
                Log.w(TAG, "downloadConfig - Error: " + e);
                serveStoredConfig(request);
            }
        }
    }

    protected static boolean configNeedsUpdate(PNConfigRequestModel request) {
        boolean result = false;
        String storedConfigString = getStoredConfigString(request.context);
        String storedAppToken = getStoredAppToken(request.context);
        Long refresh = getStoredRefresh(request.context);
        Long storedTimestamp = getStoredTimestamp(request.context);
        Long currentTimestamp = System.currentTimeMillis();

        if (TextUtils.isEmpty(storedConfigString) || TextUtils.isEmpty(storedAppToken)) {
            // There is no stored config
            result = true;
        } else if (storedAppToken == null || !storedAppToken.equals(request.appToken)) {
            // Stored config is different than the requested app token
            result = true;
        } else if (refresh == null || storedTimestamp == null) {
            // There is no previous refresh or timestamp stored
            result = true;
        } else if (TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - storedTimestamp) >= refresh) {
            // refresh time was elapsed
            result = true;
        }
        return result;
    }

    private static void updateDeliveryManagerCache(Context context, PNConfigModel downloadedConfig) {
        PNConfigModel storedConfig = getStoredConfig(context);
        if (storedConfig != null) {
            Set<String> storePlacementIds = storedConfig.placements.keySet();
            for (String placementId : storePlacementIds) {
                // check if new config contains that placement.
                PNPlacementModel newPlacement = downloadedConfig.placements.get(placementId);
                PNPlacementModel storedPlacement = storedConfig.placements.get(placementId);
                if (newPlacement == null) {
                    PNDeliveryManager.resetHourlyImpressionCount(context, placementId);
                    PNDeliveryManager.resetDailyImpressionCount(context, placementId);
                    PNDeliveryManager.resetPacingCalendar(placementId);
                } else {
                    // Check if impression cap (hour) changed
                    if (storedPlacement.delivery_rule.imp_cap_hour != newPlacement.delivery_rule.imp_cap_hour) {
                        PNDeliveryManager.resetHourlyImpressionCount(context, placementId);
                    }
                    // check if impression cap (day) changed
                    if (storedPlacement.delivery_rule.imp_cap_day != newPlacement.delivery_rule.imp_cap_day) {
                        PNDeliveryManager.resetDailyImpressionCount(context, placementId);
                    }
                    // check if pacing cap changed
                    if (storedPlacement.delivery_rule.pacing_cap_minute != newPlacement.delivery_rule.pacing_cap_minute
                        || storedPlacement.delivery_rule.pacing_cap_hour != newPlacement.delivery_rule.pacing_cap_hour) {
                        PNDeliveryManager.resetPacingCalendar(placementId);
                    }
                }
            }
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected static void invokeLoaded(PNConfigModel configModel, PNConfigManager.Listener listener) {
        sIdle = true;
        if (listener != null) {
            listener.onConfigLoaded(configModel);
        }
        doNextConfigRequest();
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================

    protected static void enqueueRequest(PNConfigRequestModel item) {
        if (item != null) {
            if (sQueue == null) {
                sQueue = new ArrayList<PNConfigRequestModel>();
            }
            sQueue.add(item);
        }
    }

    protected static PNConfigRequestModel dequeueRequest() {
        PNConfigRequestModel result = null;
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
        String configDownloadBaseUrl = "https://ml.pubnative.net/ml/v1/config";
        PNConfigModel storedConfig = getStoredConfig(context);
        if (storedConfig != null && !storedConfig.isEmpty()) {
            String configUrl = (String) storedConfig.globals.get(PNConfigModel.GLOBAL.CONFIG_URL);
            if (!TextUtils.isEmpty(configUrl)) {
                configDownloadBaseUrl = configUrl;
            }
        }
        return configDownloadBaseUrl;
    }

    protected static String getConfigDownloadUrl(PNConfigRequestModel request) {

        // Parameters allowed
        // app_token
        // os_version
        // device_name
        // connection_type | "cellular" or "wifi"
        // gender | "f" or "m"
        // age

        Uri.Builder uriBuilder = Uri.parse(getConfigDownloadBaseUrl(request.context)).buildUpon();
        uriBuilder.appendQueryParameter("app_token", request.appToken);
        uriBuilder.appendQueryParameter("os_version", PNSettings.osVersion);
        uriBuilder.appendQueryParameter("device_name", PNSettings.deviceName);
        String connection = null;
        PNDeviceUtils.ConnectionType connectionType = PNDeviceUtils.getConnectionType(request.context);
        switch (connectionType) {
            case CELLULAR:
                connection = "cellular";
                break;
            case WIFI:
                connection = "wifi";
                break;
        }
        if (!TextUtils.isEmpty(connection)) {
            uriBuilder.appendQueryParameter("connection_type", connection);
        }

        if (PNSettings.targeting != null) {
            Map<String, String> extras = PNSettings.targeting.toDictionaryWithIap();
            for (String key : extras.keySet()) {
                String value = extras.get(key);
                uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().toString();
    }

    //----------------------------------------------------------------------------------------------
    // CONFIG
    //----------------------------------------------------------------------------------------------

    protected synchronized static String getStoredConfigString(Context context) {
        return getStringSharedPreference(context, CONFIG_STRING_KEY);
    }

    protected synchronized static void setStoredConfig(Context context, PNConfigModel config) {
        // ensuring the string "null" is not getting saved.
        String configString = (config != null) ? new Gson().toJson(config) : null;
        setStringSharedPreference(context, CONFIG_STRING_KEY, configString);
    }

    //----------------------------------------------------------------------------------------------
    // APP_TOKEN
    //----------------------------------------------------------------------------------------------

    protected static String getStoredAppToken(Context context) {
        return getStringSharedPreference(context, APP_TOKEN_STRING_KEY);
    }

    protected static void setStoredAppToken(Context context, String appToken) {
        setStringSharedPreference(context, APP_TOKEN_STRING_KEY, appToken);
    }

    //----------------------------------------------------------------------------------------------
    // TIMESTAMP
    //----------------------------------------------------------------------------------------------

    protected static Long getStoredTimestamp(Context context) {
        return getLongSharedPreference(context, TIMESTAMP_LONG_KEY);
    }

    protected static void setStoredTimestamp(Context context, Long timestamp) {
        setLongSharedPreference(context, TIMESTAMP_LONG_KEY, timestamp);
    }

    //----------------------------------------------------------------------------------------------
    // REFRESH
    //----------------------------------------------------------------------------------------------

    protected static Long getStoredRefresh(Context context) {
        return getLongSharedPreference(context, REFRESH_LONG_KEY);
    }

    protected static void setStoredRefresh(Context context, Long refresh) {
        setLongSharedPreference(context, REFRESH_LONG_KEY, refresh);
    }

    //----------------------------------------------------------------------------------------------
    // String
    //----------------------------------------------------------------------------------------------

    protected static String getStringSharedPreference(Context context, String key) {
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
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (context != null && !TextUtils.isEmpty(key) && editor != null) {
            if (TextUtils.isEmpty(value)) {
                editor.remove(key);
            } else {
                editor.putString(key, value);
            }
            editor.apply();
        }
    }

    //----------------------------------------------------------------------------------------------
    // Long
    //----------------------------------------------------------------------------------------------

    protected static Long getLongSharedPreference(Context context, String key) {
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

    //----------------------------------------------------------------------------------------------
    // BASE SharedPreferences item
    //----------------------------------------------------------------------------------------------

    protected static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
        }
        return result;
    }
}
