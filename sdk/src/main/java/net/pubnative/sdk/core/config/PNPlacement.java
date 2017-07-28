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
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.core.config.model.PNDeliveryRuleModel;
import net.pubnative.sdk.core.config.model.PNNetworkModel;
import net.pubnative.sdk.core.config.model.PNPlacementModel;
import net.pubnative.sdk.core.config.model.PNPriorityRuleModel;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PNPlacement {

    private static final String TAG = PNPlacement.class.getSimpleName();

    protected Context          mContext;
    protected Listener         mListener;
    protected String           mAppToken;
    protected String           mRequestID;
    protected String           mPlacementName;
    protected PNPlacementModel mPlacementModel;
    protected PNConfigModel    mConfigModel;
    protected int              mCurrentNetworkIndex;

    /**
     * Interface for placement callbacks
     */
    public interface Listener {

        /**
         * Called when the placement was loaded
         *
         * @param placement    placement that finished loading
         * @param pacingActive indicates if the pacing cap is active or not
         */
        void onPlacementReady(PNPlacement placement, boolean pacingActive);

        /**
         * Called when the placement initialize failed
         *
         * @param placement placement that failed loading
         * @param exception valid Exception with description of the failure
         */
        void onPlacementLoadFail(PNPlacement placement, Exception exception);
    }

    /**
     * Loads the basic data for the current placement
     *
     * @param context       valid context
     * @param appToken      app token string
     * @param placementName placement name string
     * @param extras        valid Map with extra request details
     * @param listener      valid listener to callback when the placement is ready
     */
    public void load(Context context, String appToken, String placementName, Map extras, final Listener listener) {

        if (listener == null) {
            Log.w(TAG, "initialize", new IllegalArgumentException("listener cannot be null, dropping this call"));
        } else {
            mListener = listener;
            if (context == null ||
                TextUtils.isEmpty(appToken) ||
                TextUtils.isEmpty(placementName)) {
                invokeOnLoadFail(PNException.REQUEST_PARAMETERS_INVALID);
            } else if (mConfigModel != null) {
                invokeOnLoadFail(new Exception("initialize - Error: placement is loaded"));
            } else {
                mContext = context;
                mAppToken = appToken;
                mPlacementName = placementName;
                mCurrentNetworkIndex = -1;
                mRequestID = UUID.randomUUID().toString();
                PNConfigManager.getConfig(mContext, mAppToken, new PNConfigManager.Listener() {
                    @Override
                    public void onConfigLoaded(PNConfigModel config) {
                        loadPlacement(config);
                    }
                });
            }
        }
    }

    /**
     * Returns this placement tracking UUID, this will be unique
     * for each instance of PNPlacement objects
     *
     * @return String representation of the UUID
     */
    public String getTrackingUUID() {
        return mRequestID;
    }

    /**
     * Returns the configured app token for this placement
     *
     * @return valid apptoken string, null if not set
     */
    public String getAppToken() {
        return mAppToken;
    }

    /**
     * Gets this placement ad format code
     *
     * @return valid string if loaded, null if not
     */
    public String getAdFormatCode() {
        String result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.ad_format_code;
        }
        return result;
    }

    /**
     * Gets the current delivery rule model
     *
     * @return valid PNDeliveryRuleModel if loaded, null if not
     */
    public PNDeliveryRuleModel getDeliveryRule() {
        PNDeliveryRuleModel result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.delivery_rule;
        }
        return result;
    }

    /**
     * Gest the loaded config model
     *
     * @return loaded PNConfigModel object
     */
    public PNConfigModel getConfig() {
        return mConfigModel;
    }

    /**
     * Gets this placement name
     *
     * @return valid placement name string
     */
    public String getName() {
        return mPlacementName;
    }

    public int getSize() {

        int result = 0;
        if (mPlacementModel != null && mPlacementModel.priority_rules != null) {
            result = mPlacementModel.priority_rules.size();
        }
        return result;
    }

    /**
     * Gets the current priority model
     *
     * @return valid PNPriorityRuleModel, null if there are no more
     */
    public PNPriorityRuleModel getCurrentPriority() {
        return getPriorityRule(mCurrentNetworkIndex);
    }

    /**
     * Gets the priority rule in the placement related to the given index
     *
     * @return valid PNPriorityRuleModel, null if not found
     */
    public PNPriorityRuleModel getPriorityRule(int index) {
        PNPriorityRuleModel result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.getPriorityRule(index);
        }
        return result;
    }

    /**
     * Gets the priority rule in the placement related to the given index
     *
     * @return valid PNPriorityRuleModel, null if not found
     */
    public List<PNPriorityRuleModel> getPriorities() {
        List<PNPriorityRuleModel> result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.priority_rules;
        }
        return result;
    }

    /**
     * Gets the current network name
     *
     * @return valid PNPriorityRuleModel, null if not found
     */
    public String getCurrentNetworkName() {
        String result = null;
        PNPriorityRuleModel rule = getCurrentPriority();
        if (rule != null) {
            result = rule.network_code;
        }
        return result;
    }

    /**
     * Gets the current network model
     *
     * @return valid PNNetworkModel, null if there are no more
     */
    public PNNetworkModel getCurrentNetwork() {
        return getNetwork(mCurrentNetworkIndex);
    }

    /**
     * Gets the current network index in the placement
     *
     * @return index of the current network
     */
    public int getCurrentNetworkIndex() {
        return mCurrentNetworkIndex;
    }

    /**
     * Gets the network related to the given priority rule index
     *
     * @return valid PNNetworkModel, null if not found
     */
    public PNNetworkModel getNetwork(int index) {
        PNNetworkModel result = null;
        PNPriorityRuleModel rule = getPriorityRule(index);
        if (rule != null && !TextUtils.isEmpty(rule.network_code)) {
            result = getNetwork(rule.network_code);
        }
        return result;
    }

    /**
     * Gets the network related to the given network code
     *
     * @param network valid network code
     * @return network model if present, null if not found
     */
    public PNNetworkModel getNetwork(String network) {
        PNNetworkModel result = null;
        if (!TextUtils.isEmpty(network)) {
            result = mConfigModel.getNetwork(network);
        }
        return result;
    }

    /**
     * Tells if this placement has ad caching enabled
     *
     * @return true if ad fetchAssets is enabled, false if not
     */
    public boolean isAdCacheEnabled() {
        boolean result = false;
        if (mPlacementModel != null) {
            result = mPlacementModel.ad_cache;
        }
        return result;
    }

    /**
     * Tells if this any network from this placement has ad caching enabled
     *
     * @return true if ad fetchAssets is enabled for the network, false if not
     */
    public boolean hasNetworkCacheEnabled() {
        boolean result = false;
        if (mPlacementModel != null) {
            for (Map.Entry<String, PNNetworkModel> networkModelEntry : mConfigModel.networks.entrySet()) {
                PNNetworkModel network = networkModelEntry.getValue();

                if (network.ad_cache) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }


    /**
     * Waterfalls to the next network
     */
    public void next() {
        mCurrentNetworkIndex++;
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected void loadPlacement(PNConfigModel config) {
        mConfigModel = config;
        if (mConfigModel == null || mConfigModel.isEmpty()) {
            invokeOnLoadFail(PNException.REQUEST_CONFIG_INVALID);
        } else {
            mPlacementModel = mConfigModel.getPlacement(mPlacementName);
            if (mPlacementModel == null) {
                invokeOnLoadFail(PNException.PLACEMENT_NOT_FOUND);
            } else if (mPlacementModel.delivery_rule == null
                       || mPlacementModel.priority_rules == null
                       || mPlacementModel.priority_rules.size() == 0) {
                invokeOnLoadFail(PNException.REQUEST_CONFIG_EMPTY);
            } else if (isDisabled()) {
                invokeOnLoadFail(PNException.PLACEMENT_DISABLED);
            } else if (isFrequencyCapActive()) {
                invokeOnLoadFail(PNException.PLACEMENT_FREQUENCY_CAP);
            } else {
                invokeOnReady(isPacingCapActive());
            }
        }
    }

    protected boolean isDisabled() {
        boolean result = true;
        if (mPlacementModel != null) {
            PNDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            result = deliveryRuleModel.isDisabled();
        }
        return result;
    }

    protected boolean isFrequencyCapActive() {
        boolean result = false;
        if (mPlacementModel != null) {
            PNDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            result = deliveryRuleModel.isFrequencyCapReached(mContext, mPlacementName);
        }
        return result;
    }

    protected boolean isPacingCapActive() {
        boolean result = false;
        if (mPlacementModel != null) {
            PNDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PNDeliveryManager.getPacingCalendar(mPlacementName);
            if (overdueCalendar == null || pacingCalendar == null || pacingCalendar.before(overdueCalendar)) {
                // Pacing cap reset or deactivated or not reached
                result = false;
            } else {
                // Pacing cap active and limit reached
                result = true;
            }
        }
        return result;
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeOnReady(boolean pacingActive) {
        if (mListener != null) {
            mListener.onPlacementReady(this, pacingActive);
        }
    }

    protected void invokeOnLoadFail(Exception exception) {
        if (mListener != null) {
            mListener.onPlacementLoadFail(this, exception);
        }
    }
}
