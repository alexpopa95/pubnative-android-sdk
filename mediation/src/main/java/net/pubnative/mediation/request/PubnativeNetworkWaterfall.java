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

package net.pubnative.mediation.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.AdvertisingIdClient;
import net.pubnative.mediation.adapter.PubnativeNetworkHub;
import net.pubnative.mediation.adapter.PubnativeNetworkHubFactory;
import net.pubnative.mediation.config.PubnativePlacement;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.insights.model.PubnativeInsightModel;
import net.pubnative.mediation.request.model.PubnativeAdTargetingModel;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class PubnativeNetworkWaterfall {

    private static         String TAG                           = PubnativeNetworkRequest.class.getSimpleName();
    protected static final String TRACKING_PARAMETER_APP_TOKEN  = "app_token";
    protected static final String TRACKING_PARAMETER_REQUEST_ID = "reqid";
    protected Context                   mContext;
    protected PubnativePlacement        mPlacement;
    protected PubnativeInsightModel     mInsight;
    protected PubnativeAdTargetingModel mTargeting;
    protected Map<String, String>       mRequestParameters;
    //==============================================================================================
    // Tracking data
    //==============================================================================================

    /**
     * Sets the targeting model for the request
     *
     * @param targeting targeting model with extended targeting config
     */
    public void setTargeting(PubnativeAdTargetingModel targeting) {

        Log.v(TAG, "setTargeting");
        mTargeting = targeting;
    }

    /**
     * Add additional and customisable request parameters for API requests
     * @param key valid key String
     * @param value valid key Value
     */
    public void setRequestParameter(String key, String value) {

        if(mRequestParameters == null){
            mRequestParameters = new HashMap<String, String>();
        }
        mRequestParameters.put(key, value);
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected synchronized void initialize(Context context, String appToken, String placementName) {

        Log.v(TAG, "initialize");
        if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementName)) {
            onWaterfallError(PubnativeException.REQUEST_PARAMETERS_INVALID);
        } else if (PubnativeDeviceUtils.isNetworkAvailable(context)) {
            mContext = context;
            mPlacement = new PubnativePlacement();
            Map extras = new HashMap();
            if (mTargeting != null) {
                extras.putAll(mTargeting.toDictionary());
            }
            if(mRequestParameters != null) {
                extras.putAll(mRequestParameters);
            }
            mPlacement.load(mContext, appToken, placementName, extras, new PubnativePlacement.Listener() {

                @Override
                public void onPubnativePlacementReady(PubnativePlacement placement, boolean pacingActive) {

                    if (pacingActive) {
                        onWaterfallLoadFinish(pacingActive);
                    } else {
                        startTracking();
                    }
                }

                @Override
                public void onPubnativePlacementLoadFail(PubnativePlacement placement, Exception exception) {

                    onWaterfallError(exception);
                }
            });
        } else {
            onWaterfallError(PubnativeException.REQUEST_NO_INTERNET);
        }
    }

    protected void startTracking() {

        String requestUrl = (String) mPlacement.getConfig().getGlobal(PubnativeConfigModel.GLOBAL.REQUEST_BEACON);
        String impressionUrl = (String) mPlacement.getConfig().getGlobal(PubnativeConfigModel.GLOBAL.IMPRESSION_BEACON);
        String clickUrl = (String) mPlacement.getConfig().getGlobal(PubnativeConfigModel.GLOBAL.CLICK_BEACON);
        mInsight = new PubnativeInsightModel(mContext);
        mInsight.setInsightURLs(requestUrl, impressionUrl, clickUrl);
        mInsight.setPlacement(mPlacement.getName());
        mInsight.setSegments(mPlacement.getDeliveryRule().segment_ids);
        mInsight.setAdFormatCode(mPlacement.getAdFormatCode());
        mInsight.addExtra(TRACKING_PARAMETER_APP_TOKEN, mPlacement.getAppToken());
        mInsight.addExtra(TRACKING_PARAMETER_REQUEST_ID, mPlacement.getTrackingUUID());
        mInsight.addExtras(mPlacement.getConfig().request_params);
        AdvertisingIdClient.getAdvertisingId(mContext, new AdvertisingIdClient.Listener() {

            @Override
            public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {

                if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                    mInsight.setUserId(adInfo.getId());
                }
                onWaterfallLoadFinish(false);
            }

            @Override
            public void onAdvertisingIdClientFail(Exception exception) {

                onWaterfallLoadFinish(false);
            }
        });
    }

    protected void getNextNetwork() {

        Log.v(TAG, "getNextNetwork");
        mPlacement.next();
        PubnativeNetworkModel network = mPlacement.currentNetwork();
        if (network == null) {
            mInsight.sendRequestInsight();
            onWaterfallError(PubnativeException.PLACEMENT_NO_FILL);
        } else {
            PubnativeNetworkHub hub = PubnativeNetworkHubFactory.createHub(network);
            if (hub == null) {
                mInsight.trackUnreachableNetwork(mPlacement.currentPriority(), 0, PubnativeException.ADAPTER_NOT_FOUND);
                getNextNetwork();
            } else {
                Map<String, String> extras = new HashMap<String, String>();
                extras.put(TRACKING_PARAMETER_REQUEST_ID, mPlacement.getTrackingUUID());
                if (mTargeting != null) {
                    extras.putAll(mTargeting.toDictionary());
                }
                if(mPlacement.getConfig().request_params != null) {
                    extras.putAll(mPlacement.getConfig().request_params);
                }
                if(mRequestParameters != null) {
                    extras.putAll(mRequestParameters);
                }

                onWaterfallNextNetwork(hub, network, extras, mPlacement.currentPriority().cache_links);
            }
        }
    }

    //==============================================================================================
    // Abstract methods
    //==============================================================================================
    protected abstract void onWaterfallLoadFinish(boolean pacingActive);

    protected abstract void onWaterfallError(Exception exception);

    protected abstract void onWaterfallNextNetwork(PubnativeNetworkHub hub, PubnativeNetworkModel network, Map extras, boolean isCached);
}
