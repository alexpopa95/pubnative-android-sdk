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

import com.google.gson.Gson;

import net.pubnative.mediation.config.model.PubnativeConfigAPIResponseModel;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.InputStream;

public class PubnativeConfigTestUtils {

    /**
     * This method should be used to set up a test getConfig file stored under /src/test/resources
     *
     * @param context   valid Context object
     * @param filename  name of the resource file
     * @param app_token valid app token obtained from pubnative
     */
    public static void setTestConfig(Context context, String filename, String app_token) {
        InputStream configStream = PubnativeConfigTestUtils.class.getResourceAsStream("/configs/" + filename);
        String      configString = PubnativeStringUtils.readStringFromInputStream(configStream);
        try {
            PubnativeConfigModel model = new Gson().fromJson(configString, PubnativeConfigModel.class);
            PubnativeConfigManager.updateConfig(context, app_token, model);
        } catch (Exception e) {
            // Do nothing
        }
    }

    /**
     * This method simulates the config download using a stored config json file inside resources.
     *
     * @param fileName name of the resource file that contains config json
     * @return valid JSON of the api response with a status "OK"
     */
    public static String getConfigApiResponseJsonFromResource(String fileName) {
        Gson        gson         = new Gson();
        InputStream configStream = PubnativeConfigTestUtils.class.getResourceAsStream("/configs/" + fileName);
        String      configString = PubnativeStringUtils.readStringFromInputStream(configStream);

        PubnativeConfigAPIResponseModel apiResponseModel = new PubnativeConfigAPIResponseModel();
        apiResponseModel.status = PubnativeConfigAPIResponseModel.Status.OK;
        apiResponseModel.config = gson.fromJson(configString, PubnativeConfigModel.class);

        return gson.toJson(apiResponseModel);
    }
}
