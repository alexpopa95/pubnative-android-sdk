package net.pubnative.mediation.insights.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.insights.PubnativeInsightsManager;
import net.pubnative.mediation.request.model.PubnativeAdTargetingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by davidmartin on 04/05/16.
 */
public class PubnativeInsightModel {

    private static final String TAG = PubnativeInsightModel.class.getSimpleName();
    protected String                    mRequestInsightURL;
    protected String                    mImpressionInsightURL;
    protected String                    mClickInsightURL;
    protected Context                   mContext;
    protected PubnativeInsightDataModel mData;
    protected Map<String, String>       mExtras;

    public interface Listener {

        void onInsightLoaded();
    }

    public PubnativeInsightModel(Context context) {

        mContext = context;
        mData = new PubnativeInsightDataModel();
        mData.fillDefaults(mContext);
    }

    /**
     * Sets the placement name for this insight
     *
     * @param placement valid placement name
     */
    public void setPlacement(String placement) {

        mData.placement_name = placement;
    }

    /**
     * Sets the segments list
     *
     * @param deliverySegments valid segments list
     */
    public void setSegments(List<Integer> deliverySegments) {

        mData.delivery_segment_ids = deliverySegments;
    }

    public void setAdFormatCode(String adFormatCode) {

        mData.ad_format_code = adFormatCode;
    }

    public void setUserId(String uid) {

        mData.user_uid = uid;
    }
    //==============================================================================================
    // Tracking data
    //==============================================================================================

    /**
     * Sets the data targeting values
     *
     * @param targeting valid targeting model
     */
    public void setTargeting(PubnativeAdTargetingModel targeting) {

        Log.v(TAG, "setTargeting");
        if (targeting != null) {
            mData.setTargetting(targeting);
        }
    }

    /**
     * Gets the ad format code
     *
     * @return valid ad format code string
     */
    public String getAdFormat() {

        Log.v(TAG, "getAdFormat");
        return mData.ad_format_code;
    }

    /**
     * Sets the creative url of the data
     *
     * @param url valid url string for the creative
     */
    public void setCreativeUrl(String url) {

        Log.v(TAG, "setCreativeUrl");
        mData.creative_url = url;
    }

    /**
     * Adds extra fields to be added in the insight query string
     *
     * @param extras dictionary with extras key and values
     */
    public void addExtras(Map<String, String> extras) {

        Log.v(TAG, "addExtra");
        if(extras != null) {
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

        Log.v(TAG, "addExtra");
        if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
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
     */
    public void setInsightURLs(String requestUrl, String impressionUrl, String clickUrl) {

        Log.v(TAG, "setInsightURLs");
        mRequestInsightURL = requestUrl;
        mImpressionInsightURL = impressionUrl;
        mClickInsightURL = clickUrl;
    }
    //==============================================================================================
    // Tracking
    //==============================================================================================

    /**
     * Sets the current network as unreachable due to the passed exception
     * @param priorityRuleModel valid model
     * @param responseTime time in milliseconds that this network took to fail
     * @param exception exception with the details
     */
    public void trackUnreachableNetwork(PubnativePriorityRuleModel priorityRuleModel, long responseTime, Exception exception) {

        Log.v(TAG, "trackUnreachableNetwork", exception);
        PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
        crashModel.error = exception.getMessage();
        crashModel.details = exception.toString();
        mData.addUnreachableNetwork(priorityRuleModel.network_code);
        mData.addNetwork(priorityRuleModel, responseTime, crashModel);
    }

    /**
     * Sets the current network as attempted but failed
     * @param priorityRuleModel valid model
     * @param responseTime time in milliseconds that this attempt took to fail
     * @param exception exception with details
     */
    public void trackAttemptedNetwork(PubnativePriorityRuleModel priorityRuleModel, long responseTime, Exception exception) {

        Log.v(TAG, "trackAttemptedNetwork", exception);
        PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
        crashModel.error = exception.getMessage();
        crashModel.details = exception.toString();
        mData.addAttemptedNetwork(priorityRuleModel.network_code);
        mData.addNetwork(priorityRuleModel, responseTime, crashModel);
    }

    /**
     * Sets the current network as succeded
     * @param priorityRuleModel valid model
     * @param responseTime time in milliseconds that it took this request to be success
     */
    public void trackSuccededNetwork(PubnativePriorityRuleModel priorityRuleModel, long responseTime) {

        Log.v(TAG, "trackSuccededNetwork");
        mData.network = priorityRuleModel.network_code;
        mData.addNetwork(priorityRuleModel, responseTime, null);
        PubnativeDeliveryManager.updatePacingCalendar(mData.placement_name);
    }

    /**
     * Sends request insight data
     */
    public void sendRequestInsight() {

        Log.v(TAG, "sendRequestInsight");
        PubnativeInsightsManager.trackData(mContext, mRequestInsightURL, mExtras, mData);
    }

    /**
     * Sends impression insight data
     */
    public void sendImpressionInsight() {

        Log.v(TAG, "sendImpressionInsight");
        PubnativeDeliveryManager.logImpression(mContext, mData.placement_name);
        PubnativeInsightsManager.trackData(mContext, mImpressionInsightURL, mExtras, mData);
    }

    /**
     * Sends a request insight data
     */
    public void sendClickInsight() {

        Log.v(TAG, "sendClickInsight");
        PubnativeInsightsManager.trackData(mContext, mClickInsightURL, mExtras, mData);
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    public void invokeOnLoaded(Listener listener) {

        Log.v(TAG, "invokeOnLoaded");
        if (listener == null) {
            listener.onInsightLoaded();
        }
    }
}
