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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;
import com.flurry.android.ads.FlurryAdNativeListener;

import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.core.utils.PNBitmapDownloader;
import net.pubnative.sdk.R;

public class YahooNativeAdModel extends PNAdModel implements FlurryAdNativeListener {

    private static String TAG = YahooNativeAdModel.class.getSimpleName();
    protected FlurryAdNative mFlurryAdNative;

    public YahooNativeAdModel(Context context, FlurryAdNative flurryAdNative) {

        super(context);
        mFlurryAdNative = flurryAdNative;
    }

    //==============================================================================================
    // PNAPIAdModel methods
    //==============================================================================================
    // Fields
    //----------------------------------------------------------------------------------------------

    @Override
    public String getTitle() {
        // The Ad headline, typically a single line. Type: STRING
        return getStringValueOfFirstAsset("headline");
    }

    @Override
    public String getDescription() {
        // The call to action summary of the advertisement. Type: STRING
        return getStringValueOfFirstAsset("summary");
    }

    @Override
    public String getIconUrl() {
        // secOrigImg: 	The secured original image, size: 627px x 627px. Optional asset, not present for the video ads
        // secImage:    The secured image, size: 82px x 82px. Optional asset, not present for the video ads.
        return getStringValueOfFirstAsset("secOrigImg", "secImage");
    }

    @Override
    public String getBannerUrl() {
        // secHqImage:  The secured high quality image, size: 1200px x 627px. Optional asset, not present for the video ads
        return getStringValueOfFirstAsset("secHqImage");
    }

    @Override
    public String getCallToAction() {
        /**
         * Yahoo currently does not provide the short Call To Action (CTA)
         * asset or string at this time. Instead, you can create your own
         * CTA for each ad. For an ad that contains app install specific assets like
         * “appCategory” or “appRating”, the CTA could be ‘Install Now’.
         * For an ad that does not contain app specific assets, the CTA could be ‘Read More’.
         */
        String result = "Read More";
        if (getStringValueOfFirstAsset("appCategory") != null || getStringValueOfFirstAsset("appRating") != null) {
            result = "Install Now";
        }
        return result;
    }

    @Override
    public float getStarRating() {
        float result = 0;
        String appRating = getStringValueOfFirstAsset("appRating");
        if (appRating != null) {
            String[] parts = appRating.split("/");
            if (parts.length == 2) {
                try {
                    int ratingVal = Integer.parseInt(parts[0]);
                    int scaleVal = Integer.parseInt(parts[1]);
                    if (scaleVal != 0) {
                        result = (ratingVal / scaleVal) * 5;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getStarRating - Error while parsing star rating :" + e);
                }
            }
        }
        return result;
    }

    @Override
    public View getContentInfoView() {
        LinearLayout parent = new LinearLayout(mContext);
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setGravity(Gravity.CENTER_VERTICAL);
        parent.setBackgroundColor(mContext.getResources().getColor(R.color.pubnative_content_info_background_color));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);

        TextView adText = new TextView(mContext);
        parent.addView(adText);
        adText.setText(mContext.getResources().getText(R.string.ad));
        adText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.pubnative_social_context_size));
        adText.setTextColor(Color.BLACK);
        adText.setLayoutParams(params);

        final ImageView image = new ImageView(mContext);
        parent.addView(image);
        String contentInfoImage = getContentInfoImageUrl();
        if(!TextUtils.isEmpty(contentInfoImage)) {
            new PNBitmapDownloader().download(contentInfoImage, new PNBitmapDownloader.DownloadListener() {
                @Override
                public void onDownloadFinish(String url, Bitmap bitmap) {
                    image.setImageBitmap(bitmap);
                }

                @Override
                public void onDownloadFailed(String url, Exception exception) {
                    Log.e(TAG, "onDownloadFailed: Can't download branding logo!", exception);
                }
            });
        }

        return parent;
    }

    // Extension
    //----------------------------------------------------------------------------------------------
    @Override
    protected String getContentInfoImageUrl() {
        return getStringValueOfFirstAsset("secHqBrandingLogo");
    }

    // Tracking
    //----------------------------------------------------------------------------------------------

    @Override
    public void startTracking(ViewGroup adView) {
        if (mFlurryAdNative != null && adView != null) {
            mFlurryAdNative.setListener(this);
            mFlurryAdNative.setTrackingView(adView);
        }
    }

    @Override
    public void stopTracking() {
        if (mFlurryAdNative != null) {
            mFlurryAdNative.setListener(null);
            mFlurryAdNative.removeTrackingView();
        }
    }

    //==============================================================================================
    // YahooNativeAdModel methods
    //==============================================================================================
    protected String getStringValueOfFirstAsset(String... keys) {
        String result = null;
        if (mFlurryAdNative != null) {
            for (String key : keys) {
                FlurryAdNativeAsset asset = mFlurryAdNative.getAsset(key);
                if (asset != null) {
                    result = asset.getValue();
                    break;
                }
            }
        }
        return result;
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // FlurryAdNativeListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onFetched(FlurryAdNative flurryAdNative) {}

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative) {}

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative) {}

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative) {}

    @Override
    public void onClicked(FlurryAdNative flurryAdNative) {
        invokeClick();
    }

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative) {
        invokeImpressionConfirmed();
    }

    @Override
    public void onExpanded(FlurryAdNative flurryAdNative) {}

    @Override
    public void onCollapsed(FlurryAdNative flurryAdNative) {}

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int i) {}
}
