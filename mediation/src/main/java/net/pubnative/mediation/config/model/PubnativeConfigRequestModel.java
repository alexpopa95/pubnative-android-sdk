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

import android.content.Context;

import net.pubnative.mediation.config.PubnativeConfigManager;

import java.util.HashMap;
import java.util.Map;

public class PubnativeConfigRequestModel {

    public Context                         context;
    public PubnativeConfigManager.Listener listener;
    public Map<String, String>             parameters;

    public void setAppToken(String appToken) {
        if(parameters == null) {
            parameters = new HashMap<String, String>();
        }
        parameters.put("app_token", appToken);
    }
    public String getAppToken() {
        return parameters.get("app_token");
    }
    //==============================================================================================
    // PubnativeConfigRequestModel
    //==============================================================================================
}
