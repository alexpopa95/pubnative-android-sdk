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
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.api.core.utils.PNAPIImageDownloader;
import net.pubnative.sdk.core.config.model.PNPlacementModel;
import net.pubnative.sdk.core.insights.model.PNInsightDataModel;
import net.pubnative.sdk.core.insights.model.PNInsightModel;

import java.util.ArrayList;
import java.util.List;

public abstract class PNAdModel {

    private static final String TAG = PNAdModel.class.getSimpleName();

    // Model
    protected Context                  mContext                  = null;
    protected Listener                 mListener                 = null;
    protected List<FetchListener>      mFetchListeners           = null;
    // Tracking
    protected PNInsightModel           mInsightModel             = null;
    // View
    protected TextView                 mTitleView                = null;
    protected TextView                 mDescriptionView          = null;
    protected ImageView                mIconView                 = null;
    protected ViewGroup                mBannerView               = null;
    protected RatingBar                mRatingView               = null;
    protected View                     mCallToActionView         = null;
    protected ViewGroup                mContentInfoView          = null;
    // Cached assets
    protected LruCache<String, Bitmap> mCachedAssets             = null;
    protected int                      mRemainingCacheableAssets = 0;

    //==============================================================================================
    // LoadListener
    //==============================================================================================

    /**
     * LoadListener with all callbacks of the model
     */
    public interface Listener {

        /**
         * Callback that will be invoked when the impression is confirmed
         *
         * @param model model where the impression was confirmed
         */
        void onPNAdImpression(PNAdModel model);

        /**
         * Callback that will be invoked when the ad click was detected
         *
         * @param model model where the click was confirmed
         */
        void onPNAdClick(PNAdModel model);
    }

    /**
     * LoadListener
     */
    protected interface FetchListener {

        /**
         * Invoked when ad was received successfully from the network.
         *
         * @param model model that finished fetching
         */
        void onFetchFinish(PNAdModel model);

        /**
         * Invoked when ad request is failed or when networks gives no ad.
         *
         * @param model     model that finished fetching
         * @param exception Exception raised with proper message to indicate request failure.
         */
        void onFetchFail(PNAdModel model, Exception exception);
    }

    /**
     * Sets the a listener for tracking callbacks
     *
     * @param listener valid LoadListener
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setLoadListener");
        mListener = listener;
    }

    public PNAdModel(Context context) {
        mContext = context;
    }

    //==============================================================================================
    // ABSTRACT
    //==============================================================================================
    // MODEL FIELDs
    //----------------------------------------------------------------------------------------------

    /**
     * gets title of the current ad
     *
     * @return short string with ad title
     */
    public abstract String getTitle();

    /**
     * gets description of the current ad
     *
     * @return long string with ad details
     */
    public abstract String getDescription();

    /**
     * gets the banner item from the responding network
     *
     * @return banner view initialised
     */
    public View getBanner() {

        final Bitmap bannerBitmap = getAsset(getBannerUrl());

        final ImageView banner = new ImageView(mContext);
        if (mBannerView == null) {
            mBannerView = new RelativeLayout(mContext);
            mBannerView.addView(banner, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }

        if (bannerBitmap == null) {

            // Assets not yet cached
            fetchAssets(new FetchListener() {
                @Override
                public void onFetchFinish(PNAdModel model) {
                    Bitmap bitmap = getAsset(getBannerUrl());
                    banner.setImageBitmap(bitmap);
                }

                @Override
                public void onFetchFail(PNAdModel model, Exception exception) {
                    Log.w(TAG, "error fetching assets", exception);
                }
            });
        } else {

            // Assets already cached
            banner.setImageBitmap(bannerBitmap);
        }

        return mBannerView;
    }

    /**
     * gets the icon image for ad.
     *
     * @return icon bitmap image.
     */
    public View getIcon() {

        final Bitmap iconBitmap = getAsset(getIconUrl());

        if (mIconView == null) {
            mIconView = new ImageView(mContext);
        }

        if (iconBitmap == null) {
            // Assets not yet cached
            fetchAssets(new FetchListener() {
                @Override
                public void onFetchFinish(PNAdModel model) {
                    Bitmap bitmap = getAsset(getBannerUrl());
                    mIconView.setImageBitmap(bitmap);
                }

                @Override
                public void onFetchFail(PNAdModel model, Exception exception) {
                    Log.w(TAG, "error fetching assets", exception);
                }
            });
        } else {
            // Assets already cached
            mIconView.setImageBitmap(iconBitmap);
        }

        return mIconView;
    }

    /**
     * gets the call to action string (download, free, etc)
     *
     * @return call to action string
     */
    public abstract String getCallToAction();

    /**
     * gets the star rating in a base of 5 stars
     *
     * @return float with value between 0.0 and 5.0
     */
    public abstract float getStarRating();

    /**
     * gets the advertising disclosure item for the current network (Ad choices, Sponsor label, etc)
     *
     * @return Disclosure view to be added on top of the ad.
     */
    public abstract View getContentInfoView();

    //----------------------------------------------------------------------------------------------
    // Extensions
    //----------------------------------------------------------------------------------------------

    protected String getContentInfoClickUrl() {
        return null;
    }

    protected String getContentInfoImageUrl() {
        return null;
    }

    protected String getBannerUrl() {
        return null;
    }

    protected String getIconUrl() {
        return null;
    }

    //----------------------------------------------------------------------------------------------
    // VIEW TRACKING
    //----------------------------------------------------------------------------------------------

    /**
     * Sets the title view for tracking
     *
     * @param view valid TextView containing the title
     * @return this object
     */
    public PNAdModel withTitle(TextView view) {
        mTitleView = view;
        if (mTitleView != null) {
            mTitleView.setText(getTitle());
        }
        return this;
    }

    /**
     * Sets the description view for tracking
     *
     * @param view valid TextView containing the description
     * @return this object
     */
    public PNAdModel withDescription(TextView view) {
        mDescriptionView = view;
        if (mDescriptionView != null) {
            mDescriptionView.setText(getDescription());
        }
        return this;
    }

    /**
     * Sets the icon view for tracking
     *
     * @param view valid ImageView containing the icon
     * @return this object
     */
    public PNAdModel withIcon(ImageView view) {
        mIconView = view;
        if (mIconView != null) {
            getIcon(); // This sentence will inject the icon bitmap
        }
        return this;
    }

    /**
     * Sets the banner view for tracking
     *
     * @param view valid ImageView containing the banner
     * @return this object
     */
    public PNAdModel withBanner(ViewGroup view) {
        if (view != null) {
            View banner = getBanner();
            view.addView(banner);
            banner.bringToFront();
        }
        return this;
    }

    /**
     * Sets the rating view for tracking
     *
     * @param view valid RatingBar containing the rating
     * @return this object
     */
    public PNAdModel withRating(RatingBar view) {
        mRatingView = view;
        if (mRatingView != null) {
            mRatingView.setRating(getStarRating());
        }
        return this;
    }

    /**
     * Sets the call to action view for tracking
     *
     * @param view valid Button containing the call to action
     * @return this object
     */
    public PNAdModel withCallToAction(Button view) {
        mCallToActionView = view;
        if (mCallToActionView != null) {
            ((Button) mCallToActionView).setText(getCallToAction());
        }
        return this;
    }

    /**
     * Sets the call to action view for tracking
     *
     * @param view valid TextView containing the call to action
     * @return this object
     */
    public PNAdModel withCallToAction(TextView view) {
        mCallToActionView = view;
        if (mCallToActionView != null) {
            ((TextView) mCallToActionView).setText(getCallToAction());
        }
        return this;
    }

    public PNAdModel withContentInfoContainer(ViewGroup view) {
        mContentInfoView = view;
        View contentInfo = getContentInfoView();
        if (mContentInfoView != null && contentInfo != null) {
            mContentInfoView.addView(contentInfo);
        }
        return this;
    }

    //----------------------------------------------------------------------------------------------
    // TRACKING
    //----------------------------------------------------------------------------------------------

    /**
     * Start tracking a view to automatically confirm impressions and handle clicks
     *
     * @param adView view that will handle clicks and will be tracked to confirm impression
     */
    public abstract void startTracking(ViewGroup adView);

    /**
     * Stop using the view for confirming impression and handle clicks
     */
    public abstract void stopTracking();

    /**
     * Sets extended tracking (used to initialize the view)
     *
     * @param insightModel insight model with all the tracking data
     */
    public void setInsightModel(PNInsightModel insightModel) {
        mInsightModel = insightModel;
        // We set the creative based on  the model creative
        if (mInsightModel != null) {
            PNInsightDataModel data = mInsightModel.getData();
            if (PNPlacementModel.AdFormatCode.NATIVE_ICON.equals(data.ad_format_code)) {
                data.creative_url = getIconUrl();
            } else {
                data.creative_url = getBannerUrl();
            }
            insightModel.setData(data);
        }
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected void fetchAssets(FetchListener listener) {
        if (mFetchListeners == null) {
            mRemainingCacheableAssets = 2;
            mFetchListeners = new ArrayList<FetchListener>();
            mFetchListeners.add(listener);
            // This needs to be updated whenever we are going to fetchAssets more resources.
            fetchAsset(getIconUrl()); // fetchAssets icon
            fetchAsset(getBannerUrl()); // fetchAssets banner
            Log.w(TAG, "LoadListener is not set");
        } else {
            mFetchListeners.add(listener);
        }
    }
    //==============================================================================================
    // Tracking data
    //==============================================================================================

    protected void fetchAsset(final String url) {
        if (TextUtils.isEmpty(url)) {
            checkFetchProgress();
        } else {
            new PNAPIImageDownloader().load(url, new PNAPIImageDownloader.Listener() {
                @Override
                public void onImageLoad(String url, Bitmap bitmap) {
                    cacheAsset(url, bitmap);
                    checkFetchProgress();
                }

                @Override
                public void onImageFailed(String url, Exception exception) {
                    Log.w(TAG, "Asset download error: " + url, exception);
                    invokeFetchFail(exception);
                }
            });
        }
    }

    protected void cacheAsset(String url, Bitmap asset) {
        if (mCachedAssets == null) {
            mCachedAssets = new LruCache<String, Bitmap>(2);
        }

        if (!TextUtils.isEmpty(url) && asset != null) {
            mCachedAssets.put(url, asset);
        }
    }

    protected void checkFetchProgress() {
        mRemainingCacheableAssets--;
        if (mRemainingCacheableAssets == 0) {
            invokeFetchFinish();
        }
    }

    protected Bitmap getAsset(String url) {
        Bitmap result = null;
        if (mCachedAssets != null) {
            result = mCachedAssets.get(url);
        }
        return result;
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeImpressionConfirmed() {
        if (mInsightModel != null) {
            mInsightModel.sendImpressionInsight();
        }

        if (mListener != null) {
            mListener.onPNAdImpression(this);
        }
    }

    protected void invokeClick() {
        if (mInsightModel != null) {
            mInsightModel.sendClickInsight();
        }
        if (mListener != null) {
            mListener.onPNAdClick(this);
        }
    }

    protected void invokeFetchFinish() {
        List<FetchListener> listeners = mFetchListeners;
        mFetchListeners = null;
        if(listeners != null) {
            for (FetchListener listener : listeners) {
                if (listener != null) {
                    listener.onFetchFinish(this);
                }
            }
        }
    }

    protected void invokeFetchFail(Exception exception) {
        List<FetchListener> listeners = mFetchListeners;
        mFetchListeners = null;
        if(listeners != null) {
            for (FetchListener listener : listeners) {
                if (listener != null) {
                    listener.onFetchFail(this, exception);
                }
            }
        }
    }
}
