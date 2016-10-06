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
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.model.AdMobNativeAppInstallAdModel;

import java.util.Map;

public class AdMobNetworkAdapter extends PubnativeNetworkAdapter
        implements NativeAppInstallAd.OnAppInstallAdLoadedListener {

    public static final    String TAG           = AdMobNetworkAdapter.class.getSimpleName();
    protected static final String ADMOB_UNIT_ID = "ad_unit_id";

    /**
     * Creates a new instance of PubnativeNetworkAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public AdMobNetworkAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeNetworkAdapter methods
    //==============================================================================================
    @Override
    public void request(Context context) {

        Log.v(TAG, "request");
        if (context == null || mData == null) {
            invokeFailed(new Exception(TAG + " - error: invalid arguments"));
        } else {
            String unitID = (String) mData.get(ADMOB_UNIT_ID);
            if (TextUtils.isEmpty(unitID)) {
                invokeFailed(new Exception(TAG + " - error: missing argument unit_id"));
            } else {
                createRequest(context, unitID);
            }
        }
    }

    //==============================================================================================
    // AdMobNetworkAdapter methods
    //==============================================================================================
    protected void createRequest(Context context, String unitId) {

        Log.v(TAG, "createRequest");
        AdLoader adLoader = new AdLoader.Builder(context, unitId)
                .forAppInstallAd(this)
                .withAdListener(new NativeAdListener())
                .build();
        adLoader.loadAd(getAdRequest());
    }

    protected AdRequest getAdRequest() {

        return new AdRequest.Builder()
                            .addTestDevice("5169CE50DEC340749C2DD17F6B6BCEBB")
                            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .build();
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    protected class NativeAdListener extends com.google.android.gms.ads.AdListener {

        @Override
        public void onAdFailedToLoad(int var1) {

            Log.v(TAG, "onAdFailedToLoad");
            invokeFailed(new Exception(TAG + " - error loading the ad with code:" + String.valueOf(var1)));
        }
    }
    // NativeAppInstallAd.OnAppInstallAdLoadedListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {

        Log.v(TAG, "onAppInstallAdLoaded");
        AdMobNativeAppInstallAdModel wrapper = new AdMobNativeAppInstallAdModel(nativeAppInstallAd);
        invokeLoaded(wrapper);
    }
}
