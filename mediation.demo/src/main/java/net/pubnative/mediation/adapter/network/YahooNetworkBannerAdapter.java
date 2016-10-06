package net.pubnative.mediation.adapter.network;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdBannerListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkBannerAdapter extends PubnativeNetworkBannerAdapter
        implements FlurryAdBannerListener {

    public static final String TAG = YahooNetworkBannerAdapter.class.getSimpleName();
    protected FlurryAdBanner mAdBanner;
    private   Context        mContext;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkBannerAdapter(Map data) {

        super(data);
    }
    //==============================================================================================
    // Public
    //==============================================================================================

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        mContext = context;
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            createRequest(context);
        }
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        mAdBanner.displayAd();
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mAdBanner != null) {
            FlurryAgent.onEndSession(mContext);
            mAdBanner.destroy();
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mAdBanner != null) {
            result = mAdBanner.isReady();
        }
        return result;
    }

    @Override
    public void hide() {

        Log.v(TAG, "hide");
    }
    //==============================================================================================
    // Private
    //==============================================================================================

    protected void createRequest(Context context) {

        Log.v(TAG, "createRequest");
        mContext = context;
        String adSpaceName = (String) mData.get(YahooNetworkRequestAdapter.KEY_AD_SPACE_NAME);
        String apiKey = (String) mData.get(YahooNetworkRequestAdapter.KEY_FLURRY_API_KEY);
        if (TextUtils.isEmpty(adSpaceName) || TextUtils.isEmpty(apiKey)) {
            invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
        } else {
            // initialize flurry with new apiKey
            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .build(mContext, apiKey);
            // execute/resume session
            if (!FlurryAgent.isSessionActive()) {
                FlurryAgent.onStartSession(context);
            }
            // create container for banner
            ViewGroup rootView = (ViewGroup) ((Activity) mContext).findViewById(android.R.id.content);
            RelativeLayout container = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container.setLayoutParams(params);
            rootView.addView(container);
            //create flurry banner
            mAdBanner = new FlurryAdBanner(mContext, container, adSpaceName);
            mAdBanner.setListener(this);
            // Add targeting
            FlurryAdTargeting targeting = getTargeting();
            if (targeting != null) {
                mAdBanner.setTargeting(targeting);
            }
            mAdBanner.fetchAd();
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
    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // FlurryAdBannerListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onFetched(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onFetched");
        invokeLoadFinish();
    }

    @Override
    public void onRendered(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onRendered");
        invokeShow();
    }

    @Override
    public void onShowFullscreen(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onShowFullscreen");
    }

    @Override
    public void onCloseFullscreen(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onCloseFullscreen");
        destroy();
    }

    @Override
    public void onAppExit(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onAppExit");
    }

    @Override
    public void onClicked(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onClicked");
        invokeClick();
    }

    @Override
    public void onVideoCompleted(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onVideoCompleted");
    }

    @Override
    public void onError(FlurryAdBanner flurryAdBanner, FlurryAdErrorType flurryAdErrorType, int errorCode) {

        Log.v(TAG, "onError: " + errorCode);
        Map extras = new HashMap();
        extras.put("code", errorCode);
        extras.put("type", flurryAdErrorType.name());
        invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extras));
    }
}
