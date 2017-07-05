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

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PNAdTargetingModel {

    public Integer      age;
    public String       education;
    public List<String> interests;
    public String       gender;
    public Boolean      iap; // In app purchase enabled, Just open it for the user to fill
    public Float        iap_total; // In app purchase total spent, just open for the user to fill

    public interface Keys {

        String age = "age";
        String education = "education";
        String interests = "interests";
        String gender = "gender";
        String iap = "iap";
        String iap_total = "iap_total";
    }

    public void addInterest(String interest) {

        if (!TextUtils.isEmpty(interest)) {
            if (interests == null) {
                interests = new ArrayList<String>();
            }
            interests.add(interest);
        }
    }

    public Map toDictionary() {

        Map result = new HashMap();
        if (age != null) {
            result.put(Keys.age, String.valueOf(age));
        }
        if (!TextUtils.isEmpty(education)) {
            result.put(Keys.education, education);
        }
        if (interests != null && interests.size() > 0) {
            result.put(Keys.interests, TextUtils.join(",", interests.toArray()));
        }
        if (!TextUtils.isEmpty(gender)) {
            result.put(Keys.gender, gender);
        }
        return result;
    }

    public Map toDictionaryWithIap() {

        Map result = toDictionary();

        if (iap != null) {
            result.put(Keys.iap, String.valueOf(iap));
        }
        if (iap_total != null) {
            result.put(Keys.iap_total, String.valueOf(iap_total));
        }
        return result;
    }
}
