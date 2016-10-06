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

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.Map;

public abstract class PubnativeNetworkRequestAdapter extends PubnativeNetworkAdapter {

    private static String TAG = PubnativeNetworkRequestAdapter.class.getSimpleName();
    protected Listener mListener;

    /**
     * Listener
     */
    public interface Listener {

        /**
         * Invoked when PubnativeNetworkRequestAdapter starts the request with valid params.
         *
         * @param adapter Object used for requesting the ad.
         */
        void onPubnativeNetworkAdapterRequestStarted(PubnativeNetworkRequestAdapter adapter);

        /**
         * Invoked when ad was received successfully from the network.
         *
         * @param adapter Object used for requesting the ad.
         * @param ad      Loaded ad object.
         */
        void onPubnativeNetworkAdapterRequestLoaded(PubnativeNetworkRequestAdapter adapter, PubnativeAdModel ad);

        /**
         * Invoked when ad request is failed or when networks gives no ad.
         *
         * @param adapter   Object used for requesting the ad.
         * @param exception Exception raised with proper message to indicate request failure.
         */
        void onPubnativeNetworkAdapterRequestFailed(PubnativeNetworkRequestAdapter adapter, Exception exception);
    }

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkRequestAdapter(Map data) {

        super(data);
    }

    /**
     * Sets listener for this request
     *
     * @param listener valid listener
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    @Override
    public void execute(Context context, int timeoutInMillis) {

        invokeStart();
        startTimeout(timeoutInMillis);
        request(context);
    }

    @Override
    protected void onTimeout() {

        invokeFailed(PubnativeException.ADAPTER_TIMEOUT);
    }

    //==============================================================================================
    // Abstract methods
    //==============================================================================================
    protected abstract void request(Context context);

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeStart() {

        Log.v(TAG, "invokeStart");
        if (mListener != null) {
            mListener.onPubnativeNetworkAdapterRequestStarted(this);
        }
    }

    protected void invokeLoaded(PubnativeAdModel ad) {

        Log.v(TAG, "invokeLoaded");
        cancelTimeout();
        if (mListener != null) {
            mListener.onPubnativeNetworkAdapterRequestLoaded(this, ad);
        }
        mListener = null;
    }

    protected void invokeFailed(Exception exception) {

        Log.v(TAG, "invokeFailed: " + exception);
        cancelTimeout();
        if (mListener != null) {
            mListener.onPubnativeNetworkAdapterRequestFailed(this, exception);
        }
        mListener = null;
    }
}
