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

package net.pubnative.sdk.layouts.adapter.small;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.api.core.utils.PNAPIImageDownloader;
import net.pubnative.sdk.layouts.PNSmallLayoutView;
import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.R;

public class PNSmallLayoutRequestView extends PNSmallLayoutView {

    private static final String TAG = PNSmallLayoutContainerView.class.getSimpleName();

    protected RelativeLayout mRootView;
    protected ImageView      mIcon;
    protected TextView       mCallToAction;
    protected RelativeLayout mBodyView;
    protected TextView       mTitle;
    protected IconPosition   mCurrentIconPosition = IconPosition.LEFT;
    protected RelativeLayout mContentInfoView;

    public PNSmallLayoutRequestView(Context context) {
        super(context);
    }

    @Override
    public void setAdBackgroundColor(int color) {
        if (mRootView != null) {
            mRootView.setBackgroundColor(color);
        }
    }

    // TITLE
    @Override
    public void setTitleTextColor(int color) {
        if (mTitle != null) {
            mTitle.setTextColor(color);
        }
    }

    @Override
    public void setTitleTextSize(float size) {
        if (mTitle != null) {
            mTitle.setTextSize(size);
        }
    }

    @Override
    public void setTitleTextFont(Typeface font) {
        if (mTitle != null) {
            mTitle.setTypeface(font);
        }
    }

    // CTA
    @Override
    public void setCallToActionBackgroundColor(int color) {
        if (mCallToAction != null) {
            mCallToAction.setBackgroundColor(color);
        }
    }

    @Override
    public void setCallToActionTextColor(int color) {
        if (mCallToAction != null) {
            mCallToAction.setTextColor(color);
        }
    }

    @Override
    public void setCallToActionTextSize(float size) {
        if (mCallToAction != null) {
            mCallToAction.setTextSize(size);
        }
    }

    @Override
    public void setCallToActionTextFont(Typeface font) {
        if (mCallToAction != null) {
            mCallToAction.setTypeface(font);
        }
    }

    @Override
    public void setIconPosition(IconPosition position) {
        Log.v(TAG, "setIconPosition");

        if (mIcon != null && mBodyView != null) {

            mCurrentIconPosition = position;

            RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) mIcon.getLayoutParams();
            RelativeLayout.LayoutParams bodyParams = (RelativeLayout.LayoutParams) mBodyView.getLayoutParams();
            RelativeLayout.LayoutParams infoParams = (RelativeLayout.LayoutParams) mContentInfoView.getLayoutParams();

            switch (position) {
                case RIGHT: {
                    iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

                    infoParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    infoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

                    bodyParams.addRule(RelativeLayout.RIGHT_OF, 0);
                    bodyParams.addRule(RelativeLayout.LEFT_OF, mIcon.getId());
                    bodyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    bodyParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }
                break;
                case LEFT: {
                    iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                    infoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    infoParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                    bodyParams.addRule(RelativeLayout.RIGHT_OF, mIcon.getId());
                    bodyParams.addRule(RelativeLayout.LEFT_OF, 0);
                    bodyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    bodyParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                }
                break;
            }

            mIcon.setLayoutParams(iconParams);
            mBodyView.setLayoutParams(bodyParams);
        }
    }

    public void loadWithAd(Context context, PNAdModel nativeAd) {

        mRootView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_asset_group_1, this, true);

        mIcon = (ImageView) mRootView.findViewById(R.id.pubnative_icon);
        mCallToAction = (TextView) mRootView.findViewById(R.id.pubnative_callToAction);

        mBodyView = (RelativeLayout) mRootView.findViewById(R.id.pubnative_body);
        mTitle = (TextView) mBodyView.findViewById(R.id.pubnative_title);

        mContentInfoView = (RelativeLayout) mRootView.findViewById(R.id.pubnative_content_info_container);

        // Assign values
        nativeAd.withIcon(mIcon)
                .withTitle(mTitle)
                .withCallToAction(mCallToAction)
                .withContentInfoContainer(mContentInfoView);
    }
}
