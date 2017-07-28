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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.pubnative.sdk.core.adapter.request.PNAdapter;
import net.pubnative.sdk.core.adapter.request.PNAdapterFactory;
import net.pubnative.sdk.core.adapter.request.PubnativeLibraryNetworkAdapter;
import net.pubnative.sdk.core.config.model.PNNetworkModel;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Map;

public class PNRequest extends PNWaterfall implements PNAdapter.LoadListener {

    private static String TAG = PNRequest.class.getSimpleName();

    //==============================================================================================
    // Properties
    //==============================================================================================
    protected Listener  mListener                 = null;
    protected boolean   mIsRunning                = false;
    protected Handler   mHandler                  = null;
    protected PNAdModel mAd                       = null;
    protected boolean   mIsCachingResourceEnabled = true;

    //==============================================================================================
    // LoadListener
    //==============================================================================================

    /**
     * Interface for request callbacks that will inform about the request status
     */
    public interface Listener {

        /**
         * Invoked when ad request returns valid ads.
         *
         * @param request Object used to make the ad request.
         * @param ad      Loaded ad model.
         */
        void onPNRequestLoadFinish(PNRequest request, PNAdModel ad);

        /**
         * Invoked when ad request fails or when no ad is retrieved.
         *
         * @param request   Object used to make the ad request.
         * @param exception Exception with proper message of request failure.
         */
        void onPNRequestLoadFail(PNRequest request, Exception exception);
    }

    //==============================================================================================
    // Pubic methods
    //==============================================================================================

    /**
     * Starts a new mAd request.
     *
     * @param context       valid Context object.
     * @param appToken      valid AppToken provided by Pubnative.
     * @param placementName valid placementId provided by Pubnative.
     * @param listener      valid LoadListener to keep track of request callbacks.
     */
    public synchronized void start(Context context, String appToken, String placementName, Listener listener) {
        if (listener == null) {
            Log.w(TAG, "start - Error: listener not specified, dropping the call");
        } else if (mIsRunning) {
            Log.w(TAG, "start - Error: request already loading, dropping the call");
        } else {
            mIsRunning = true;
            mHandler = new Handler(Looper.getMainLooper());
            mListener = listener;
            initialize(context, appToken, placementName);
        }
    }

    /**
     * this method enables caching for ad resources.
     *
     * @param enabled true for enable, false for disable.
     */
    public void setCacheResources(boolean enabled) {
        mIsCachingResourceEnabled = enabled;
    }

    //==============================================================================================
    // PNRequest
    //==============================================================================================
    @Override
    protected void onWaterfallLoadFinish(boolean pacingActive) {
        if (pacingActive && mAd == null) {
            invokeFail(PNException.PLACEMENT_PACING_CAP);
        } else if (pacingActive) {
            onRequestLoad(mAd);
        } else {
            if (isCaching() || !PNCacheManager.isPlacementCached(mPlacement.getName())) {
                doParalelRequests();
            }
            getNextNetwork();
        }
    }

    protected void doParalelRequests() {
        // Get all networks
        Map<String, PNNetworkModel> networks = getAllNetworks(PubnativeLibraryNetworkAdapter.class.getSimpleName(), false);

        for (String networkName : networks.keySet()) {
            PNNetworkModel network = networks.get(networkName);
            PNAdapter adapter = PNAdapterFactory.create(network);
            if (adapter != null) {
                paralelRequest(adapter, network, getExtras());
            }
        }
    }

    protected boolean isCaching() {
        return false;
    }

    @Override
    protected void onWaterfallError(Exception exception) {
        invokeFail(exception);
    }

    @Override
    protected void onWaterfallNextNetwork(PNAdapter adapter, Map extras) {
        PNNetworkModel network = mPlacement.getCurrentNetwork();
        if (canUseCache(network)) {
            PNAdModelCache cachedAd = PNCacheManager.getCachedAd(mPlacement.getName(), mPlacement.getCurrentNetworkIndex());
            if (cachedAd == null) {
                // This network returned NO FILL on fetchAssets process, go for the next one
                getNextNetwork();
            } else if (cachedAd.isValid()) {
                onRequestLoad(cachedAd.ad);
            } else {
                // If the ad is present but invalid, we do a RTR
                request(adapter, network, extras);
            }
        } else {
            request(adapter, network, extras);
        }
    }

    protected boolean canUseCache(PNNetworkModel network) {
        return mPlacement.isAdCacheEnabled()
                && network.isAdCacheEnabled();
    }

    protected void paralelRequest(PNAdapter adapter, PNNetworkModel network, Map extras) {
        request(adapter, network, extras, true);
    }

    protected void request(PNAdapter adapter, PNNetworkModel network, Map extras) {
        request(adapter, network, extras, false);
    }

    protected void request(PNAdapter adapter, PNNetworkModel network, Map extras, boolean isParallel) {
        if (adapter == null) {
            if (!isParallel) {
                trackUnreachableNetwork(0, PNException.ADAPTER_TYPE_NOT_IMPLEMENTED);
                getNextNetwork();
            }
        } else {
            // Remove listener from previous request to avoid interactions
            // Set new adapter
            adapter.setCPICacheEnabled(network.isCPACacheEnabled() && !isParallel);
            adapter.setInsight(mInsight);
            adapter.setParallelRequestMode(isParallel);
            adapter.setExtras(extras);
            adapter.setLoadListener(this);
            adapter.execute(mContext, getRequestTimeout(network));
        }
    }

    protected int getRequestTimeout(PNNetworkModel network) {
        int result = 0;
        if (network != null) {
            result = network.getTimeout();
        }
        return result;
    }

    protected void cache() {
        if (mPlacement != null && mPlacement.isAdCacheEnabled() && mPlacement.hasNetworkCacheEnabled()) {
            PNCacheManager.cachePlacement(mContext,
                                          mPlacement.getAppToken(),
                                          mPlacement.getName(),
                                          mPlacement.getConfig());
        }
    }

    protected void onRequestLoad(final PNAdModel ad) {

        if (mIsCachingResourceEnabled) {
            ad.fetchAssets(new PNAdModel.FetchListener() {
                @Override
                public void onFetchFinish(PNAdModel model) {
                    invokeLoad(model);
                }

                @Override
                public void onFetchFail(PNAdModel model, Exception exception) {
                    invokeFail(exception);
                }
            });
        } else {
            invokeLoad(ad);
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoad(final PNAdModel ad) {
        cache();
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsRunning = false;
                Listener listener = mListener;
                mListener = null;
                if (listener != null) {
                    listener.onPNRequestLoadFinish(PNRequest.this, ad);
                }
            }
        });
    }

    protected void invokeFail(final Exception exception) {
        cache();
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsRunning = false;
                Listener listener = mListener;
                mListener = null;
                if (listener != null) {
                    listener.onPNRequestLoadFail(PNRequest.this, exception);
                }
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PNAdapter.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterLoadFinish(PNAdapter adapter, PNAdModel ad) {
        // Attend only to current request callbacks
        if (!adapter.isParallelRequest()) {

            long elapsedTime = -1l;
            if (adapter != null) {
                elapsedTime = adapter.getElapsedTime();
            }

            if (ad == null) {
                trackAttemptedNetwork(elapsedTime, PNException.ADAPTER_NO_FILL);
                getNextNetwork();
            } else {
                // Track succeded network
                trackSuccededNetwork(elapsedTime);

                mAd = ad;
                mAd.setInsightModel(mInsight);
                onRequestLoad(mAd);
            }
        }
    }

    @Override
    public void onAdapterLoadFail(PNAdapter adapter, Exception exception) {
        // Attend only to current request callbacks
        if (!adapter.isParallelRequest()) {
            long elapsedTime = -1l;
            if (adapter != null) {
                elapsedTime = adapter.getElapsedTime();
            }
            if (exception.getClass().isAssignableFrom(PNException.class)) {
                trackUnreachableNetwork(elapsedTime, exception);
            } else {
                trackAttemptedNetwork(elapsedTime, exception);
            }
            getNextNetwork();
        }
    }
}

