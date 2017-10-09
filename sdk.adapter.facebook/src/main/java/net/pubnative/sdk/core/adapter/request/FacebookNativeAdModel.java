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

package net.pubnative.sdk.core.adapter.request;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import net.pubnative.sdk.core.request.PNAdModel;

import java.util.ArrayList;
import java.util.List;

public class FacebookNativeAdModel extends PNAdModel implements AdListener {

    private static String TAG = FacebookNativeAdModel.class.getSimpleName();
    protected NativeAd  mNativeAd;
    protected MediaView mMediaView;
    private boolean mTrackEntireLayout = true;

    public FacebookNativeAdModel(Context context, NativeAd nativeAd) {
        super(context);
        if (nativeAd != null) {
            mNativeAd = nativeAd;
            mNativeAd.setAdListener(this);
        }
    }

    //==============================================================================================
    // PNAPIAdModel methods
    //==============================================================================================

    // Fields
    //----------------------------------------------------------------------------------------------
    @Override
    public String getTitle() {
        String result = null;
        if (mNativeAd != null) {
            result = mNativeAd.getAdTitle();
        }
        return result;
    }

    @Override
    public String getDescription() {
        String result = null;
        if (mNativeAd != null) {
            result = mNativeAd.getAdBody();
        }
        return result;
    }

    @Override
    public String getIconUrl() {
        String iconUrl = null;
        if (mNativeAd != null && mNativeAd.getAdIcon() != null) {
            iconUrl = mNativeAd.getAdIcon().getUrl();
        }
        return iconUrl;
    }

    @Deprecated
    @Override
    public String getBannerUrl() {
        String bannerUrl = null;
        if (mNativeAd != null && mNativeAd.getAdCoverImage() != null) {
            bannerUrl = mNativeAd.getAdCoverImage().getUrl();
        }
        return bannerUrl;
    }

    @Override
    public View getBanner() {

        if(mMediaView == null) {
            mMediaView = new MediaView(mContext);
            mMediaView.setNativeAd(mNativeAd);
        }
        return mMediaView;
    }

    @Override
    public String getCallToAction() {
        String result = null;
        if (mNativeAd != null) {
            result = mNativeAd.getAdCallToAction();
        }
        return result;
    }

    @Override
    public float getStarRating() {
        float starRating = 0;
        if (mNativeAd != null) {
            NativeAd.Rating rating = mNativeAd.getAdStarRating();
            if (rating != null) {
                double ratingScale = rating.getScale();
                double ratingValue = rating.getValue();
                starRating = (float) ((ratingValue / ratingScale) * 5);
            }
        }
        return starRating;
    }

    @Override
    public View getContentInfoView() {
        View result = null;
        if (mNativeAd != null) {
            LinearLayout adChoicesContainer = new LinearLayout(mContext);
            adChoicesContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            adChoicesContainer.setGravity(Gravity.CENTER);
            adChoicesContainer.setOrientation(LinearLayout.VERTICAL);
            adChoicesContainer.addView(new AdChoicesView(mContext, mNativeAd, true));
            return adChoicesContainer;
        }
        return result;
    }

    @Override
    protected String getContentInfoClickUrl() {
        return mNativeAd.getAdChoicesLinkUrl();
    }

    @Override
    protected String getContentInfoImageUrl() {
        return mNativeAd.getAdChoicesIcon().getUrl();
    }

    //----------------------------------------------------------------------------------------------
    // Tracking
    //----------------------------------------------------------------------------------------------

    public PNAdModel trackEntireLayout(boolean trackEntireLayout) {
        mTrackEntireLayout = trackEntireLayout;
        return this;
    }

    @Override
    public void startTracking(ViewGroup adView) {
        if (mNativeAd != null && adView != null) {
            if(mTrackEntireLayout) {
                mNativeAd.registerViewForInteraction(adView);
            } else {
                mNativeAd.registerViewForInteraction(adView, prepareClickableViewList());
            }
        }
    }

    @Override
    public void stopTracking() {
        if (mNativeAd != null) {
            mNativeAd.unregisterView();
        }
    }

    @NonNull
    private List<View> prepareClickableViewList() {
        List<View> clickableViews = new ArrayList<>();
        if (mBannerView != null) {
            clickableViews.add(mBannerView);
        }
        if (mTitleView != null) {
            clickableViews.add(mTitleView);
        }
        if (mDescriptionView != null) {
            clickableViews.add(mDescriptionView);
        }
        if (mCallToActionView != null) {
            clickableViews.add(mCallToActionView);
        }
        if (mIconView != null) {
            clickableViews.add(mIconView);
        }
        if (mRatingView != null) {
            clickableViews.add(mRatingView);
        }
        if (mContentInfoView != null) {
            clickableViews.add(mContentInfoView);
        }
        return clickableViews;
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onError(Ad ad, AdError adError) {
    }

    @Override
    public void onAdLoaded(Ad ad) {
    }

    @Override
    public void onAdClicked(Ad ad) {
        invokeClick();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        invokeImpressionConfirmed();
    }
}
