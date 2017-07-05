// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
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

package net.pubnative.api.layouts;

import android.content.Context;
import android.util.Log;

import net.pubnative.api.layouts.asset_group.PNAPIAssetGroup;
import net.pubnative.api.layouts.asset_group.PNAPIAssetGroupFactory;
import net.pubnative.api.core.request.PNAPIRequest;
import net.pubnative.api.core.request.model.PNAPIAdModel;

import java.util.List;

public class PNAPILayout extends PNAPIRequest implements PNAPIRequest.Listener {

    private static final String TAG = PNAPILayout.class.getSimpleName();

    public interface LoadListener {

        void onPubnativeLayoutLoadFinish(PNAPILayout layout, PNAPIAdModel model);

        void onPubnativeLayoutLoadFail(PNAPILayout layout, Exception exception);
    }

    public interface FetchListener {

        void onPubnativeLayoutFetchFinish(PNAPILayout layout, PNAPILayoutView view);

        void onPubnativeLayoutFetchFail(PNAPILayout layout, Exception exception);
    }

    public enum Size {

        SMALL("s"),
        MEDIUM("m"),
        LARGE("l");

        private final String size;

        private Size(String s) {
            size = s;
        }

        public String toString() {
            return this.size;
        }
    }

    protected LoadListener  mLoadListener;
    protected FetchListener mFetchListener;
    protected PNAPIAdModel  mAdModel;
    protected Size          mSize;

    //==============================================================================================
    // PUBLIC METHODS
    //==============================================================================================
    public void load(Context context, Size size, LoadListener listener) {

        if (listener == null) {
            Log.e(TAG, "Listener is required and null, dropping this call");
        } else if (size == null) {
            Log.e(TAG, "Size is required and null, please specify the size, dropping this call");
        } else {
            mSize = size;
            mLoadListener = listener;
            setParameter(Parameters.ASSET_LAYOUT, size.toString());
            super.start(context, this);
        }
    }

    public void fetch(FetchListener listener) {

        if (listener == null) {
            Log.e(TAG, "Listener is required and null, dropping this call");
        } else {
            mFetchListener = listener;
            PNAPIAssetGroup assetGroup = PNAPIAssetGroupFactory.createView(mContext, mAdModel);
            if (assetGroup == null) {
                invokeFail(new Exception("Error: could not initialise asset group view"));
            } else {
                assetGroup.setLoadListener(new PNAPIAssetGroup.LoadListener() {
                    @Override
                    public void onPubnativeAssetGroupLoadFinish(PNAPIAssetGroup view) {
                        invokeFetchFinish(view);
                    }

                    @Override
                    public void onPubnativeAssetGroupLoadFail(PNAPIAssetGroup view, Exception exception) {
                        invokeFetchFail(exception);
                    }
                });
                assetGroup.load();
            }
        }
    }

    public void setAdModel(PNAPIAdModel model) {
        mAdModel = model;
    }

    //==============================================================================================
    // HELPERS
    //==============================================================================================
    protected void invokeLoadFinish() {
        if (mLoadListener != null) {
            mLoadListener.onPubnativeLayoutLoadFinish(this, mAdModel);
        }
    }

    protected void invokeFail(Exception exception) {
        if (mLoadListener != null) {
            mLoadListener.onPubnativeLayoutLoadFail(this, exception);
        }
    }

    protected void invokeFetchFinish(PNAPILayoutView view) {
        if (mFetchListener != null) {
            mFetchListener.onPubnativeLayoutFetchFinish(this, view);
        }
    }

    protected void invokeFetchFail(Exception exception) {
        if (mFetchListener != null) {
            mFetchListener.onPubnativeLayoutFetchFail(this, exception);
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    @Override
    public void onPNAPIRequestFinish(PNAPIRequest request, List<PNAPIAdModel> ads) {

        if (ads == null || ads.size() == 0) {
            invokeFail(new Exception("No fill, pubnative did not return any valid ad"));
        } else {
            mAdModel = ads.get(0);
            invokeLoadFinish();
        }
    }

    @Override
    public void onPNAPIRequestFail(PNAPIRequest request, Exception ex) {

        invokeFail(ex);
    }
}