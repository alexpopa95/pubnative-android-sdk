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

package net.pubnative.sdk.layouts;

import android.content.Context;
import android.util.Log;

import net.pubnative.sdk.layouts.adapter.PNLayoutAdapter;
import net.pubnative.sdk.layouts.adapter.PNLayoutFeedAdapter;
import net.pubnative.sdk.layouts.adapter.medium.PNMediumLayoutAdapterFactory;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Map;

public class PNMediumLayout extends PNLayout implements PNLayoutAdapter.LoadListener,
                                                        PNLayoutAdapter.TrackListener {

    private static final String TAG = PNMediumLayout.class.getSimpleName();

    protected PNLayoutFeedAdapter mAdapter;

    public PNMediumLayoutView getView(Context context) {
        PNMediumLayoutView result = null;
        if (mAdapter == null) {
            Log.w(TAG, "Error: Ad not loaded, or failed during load, please reload it again");
        } else {
            result = (PNMediumLayoutView) mAdapter.getView(context);
        }
        return result;
    }

    public void startTrackingView() {
        if (mAdapter == null) {
            Log.w(TAG, "Error: Ad not loaded, or failed during load, please reload it again");
        } else {
            mAdapter.setTrackListener(this);
            mAdapter.startTracking();
        }
    }

    public void stopTrackingView() {
        if (mAdapter == null) {
            Log.w(TAG, "Error: Ad not loaded, or failed during load, please reload it again");
        } else {
            mAdapter.stopTracking();
            mAdapter.setTrackListener(null);
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PNMediumLayoutAdapter.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterLoadFinished(PNLayoutAdapter adapter) {
        trackSuccededNetwork(adapter.getElapsedTime());
        mAdapter = (PNLayoutFeedAdapter) adapter;
        invokeFinish();
    }

    @Override
    public void onAdapterLoadFail(PNLayoutAdapter adapter, Exception exception) {
        if (exception.getClass().isAssignableFrom(PNException.class)) {
            trackUnreachableNetwork(adapter.getElapsedTime(), exception);
        } else {
            trackAttemptedNetwork(adapter.getElapsedTime(), exception);
        }
        getNextNetwork();
    }

    // PNMediumLayoutAdapter.TrackListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterTrackImpression() {
        invokeImpression();
    }

    @Override
    public void onAdapterTrackClick() {
        invokeClick();
    }


    //==============================================================================================
    // OVERRIDEN METHODS
    //==============================================================================================
    @Override
    protected void onPubnativeNetworkLayoutWaterfallLoadFinish(boolean pacingActive) {
        if (pacingActive && mAdapter == null) {
            invokeFail(PNException.PLACEMENT_PACING_CAP);
        } else if (pacingActive) {
            invokeFinish();
        } else {
            getNextNetwork();
        }
    }

    @Override
    protected void onPubnativeNetworkLayoutWaterfallLoadFail(Exception exception) {
        invokeFail(exception);
    }

    @Override
    protected void onPubnativeNetworkLayoutWaterfallNextNetwork(Map extras) {
        PNLayoutAdapter adapter = PNMediumLayoutAdapterFactory.getAdapter(mPlacement.getCurrentNetwork());
        if (adapter == null) {
            trackUnreachableNetwork(0, PNException.ADAPTER_TYPE_NOT_IMPLEMENTED);
            getNextNetwork();
        } else {
            adapter.setCPICacheEnabled(mPlacement.getCurrentNetwork().isCPACacheEnabled());
            adapter.setInsight(mInsight);
            adapter.setLoadListener(this);
            adapter.execute(mContext, mPlacement.getCurrentNetwork().getTimeout());
        }
    }
}
