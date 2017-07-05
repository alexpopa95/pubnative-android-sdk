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

package net.pubnative.sdk.core.insights.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.sdk.core.config.PNDeliveryManager;
import net.pubnative.sdk.core.config.model.PNPriorityRuleModel;
import net.pubnative.sdk.core.insights.PNInsightsManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class PNInsightModel {

    private static final String TAG = PNInsightModel.class.getSimpleName();

    public static final int ERORR_LINES_COUNT = 10;

    protected String              mRequestInsightURL;
    protected String              mImpressionInsightURL;
    protected String              mClickInsightURL;
    protected String              mRescueRequestURL;
    protected Context             mContext;
    protected PNInsightDataModel  mData;
    protected Map<String, String> mExtras;

    public PNInsightModel(Context context) {

        mContext = context;
        mData = new PNInsightDataModel(context);
    }

    public PNInsightDataModel getData() {
        return mData;
    }

    public void setData(PNInsightDataModel data) {
        mData = data;
    }

    /**
     * Adds extra fields to be added in the insight query string
     *
     * @param extras dictionary with extras key and values
     */
    public void addExtras(Map<String, String> extras) {
        if (extras != null) {
            if (mExtras == null) {
                mExtras = new HashMap<String, String>();
            }
            mExtras.putAll(extras);
        }
    }

    /**
     * Adds extra fields to be added in the insight query string
     *
     * @param key   key string
     * @param value value string
     */
    public void addExtra(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (mExtras == null) {
                mExtras = new HashMap<String, String>();
            }
            mExtras.put(key, value);
        }
    }

    /**
     * This will set up the insight urls to use in the track process
     *
     * @param requestUrl    valid request url string
     * @param impressionUrl valid impression url string
     * @param clickUrl      valid click url string
     * @param rescueUrl     valid rescue url string
     */
    public void setInsightURLs(String requestUrl, String impressionUrl, String clickUrl, String rescueUrl) {
        mRequestInsightURL = requestUrl;
        mImpressionInsightURL = impressionUrl;
        mClickInsightURL = clickUrl;
        mRescueRequestURL = rescueUrl;
    }
    //==============================================================================================
    // Tracking
    //==============================================================================================

    /**
     * Sets the current network as unreachable due to the passed exception
     *
     * @param priorityRuleModel valid model
     * @param responseTime      time in milliseconds that this network took to fail
     * @param exception         exception with the details
     */
    public void trackUnreachableNetwork(PNPriorityRuleModel priorityRuleModel, long responseTime, Exception exception) {
        if (priorityRuleModel != null && !TextUtils.isEmpty(priorityRuleModel.network_code)) {
            mData.addUnreachableNetwork(priorityRuleModel.network_code);
        }
        PNInsightCrashModel crashModel = null;
        if (exception != null) {
            crashModel = new PNInsightCrashModel();
            crashModel.error = getExceptionDescription(exception);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            crashModel.details = sw.toString();
        }
        mData.addNetwork(priorityRuleModel, responseTime, crashModel);
    }

    /**
     * Sets the current network as attempted but failed
     *
     * @param priorityRuleModel valid model
     * @param responseTime      time in milliseconds that this attempt took to fail
     * @param exception         exception with details
     */
    public void trackAttemptedNetwork(PNPriorityRuleModel priorityRuleModel, long responseTime, Exception exception) {
        if (priorityRuleModel != null && !TextUtils.isEmpty(priorityRuleModel.network_code)) {
            mData.addAttemptedNetwork(priorityRuleModel.network_code);
        }
        PNInsightCrashModel crashModel = null;
        if (exception != null) {
            crashModel = new PNInsightCrashModel();
            crashModel.error = getExceptionDescription(exception);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            crashModel.details = sw.toString();
        }
        mData.addNetwork(priorityRuleModel, responseTime, crashModel);
    }

    /**
     * Sets the current network as succeded
     *
     * @param priorityRuleModel valid model
     * @param responseTime      time in milliseconds that it took this request to be success
     */
    public void trackSuccededNetwork(PNPriorityRuleModel priorityRuleModel, long responseTime) {
        if (priorityRuleModel != null && !TextUtils.isEmpty(priorityRuleModel.network_code)) {
            mData.network = priorityRuleModel.network_code;
        }
        mData.addNetwork(priorityRuleModel, responseTime, null);
        PNDeliveryManager.updatePacingCalendar(mData.placement_name);
    }

    /**
     * Sends request insight data
     */
    public void sendRequestInsight(Map<String, String> extras) {
        Map<String, String> insightExtras = new HashMap<String, String>();
        if (mExtras != null) {
            insightExtras.putAll(mExtras);
        }
        if (extras != null) {
            insightExtras.putAll(extras);
        }
        PNInsightsManager.trackData(mContext, mRequestInsightURL, insightExtras, mData);
    }

    /**
     * Sends impression insight data
     */
    public void sendImpressionInsight() {
        PNDeliveryManager.logImpression(mContext, mData.placement_name);
        PNInsightsManager.trackData(mContext, mImpressionInsightURL, mExtras, mData);
    }

    /**
     * Sends a request insight data
     */
    public void sendClickInsight() {
        PNInsightsManager.trackData(mContext, mClickInsightURL, mExtras, mData);
    }

    /**
     * Sends a request for rescue insight data
     */
    public void sendRescueInsight(String network, long responseTime) {
        if (!TextUtils.isEmpty(network)) {
            mData.network = network;
            mData.response_time = responseTime;
            PNInsightsManager.trackData(mContext, mRescueRequestURL, mExtras, mData);
        }
    }

    private String getExceptionDescription(Exception exception) {
        StringBuilder errorDesc = new StringBuilder();
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < ERORR_LINES_COUNT && i < stackTrace.length; i++) {
            errorDesc.append(stackTrace[i])
                    .append("\n");
        }
        return errorDesc.toString();
    }
}
