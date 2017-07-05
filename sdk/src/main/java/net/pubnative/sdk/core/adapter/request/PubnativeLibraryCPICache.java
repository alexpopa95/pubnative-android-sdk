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
import android.util.Log;

import net.pubnative.api.core.request.PNAPIRequest;
import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.config.model.PNConfigModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class PubnativeLibraryCPICache implements PNAPIRequest.Listener {

    private static final String TAG = PubnativeLibraryCPICache.class.getSimpleName();

    protected static List<CacheItem>     sAdQueue           = new ArrayList<CacheItem>();
    protected static boolean             sIsRequesting      = false;
    protected static Map<String, String> sRequestParameters = new HashMap<String, String>();
    protected static Listener            sListener          = null;
    protected static int                 sCacheMinSize      = 2;
    protected static boolean             sIsCacheEnabled    = false;
    protected static int                 sCacheThreshold    = 60; // In minutes

    public class CacheItem {

        public PNAPIAdModel ad        = null;
        public long         timestamp = -1;

        public CacheItem(PNAPIAdModel ad) {
            this.ad = ad;
            this.timestamp = System.currentTimeMillis();
        }
    }

    //==============================================================================================
    // LoadListener
    //==============================================================================================
    public interface Listener {

        /**
         * Called when the PubnativeLibraryCPICache has just finished loading
         */
        void onPubnativeCpiCacheLoadFinish();
    }

    //==============================================================================================
    // SINGLETON
    //==============================================================================================
    private static PubnativeLibraryCPICache sInstance;

    private PubnativeLibraryCPICache() {
    }

    protected synchronized static PubnativeLibraryCPICache getInstance() {
        if (sInstance == null) {
            sInstance = new PubnativeLibraryCPICache();
        }
        return sInstance;
    }

    //==============================================================================================
    // Public
    //==============================================================================================
    public static void init(Context context, String appToken, PNConfigModel config, PubnativeLibraryCPICache.Listener listener) {
        sListener = listener;
        if (context == null) {
            Log.w(TAG, "context is null or empty and required, dropping this call");
            getInstance().invokeLoadFinish();
        } else if (TextUtils.isEmpty(appToken)) {
            Log.w(TAG, "app token is null or empty and required, dropping this call");
            getInstance().invokeLoadFinish();
        } else if (config == null || config.isEmpty()) {
            Log.w(TAG, "config is null or empty and required, dropping this call");
            getInstance().invokeLoadFinish();
        } else if (getInstance().isCacheSizeCritical()) {
            // 1. PARAMETERS
            sRequestParameters.put(PNAPIRequest.Parameters.APP_TOKEN, appToken);
            if (config.ad_cache_params != null) {
                sRequestParameters.putAll(config.ad_cache_params);
            }
            // 2. CACHE MIN SIZE
            if (config.globals.containsKey(PNConfigModel.GLOBAL.AD_CACHE_MIN_SIZE)) {
                Double minSize = (Double) config.getGlobal(PNConfigModel.GLOBAL.AD_CACHE_MIN_SIZE);
                sCacheMinSize = minSize.intValue();
            }
            // 3. CACHE VALID THRESHOLD
            if (config.globals.containsKey(PNConfigModel.GLOBAL.REFRESH_AD_CACHE)) {
                Double threshold = (Double) config.getGlobal(PNConfigModel.GLOBAL.REFRESH_AD_CACHE);
                sCacheThreshold = threshold.intValue();
            }
            // 4. CACHE ENABLE/DISABLE
            if (config.globals.containsKey(PNConfigModel.GLOBAL.CPA_CACHE)) {
                Boolean enabled = (Boolean) config.getGlobal(PNConfigModel.GLOBAL.CPA_CACHE);
                sIsCacheEnabled = enabled;
            }
            getInstance().request(context);
        } else {
            getInstance().invokeLoadFinish();
        }
    }

    public synchronized static PNAPIAdModel get(Context context) {
        PNAPIAdModel result;
        // 1. DEQUEUE
        result = getInstance().dequeue();
        // 2. CHECK IF WE NEED TO REQUEST MORE ADS
        if (getInstance().isCacheSizeCritical()) {
            getInstance().request(context);
        }
        return result;
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    private void request(Context context) {
        if (context == null) {
            Log.w(TAG, "context is nil and required, dropping this call");
        } else if (sIsRequesting) {
            Log.v(TAG, "currently requesting, dropping this call");
        } else if (sIsCacheEnabled) {
            sIsRequesting = true;
            PNAPIRequest request = new PNAPIRequest();
            for (String key : sRequestParameters.keySet()) {
                String value = sRequestParameters.get(key);
                request.setParameter(key, value);
            }

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
        } else {
            invokeLoadFinish();
        }
    }

    private void invokeLoadFinish() {
        sIsRequesting = false;
        Listener listener = sListener;
        sListener = null;
        if (listener != null) {
            listener.onPubnativeCpiCacheLoadFinish();
        }
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================
    protected boolean isCacheSizeCritical() {
        return sAdQueue.size() <= sCacheMinSize;
    }

    protected void enqueue(List<PNAPIAdModel> ads) {
        // Refill fetchAssets with received response from server
        for (PNAPIAdModel ad : ads) {
            ad.setUseClickCaching(true);
            ad.fetch();
            enqueue(ad);
        }
    }

    protected void enqueue(PNAPIAdModel ad) {
        // Refill fetchAssets with received response from server
        sAdQueue.add(new CacheItem(ad));
    }

    protected void enqueue(CacheItem ad) {
        sAdQueue.add(ad);
    }

    protected PNAPIAdModel dequeue() {
        PNAPIAdModel result = null;

        if (sAdQueue.size() > 0) {
            CacheItem item = sAdQueue.remove(0);
            if (System.currentTimeMillis() > item.timestamp + TimeUnit.MINUTES.toMillis(sCacheThreshold)) {
                result = dequeue();
            } else {
                result = item.ad;
            }
        }
        return result;
    }

    protected void clear() {
        sAdQueue.clear();
    }

    protected int getQueueSize() {
        return sAdQueue.size();
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PNAPIAdModel.LoadListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPNAPIRequestFinish(PNAPIRequest request, List<PNAPIAdModel> ads) {
        if (ads != null) {
            enqueue(ads);
        }
        invokeLoadFinish();
    }

    @Override
    public void onPNAPIRequestFail(PNAPIRequest request, Exception ex) {
        invokeLoadFinish();
    }
}
