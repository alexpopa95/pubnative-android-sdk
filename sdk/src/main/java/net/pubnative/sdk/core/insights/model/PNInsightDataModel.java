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
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.config.model.PNPriorityRuleModel;
import net.pubnative.sdk.core.request.PNAdTargetingModel;
import net.pubnative.sdk.core.utils.PNDeviceUtils;

import java.util.ArrayList;
import java.util.List;

public class PNInsightDataModel {

    private static final String TAG = PNInsightDataModel.class.getName();

    // Tracking info
    public String                      network;
    public List<String>                attempted_networks;
    public List<String>                unreachable_networks;
    public List<Integer>               delivery_segment_ids;
    public List<PNInsightNetworkModel> networks;
    public String                      placement_name;
    public String                      pub_app_version;
    public String                      pub_app_bundle_id;
    public String                      os_version;
    public String                      sdk_version;
    public String                      user_uid; // android advertiser id
    public String                      connection_type; //type "wifi" or "cellular"
    public String                      device_name;
    public String                      ad_format_code;
    public String                      creative_url; // Creative selected from the ad_format_code value of the config
    public Boolean                     video_start;
    public Boolean                     video_complete;
    public int                         retry;
    public String                      retry_error;
    public String                      coppa;
    // User info
    public Integer                     age;
    public String                      education;
    public List<String>                interests;
    public String                      gender;
    public Boolean                     iap; // In app purchase enabled, Just open it for the user to fill
    public Float                       iap_total; // In app purchase total spent, just open for the user to fill
    public Long                        generated_at;
    public Long                        response_time;

    public PNInsightDataModel(Context context) {
        fillDefaults(context);
    }

    //==============================================================================================
    // Object
    //==============================================================================================

    @Override
    public boolean equals(Object object) {

        Log.v(TAG, "equals");
        if (this == object) {
            // return true immediately if both objects are identical.
            return true;
        }
        if (!(object instanceof PNInsightDataModel)) {
            // return immediately if the object is of another class,
            // this is to avoid possible class cast exception in next line.
            return false;
        }
        PNInsightDataModel dataModel = (PNInsightDataModel) object;
        boolean result = isEqual(network, dataModel.network);
        if (result) {
            result = isEqual(attempted_networks, dataModel.attempted_networks);
        }
        if (result) {
            result = isEqual(placement_name, dataModel.placement_name);
        }
        if (result) {
            result = isEqual(pub_app_version, dataModel.pub_app_version);
        }
        if (result) {
            result = isEqual(pub_app_bundle_id, dataModel.pub_app_bundle_id);
        }
        if (result) {
            result = isEqual(os_version, dataModel.os_version);
        }
        if (result) {
            result = isEqual(sdk_version, dataModel.sdk_version);
        }
        if (result) {
            result = isEqual(user_uid, dataModel.user_uid);
        }
        if (result) {
            result = isEqual(connection_type, dataModel.connection_type);
        }
        if (result) {
            result = isEqual(device_name, dataModel.device_name);
        }
        if (result) {
            result = isEqual(ad_format_code, dataModel.ad_format_code);
        }
        if (result) {
            result = isEqual(creative_url, dataModel.creative_url);
        }
        if (result) {
            result = isEqual(video_start, dataModel.video_start);
        }
        if (result) {
            result = isEqual(video_complete, dataModel.video_complete);
        }

        if (result) {
            result = isEqual(coppa, dataModel.coppa);
        }
        // user info
        if (result) {
            result = isEqual(age, dataModel.age);
        }
        if (result) {
            result = isEqual(education, dataModel.education);
        }
        if (result) {
            result = isEqual(interests, dataModel.interests);
        }
        if (result) {
            result = isEqual(gender, dataModel.gender);
        }
        if (result) {
            result = isEqual(iap, dataModel.iap);
        }
        if (result) {
            result = isEqual(iap_total, dataModel.iap_total);
        }
        return result;
    }

    //==============================================================================================
    // PNInsightDataModel
    //==============================================================================================
    // Private
    //----------------------------------------------------------------------------------------------

    /**
     * This method takes two Objects "first" and "second" as arguments and does a comparison.
     * Returns true if they are equal.
     * Returns false if they are not equal or not comparable.
     */
    private boolean isEqual(Object first, Object second) {
        return (first != null) ? first.equals(second) : second == null;
    }

    //----------------------------------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------------------------------

    /**
     * Adds network insight data to the insight
     *
     * @param priorityRuleModel valid PNPriorityRuleModel object
     * @param responseTime      valid long in milliseconds
     * @param crashModel        valid PNInsightCrashModel or null
     */
    public void addNetwork(PNPriorityRuleModel priorityRuleModel, long responseTime, PNInsightCrashModel crashModel) {
        if (networks == null) {
            networks = new ArrayList<PNInsightNetworkModel>();
        }
        PNInsightNetworkModel networkModel = new PNInsightNetworkModel();
        if (priorityRuleModel != null) {
            networkModel.code = priorityRuleModel.network_code;
            networkModel.priority_rule_id = priorityRuleModel.id;
            networkModel.priority_segment_ids = priorityRuleModel.segment_ids;
        }
        networkModel.response_time = responseTime;
        if (crashModel != null) {
            networkModel.crash_report = crashModel;
        }
        networks.add(networkModel);
    }

    /**
     * Adds a network code to the attempted_networks list
     *
     * @param network valid String
     */
    public void addAttemptedNetwork(String network) {
        if (!TextUtils.isEmpty(network)) {
            if (attempted_networks == null) {
                attempted_networks = new ArrayList<String>();
            }
            attempted_networks.add(network);
        }
    }

    /**
     * Adds a network code to the unreachable_networks list
     *
     * @param network valid String
     */
    public void addUnreachableNetwork(String network) {
        if (!TextUtils.isEmpty(network)) {
            if (unreachable_networks == null) {
                unreachable_networks = new ArrayList<String>();
            }
            unreachable_networks.add(network);
        }
    }

    /**
     * Clear all related request tracking insight data
     */
    public void reset() {
        retry = 0;
        retry_error = null;
        network = null;
        networks = null;
        delivery_segment_ids = null;
        attempted_networks = null;
        unreachable_networks = null;
        generated_at = null;
    }

    /**
     * Fills insight data model with default available data.
     *
     * @param context valid Context object
     */
    protected void fillDefaults(Context context) {
        if (context != null) {

            retry = 0;
            os_version = PNSettings.osVersion;
            device_name = PNSettings.deviceName;
            sdk_version = PNSettings.sdkVersion;
            pub_app_version = PNSettings.appVersion;
            pub_app_bundle_id = PNSettings.appBundleID;
            PNDeviceUtils.ConnectionType connectionType = PNDeviceUtils.getConnectionType(context);
            switch (connectionType) {
                case CELLULAR:  connection_type = "cellular";   break;
                case WIFI:      connection_type = "wifi";       break;
            }
        }
    }

    public void setTargeting(PNAdTargetingModel targeting) {
        age = targeting.age;
        education = targeting.education;
        interests = targeting.interests;
        gender = targeting.gender;
        iap = targeting.iap;
        iap_total = targeting.iap_total;
    }
}
