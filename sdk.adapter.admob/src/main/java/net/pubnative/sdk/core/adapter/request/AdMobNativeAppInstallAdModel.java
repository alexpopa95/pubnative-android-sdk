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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;

import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.core.utils.PNDeviceUtils;

public class AdMobNativeAppInstallAdModel extends PNAdModel implements NativeAppInstallAd.OnAppInstallAdLoadedListener {

    public static final String TAG = AdMobNativeAppInstallAdModel.class.getSimpleName();

    public interface LoadListener {
        void onLoadFinish();

        void onLoadFail(Exception exception);
    }

    protected NativeAppInstallAdView mNativeAdView;
    protected NativeAppInstallAd     mNativeAd;
    protected LoadListener           mListener;
    protected ViewGroup              mAdView;
    protected MediaView              mMediaView;
    protected boolean                isImpressionConfirmed;
    protected AdListener             mAdListener;

    public AdMobNativeAppInstallAdModel(Context context, LoadListener listener) {
        super(context);
        mListener = listener;
    }

    public AdListener getAdListener() {
        if (mAdListener == null) {
            mAdListener = new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    if (mListener != null) {
                        mListener.onLoadFail(new Exception("Error loading AdMob ad: " + i));
                    }
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    invokeClick();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }
            };
        }
        return mAdListener;
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
            result = String.valueOf(mNativeAd.getHeadline());
        }

        return result;
    }

    @Override
    public String getDescription() {
        String result = null;
        if (mNativeAd != null) {
            result = String.valueOf(mNativeAd.getBody());
        }

        return result;
    }

    @Override
    public View getBanner() {

        if (mNativeAd != null
            && mNativeAd.getVideoController() != null
            && mNativeAd.getVideoController().hasVideoContent()) {

            if (mMediaView == null) {
                mMediaView = new MediaView(mContext);
                mMediaView.setLayoutParams(new MediaView.LayoutParams(LayoutParams.MATCH_PARENT, MediaView.LayoutParams.MATCH_PARENT));
            }

            if (mBannerView == null) {
                mBannerView = new RelativeLayout(mContext);
                mBannerView.addView(mMediaView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            }

            return mBannerView;
        } else {
            return super.getBanner();
        }
    }

    @Override
    public String getCallToAction() {
        String result = null;
        if (mNativeAd != null) {
            result = mNativeAd.getCallToAction().toString();
        }

        return result;
    }

    @Override
    public float getStarRating() {
        float starRating = 0;
        if (mNativeAd != null) {
            starRating = mNativeAd.getStarRating().floatValue();
        }

        return starRating;
    }

    @Override
    public View getContentInfoView() {

        return null;
    }

    // Extension
    //----------------------------------------------------------------------------------------------
    @Override
    protected String getIconUrl() {
        String result = null;
        if (mNativeAd != null && mNativeAd.getIcon() != null) {
            result = mNativeAd.getIcon().getUri().toString();
        }

        return result;
    }

    @Override
    protected String getBannerUrl() {
        String result = null;
        if (mNativeAd != null && mNativeAd.getImages() != null && mNativeAd.getImages().size() > 0) {
            result = mNativeAd.getImages().get(0).getUri().toString();
        }

        return result;
    }

    // Tracking
    //----------------------------------------------------------------------------------------------

    @Override
    public void startTracking(ViewGroup adView) {

        mAdView = adView;

        if (mNativeAdView == null) {
            // We create the ADVIEW here because it's the one that renders in itself the ADCHOICES icon
            // and also controls the click and impression of all related items
            mNativeAdView = new NativeAppInstallAdView(mContext);
            mNativeAdView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mNativeAdView.setHeadlineView(mTitleView);
            mNativeAdView.setBodyView(mDescriptionView);
            mNativeAdView.setIconView(mIconView);
            mNativeAdView.setImageView(mBannerView);
            mNativeAdView.setMediaView(mMediaView);
            mNativeAdView.setCallToActionView(mCallToActionView);
            mNativeAdView.setStarRatingView(mRatingView);
            mNativeAdView.setNativeAd(mNativeAd);
        }
        mAdView.addView(mNativeAdView, 0);

        if (!isImpressionConfirmed) {
            isImpressionConfirmed = true;
            invokeImpressionConfirmed();
        }
    }

    @Override
    public void stopTracking() {

        if(mNativeAd != null && mNativeAdView != null && mAdView != null) {
            mAdView.removeView(mNativeAdView);
        }
    }

    @Override
    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
        mNativeAd = nativeAppInstallAd;
        if (mListener != null) {
            mListener.onLoadFinish();
        }
    }
}
