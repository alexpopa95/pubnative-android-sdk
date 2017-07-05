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

package net.pubnative.sdk.core.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.adapter.request.PNAdapter;
import net.pubnative.sdk.core.adapter.request.PNAdapterFactory;
import net.pubnative.sdk.core.config.PNPlacement;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.core.config.model.PNNetworkModel;
import net.pubnative.sdk.core.config.model.PNPriorityRuleModel;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.core.insights.model.PNInsightDataModel;
import net.pubnative.sdk.core.insights.model.PNInsightModel;
import net.pubnative.sdk.core.utils.PNDeviceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PNWaterfall {

    private static String TAG = PNWaterfall.class.getSimpleName();

    protected static final String TRACKING_PARAMETER_APP_TOKEN  = "app_token";
    protected static final String TRACKING_PARAMETER_REQUEST_ID = "reqid";

    protected Context             mContext;
    protected String              mAppToken;
    protected PNPlacement         mPlacement;
    protected PNInsightModel      mInsight;

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected synchronized void initialize(Context context, String appToken, String placementName) {
        if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementName)) {
            onWaterfallError(PNException.REQUEST_PARAMETERS_INVALID);
        } else if (PNDeviceUtils.isNetworkAvailable(context)) {
            mContext = context;
            mAppToken = appToken;
            mPlacement = new PNPlacement();
            Map extras = getExtras();
            mPlacement.load(mContext, appToken, placementName, extras, new PNPlacement.Listener() {

                @Override
                public void onPlacementReady(PNPlacement placement, boolean pacingActive) {
                    if (pacingActive) {
                        onWaterfallLoadFinish(pacingActive);
                    } else {
                        startTracking();
                    }
                }

                @Override
                public void onPlacementLoadFail(PNPlacement placement, Exception exception) {
                    onWaterfallError(exception);
                }
            });
        } else {
            onWaterfallError(PNException.REQUEST_NO_INTERNET);
        }
    }

    protected void startTracking() {
        String requestUrl = (String) mPlacement.getConfig().getGlobal(PNConfigModel.GLOBAL.REQUEST_BEACON);
        String impressionUrl = (String) mPlacement.getConfig().getGlobal(PNConfigModel.GLOBAL.IMPRESSION_BEACON);
        String clickUrl = (String) mPlacement.getConfig().getGlobal(PNConfigModel.GLOBAL.CLICK_BEACON);
        String rescueUrl = (String) mPlacement.getConfig().getGlobal(PNConfigModel.GLOBAL.RESCUE_BEACON);
        mInsight = new PNInsightModel(mContext);
        mInsight.setInsightURLs(requestUrl, impressionUrl, clickUrl, rescueUrl);
        mInsight.addExtra(TRACKING_PARAMETER_APP_TOKEN, mPlacement.getAppToken());
        mInsight.addExtra(TRACKING_PARAMETER_REQUEST_ID, mPlacement.getTrackingUUID());
        mInsight.addExtras(mPlacement.getConfig().request_params);

        PNInsightDataModel data = mInsight.getData();
        data.placement_name = mPlacement.getName();
        data.delivery_segment_ids = mPlacement.getDeliveryRule().segment_ids;
        data.ad_format_code = mPlacement.getAdFormatCode();
        data.coppa = PNSettings.isCoppaModeEnabled ? "1" : "0";
        data.user_uid = PNSettings.advertisingId;
        mInsight.setData(data);
        onWaterfallLoadFinish(false);
    }

    protected Map<String, PNNetworkModel> getAllNetworks(String adapterName, boolean ignoreFirst) {
        Map<String, PNNetworkModel> result = new HashMap<String, PNNetworkModel>();
        List<PNPriorityRuleModel> rules = mPlacement.getPriorities();
        if (TextUtils.isEmpty(adapterName)) {
            Log.w(TAG, "adapter is null or empty and required, " + adapterName);
        } else if (rules == null || rules.size() == 0) {
            Log.w(TAG, "no priorities found in this placement, " + adapterName);
        } else {

            for (int i = 0; i < rules.size(); i++) {
                PNPriorityRuleModel rule = rules.get(i);
                if (ignoreFirst && i == 0) {
                    continue;
                } else {
                    PNNetworkModel network = mPlacement.getNetwork(rule.network_code);
                    if (network != null && network.adapter.equals(adapterName)) {
                        result.put(rule.network_code, network);
                    }
                }
            }
        }
        return result;
    }

    protected void getNextNetwork() {
        mPlacement.next();
        PNNetworkModel network = mPlacement.getCurrentNetwork();
        if (network == null) {
            sendRequestInsight();
            onWaterfallError(PNException.REQUEST_NO_FILL);
        } else {

            PNAdapter adapter = PNAdapterFactory.create(network);
            if (adapter == null) {
                trackUnreachableNetwork(0, PNException.ADAPTER_NOT_FOUND);
                getNextNetwork();
            } else {
                onWaterfallNextNetwork(adapter, getExtras());
            }
        }
    }

    protected Map getExtras() {
        Map<String, String> extras = new HashMap<String, String>();
        extras.put(TRACKING_PARAMETER_REQUEST_ID, mPlacement.getTrackingUUID());
        if (PNSettings.targeting != null) {
            extras.putAll(PNSettings.targeting.toDictionary());
        }
        return extras;
    }

    //==============================================================================================
    // Tracking insight helpers
    //==============================================================================================
    protected synchronized void trackSuccededNetwork(long responseTime) {
        mInsight.trackSuccededNetwork(mPlacement.getCurrentPriority(), responseTime);
        sendRequestInsight();
    }

    protected void sendRequestInsight() {
        mInsight.sendRequestInsight(null);
    }

    protected void trackAttemptedNetwork(long responseTime, Exception reportedException) {
        if (mPlacement != null) {
            Exception exception = null;
            PNNetworkModel currentNetwork = mPlacement.getCurrentNetwork();
            if (currentNetwork != null && currentNetwork.isCrashReportEnabled()) {
                exception = reportedException;
            }
            mInsight.trackAttemptedNetwork(mPlacement.getCurrentPriority(), responseTime, exception);
        }
    }

    protected void trackUnreachableNetwork(long responseTime, Exception reportedException) {
        if (mPlacement != null) {
            Exception exception = null;
            PNNetworkModel currentNetwork = mPlacement.getCurrentNetwork();
            if (currentNetwork != null && currentNetwork.isCrashReportEnabled()) {
                exception = reportedException;
            }
            mInsight.trackUnreachableNetwork(mPlacement.getCurrentPriority(), responseTime, exception);
        }
    }

    //==============================================================================================
    // Abstract methods
    //==============================================================================================
    protected abstract void onWaterfallLoadFinish(boolean pacingActive);

    protected abstract void onWaterfallError(Exception exception);

    protected abstract void onWaterfallNextNetwork(PNAdapter adapter, Map extras);
}
