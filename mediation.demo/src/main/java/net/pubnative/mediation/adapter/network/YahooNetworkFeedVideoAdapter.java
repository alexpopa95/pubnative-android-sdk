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

package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkFeedVideoAdapter extends PubnativeNetworkFeedVideoAdapter
        implements FlurryAdNativeListener {

    private static final String  TAG = YahooNetworkFeedVideoAdapter.class.getSimpleName();
    public  static final String KEY_AD_SPACE_NAME  = "ad_space_name";
    public  static final String KEY_FLURRY_API_KEY = "api_key";
    private static final String AD_ASSET_VIDEO_URL = "videoUrl";
    //==============================================================================================
    // Properties
    //==============================================================================================
    private FlurryAdNative mFeedVideo;
    private boolean mIsLoaded;
    protected Context mContext;
    private FrameLayout mVideoAdContainer;

    /**
     * Creates a new instance of YahooNetworkFeedVideoAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkFeedVideoAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String adSpaceName = (String) mData.get(KEY_AD_SPACE_NAME);
            String apiKey = (String) mData.get(KEY_FLURRY_API_KEY);
            if (TextUtils.isEmpty(adSpaceName) || TextUtils.isEmpty(apiKey)) {
                invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                mContext = context;
                new FlurryAgent.Builder()
                        .withLogEnabled(true)
                        .build(context, apiKey);
                // execute/resume session
                if (!FlurryAgent.isSessionActive()) {
                    FlurryAgent.onStartSession(context);
                }
                // Make request
                FlurryAdNative flurryAdNative = new FlurryAdNative(context, adSpaceName);
                // Add targeting
                FlurryAdTargeting targeting = getTargeting();
                if (targeting != null) {
                    flurryAdNative.setTargeting(targeting);
                }
                flurryAdNative.setListener(this);
                flurryAdNative.fetchAd();
            }
        }
    }

    protected FlurryAdTargeting getTargeting() {

        FlurryAdTargeting result = null;
        if (mTargeting != null) {
            result = new FlurryAdTargeting();
            result.setAge(mTargeting.age);

            if (mTargeting.gender == null) {
                result.setGender(FlurryGender.UNKNOWN);
            } else if (mTargeting.gender.equals("female")) {
                result.setGender(FlurryGender.FEMALE);
            } else if (mTargeting.gender.equals("male")) {
                result.setGender(FlurryGender.MALE);
            } else {
                result.setGender(FlurryGender.UNKNOWN);
            }

            if (mTargeting.interests != null) {
                Map interests = new HashMap();
                interests.put("interest", TextUtils.join(",", mTargeting.interests));
                result.setKeywords(interests);
            }
        }
        return result;
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mFeedVideo != null) {
            result = mIsLoaded;
        }
        return result;
    }

    @Override
    public void show(ViewGroup container) {

        Log.v(TAG, "show");
        mVideoAdContainer = new FrameLayout(mContext);
        container.addView(mVideoAdContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFeedVideo.getAsset(AD_ASSET_VIDEO_URL).loadAssetIntoView(mVideoAdContainer);
        invokeShow();
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mFeedVideo != null) {
            mFeedVideo.destroy();
        }
    }

    @Override
    public void hide() {
        if (mVideoAdContainer != null && mVideoAdContainer.getParent() != null) {
            ((ViewGroup) mVideoAdContainer.getParent()).removeView(mVideoAdContainer);
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // FlurryAdNativeListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onFetched(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onFetched");

        FlurryAgent.onEndSession(mContext);
        if(flurryAdNative.isVideoAd()) {
            invokeLoadFinish();
        } else {
            Map extras = new HashMap();
            extras.put("code", 0);
            extras.put("type", FlurryAdErrorType.FETCH.name());
            invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extras));
        }
    }

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int errCode) {

        Log.v(TAG, "onError: " + errCode);

        FlurryAgent.onEndSession(mContext);
        Map extras = new HashMap();
        extras.put("code", errCode);
        extras.put("type", flurryAdErrorType.name());
        invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extras));
    }

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onShowFullscreen");
        // Do nothing for now.
    }

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onCloseFullscreen");
        // Do nothing for now.
    }

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onAppExit");
        // Do nothing for now.
    }

    @Override
    public void onClicked(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onClicked");
        // Do nothing for now.
    }

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onImpressionLogged");
        // Do nothing for now.
    }

    @Override
    public void onExpanded(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onExpanded");
    }

    @Override
    public void onCollapsed(FlurryAdNative flurryAdNative) {

        Log.v(TAG, "onCollapsed");
    }
}
