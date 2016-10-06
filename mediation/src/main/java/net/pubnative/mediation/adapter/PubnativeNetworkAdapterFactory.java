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

package net.pubnative.mediation.adapter;

import android.util.Log;

import net.pubnative.mediation.config.model.PubnativeNetworkModel;

import java.lang.reflect.Constructor;
import java.util.Map;

public class PubnativeNetworkAdapterFactory {

    private static         String TAG             = PubnativeNetworkAdapterFactory.class.getSimpleName();
    protected final static String NETWORK_PACKAGE = "net.pubnative.mediation.adapter.network";

    /**
     * Creates a new network adapter instance by using the values passed in using model
     *
     * @param model network model that contains the values needed for creating a network adapter
     *
     * @return instance of PubnativeNetworkAdapter if created, else null
     */
    public static PubnativeNetworkAdapter createAdapter(PubnativeNetworkModel model) {

        Log.v(TAG, "createAdapter");
        PubnativeNetworkAdapter result = null;
        try {
            Class<?> networkClass = Class.forName(getPackageName(model.adapter));
            Constructor<?> constructor = networkClass.getConstructor(Map.class);
            result = (PubnativeNetworkAdapter) constructor.newInstance(model.params);
        } catch (Exception e) {
            // Don't crash, just return null, log error and return null
            Log.e(TAG, "Pubnative - Error creating adapter: " + e);
        }
        return result;
    }

    protected static String getPackageName(String classSimpleName) {

        Log.v(TAG, "getPackageName");
        String result = null;
        if (classSimpleName != null) {
            result = NETWORK_PACKAGE + "." + classSimpleName;
        }
        return result;
    }
}
