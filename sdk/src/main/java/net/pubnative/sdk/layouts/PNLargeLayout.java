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

import android.util.Log;

import net.pubnative.sdk.layouts.adapter.PNLayoutAdapter;
import net.pubnative.sdk.layouts.adapter.PNLayoutFullscreenAdapter;
import net.pubnative.sdk.layouts.adapter.large.PNLargeLayoutAdapterFactory;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Map;

public class PNLargeLayout extends PNLayout implements PNLayoutFullscreenAdapter.LoadListener,
                                                       PNLayoutFullscreenAdapter.TrackListener,
                                                       PNLayoutFullscreenAdapter.ViewListener {

    private static final String TAG = PNLargeLayout.class.getSimpleName();

    public interface ViewListener {
        void onPNLayoutViewShown(PNLayout layout);

        void onPNLayoutViewHidden(PNLayout layout);
    }

    protected ViewListener              mViewListener;
    protected PNLayoutFullscreenAdapter mAdapter;
    protected boolean                   isShown;

    /**
     * This method will set up the view listener to listen for viewevents
     *
     * @param viewListener
     */
    public void setViewListener(ViewListener viewListener) {
        mViewListener = viewListener;
    }

    /**
     * This method will tell if the current layout is ready to be shown
     *
     * @return
     */
    public boolean isReady() {
        return mAdapter != null;
    }

    /**
     * This methods will show the ad if it's ready
     */
    public void show() {

        if (isReady()) {
            mAdapter.setViewListener(this);
            mAdapter.setTrackListener(this);
            mAdapter.show();
        } else {
            Log.w(TAG, "This layout is not loaded, did you forgot to load it before?");
        }
    }

    public void hide() {
        if (isReady() && isShown) {
            mAdapter.hide();
            mAdapter.setViewListener(null);
            mAdapter.setTrackListener(null);
        } else {
            Log.w(TAG, "This layout is not loaded or shown, did you forgot to load or show it before?");
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeNetworkLargeLayoutAdapter.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterLoadFinished(PNLayoutAdapter adapter) {
        trackSuccededNetwork(adapter.getElapsedTime());
        mAdapter = (PNLayoutFullscreenAdapter) adapter;
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

    // PubnativeNetworkLargeLayoutAdapter.TrackListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterTrackImpression() {
        invokeImpression();
    }

    @Override
    public void onAdapterTrackClick() {
        invokeClick();
    }

    // PubnativeNetworkLargeLayoutAdapter.ViewListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterViewShow() {
        invokeShow();
    }

    @Override
    public void onAdapterViewHide() {
        invokeHide();
    }

    //==============================================================================================
    // Helper methods
    //==============================================================================================
    protected void invokeShow() {
        isShown = true;
        if (mViewListener != null) {
            mViewListener.onPNLayoutViewShown(this);
        }
    }

    protected void invokeHide() {
        isShown = false;
        if (mViewListener != null) {
            mViewListener.onPNLayoutViewHidden(this);
        }
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
        PNLayoutAdapter adapter = PNLargeLayoutAdapterFactory.getAdapter(mPlacement.getCurrentNetwork());
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
