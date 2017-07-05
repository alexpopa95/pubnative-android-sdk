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

package net.pubnative.sdk.core.insights;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.core.insights.model.PNInsightDataModel;
import net.pubnative.sdk.core.insights.model.PNInsightRequestModel;
import net.pubnative.sdk.core.insights.model.PNInsightsAPIResponseModel;
import net.pubnative.sdk.core.network.PNHttpRequest;
import net.pubnative.sdk.core.utils.PNStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PNInsightsManager {

    private static String TAG = PNInsightsManager.class.getSimpleName();

    protected static final String INSIGHTS_PREFERENCES_KEY  = "net.pubnative.mediation.tracking.PNInsightsManager";
    protected static final String INSIGHTS_PENDING_DATA     = "pending_data";
    protected static final String INSIGHTS_FAILED_DATA      = "failed_data";

    protected static boolean sIdle = true;

    //==============================================================================================
    // PNInsightsManager
    //==============================================================================================

    /**
     * Queues impression/click tracking data and sends it to pubnative server.
     *
     * @param context   valid Context object
     * @param baseURL   the base URL of the tracking server
     * @param extras    added parameters that will be included as querystring parameters
     * @param dataModel PNInsightDataModel object with values filled in.
     */
    public synchronized static void trackData(Context context, String baseURL, Map<String, String> extras, PNInsightDataModel dataModel) {
        if (context == null) {
            Log.w(TAG, "trackData - context can't be null. Dropping call");
        } else if (TextUtils.isEmpty(baseURL)) {
            Log.w(TAG, "trackData - baseURL can't be empty. Dropping call");
        } else if (dataModel == null) {
            Log.w(TAG, "trackData - dataModel can't be null. Dropping call");
        } else {
            Uri.Builder uriBuilder = Uri.parse(baseURL).buildUpon();
            // Fill with passed parameters
            if (extras != null && extras.size() > 0) {
                for (String key : extras.keySet()) {
                    uriBuilder.appendQueryParameter(key, extras.get(key));
                }
            }
            dataModel.generated_at = System.currentTimeMillis() * 1000;
            PNInsightRequestModel model = new PNInsightRequestModel(uriBuilder.build().toString(), dataModel);
            // Enqueue failed
            List<PNInsightRequestModel> failedList = getTrackingList(context, INSIGHTS_FAILED_DATA);
            enqueueInsightList(context, INSIGHTS_PENDING_DATA, failedList);
            setTrackingList(context, INSIGHTS_FAILED_DATA, null);
            // Enqueue current
            enqueueInsightItem(context, INSIGHTS_PENDING_DATA, model);
            // Start tracking
            trackNext(context);
        }
    }

    //==============================================================================================
    // WORKFLOW
    //==============================================================================================

    protected synchronized static void trackNext(final Context context) {
        if (context == null) {
            Log.w(TAG, "trackNext - context can't be null. Dropping call");
        } else if (sIdle) {
            sIdle = false;
            final PNInsightRequestModel model = dequeueInsightItem(context, INSIGHTS_PENDING_DATA);
            if (model == null) {
                Log.w(TAG, "trackNext - Dequeued item is null. Dropping call");
                sIdle = true;
            } else {
                String trackingDataString = new Gson().toJson(model.dataModel);
                if (!TextUtils.isEmpty(model.url) && !TextUtils.isEmpty(trackingDataString)) {
                    PNHttpRequest.Listener listener = new PNHttpRequest.Listener() {

                        @Override
                        public void onPNHttpRequestFinish(PNHttpRequest request, String result) {
                            if (TextUtils.isEmpty(result)) {
                                trackingFailed(context, model, "invalid insight response (empty or null)");
                            } else {
                                try {
                                    PNInsightsAPIResponseModel response = new Gson().fromJson(result, PNInsightsAPIResponseModel.class);
                                    if (PNInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                                        trackingFinished(context, model);
                                    } else {
                                        trackingFailed(context, model, response.error_message);
                                    }
                                } catch (Exception e) {
                                    Map errorData = new HashMap();
                                    errorData.put("parsingException", e.toString());
                                    errorData.put("serverResponse", result);
                                    trackingFailed(context, model, PNException.extraException(errorData).toString());
                                }
                            }
                        }

                        @Override
                        public void onPNHttpRequestFail(PNHttpRequest request, Exception exception) {
                            trackingFailed(context, model, exception.toString());
                        }
                    };
                    sendTrackingDataToServer(context, trackingDataString, model.url, listener);
                } else {
                    // Drop the call, tracking data is errored
                    trackingFinished(context, model);
                }
            }
        } else {
            Log.w(TAG, "trackNext - Already tracking one request. Dropping call");
        }
    }

    protected static void trackingFailed(Context context, PNInsightRequestModel model, String message) {
        // Add a retry
        model.dataModel.retry = model.dataModel.retry + 1;
        model.dataModel.retry_error = message;
        enqueueInsightItem(context, INSIGHTS_FAILED_DATA, model);
        sIdle = true;
        trackNext(context);
    }

    protected static void trackingFinished(Context context, PNInsightRequestModel model) {
        sIdle = true;
        trackNext(context);
    }

    protected static void sendTrackingDataToServer(Context context, String trackingDataString, String url, PNHttpRequest.Listener listener) {
        PNHttpRequest http = new PNHttpRequest();
        http.setPOSTString(trackingDataString);
        http.start(context, url, listener);
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================

    protected static void enqueueInsightItem(Context context, String listKey, PNInsightRequestModel model) {
        if (context != null && model != null) {
            List<PNInsightRequestModel> pendingList = getTrackingList(context, listKey);
            if (pendingList == null) {
                pendingList = new ArrayList<PNInsightRequestModel>();
            }
            pendingList.add(model);
            setTrackingList(context, listKey, pendingList);
        }
    }

    protected static void enqueueInsightList(Context context, String listKey, List<PNInsightRequestModel> list) {
        if (context != null && list != null) {
            List<PNInsightRequestModel> insightList = getTrackingList(context, listKey);
            if (insightList == null) {
                insightList = new ArrayList<PNInsightRequestModel>();
            }
            insightList.addAll(list);
            setTrackingList(context, listKey, insightList);
        }
    }

    protected static PNInsightRequestModel dequeueInsightItem(Context context, String listKey) {
        PNInsightRequestModel result = null;
        if (context != null) {
            List<PNInsightRequestModel> pendingList = getTrackingList(context, listKey);
            if (pendingList != null && pendingList.size() > 0) {
                result = pendingList.get(0);
                pendingList.remove(0);
                setTrackingList(context, listKey, pendingList);
            }
        }
        return result;
    }

    //==============================================================================================
    // SHARED PREFERENCES
    //==============================================================================================
    // TRACKING LIST
    //----------------------------------------------------------------------------------------------

    protected static List<PNInsightRequestModel> getTrackingList(Context context, String listKey) {
        List<PNInsightRequestModel> result = null;
        if (context != null) {
            SharedPreferences preferences = getSharedPreferences(context);
            if (preferences != null) {
                String pendingListString = preferences.getString(listKey, null);
                if (!TextUtils.isEmpty(pendingListString)) {
                    try {
                        result = PNStringUtils.convertStringToObjects(pendingListString, PNInsightRequestModel.class);
                    } catch (JsonSyntaxException e) {
                        Log.e(TAG, "getTrackingList: ", e);
                    }
                }
            }
        }
        return result;
    }

    protected static void setTrackingList(Context context, String listKey, List<PNInsightRequestModel> pendingList) {
        if (context != null) {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            if (editor != null) {
                if (pendingList == null || pendingList.size() == 0) {
                    editor.remove(listKey);
                } else {
                    String cacheModelString = PNStringUtils.convertObjectsToJson(pendingList);
                    if (!TextUtils.isEmpty(cacheModelString)) {
                        editor.putString(listKey, cacheModelString);
                    }
                }
                editor.apply();
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // Shared preferences base item
    //----------------------------------------------------------------------------------------------

    protected static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        SharedPreferences.Editor result = null;
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            result = preferences.edit();
        }
        return result;
    }

    protected static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(INSIGHTS_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }
        return result;
    }
}
