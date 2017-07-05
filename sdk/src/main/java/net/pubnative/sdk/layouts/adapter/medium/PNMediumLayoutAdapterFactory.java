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

package net.pubnative.sdk.layouts.adapter.medium;

import android.util.Log;

import net.pubnative.sdk.layouts.adapter.PNLayoutAdapter;
import net.pubnative.sdk.core.config.model.PNNetworkModel;

import java.lang.reflect.Constructor;

public class PNMediumLayoutAdapterFactory {

    private static final String TAG     = PNMediumLayoutAdapterFactory.class.getSimpleName();
    private static final String PACKAGE = PNMediumLayoutAdapterFactory.class.getPackage().getName();

    public static PNLayoutAdapter getAdapter(PNNetworkModel model) {
        PNLayoutAdapter result = null;
        try {
            Class<?> adapterClass = Class.forName(getPackageName(model.adapter));
            Constructor<?> adapterConstructor = adapterClass.getConstructor();
            result = (PNLayoutAdapter) adapterConstructor.newInstance();
            if (result != null) {
                result.setNetworkData(model.params);
            }
        } catch (Exception e) {
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
