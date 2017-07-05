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
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.sdk.core.request.PNAdModel;

import java.util.Map;

public class PubnativeLibraryAdModel extends PNAdModel implements PNAPIAdModel.Listener {

    private static String TAG = PubnativeLibraryAdModel.class.getSimpleName();

    protected PNAPIAdModel        mAdModel;
    protected Map<String, String> mTrackingExtras;

    public PubnativeLibraryAdModel(Context context, PNAPIAdModel model) {
        this(context, model, null);
    }

    public PubnativeLibraryAdModel(Context context, PNAPIAdModel model, Map<String, String> trackingExtras) {

        super(context);
        mAdModel = model;
        mTrackingExtras = trackingExtras;
    }

    //==============================================================================================
    // PNAPIAdModel methods
    //==============================================================================================
    // Fields
    //----------------------------------------------------------------------------------------------

    @Override
    public String getTitle() {
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getTitle();
        }
        return result;
    }

    @Override
    public String getDescription() {
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getDescription();
        }
        return result;
    }

    @Override
    public String getIconUrl() {
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getIconUrl();
        }
        return result;
    }

    @Override
    public String getBannerUrl() {
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getBannerUrl();
        }
        return result;
    }

    @Override
    public String getCallToAction() {
        String result = null;
        if (mAdModel != null) {
            result = mAdModel.getCtaText();
        }
        return result;
    }

    @Override
    public float getStarRating() {
        float result = 0F;
        if (mAdModel != null) {
            result = mAdModel.getRating();
        }
        return result;
    }

    @Override
    public View getContentInfoView() {
        return mAdModel.getContentInfo(mContext);
    }
    //----------------------------------------------------------------------------------------------
    // Extension
    //----------------------------------------------------------------------------------------------
    @Override
    protected String getContentInfoClickUrl() {
        return mAdModel.getContentInfoClickUrl();
    }

    @Override
    protected String getContentInfoImageUrl() {
        return mAdModel.getContentInfoIconUrl();
    }

    //----------------------------------------------------------------------------------------------
    // Tracking
    //----------------------------------------------------------------------------------------------
    @Override
    public void startTracking(ViewGroup adView) {
        if (mAdModel != null && adView != null) {
            mAdModel.startTracking(adView, adView, mTrackingExtras, this);
        }
    }

    @Override
    public void stopTracking() {
        mAdModel.stopTracking();
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PNAPIAdModel.LoadListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPNAPIAdModelImpression(PNAPIAdModel PNAPIAdModel, View view) {
        invokeImpressionConfirmed();
    }

    @Override
    public void onPNAPIAdModelClick(PNAPIAdModel PNAPIAdModel, View view) {
        invokeClick();
    }

    @Override
    public void onPNAPIAdModelOpenOffer(PNAPIAdModel PNAPIAdModel) {
    }
}
