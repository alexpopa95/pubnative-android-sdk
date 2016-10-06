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

package net.pubnative.mediation.insights;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.insights.model.PubnativeInsightRequestModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.task.PubnativeHttpTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PubnativeInsightsManager {

    private static         String  TAG                      = PubnativeInsightsManager.class.getSimpleName();
    protected static final String  INSIGHTS_PREFERENCES_KEY = "net.pubnative.mediation.tracking.PubnativeInsightsManager";
    protected static final String  INSIGHTS_PENDING_DATA    = "pending_data";
    protected static final String  INSIGHTS_FAILED_DATA     = "failed_data";
    protected static final String  PARAMETER_APP_TOKEN_KEY  = "app_token";
    protected static       boolean sIdle                    = true;
    //==============================================================================================
    // PubnativeInsightsManager
    //==============================================================================================

    /**
     * Queues impression/click tracking data and sends it to pubnative server.
     *
     * @param context   valid Context object
     * @param baseURL   the base URL of the tracking server
     * @param parameters added parameters that will be included as querystring parameters
     * @param dataModel PubnativeInsightDataModel object with values filled in.
     */
    public synchronized static void trackData(Context context, String baseURL, Map<String, String> parameters, PubnativeInsightDataModel dataModel) {

        Log.v(TAG, "trackData");
        if (context == null) {
            Log.e(TAG, "trackData - context can't be null. Dropping call");
        } else if (TextUtils.isEmpty(baseURL)) {
            Log.e(TAG, "trackData - baseURL can't be empty. Dropping call");
        } else if (dataModel == null) {
            Log.e(TAG, "trackData - dataModel can't be null. Dropping call");
        } else {
            Uri.Builder uriBuilder = Uri.parse(baseURL).buildUpon();
            // Fill with passed parameters
            if (parameters != null) {
                for (String key : parameters.keySet()) {
                    uriBuilder.appendQueryParameter(key, parameters.get(key));
                }
            }
            PubnativeInsightRequestModel model = new PubnativeInsightRequestModel(uriBuilder.build().toString(), dataModel);
            // Enqueue failed
            List<PubnativeInsightRequestModel> failedList = getTrackingList(context, INSIGHTS_FAILED_DATA);
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

        Log.v(TAG, "trackNext");
        if (context == null) {
            Log.e(TAG, "trackNext - context can't be null. Dropping call");
        } else if (sIdle) {
            sIdle = false;
            final PubnativeInsightRequestModel model = dequeueInsightItem(context, INSIGHTS_PENDING_DATA);
            if (model == null) {
                Log.w(TAG, "trackNext - Dequeued item is null. Dropping call");
                sIdle = true;
            } else {

                String trackingDataString = new Gson().toJson(model.dataModel);
                if (!TextUtils.isEmpty(model.url) && !TextUtils.isEmpty(trackingDataString)) {
                    PubnativeHttpTask.Listener listener = new PubnativeHttpTask.Listener() {

                        @Override
                        public void onHttpTaskSuccess(PubnativeHttpTask task, String result) {

                            Log.v(TAG, "onHttpTaskSuccess");
                            if (TextUtils.isEmpty(result)) {
                                trackingFailed(context, model, "invalid insight response (empty or null)");
                            } else {
                                try {
                                    PubnativeInsightsAPIResponseModel response = new Gson().fromJson(result, PubnativeInsightsAPIResponseModel.class);
                                    if (PubnativeInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                                        trackingFinished(context, model);
                                    } else {
                                        trackingFailed(context, model, response.error_message);
                                    }
                                } catch (Exception e) {
                                    trackingFailed(context, model, e.toString());
                                }
                            }
                        }

                        @Override
                        public void onHttpTaskFailed(PubnativeHttpTask task, String errorMessage) {

                            Log.v(TAG, "onHttpTaskFailed: " + errorMessage);
                            trackingFailed(context, model, errorMessage);
                        }
                    };
                    sendTrackingDataToServer(context, trackingDataString, model.url, listener);
                } else {
                    // Drop the call, tracking data is errored
                    trackingFinished(context, model);
                }
            }
        } else {
            Log.e(TAG, "trackNext - Already tracking one request. Dropping call");
        }
    }

    protected static void trackingFailed(Context context, PubnativeInsightRequestModel model, String message) {

        Log.v(TAG, "trackingFailed");
        // Add a retry
        model.dataModel.retry = model.dataModel.retry + 1;
        enqueueInsightItem(context, INSIGHTS_FAILED_DATA, model);
        sIdle = true;
        trackNext(context);
    }

    protected static void trackingFinished(Context context, PubnativeInsightRequestModel model) {

        Log.v(TAG, "trackingFinished");
        sIdle = true;
        trackNext(context);
    }

    protected static void sendTrackingDataToServer(Context context, String trackingDataString, String url, PubnativeHttpTask.Listener listener) {

        Log.v(TAG, "sendTrackingDataToServer");
        PubnativeHttpTask http = new PubnativeHttpTask(context);
        http.setPOSTData(trackingDataString);
        http.setListener(listener);
        http.execute(url);
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================
    protected static void enqueueInsightItem(Context context, String listKey, PubnativeInsightRequestModel model) {

        Log.v(TAG, "enqueueInsightItem");
        if (context != null && model != null) {
            List<PubnativeInsightRequestModel> pendingList = getTrackingList(context, listKey);
            if (pendingList == null) {
                pendingList = new ArrayList<PubnativeInsightRequestModel>();
            }
            pendingList.add(model);
            setTrackingList(context, listKey, pendingList);
        }
    }

    protected static void enqueueInsightList(Context context, String listKey, List<PubnativeInsightRequestModel> list) {

        Log.v(TAG, "enqueueInsightList");
        if (context != null && list != null) {
            List<PubnativeInsightRequestModel> insightList = getTrackingList(context, listKey);
            if (insightList == null) {
                insightList = new ArrayList<PubnativeInsightRequestModel>();
            }
            insightList.addAll(list);
            setTrackingList(context, listKey, insightList);
        }
    }

    protected static PubnativeInsightRequestModel dequeueInsightItem(Context context, String listKey) {

        Log.v(TAG, "dequeueInsightItem");
        PubnativeInsightRequestModel result = null;
        if (context != null) {
            List<PubnativeInsightRequestModel> pendingList = getTrackingList(context, listKey);
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
    protected static List<PubnativeInsightRequestModel> getTrackingList(Context context, String listKey) {

        Log.v(TAG, "getTrackingList");
        List<PubnativeInsightRequestModel> result = null;
        if (context != null) {
            SharedPreferences preferences = getSharedPreferences(context);
            if (preferences != null) {
                String pendingListString = preferences.getString(listKey, null);
                if (!TextUtils.isEmpty(pendingListString)) {
                    try {
                        PubnativeInsightRequestModel[] cacheModel = new Gson().fromJson(pendingListString, PubnativeInsightRequestModel[].class);
                        if (cacheModel != null && cacheModel.length > 0) {
                            result = new ArrayList<PubnativeInsightRequestModel>(Arrays.asList(cacheModel));
                        }
                    } catch (JsonSyntaxException e) {
                        // Do nothing
                    }
                }
            }
        }
        return result;
    }

    protected static void setTrackingList(Context context, String listKey, List<PubnativeInsightRequestModel> pendingList) {

        Log.v(TAG, "getTrackingList");
        if (context != null) {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            if (editor != null) {
                if (pendingList == null || pendingList.size() == 0) {
                    editor.remove(listKey);
                } else {
                    String cacheModelString = new Gson().toJson(pendingList.toArray());
                    if (!TextUtils.isEmpty(cacheModelString)) {
                        editor.putString(listKey, cacheModelString);
                    }
                }
                editor.apply();
            }
        }
    }

    // Shared preferences base item
    //----------------------------------------------------------------------------------------------
    protected static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {

        Log.v(TAG, "getSharedPreferencesEditor");
        SharedPreferences.Editor result = null;
        SharedPreferences preferences = getSharedPreferences(context);
        if (preferences != null) {
            result = preferences.edit();
        }
        return result;
    }

    protected static SharedPreferences getSharedPreferences(Context context) {

        Log.v(TAG, "getSharedPreferences");
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(INSIGHTS_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }
        return result;
    }
}
