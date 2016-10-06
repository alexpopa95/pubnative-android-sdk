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

package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.util.Log;

import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;
import net.pubnative.mediation.adapter.model.PubnativeLibraryAdModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.List;
import java.util.Map;

public class PubnativeLibraryNetworkRequestAdapter extends PubnativeNetworkRequestAdapter
        implements PubnativeRequest.Listener {

    private static String TAG = PubnativeLibraryNetworkRequestAdapter.class.getSimpleName();

    public PubnativeLibraryNetworkRequestAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeNetworkAdapter methods
    //==============================================================================================
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context == null || mData == null) {
            invokeFailed(PubnativeException.ADAPTER_MISSING_DATA);
        } else {
            createRequest(context);
        }
    }

    //==============================================================================================
    // PubnativeLibraryNetworkRequestAdapter methods
    //==============================================================================================
    protected void createRequest(Context context) {

        Log.v(TAG, "createRequest");
        PubnativeRequest request = new PubnativeRequest();
        // We add all params
        for (Object key : mData.keySet()) {
            Object value = mData.get(key);
            request.setParameter((String) key, value.toString());
        }
        // Add extras
        if (mExtras != null) {
            for (String key : mExtras.keySet()) {
                request.setParameter(key, mExtras.get(key));
            }
        }
        if (mTargeting != null) {
            Map targeting = mTargeting.toDictionary();
            for (Object key : targeting.keySet()) {
                String value = (String) targeting.get(key);
                request.setParameter((String) key, value);
            }
        }
        request.start(context, this);
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

        Log.v(TAG, "onPubnativeRequestSuccess");

        net.pubnative.mediation.request.model.PubnativeAdModel wrapAd = null;
        if (ads != null && ads.size() > 0) {
            wrapAd = new PubnativeLibraryAdModel(ads.get(0));
            wrapAd.setLinkCaching(mUseCaching);
        }
        invokeLoaded(wrapAd);
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

        Log.v(TAG, "onPubnativeRequestFailed: " + ex);
        invokeFailed(ex);
    }
}
