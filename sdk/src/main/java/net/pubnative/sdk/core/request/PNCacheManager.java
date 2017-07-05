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

package net.pubnative.sdk.core.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.core.config.model.PNPlacementModel;

import java.util.HashMap;
import java.util.Map;

public class PNCacheManager {

    private static final String TAG = PNCacheManager.class.getSimpleName();

    protected static Map<String, PNAdModelCache[]> sCacheArray = new HashMap<String, PNAdModelCache[]>();
    //==============================================================================================
    // PNCacheManager
    //==============================================================================================

    /**
     * This methods start a process to fetchAssets an ad from the given placement
     *
     * @param context   valid context
     * @param appToken  valid app token
     * @param config    valid config
     */
    public static synchronized void cachePlacement(Context context, final String appToken, String placement, PNConfigModel config) {
        if (context == null) {
            Log.w(TAG, "context is null and required, dropping call");
        } else if (config == null) {
            Log.w(TAG, "config is null or empty and required, dropping call");
        } else if (TextUtils.isEmpty(appToken)) {
            Log.w(TAG, "app token is null and required, dropping call");
        } else if (TextUtils.isEmpty(placement)) {
            Log.w(TAG, "placement is null and required, dropping call");
        } else {
            PNPlacementModel model = config.getPlacement(placement);
            if (model != null
                    && model.ad_cache
                    && model.priority_rules.size() > 0
                    && !model.delivery_rule.isDisabled()) {

                // 1. Reset current placement to
                sCacheArray.put(placement, new PNAdModelCache[model.priority_rules.size()]);
                PNRequestCache request = new PNRequestCache();
                request.start(context, appToken, placement, new PNRequest.Listener() {
                    @Override
                    public void onPNRequestLoadFinish(PNRequest request, PNAdModel ad) {
                        PNCacheManager.cacheAd(request.mPlacement.getName(),
                                               request.mPlacement.getCurrentNetworkIndex(),
                                               request.mPlacement.getCurrentNetwork().getCacheExpirationTime(),
                                               ad);
                    }

                    @Override
                    public void onPNRequestLoadFail(PNRequest request, Exception exception) {
                        // NO FILL caching this placement
                    }
                });
            }
        }
    }

    /**
     * This method returns the cached ad for the given placement/network tuple and
     * removes It from the current fetchAssets
     *
     * @param placement    valid placement name
     * @param networkIndex valid network index
     * @return cached ad model
     */
    public static PNAdModelCache getCachedAd(String placement, int networkIndex) {
        PNAdModelCache result = getNetworkCache(placement, networkIndex);
        setNetworkCache(placement, networkIndex, null);
        cleanPlacement(placement);
        return result;
    }

    public static boolean isPlacementCached(String placement) {
        return getPlacementCache(placement) != null;
    }

    /**
     * Clean cached Ads
     */
    public static void cleanCache() {
        sCacheArray = new HashMap<String, PNAdModelCache[]>();
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================

    protected static void cleanPlacement(String placement) {
        PNAdModelCache[] cache = getPlacementCache(placement);
        if (cache != null) {
            for (int i = 0; i < cache.length; i++) {
                PNAdModelCache cachedAd = cache[i];
                if (cachedAd != null && !cachedAd.isValid()) {
                    cache[i] = null; // Ensure to remove all the invalid items
                }
            }
            setPlacementCache(placement, cache);
        }
    }

    protected static void cacheAd(String placement, int networkIndex, Integer expiration, PNAdModel ad) {
        PNAdModelCache[] cache = getPlacementCache(placement);
        if (placement == null) {
            Log.w(TAG, "placement fetchAssets not found, cannot set network fetchAssets");
        } else if (ad == null) {
            Log.w(TAG, "caching null ad, ignoring it");
        } else if (networkIndex < cache.length) {

            PNAdModelCache item = null;
            if (ad != null) {
                item = new PNAdModelCache();
                item.ad = ad;
                item.ad_expiration = expiration == null ? 0 : expiration;
            }
            setNetworkCache(placement, networkIndex, item);

        } else {
            Log.e(TAG, "invalid given network index, cannot set network fetchAssets");
        }
    }

    // Cache helpers
    //----------------------------------------------------------------------------------------------
    protected static PNAdModelCache[] getPlacementCache(String placement) {
        PNAdModelCache[] result = null;
        if (TextUtils.isEmpty(placement)) {
            Log.w(TAG, "placement name is null or empty and required, cannot retrieve placement fetchAssets");
        } else {
            result = sCacheArray.get(placement);
        }
        return result;
    }

    protected static PNAdModelCache getNetworkCache(String placement, int networkIndex) {
        PNAdModelCache result = null;
        PNAdModelCache[] cache = getPlacementCache(placement);
        if (cache == null) {
            Log.w(TAG, "network fetchAssets cannot be retrieved because placement fetchAssets cannot be found");
        } else if (networkIndex < cache.length) {
            result = cache[networkIndex];
        } else {
            Log.w(TAG, "invalid networkIndex provided");
        }
        return result;
    }

    protected static void setPlacementCache(String placement, PNAdModelCache[] cache) {
        if (TextUtils.isEmpty(placement)) {
            Log.w(TAG, "placement name is null or empty and required, cannot set placement fetchAssets");
        } else {
            sCacheArray.put(placement, cache);
        }
    }

    protected static void setNetworkCache(String placement, int networkIndex, PNAdModelCache item) {
        PNAdModelCache[] cache = getPlacementCache(placement);
        if (cache == null) {
            Log.w(TAG, "placement fetchAssets cannot be found, cannot set network fetchAssets");
        } else if (networkIndex < cache.length) {
            cache[networkIndex] = item;
            setPlacementCache(placement, cache);
        } else {
            Log.w(TAG, "invalid given network index, cannot set network fetchAssets");
        }
    }
}
