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

package net.pubnative.sdk.core.config.model;

import android.text.TextUtils;

import java.util.Map;

public class PNConfigModel {

    private static final String TAG = PNConfigModel.class.getSimpleName();

    public Map<String, Object>           globals;
    public Map<String, PNNetworkModel>   networks;
    public Map<String, String>           request_params;
    public Map<String, PNPlacementModel> placements;
    public Map<String, String>           ad_cache_params;

    //==============================================================================================
    // PNConfigModel.GLOBAL
    //==============================================================================================
    public interface GLOBAL {
        String REFRESH           = "refresh";
        String IMPRESSION_BEACON = "impression_beacon";
        String CLICK_BEACON      = "click_beacon";
        String REQUEST_BEACON    = "request_beacon";
        String CONFIG_URL        = "config_url";
        String RESCUE_BEACON     = "recovered_network_url";
        String AD_CACHE_MIN_SIZE = "ad_cache_min_size";
        String REFRESH_AD_CACHE  = "refresh_ad_cache";
        String CPA_CACHE         = "cpa_cache";
    }

    //==============================================================================================
    // PNConfigModel
    //==============================================================================================
    public boolean isEmpty() {
        return networks == null
                || placements == null
                || networks.size() == 0
                || placements.size() == 0;
    }

    public Object getGlobal(String globalKey) {
        Object result = null;
        if (!TextUtils.isEmpty(globalKey)
                && globals != null) {
            result = globals.get(globalKey);
        }
        return result;
    }

    public PNPlacementModel getPlacement(String placementID) {
        PNPlacementModel result = null;
        if (!TextUtils.isEmpty(placementID)
                && placements != null) {
            result = placements.get(placementID);
        }
        return result;
    }

    public PNNetworkModel getNetwork(String networkID) {
        PNNetworkModel result = null;
        if (!TextUtils.isEmpty(networkID)
                && networks != null) {
            result = networks.get(networkID);
        }
        return result;
    }
}
