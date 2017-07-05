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

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.api.core.request.PNAPIRequest;
import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubnativeLibraryNetworkAdapter extends PNAdapter implements PNAPIRequest.Listener {

    private static final String TAG = PubnativeLibraryNetworkAdapter.class.getSimpleName();

    private Context mContext = null;

    public PubnativeLibraryNetworkAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeLibraryNetworkAdapter methods
    //==============================================================================================
    protected void request(Context context) {
        if (context == null || mData == null) {
            invokeLoadFail(PNException.ADAPTER_MISSING_DATA);
        } else {
            mContext = context;
            createRequest(context);
        }
    }

    //==============================================================================================
    // PubnativeLibraryNetworkRequestAdapter methods
    //==============================================================================================
    protected void createRequest(Context context) {
        PNAPIRequest request = new PNAPIRequest();
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
        // Add targetting
        if(PNSettings.targeting != null) {
            Map <String, String> targeting = PNSettings.targeting.toDictionary();
            for (String key : targeting.keySet()) {
                String value = targeting.get(key);
                request.setParameter(key, value);
            }
        }
        request.setTestMode(PNSettings.isTestModeEnabled);
        request.setCoppaMode(PNSettings.isCoppaModeEnabled);
        request.start(context, this);
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PNAPIRequest.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPNAPIRequestFinish(PNAPIRequest request, List<PNAPIAdModel> ads) {
        PNAPIAdModel ad = null;
        Map<String, String> extras = new HashMap<String, String>();
        if (ads != null && ads.size() > 0) {
            ad = ads.get(0);
        }
        if (mIsCPICacheEnabled && (ad == null || ad.isRevenueModelCPA())) {
            PNAPIAdModel cachedAd = PubnativeLibraryCPICache.get(mContext);
            if (cachedAd != null) {
                // CREATE EXTRAS WITH ZONEID PARAMETER FROM NETWORK ONLY IF CACHED
                String zoneId = (String) mData.get(PNAPIRequest.Parameters.ZONE_ID);
                if (!TextUtils.isEmpty(zoneId)) {
                    extras.put(PNAPIRequest.Parameters.ZONE_ID, zoneId);
                }

                ad = cachedAd;
            }
        }
        if (ad == null) {
            invokeLoadFinish(null);
        } else {
            invokeLoadFinish(new PubnativeLibraryAdModel(mContext, ad, extras));
        }
    }

    @Override
    public void onPNAPIRequestFail(PNAPIRequest request, Exception ex) {
        PNAPIAdModel ad = PubnativeLibraryCPICache.get(mContext);
        if (ad == null) {
            invokeLoadFail(ex);
        } else {
            invokeLoadFinish(new PubnativeLibraryAdModel(mContext, ad));
        }
    }
}
