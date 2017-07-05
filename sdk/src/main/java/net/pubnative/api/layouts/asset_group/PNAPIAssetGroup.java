package net.pubnative.api.layouts.asset_group;

import android.content.Context;

import net.pubnative.api.layouts.PNAPILayoutView;
import net.pubnative.api.core.request.model.PNAPIAdModel;

public abstract class PNAPIAssetGroup extends PNAPILayoutView {

    protected Context mContext;

    public PNAPIAssetGroup(Context context) {
        super(context);
        mContext = context;
    }

    public interface LoadListener {

        void onPubnativeAssetGroupLoadFinish(PNAPIAssetGroup view);
        void onPubnativeAssetGroupLoadFail(PNAPIAssetGroup view, Exception exception);
    }

    protected LoadListener mLoadListener;
    protected PNAPIAdModel mAdModel;

    //==============================================================================================
    // Helpers
    //==============================================================================================
    protected void invokeOnLoadFinish() {
        LoadListener listener = mLoadListener;
        mLoadListener = null;
        if (listener != null) {
            listener.onPubnativeAssetGroupLoadFinish(this);
        }
    }

    protected void invokeOnLoadFail(Exception exception) {
        LoadListener listener = mLoadListener;
        mLoadListener = null;
        if (listener != null) {
            listener.onPubnativeAssetGroupLoadFail(this, exception);
        }
    }

    //==============================================================================================
    // PUBLIC
    //==============================================================================================
    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    // Abstract
    //----------------------------------------------------------------------------------------------
    public abstract void load();
}
