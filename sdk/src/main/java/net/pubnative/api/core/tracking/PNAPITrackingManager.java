// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
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

package net.pubnative.api.core.tracking;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.api.core.network.PNAPIHttpRequest;
import net.pubnative.api.core.tracking.model.PNAPITrackingURLModel;

import java.util.ArrayList;
import java.util.List;

public class PNAPITrackingManager {

    private static final   String  TAG                 = PNAPITrackingManager.class.getSimpleName();
    private static final   String  SHARED_PREFERENCES  = "PNAPITrackingManager";
    protected static final String  SHARED_PENDING_LIST = "pending";
    protected static final String  SHARED_FAILED_LIST  = "failed";
    private static final   long    ITEM_VALIDITY_TIME  = 1800000; // 30 minutes
    private static         boolean sIsTracking         = false;

    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * This method is used to send impression request
     *
     * @param context valid context
     * @param url     Url to track
     */
    public synchronized static void track(Context context, String url) {
        if (context == null) {
            Log.w(TAG, "track - ERROR: Context parameter is null");
        } else if (TextUtils.isEmpty(url)) {
            Log.w(TAG, "track - ERROR: url parameter is null");
        } else {
            // 1. Enqueue failed items
            enqueueFailedList(context);
            // 2. Enqueue current item
            PNAPITrackingURLModel model = new PNAPITrackingURLModel();
            model.url = url;
            model.startTimestamp = System.currentTimeMillis();
            enqueueItem(context, SHARED_PENDING_LIST, model);
            // 3. Try doing next item
            trackNextItem(context);
        }
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================

    protected synchronized static void trackNextItem(final Context context) {
        if (sIsTracking) {
            Log.w(TAG, "trackNextItem - Currently tracking, dropping the call, will be resumed soon");
        } else {
            sIsTracking = true;
            // Extract pending item
            final PNAPITrackingURLModel model = dequeueItem(context, SHARED_PENDING_LIST);
            if (model == null) {
                sIsTracking = false;
            } else {
                if (model.startTimestamp + ITEM_VALIDITY_TIME < System.currentTimeMillis()) {
                    sIsTracking = false;
                    trackNextItem(context);
                } else {
                    new PNAPIHttpRequest().start(context, model.url, new PNAPIHttpRequest.Listener() {

                        @Override
                        public void onPNAPIHttpRequestFinish(PNAPIHttpRequest request, String result, int statusCode) {
                            sIsTracking = false;
                            trackNextItem(context);
                        }

                        @Override
                        public void onPNAPIHttpRequestFail(PNAPIHttpRequest request, Exception exception) {
                            // Since this failed, we re-enqueue it
                            enqueueItem(context, SHARED_FAILED_LIST, model);
                            sIsTracking = false;
                            trackNextItem(context);
                        }
                    });
                }
            }
        }
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================

    protected static void enqueueFailedList(Context context) {
        List<PNAPITrackingURLModel> failedList = getList(context, SHARED_FAILED_LIST);
        List<PNAPITrackingURLModel> pendingList = getList(context, SHARED_PENDING_LIST);
        pendingList.addAll(failedList);
        setList(context, SHARED_PENDING_LIST, pendingList);
        failedList.clear();
        setList(context, SHARED_FAILED_LIST, failedList);
    }

    protected static void enqueueItem(Context context, String listKey, PNAPITrackingURLModel model) {
        List<PNAPITrackingURLModel> list = getList(context, listKey);
        list.add(model);
        setList(context, listKey, list);
    }

    protected static PNAPITrackingURLModel dequeueItem(Context context, String listKey) {
        PNAPITrackingURLModel result = null;
        List<PNAPITrackingURLModel> list = getList(context, listKey);
        if (list.size() > 0) {
            result = list.get(0);
            list.remove(0);
            setList(context, listKey, list);
        }
        return result;
    }

    //==============================================================================================
    // SHARED PREFERENCES
    //==============================================================================================
    // List helper
    //----------------------------------------------------------------------------------------------

    protected static List<PNAPITrackingURLModel> getList(Context context, String key) {
        List<PNAPITrackingURLModel> result = null;
        SharedPreferences preferences = getSharedPreferences(context);
        String sharedPendingString = preferences.getString(key, null);
        if (sharedPendingString == null) {
            result = new ArrayList<PNAPITrackingURLModel>();
        } else {
            result = new Gson().fromJson(sharedPendingString, new TypeToken<List<PNAPITrackingURLModel>>() {}.getType());
        }
        return result;
    }

    protected static void setList(Context context, String key, List<PNAPITrackingURLModel> value) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        if (value == null) {
            preferencesEditor.remove(key);
        } else {
            String listString = new Gson().toJson(value);
            preferencesEditor.putString(key, listString);
        }
        preferencesEditor.apply();
    }

    //----------------------------------------------------------------------------------------------
    // Base shared preferences
    //----------------------------------------------------------------------------------------------

    protected static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }
}
