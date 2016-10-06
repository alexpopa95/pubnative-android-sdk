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

package net.pubnative.mediation.config.model;

import android.util.Log;

import java.util.Map;

public class PubnativeConfigModel {

    private static final String TAG = PubnativeConfigModel.class.getSimpleName();
    public Map<String, Object>                  globals;
    public Map<String, PubnativeNetworkModel>   networks;
    public Map<String, String>                  request_params;
    public Map<String, PubnativePlacementModel> placements;

    //==============================================================================================
    // PubnativeConfigModel.GLOBAL
    //==============================================================================================
    public interface GLOBAL {
        String REFRESH           = "refresh";
        String IMPRESSION_BEACON = "impression_beacon";
        String CLICK_BEACON      = "click_beacon";
        String REQUEST_BEACON    = "request_beacon";
        String CONFIG_URL        = "config_url";
    }

    //==============================================================================================
    // PubnativeConfigModel
    //==============================================================================================
    public boolean isEmpty() {
        Log.v(TAG, "isEmpty");
        return networks == null
               || placements == null
               || networks.size() == 0
               || placements.size() == 0;
    }

    public Object getGlobal(String globalKey) {

        Log.v(TAG, "getGlobal: " + globalKey);
        Object result = null;
        if (globals != null) {
            result = globals.get(globalKey);
        }
        return result;
    }

    public PubnativePlacementModel getPlacement(String placementID) {

        Log.v(TAG, "getPlacement: " + placementID);
        PubnativePlacementModel result = null;
        if (placements != null) {
            result = placements.get(placementID);
        }
        return result;
    }

    public PubnativeNetworkModel getNetwork(String networkID) {

        Log.v(TAG, "getNetwork: " + networkID);
        PubnativeNetworkModel result = null;
        if (networks != null) {
            result = networks.get(networkID);
        }
        return result;
    }

    public PubnativePriorityRuleModel getPriorityRule(String placementID, int index) {

        Log.v(TAG, "getPriorityRule: " + placementID);
        PubnativePriorityRuleModel result = null;
        PubnativePlacementModel placement = getPlacement(placementID);
        if (placement != null) {
            result = placement.getPriorityRule(index);
        }
        return result;
    }
}
