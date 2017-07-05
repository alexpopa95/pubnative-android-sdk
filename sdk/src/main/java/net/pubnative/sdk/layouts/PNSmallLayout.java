package net.pubnative.sdk.layouts;

import android.content.Context;
import android.util.Log;

import net.pubnative.sdk.layouts.adapter.PNLayoutAdapter;
import net.pubnative.sdk.layouts.adapter.PNLayoutFeedAdapter;
import net.pubnative.sdk.layouts.adapter.small.PNSmallLayoutAdapterFactory;
import net.pubnative.sdk.core.exceptions.PNException;

import java.util.Map;

public class PNSmallLayout extends PNLayout implements PNLayoutAdapter.LoadListener,
                                                       PNLayoutAdapter.TrackListener {

    private static final String TAG = PNSmallLayout.class.getSimpleName();

    protected PNLayoutFeedAdapter mAdapter;

    public PNSmallLayoutView getView(Context context) {

        PNSmallLayoutView result = null;
        if (mAdapter == null) {
            Log.w(TAG, "Error: Ad not loaded, or failed during load, please reload it again");
        } else {
            result = (PNSmallLayoutView) mAdapter.getView(context);
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
        PNLayoutAdapter adapter = PNSmallLayoutAdapterFactory.getAdapter(mPlacement.getCurrentNetwork());
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
