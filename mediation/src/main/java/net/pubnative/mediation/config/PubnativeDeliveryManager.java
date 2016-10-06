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
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PubnativeDeliveryManager {

    private static         String                TAG                           = PubnativeDeliveryManager.class.getSimpleName();
    protected static final String                IMPRESSION_PREFERENCES_KEY    = "net.pubnative.mediation.frequency_manager";
    protected static final String                IMPRESSION_COUNT_DAY_APPEND   = "_impression_count_day";
    protected static final String                IMPRESSION_COUNT_HOUR_APPEND  = "_impression_count_hour";
    protected static final String                IMPRESSION_LAST_UPDATE_APPEND = "_impression_last_update";
    protected              Map<String, Calendar> mCurrentPacing                = new HashMap<String, Calendar>();

    //==============================================================================================
    // PubnativeDeliveryManager
    //==============================================================================================
    // Singleton
    //----------------------------------------------------------------------------------------------
    private PubnativeDeliveryManager() {

    }

    protected static PubnativeDeliveryManager sInstance = null;

    protected static synchronized PubnativeDeliveryManager getInstance() {

        if (sInstance == null) {
            sInstance = new PubnativeDeliveryManager();
        }
        return sInstance;
    }
    // Public
    //----------------------------------------------------------------------------------------------

    /**
     * Gets the Calendar Object that points to the last pacing update for the given placementID
     *
     * @param placementID valid String
     *
     * @return Calendar object
     */
    public static Calendar getPacingCalendar(String placementID) {

        Log.v(TAG, "getPacingCalendar");
        Calendar result = null;
        if (getInstance().mCurrentPacing.containsKey(placementID)) {
            result = getInstance().mCurrentPacing.get(placementID);
        }
        return result;
    }

    /**
     * Sets the current pacing calendar to the current time for the given placementID
     *
     * @param placementID valid String
     */
    public static void updatePacingCalendar(String placementID) {

        Log.v(TAG, "updatePacingCalendar");
        getInstance().mCurrentPacing.put(placementID, Calendar.getInstance());
    }

    /**
     * Removes current pacing Calendar from the system for the given placementID
     *
     * @param placementID valid String
     */
    public static void resetPacingCalendar(String placementID) {

        Log.v(TAG, "resetPacingCalendar");
        getInstance().mCurrentPacing.put(placementID, null);
    }

    /**
     * Logs an impression for the given placementID
     *
     * @param context     valid Context
     * @param placementID valid String
     */
    public static void logImpression(Context context, String placementID) {

        Log.v(TAG, "logImpression");
        int dayCount = getImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID);
        int hourCount = getImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID);
        setImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID, ++dayCount);
        setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, ++hourCount);
    }

    /**
     * Resets daily impression count for the given placementID
     *
     * @param context     valid Context
     * @param placementID valid String
     */
    public static void resetDailyImpressionCount(Context context, String placementID) {

        Log.v(TAG, "resetDailyImpressionCount");
        setImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID, 0);
    }

    /**
     * Resets hourly impression count for the given placementID
     *
     * @param context     valid Context
     * @param placementID valid String
     */
    public static void resetHourlyImpressionCount(Context context, String placementID) {

        Log.v(TAG, "resetHourlyImpressionCount");
        setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, 0);
    }

    /**
     * Gets current daily impression count for the given placementID
     *
     * @param context     valid Context
     * @param placementID valid String
     *
     * @return daily impression count
     */
    public static int getCurrentDailyCount(Context context, String placementID) {

        Log.v(TAG, "getCurrentDailyCount");
        return getImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID);
    }

    /**
     * Gets current hourly impression count for the given placementID
     *
     * @param context     valid Context
     * @param placementID valid String
     *
     * @return hourly impression count
     */
    public static int getCurrentHourlyCount(Context context, String placementID) {

        Log.v(TAG, "getCurrentHourlyCount");
        return getImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID);
    }

    /**
     * Sets the last impression tracking update for the given placementID
     *
     * @param context     valid Context object
     * @param placementID valid String
     * @param calendar    calendar object with the timestamp
     */
    public static void setImpressionLastUpdate(Context context, String placementID, Calendar calendar) {

        Log.v(TAG, "setImpressionLastUpdate");
        if (context != null && !TextUtils.isEmpty(placementID)) {
            SharedPreferences.Editor editor = getPreferencesEditor(context);
            if (editor != null) {
                String placementLastUpdateKey = placementID.concat(IMPRESSION_LAST_UPDATE_APPEND);
                if (calendar == null) {
                    editor.remove(placementLastUpdateKey);
                } else {
                    editor.putLong(placementLastUpdateKey, calendar.getTimeInMillis());
                }
                editor.apply();
            }
        }
    }

    /**
     * Gets the last impression update for the given placementID
     *
     * @param context     valid Context
     * @param placementID valid String
     *
     * @return Calendar with the timestamp
     */
    public static Calendar getImpressionLastUpdate(Context context, String placementID) {

        Log.v(TAG, "getImpressionLastUpdate");
        Calendar result = null;
        if (context != null && !TextUtils.isEmpty(placementID)) {
            SharedPreferences preferences = getPreferences(context);
            if (preferences != null) {
                String placementLastUpdateKey = placementID.concat(IMPRESSION_LAST_UPDATE_APPEND);
                long frequencyMillis = preferences.getLong(placementLastUpdateKey, 0);
                if (frequencyMillis > 0) {
                    result = Calendar.getInstance();
                    result.setTimeInMillis(frequencyMillis);
                }
            }
        }
        return result;
    }

    // Private
    //----------------------------------------------------------------------------------------------
    protected static void updateImpressionCount(Context context, String placementID) {

        Log.v(TAG, "updateImpressionCount");
        if (context != null && !TextUtils.isEmpty(placementID)) {
            Calendar storedCalendar = getImpressionLastUpdate(context, placementID);
            if (storedCalendar != null) {
                Calendar dayCalendar = Calendar.getInstance();
                dayCalendar.set(Calendar.HOUR_OF_DAY, 0);
                dayCalendar.set(Calendar.MINUTE, 0);
                dayCalendar.set(Calendar.SECOND, 0);
                dayCalendar.set(Calendar.MILLISECOND, 0);
                if (storedCalendar.before(dayCalendar)) {
                    setImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID, 0);
                    setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, 0);
                } else {
                    Calendar hourCalendar = Calendar.getInstance();
                    hourCalendar.set(Calendar.MINUTE, 0);
                    hourCalendar.set(Calendar.SECOND, 0);
                    hourCalendar.set(Calendar.MILLISECOND, 0);
                    if (storedCalendar.before(hourCalendar)) {
                        setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, 0);
                    }
                }
            }
            setImpressionLastUpdate(context, placementID, Calendar.getInstance());
        }
    }

    protected static void setImpressionCount(Context context, String impressionCapType, String placementID, int value) {

        Log.v(TAG, "setImpressionCount");
        if (context != null && !TextUtils.isEmpty(impressionCapType) && !TextUtils.isEmpty(placementID)) {
            SharedPreferences.Editor editor = getPreferencesEditor(context);
            if (editor != null) {
                String placementTrackingKey = placementID.concat(impressionCapType);
                if (value == 0) {
                    editor.remove(placementTrackingKey);
                } else {
                    editor.putInt(placementTrackingKey, value);
                }
                editor.apply();
            }
        }
    }

    protected static int getImpressionCount(Context context, String impressionCapType, String placementID) {

        Log.v(TAG, "getImpressionCount");
        updateImpressionCount(context, placementID);
        int result = 0;
        if (context != null && !TextUtils.isEmpty(impressionCapType) && !TextUtils.isEmpty(placementID)) {
            SharedPreferences preferences = getPreferences(context);
            if (preferences != null) {
                String placementTrackingKey = placementID.concat(impressionCapType);
                result = preferences.getInt(placementTrackingKey, 0);
            }
        }
        return result;
    }

    protected static SharedPreferences.Editor getPreferencesEditor(Context context) {

        Log.v(TAG, "getPreferencesEditor");
        SharedPreferences.Editor result = null;
        if (context != null) {
            result = getPreferences(context).edit();
        }
        return result;
    }

    protected static SharedPreferences getPreferences(Context context) {

        Log.v(TAG, "getPreferences");
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(IMPRESSION_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }
        return result;
    }
}
