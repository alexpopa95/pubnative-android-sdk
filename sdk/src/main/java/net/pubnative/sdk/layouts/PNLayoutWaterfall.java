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

package net.pubnative.sdk.layouts;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.config.PNPlacement;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.core.config.model.PNNetworkModel;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.core.insights.model.PNInsightDataModel;
import net.pubnative.sdk.core.insights.model.PNInsightModel;
import net.pubnative.sdk.core.utils.PNDeviceUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class PNLayoutWaterfall {

    private static String TAG = PNLayoutWaterfall.class.getSimpleName();

    protected static final String TRACKING_PARAMETER_APP_TOKEN  = "app_token";
    protected static final String TRACKING_PARAMETER_REQUEST_ID = "reqid";
    protected Context            mContext;
    protected String             mAppToken;
    protected PNPlacement        mPlacement;
    protected PNInsightModel     mInsight;

    //==============================================================================================
    // Tracking data
    //==============================================================================================

    /**
     * sets insight to track request when time out occur for this request.
     *
     * @param insight valid insight for track this request.
     */
    public void setInsight(PNInsightModel insight) {
        mInsight = insight;
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected synchronized void initialize(Context context, String appToken, String placementName) {
        if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementName)) {
            onPubnativeNetworkLayoutWaterfallLoadFail(PNException.REQUEST_PARAMETERS_INVALID);
        } else if (PNDeviceUtils.isNetworkAvailable(context)) {
            mContext = context;
            mAppToken = appToken;
            mPlacement = new PNPlacement();
            Map extras = new HashMap<String, String>();
            mPlacement.load(mContext, appToken, placementName, extras, new PNPlacement.Listener() {

                @Override
                public void onPlacementReady(PNPlacement placement, boolean pacingActive) {

                    if (pacingActive) {
                        onPubnativeNetworkLayoutWaterfallLoadFinish(pacingActive);
                    } else {
                        startTracking();
                    }
                }

                @Override
                public void onPlacementLoadFail(PNPlacement placement, Exception exception) {

                    onPubnativeNetworkLayoutWaterfallLoadFail(exception);
                }
            });
        } else {
            onPubnativeNetworkLayoutWaterfallLoadFail(PNException.REQUEST_NO_INTERNET);
        }
    }

    protected void startTracking() {
        if (PNSettings.isCoppaModeEnabled) {
            startTracking(null); // Avoid retrieving data on copa mode enabled
        } else {
            startTracking(PNSettings.advertisingId);
        }
    }

    protected void startTracking(String userID) {
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
        data.user_uid = userID;
        mInsight.setData(data);
        onPubnativeNetworkLayoutWaterfallLoadFinish(false);
    }

    protected void getNextNetwork() {
        mPlacement.next();
        PNNetworkModel network = mPlacement.getCurrentNetwork();
        if (network == null) {
            sendRequestInsight();
            onPubnativeNetworkLayoutWaterfallLoadFail(PNException.REQUEST_NO_FILL);
        } else {
            onPubnativeNetworkLayoutWaterfallNextNetwork(getHubExtras());
        }
    }

    protected Map<String, String> getHubExtras() {
        Map<String, String> extras = new HashMap<String, String>();
        extras.put(TRACKING_PARAMETER_REQUEST_ID, mPlacement.getTrackingUUID());
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
        Exception exception = null;
        if (mPlacement.getCurrentNetwork().isCrashReportEnabled()) {
            exception = reportedException;
        }
        mInsight.trackAttemptedNetwork(mPlacement.getCurrentPriority(), responseTime, exception);
    }

    protected void trackUnreachableNetwork(long responseTime, Exception reportedException) {
        Exception exception = null;
        if (mPlacement.getCurrentNetwork().isCrashReportEnabled()) {
            exception = reportedException;
        }
        mInsight.trackUnreachableNetwork(mPlacement.getCurrentPriority(), responseTime, exception);
    }

    //==============================================================================================
    // Abstract methods
    //==============================================================================================
    protected abstract void onPubnativeNetworkLayoutWaterfallLoadFinish(boolean pacingActive);

    protected abstract void onPubnativeNetworkLayoutWaterfallLoadFail(Exception exception);

    protected abstract void onPubnativeNetworkLayoutWaterfallNextNetwork(Map extras);
}
