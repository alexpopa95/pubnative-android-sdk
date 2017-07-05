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

package net.pubnative.sdk.core.adapter.request;

import android.util.Log;

import net.pubnative.sdk.core.config.model.PNNetworkModel;

import java.lang.reflect.Constructor;
import java.util.Map;

public class PNAdapterFactory {

    private static final String TAG     = PNAdapterFactory.class.getSimpleName();
    private static final String PACKAGE = PNAdapterFactory.class.getPackage().getName();

    /**
     * Creates a new hub instance by using the values passed in using model
     *
     * @param model network model that contains the values needed for creating the hub
     * @return instance of PubnativeNetworkAdapterHub if created, else null
     */
    public static PNAdapter create(PNNetworkModel model) {
        PNAdapter result = null;
        try {
            Class<?> hubClass = Class.forName(getPackageName(model.adapter));
            Constructor<?> hubConstructor = hubClass.getConstructor(Map.class);
            result = (PNAdapter) hubConstructor.newInstance(model.params);
        } catch (Exception e) {
            // Don't crash, just return null, log error and return null
            Log.e(TAG, "Error creating adapter: " + e);
        } catch (Error e) {
            // Don't crash, just return null, log error and return null
            Log.e(TAG, "Error creating adapter: " + e);
        }
        return result;
    }

    protected static String getPackageName(String classSimpleName) {
        String result = null;
        if (classSimpleName != null) {
            result = PACKAGE + "." + classSimpleName;
        }
        return result;
    }
}
