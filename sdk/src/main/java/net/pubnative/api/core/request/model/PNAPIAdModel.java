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

package net.pubnative.api.core.request.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.pubnative.URLDriller;
import net.pubnative.api.core.request.model.api.PNAPIV3DataModel;
import net.pubnative.api.core.utils.PNAPISystemUtils;
import net.pubnative.api.core.request.PNAPIAsset;
import net.pubnative.api.core.request.PNAPIMeta;
import net.pubnative.api.core.request.model.api.PNAPIV3AdModel;
import net.pubnative.api.core.tracking.PNAPIImpressionManager;
import net.pubnative.api.core.tracking.PNAPIImpressionTracker;
import net.pubnative.api.core.tracking.PNAPITrackingManager;
import net.pubnative.api.core.view.PNAPIContentInfoView;
import net.pubnative.api.core.view.PNAPIWebView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PNAPIAdModel implements PNAPIImpressionTracker.Listener,
                                     URLDriller.Listener,
                                     Serializable {

    private static String TAG = PNAPIAdModel.class.getSimpleName();

    private static final String DATA_CONTENTINFO_LINK_KEY = "link";
    private static final String DATA_CONTENTINFO_ICON_KEY = "icon";
    private static final String DATA_TRACKING_KEY         = "tracking";
    private static final int    URL_DRILLER_DEPTH         = 15;
    //Generic Fields
    protected transient Listener            mListener;
    protected           PNAPIV3AdModel      mData;
    protected           List<String>        mUsedAssets;
    protected           UUID                mUUID;
    // Used externally to inject data for tracking
    protected           Map<String, String> mTrackingExtras;
    //Tracking
    private transient   boolean             mIsImpressionConfirmed;
    private transient   View                mClickableView;
    private transient   View                mAdView;
    //Loading View
    private transient   RelativeLayout      mLoadingView;
    // Click
    protected boolean mIsWaitingForClickCache     = false;
    protected boolean mIsClickLoaderEnabled       = true;
    protected boolean mIsClickInBackgroundEnabled = true;
    protected boolean mIsClickCachingEnabled      = false;
    protected boolean mIsClickPreparing           = false;
    protected String  mClickFinalURL              = null;

    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface definition for callbacks to be invoked when impression confirmed/failed, ad clicked/clickfailed
     */
    public interface Listener {

        /**
         * Called when impression is confirmed
         *
         * @param PNAPIAdModel PNAPIAdModel impression that was confirmed
         * @param view         The view where impression confirmed
         */
        void onPNAPIAdModelImpression(PNAPIAdModel PNAPIAdModel, View view);

        /**
         * Called when click is confirmed
         *
         * @param PNAPIAdModel PNAPIAdModel that detected the click
         * @param view         The view that was clicked
         */
        void onPNAPIAdModelClick(PNAPIAdModel PNAPIAdModel, View view);

        /**
         * Called before the model opens the offer
         *
         * @param PNAPIAdModel PNAPIAdModel which's offer will be opened
         */
        void onPNAPIAdModelOpenOffer(PNAPIAdModel PNAPIAdModel);
    }

    public static PNAPIAdModel create(PNAPIV3AdModel data) {
        PNAPIAdModel model = new PNAPIAdModel();
        model.mData = data;
        return model;
    }

    //==============================================================================================
    // Generic Fields
    //==============================================================================================

    /**
     * Gets the specified meta field raw data
     *
     * @param meta meta field type name
     * @return valid PNAPIV3DataModel if present, null if not
     */
    public PNAPIV3DataModel getMeta(String meta) {
        PNAPIV3DataModel result = null;
        if (mData == null) {
            Log.w(TAG, "getMeta - Error: ad data not present");
        } else {
            result = mData.getMeta(meta);
        }
        return result;
    }

    /**
     * Gets the specified asset field raw data
     *
     * @param asset asset field type name
     * @return valid PNAPIV3DataModel if present, null if not
     */
    public PNAPIV3DataModel getAsset(String asset) {
        return getAsset(asset, true);
    }

    protected PNAPIV3DataModel getAsset(String asset, boolean trackAsset) {
        PNAPIV3DataModel result = null;
        if (mData == null) {
            Log.w(TAG, "getAsset - Error: ad data not present");
        } else {
            result = mData.getAsset(asset);
            if (result != null) {
                recordAsset(result.getStringField(DATA_TRACKING_KEY));
            }
        }
        return result;
    }

    protected void recordAsset(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (mUsedAssets == null) {
                mUsedAssets = new ArrayList<String>();
            }
            if (!mUsedAssets.contains(url)) {
                mUsedAssets.add(url);
            }
        }
    }

    //==============================================================================================
    // Fields
    //==============================================================================================

    /**
     * Gets the title string of the ad
     *
     * @return String representation of the ad title, null if not present
     */
    public String getTitle() {
        String result = null;
        PNAPIV3DataModel data = getAsset(PNAPIAsset.TITLE);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the description string of the ad
     *
     * @return String representation of the ad Description, null if not present
     */
    public String getDescription() {
        String result = null;
        PNAPIV3DataModel data = getAsset(PNAPIAsset.DESCRIPTION);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the call to action string of the ad
     *
     * @return String representation of the call to action value, null if not present
     */
    public String getCtaText() {
        String result = null;
        PNAPIV3DataModel data = getAsset(PNAPIAsset.CALL_TO_ACTION);
        if (data != null) {
            result = data.getText();
        }
        return result;
    }

    /**
     * Gets the icon image url of the ad
     *
     * @return valid String with the url value, null if not present
     */
    public String getIconUrl() {
        String result = null;
        PNAPIV3DataModel data = getAsset(PNAPIAsset.ICON);
        if (data != null) {
            result = data.getURL();
        }
        return result;
    }

    public String getVast() {
        String result = null;
        PNAPIV3DataModel data = getAsset(PNAPIAsset.VAST);
        if (data != null) {
            result = data.getStringField("vast2");
        }
        return result;
    }

    /**
     * Gets the banner image url of the ad
     *
     * @return valid String with the url value, null if not present
     */
    public String getBannerUrl() {
        String result = null;
        PNAPIV3DataModel data = getAsset(PNAPIAsset.BANNER);
        if (data != null) {
            result = data.getURL();
        }
        return result;
    }

    /**
     * Gets url of the assets (html banner page, standard banner etc.)
     *
     * @param asset asset name for which url requested.
     * @return valid String with the url value, null if not present.
     */
    public String getAssetUrl(String asset) {
        String result = null;
        PNAPIV3DataModel data = getAsset(asset);
        if (data != null) {
            result = data.getURL();
        }
        return result;
    }

    public String getAssetHtml(String asset) {
        String result = null;
        PNAPIV3DataModel data = getAsset(asset);
        if (data != null) {
            result = data.getHtml();
        }
        return result;
    }

    /**
     * Gets the asset group id of the ad
     *
     * @return int value with the id of the asset group ad, 0 if not present
     */
    public int getAssetGroupId() {
        int result = 0;
        if (mData != null) {
            result = mData.assetgroupid;
        }
        return result;
    }

    /**
     * Gets the click url of the ad
     *
     * @return String value with the url of the click, null if not present
     */
    public String getClickUrl() {
        String result = null;
        if (mData != null) {
            result = injectExtras(mData.link);
        }
        return result;
    }

    /**
     * Gets rating of the app in a value from 0 to 5
     *
     * @return int value, 0 if not present
     */
    public int getRating() {
        int result = 0;
        PNAPIV3DataModel data = getAsset(PNAPIAsset.RATING);
        if (data != null) {
            Double rating = data.getNumber();
            if (rating != null) {
                result = rating.intValue();
            }
        }
        return result;
    }

    /**
     * Gets content info view
     *
     * @param context Valid context
     * @return View containing content info
     */
    public View getContentInfo(Context context) {
        PNAPIContentInfoView result = null;
        PNAPIV3DataModel data = getMeta(PNAPIMeta.CONTENT_INFO);
        if (context == null) {
            Log.e(TAG, "getContentInfo - not a valid context");
        } else if (data == null) {
            Log.e(TAG, "getContentInfo - contentInfo data not found");
        } else if (TextUtils.isEmpty(data.getStringField(DATA_CONTENTINFO_ICON_KEY))) {
            Log.e(TAG, "getContentInfo - contentInfo icon not found");
        } else if (TextUtils.isEmpty(data.getStringField(DATA_CONTENTINFO_LINK_KEY))) {
            Log.e(TAG, "getContentInfo - contentInfo link not found");
        } else if (TextUtils.isEmpty(data.getText())) {
            Log.e(TAG, "getContentInfo - contentInfo text not found");
        } else {
            result = new PNAPIContentInfoView(context);
            result.setIconUrl(data.getStringField(DATA_CONTENTINFO_ICON_KEY));
            result.setIconClickUrl(data.getStringField(DATA_CONTENTINFO_LINK_KEY));
            result.setContextText(data.getText());
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PNAPIContentInfoView) view).openLayout();
                }
            });
        }
        return result;
    }

    /**
     * Gets content info icon url
     *
     * @return icon url of content info
     */
    public String getContentInfoIconUrl() {
        PNAPIV3DataModel data = getMeta(PNAPIMeta.CONTENT_INFO);
        return data.getStringField(DATA_CONTENTINFO_ICON_KEY);
    }

    /**
     * Gets content info click url
     *
     * @return click url of content info
     */
    public String getContentInfoClickUrl() {
        PNAPIV3DataModel data = getMeta(PNAPIMeta.CONTENT_INFO);
        return data.getStringField(DATA_CONTENTINFO_LINK_KEY);
    }

    public boolean isRevenueModelCPA() {
        PNAPIV3DataModel model = getMeta("revenuemodel");
        if (model != null) {
            return model.getText().equalsIgnoreCase("cpa");
        } else {
            return false;
        }
    }

    //==============================================================================================
    // Helpers
    //==============================================================================================

    /**
     * This will enable / disable the spin that takes the screen on click. Default behaviour is enabled
     *
     * @param enabled true will show a spinner on top of the screen, false will disable the click spin view
     */
    public void setUseClickLoader(boolean enabled) {
        mIsClickLoaderEnabled = enabled;
    }

    public void setUseClickInBackground(boolean enabled) {
        mIsClickInBackgroundEnabled = enabled;
    }

    public void setUseClickCaching(boolean enabled) {
        mIsClickCachingEnabled = enabled;
    }

    /**
     * This method prepares all possible resources
     */
    public void fetch() {

        prepareClickURL();
    }

    protected void prepareClickURL() {

        if (isRevenueModelCPA() && mIsClickCachingEnabled && mClickFinalURL == null && !mIsClickPreparing) {
            mIsClickPreparing = true;
            mUUID = UUID.randomUUID();
            String firstReqUrl = getClickUrl() + "&uxc=true&uuid=" + mUUID.toString();
            URLDriller driller = new URLDriller();
            driller.setDrillSize(URL_DRILLER_DEPTH);
            driller.setListener(new URLDriller.Listener() {

                @Override
                public void onURLDrillerStart(String url) {
                }

                @Override
                public void onURLDrillerRedirect(String url) {
                }

                @Override
                public void onURLDrillerFinish(String url) {
                    onPrepareClickURLFinish(url);
                }

                @Override
                public void onURLDrillerFail(String url, Exception exception) {
                    onPrepareClickURLFinish(url);
                }
            });
            driller.drill(firstReqUrl);
        }

    }

    protected void onPrepareClickURLFinish(String url) {
        mClickFinalURL = url;
        mIsClickPreparing = false;
        if (mIsWaitingForClickCache) {

            mIsWaitingForClickCache = false;
            openCachedClick();
            hideLoadingView();
        }
    }


    protected void openCachedClick() {
        URLDriller driller = new URLDriller();
        driller.setDrillSize(URL_DRILLER_DEPTH);
        driller.setUserAgent(PNAPISystemUtils.getWebViewUserAgent());
        driller.setListener(PNAPIAdModel.this);
        driller.drill(getClickUrl() + "&cached=true&uuid=" + mUUID.toString());
        openURL(mClickFinalURL);
    }

    //==============================================================================================
    // Tracking
    //==============================================================================================

    // Used to inject extra data in urls
    protected String injectExtras(String url) {
        String result = url;
        if (!TextUtils.isEmpty(url)
            && mTrackingExtras != null
            && mTrackingExtras.size() > 0) {
            Uri.Builder builder = Uri.parse(url).buildUpon();
            for (String key : mTrackingExtras.keySet()) {
                String value = mTrackingExtras.get(key);
                builder.appendQueryParameter(key, value);
            }
            result = builder.build().toString();
        }
        return result;
    }

    /**
     * Start tracking of ad view to auto confirm impressions and handle clicks
     *
     * @param view     ad view
     * @param listener listener for callbacks
     */
    public void startTracking(View view, Listener listener) {
        startTracking(view, view, listener);
    }

    public void startTracking(View view, View clickableView, Listener listener) {
        startTracking(view, clickableView, null, listener);
    }

    /**
     * Start tracking of ad view to auto confirm impressions and handle clicks
     *
     * @param view          ad view
     * @param clickableView clickable view
     * @param extras        tracking Extras
     * @param listener      listener for callbacks
     */
    public void startTracking(View view, View clickableView, Map<String, String> extras, Listener listener) {
        if (listener == null) {
            Log.w(TAG, "startTracking - listener is null, start tracking without callbacks");
        }

        mListener = listener;
        mTrackingExtras = extras;

        stopTracking();

        startTrackingImpression(view);
        startTrackingClicks(clickableView);
    }

    protected void startTrackingImpression(View view) {
        if (view == null) {
            Log.w(TAG, "ad view is null, cannot start tracking");
        } else if (mIsImpressionConfirmed) {
            Log.i(TAG, "impression is already confirmed, dropping impression tracking");
        } else {
            mAdView = view;
            PNAPIImpressionManager.startTrackingView(view, this);
        }
    }

    protected void startTrackingClicks(View clickableView) {
        if (TextUtils.isEmpty(getClickUrl())) {
            Log.w(TAG, "click url is empty, clicks won't be tracked");
        } else if (clickableView == null) {
            Log.w(TAG, "click view is null, clicks won't be tracked");
        } else {
            prepareClickURL();
            mClickableView = clickableView;
            mClickableView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (mIsClickLoaderEnabled) {
                        showLoadingView();
                    }
                    invokeOnClick(view);
                    confirmClickBeacons(view.getContext());

                    if (mIsClickInBackgroundEnabled) {

                        if (mIsClickCachingEnabled) {

                            if (mClickFinalURL == null) {
                                mIsWaitingForClickCache = true;
                            } else {
                                openCachedClick();
                            }
                        } else {
                            // No CPI offer, so we simply follow redirection and open at the end
                            URLDriller driller = new URLDriller();
                            driller.setDrillSize(URL_DRILLER_DEPTH);
                            driller.setUserAgent(PNAPISystemUtils.getWebViewUserAgent());
                            driller.setListener(PNAPIAdModel.this);
                            driller.drill(getClickUrl());
                        }
                    } else {
                        openURL(getClickUrl());
                    }
                }
            });
        }
    }

    /**
     * stop tracking of ad view
     */
    public void stopTracking() {
        stopTrackingImpression();
        stopTrackingClicks();
    }

    protected void stopTrackingImpression() {
        PNAPIImpressionManager.stopTrackingAll(this);
    }

    protected void stopTrackingClicks() {
        if (mClickableView != null) {
            mClickableView.setOnClickListener(null);
        }
    }

    protected void openURL(String urlString) {
        if (TextUtils.isEmpty(urlString)) {
            Log.w(TAG, "Error: ending URL cannot be opened - " + urlString);
        } else if (mClickableView == null) {
            Log.w(TAG, "Error: clickable view not set");
        } else {
            try {
                Uri uri = Uri.parse(urlString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mClickableView.getContext().startActivity(intent);
                invokeOnOpenOffer();
            } catch (Exception ex) {
                Log.w(TAG, "openURL: Error - " + ex.getMessage());
            }
        }
    }

    protected void confirmImpressionBeacons(Context context) {
        // 1. Track assets
        if (mUsedAssets != null) {
            for (String asset : mUsedAssets) {
                PNAPITrackingManager.track(context, asset);
            }
        }
        // 2. Track impressions
        confirmBeacons(PNAPIV3AdModel.Beacon.IMPRESSION, context);
    }

    protected void confirmClickBeacons(Context context) {
        confirmBeacons(PNAPIV3AdModel.Beacon.CLICK, context);
    }

    protected void confirmBeacons(String beaconType, Context context) {
        if (mData == null) {
            Log.w(TAG, "confirmBeacons - Error: ad data not present");
            return;
        }

        List<PNAPIV3DataModel> beacons = mData.getBeacons(beaconType);
        if (beacons == null) {
            return;
        }

        for (PNAPIV3DataModel beaconData : beacons) {
            String beaconURL = injectExtras(beaconData.getURL());
            String beaconJS = beaconData.getStringField("js");
            if (!TextUtils.isEmpty(beaconURL)) {
                // URL
                PNAPITrackingManager.track(context, beaconURL);
            } else if (!TextUtils.isEmpty(beaconJS)) {
                try {
                    new PNAPIWebView(context).loadBeacon(beaconJS);
                } catch (Exception e) {
                    Log.e(TAG, "confirmImpressionBeacons - JS Error: " + e);
                }
            }
        }

    }

    //==============================================================================================
    // LoadingView
    //==============================================================================================

    protected void showLoadingView() {
        if (getRootView() == null) {
            Log.w(TAG, "showLoadingView - Error: impossible to retrieve root view");
        } else {
            hideLoadingView();
            getRootView().addView(getLoadingView(),
                                  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                             ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    protected void hideLoadingView() {
        if (getLoadingView() == null) {
            Log.w(TAG, "loading view is still not loaded, thus you cannot hide it");
        } else if (mLoadingView.getParent() == null) {
            Log.w(TAG, "loading view is still not attached to any view");
        } else {
            ((ViewGroup) mLoadingView.getParent()).removeView(mLoadingView);
        }
    }

    protected ViewGroup getRootView() {
        ViewGroup result = null;
        if (mAdView == null) {
            Log.w(TAG, "getRootView - Error: not assigned ad view, cannot retrieve root view");
        } else {
            result = (ViewGroup) mAdView.getRootView();
        }
        return result;
    }

    protected RelativeLayout getLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = new RelativeLayout(mAdView.getContext());
            mLoadingView.setGravity(Gravity.CENTER);
            mLoadingView.setBackgroundColor(Color.argb(77, 0, 0, 0));
            mLoadingView.setClickable(true);
            mLoadingView.addView(new ProgressBar(mAdView.getContext()));
        }
        return mLoadingView;
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================

    protected void invokeOnImpression(View view) {
        mIsImpressionConfirmed = true;
        if (mListener != null) {
            mListener.onPNAPIAdModelImpression(PNAPIAdModel.this, view);
        }
    }

    protected void invokeOnClick(View view) {
        if (mListener != null) {
            mListener.onPNAPIAdModelClick(PNAPIAdModel.this, view);
        }
    }

    protected void invokeOnOpenOffer() {
        if (mListener != null) {
            mListener.onPNAPIAdModelOpenOffer(this);
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PNAPIImpressionTracker.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onImpression(View visibleView) {
        confirmImpressionBeacons(visibleView.getContext());
        invokeOnImpression(visibleView);
    }

    //----------------------------------------------------------------------------------------------
    // URLDriller.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onURLDrillerStart(String url) {
    }

    @Override
    public void onURLDrillerRedirect(String url) {
    }

    @Override
    public void onURLDrillerFinish(String url) {
        if (mClickFinalURL == null) {
            openURL(url);
        }
        hideLoadingView();
    }

    @Override
    public void onURLDrillerFail(String url, Exception exception) {
        if (mClickFinalURL == null) {
            openURL(url);
        }
        hideLoadingView();
    }
}
