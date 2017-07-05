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

package net.pubnative.sdk.demo;

import android.text.TextUtils;

import net.pubnative.sdk.core.request.PNAdTargetingModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {

    public static final String DEFAULT_DEV_APP_TOKEN = "7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c";
    public static final String DEFAULT_QA_APP_TOKEN  = "de7a474dabc79eac1400b62bd6f6dc408f27b92a863f58db3d8584b2bd25f91c";

    public static    List<String>              placements          = new ArrayList<>();
    protected static List<Map<String, String>> targeting           = new ArrayList<>();
    public static    boolean                   isAssetCacheEnabled = true;
    public static    boolean                   isDevModeEnabled    = true;
    public static    String                    appToken            = isDevModeEnabled ? DEFAULT_DEV_APP_TOKEN : DEFAULT_QA_APP_TOKEN;

    public static void reset() {

        placements = new ArrayList<>();
        targeting = new ArrayList<>();
        isAssetCacheEnabled = true;
        isDevModeEnabled = true;
        appToken = isDevModeEnabled ? DEFAULT_DEV_APP_TOKEN : DEFAULT_QA_APP_TOKEN;
    }

    public static PNAdTargetingModel getTargeting() {

        PNAdTargetingModel adTargetingModel = new PNAdTargetingModel();

        Map<String, String> extras = new HashMap<>();
        for (Map<String, String> targetingItem : Settings.targeting) {
            extras.putAll(targetingItem);
        }

        String age = extras.get(PNAdTargetingModel.Keys.age);
        String gender = extras.get(PNAdTargetingModel.Keys.gender);
        String education = extras.get(PNAdTargetingModel.Keys.education);
        String interests = extras.get(PNAdTargetingModel.Keys.interests);
        String iap = extras.get(PNAdTargetingModel.Keys.iap);
        String iap_total = extras.get(PNAdTargetingModel.Keys.iap_total);

        if (!TextUtils.isEmpty(age)) {
            adTargetingModel.age = Integer.valueOf(age);
        }

        adTargetingModel.gender = gender;
        adTargetingModel.education = education;

        if (!TextUtils.isEmpty(interests)) {
            String[] interestsArray = interests.split(",");
            for (int i = 0; i < interestsArray.length; i++) {
                adTargetingModel.addInterest(interestsArray[i]);
            }
        }

        if (!TextUtils.isEmpty(iap)) {
            adTargetingModel.iap = Boolean.valueOf(iap);
        }

        if (!TextUtils.isEmpty(iap_total)) {
            adTargetingModel.iap_total = Float.valueOf(iap_total);
        }

        return adTargetingModel;
    }
}
