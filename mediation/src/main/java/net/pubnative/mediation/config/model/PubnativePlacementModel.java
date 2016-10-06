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

import java.util.List;

public class PubnativePlacementModel {

    private static final String TAG = PubnativePlacementModel.class.getSimpleName();
    public String                           ad_format_code;
    public List<PubnativePriorityRuleModel> priority_rules;
    public PubnativeDeliveryRuleModel       delivery_rule;

    //==============================================================================================
    // PubnativePlacementModel.AdFormatCode
    //==============================================================================================
    public interface AdFormatCode {

        String NATIVE_ICON   = "icon";
        String NATIVE_BANNER = "banner";
        String VIDEO         = "video";
    }

    //==============================================================================================
    // PubnativePlacementModel
    //==============================================================================================
    public PubnativePriorityRuleModel getPriorityRule(int index) {

        Log.v(TAG, "getPriorityRule: " + index);
        PubnativePriorityRuleModel result = null;
        if (this.priority_rules != null && this.priority_rules.size() > index) {
            result = this.priority_rules.get(index);
        }
        return result;
    }
}
