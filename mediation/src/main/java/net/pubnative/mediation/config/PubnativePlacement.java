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
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeDeliveryRuleModel;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class PubnativePlacement implements PubnativeConfigManager.Listener {

    private static final String TAG = PubnativePlacement.class.getSimpleName();
    protected Context                 mContext;
    protected Listener                mListener;
    protected String                  mAppToken;
    protected String                  mRequestID;
    protected String                  mPlacementName;
    protected PubnativePlacementModel mPlacementModel;
    protected PubnativeConfigModel    mConfigModel;
    protected int                     mCurrentNetworkIndex;

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
        void onPubnativePlacementReady(PubnativePlacement placement, boolean pacingActive);

        /**
         * Called when the placement initialize failed
         *
         * @param placement placement that failed loading
         */
        void onPubnativePlacementLoadFail(PubnativePlacement placement, Exception exception);
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

        Log.v(TAG, "initialize");
        if (listener == null) {
            Log.e(TAG, "initialize", new IllegalArgumentException("listener cannot be null, dropping this call"));
        } else {
            mListener = listener;
            if (context == null ||
                TextUtils.isEmpty(appToken) ||
                TextUtils.isEmpty(placementName)) {
                invokeOnLoadFail(PubnativeException.PLACEMENT_PARAMETERS_INVALID);
            } else if (mConfigModel != null) {
                invokeOnLoadFail(new Exception("initialize - Error: placement is loaded"));
            } else {
                mContext = context;
                mAppToken = appToken;
                mPlacementName = placementName;
                mCurrentNetworkIndex = -1;
                mRequestID = UUID.randomUUID().toString();
                PubnativeConfigManager.getConfig(mContext, mAppToken, extras, this);
            }
        }
    }

    /**
     * Returns this placement tracking UUID, this will be unique
     * for each instance of PubnativePlacement objects
     *
     * @return String representation of the UUID
     */
    public String getTrackingUUID() {

        Log.v(TAG, "getUUID");
        return mRequestID;
    }

    /**
     * Returns the configured app token for this placement
     *
     * @return valid apptoken string, null if not set
     */
    public String getAppToken() {

        Log.v(TAG, "getAppToken");
        return mAppToken;
    }

    /**
     * Gets this placement ad format code
     *
     * @return valid string if loaded, null if not
     */
    public String getAdFormatCode() {

        Log.v(TAG, "getAdFormatCode");
        String result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.ad_format_code;
        }
        return result;
    }

    /**
     * Gets the current delivery rule model
     *
     * @return valid PubnativeDeliveryRuleModel if loaded, null if not
     */
    public PubnativeDeliveryRuleModel getDeliveryRule() {

        Log.v(TAG, "getDeliveryRule");
        PubnativeDeliveryRuleModel result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.delivery_rule;
        }
        return result;
    }

    /**
     * Gest the loaded config model
     *
     * @return loaded PubnativeConfigModel object
     */
    public PubnativeConfigModel getConfig() {

        Log.v(TAG, "getConfig");
        return mConfigModel;
    }

    /**
     * Gets this placement name
     *
     * @return valid placement name string
     */
    public String getName() {

        Log.v(TAG, "getName");
        return mPlacementName;
    }

    /**
     * Gets the current priority model
     *
     * @return valid PubnativePriorityRuleModel, null if there are no more
     */
    public PubnativePriorityRuleModel currentPriority() {

        Log.v(TAG, "currentPriority");
        PubnativePriorityRuleModel result = null;
        if (mPlacementName != null) {
            result = mPlacementModel.getPriorityRule(mCurrentNetworkIndex);
        }
        return result;
    }

    /**
     * Gets the current network model
     *
     * @return valid PubnativeNetworkModel, null if there are no more
     */
    public PubnativeNetworkModel currentNetwork() {

        Log.v(TAG, "currentNetwork");
        PubnativeNetworkModel result = null;
        PubnativePriorityRuleModel rule = currentPriority();
        if (rule != null) {
            result = mConfigModel.getNetwork(rule.network_code);
        }
        return result;
    }

    /**
     * Waterfalls to the next network
     */
    public void next() {

        Log.v(TAG, "next");
        mCurrentNetworkIndex++;
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected void loadPlacement(PubnativeConfigModel config) {

        Log.v(TAG, "loadPlacement");
        mConfigModel = config;
        if (mConfigModel == null || mConfigModel.isEmpty()) {
            invokeOnLoadFail(PubnativeException.PLACEMENT_CONFIG_INVALID);
        } else {
            mPlacementModel = mConfigModel.getPlacement(mPlacementName);
            if (mPlacementModel == null) {
                invokeOnLoadFail(PubnativeException.PLACEMENT_NOT_FOUND);
            } else if (mPlacementModel.delivery_rule == null
                       || mPlacementModel.priority_rules == null
                       || mPlacementModel.priority_rules.size() == 0) {
                invokeOnLoadFail(PubnativeException.PLACEMENT_EMPTY);
            } else if (isDisabled()) {
                invokeOnLoadFail(PubnativeException.PLACEMENT_DISABLED);
            } else if (isFrequencyCapActive()) {
                invokeOnLoadFail(PubnativeException.PLACEMENT_FREQUENCY_CAP);
            } else {
                invokeOnReady(isPacingCapActive());
            }
        }
    }

    protected boolean isDisabled() {

        Log.v(TAG, "isDisabled");
        boolean result = true;
        if (mPlacementModel != null) {
            PubnativeDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            result = deliveryRuleModel.isDisabled();
        }
        return result;
    }

    protected boolean isFrequencyCapActive() {

        Log.v(TAG, "isFrequencyCapEnabled");
        boolean result = false;
        if (mPlacementModel != null) {
            PubnativeDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            result = deliveryRuleModel.isFrequencyCapReached(mContext, mPlacementName);
        }
        return result;
    }

    protected boolean isPacingCapActive() {

        Log.v(TAG, "isPacingCapEnabled");
        boolean result = false;
        if (mPlacementModel != null) {
            PubnativeDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PubnativeDeliveryManager.getPacingCalendar(mPlacementName);
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

        Log.v(TAG, "invokeOnReady");
        if (mListener != null) {
            mListener.onPubnativePlacementReady(this, pacingActive);
        }
    }

    protected void invokeOnLoadFail(Exception exception) {

        Log.v(TAG, "invokeOnLoadFail", exception);
        if (mListener != null) {
            mListener.onPubnativePlacementLoadFail(this, exception);
        }
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeConfigManager.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onConfigLoaded(PubnativeConfigModel config) {

        Log.v(TAG, "onConfigLoaded");
        loadPlacement(config);
    }
}
