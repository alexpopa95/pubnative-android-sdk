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

package net.pubnative.sdk.layouts.adapter.medium;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.api.core.utils.PNAPIImageDownloader;
import net.pubnative.sdk.layouts.PNMediumLayoutView;
import net.pubnative.sdk.R;
import net.pubnative.sdk.core.request.PNAdModel;

public class PNMediumLayoutRequestView extends PNMediumLayoutView {

    private static final String TAG = PNMediumLayoutRequestView.class.getSimpleName();

    protected ImageView      mIcon;
    protected TextView       mTitle;
    protected TextView       mDescription;
    protected TextView       mCallToAction;
    protected RelativeLayout mRootView;
    protected RelativeLayout mHeaderView;
    protected RelativeLayout mBodyView;
    protected RelativeLayout mFooterView;
    protected RelativeLayout mContentInfoView;

    public PNMediumLayoutRequestView(Context context) {
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

    // DESCRIPTION
    @Override
    public void setDescriptionTextColor(int color) {
        if (mDescription != null) {
            mDescription.setTextColor(color);
        }
    }

    @Override
    public void setDescriptionTextSize(float size) {
        if (mDescription != null) {
            mDescription.setTextSize(size);
        }
    }

    @Override
    public void setDescriptionTextFont(Typeface font) {
        if (mDescription != null) {
            mDescription.setTypeface(font);
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

        if (mIcon != null && mTitle != null) {

            RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) mIcon.getLayoutParams();
            RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) mTitle.getLayoutParams();

            switch (position) {
                case RIGHT: {
                    iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

                    titleParams.addRule(RelativeLayout.RIGHT_OF, 0);
                    titleParams.addRule(RelativeLayout.LEFT_OF, mIcon.getId());
                    titleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }
                break;
                case LEFT: {
                    iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                    titleParams.addRule(RelativeLayout.RIGHT_OF, mIcon.getId());
                    titleParams.addRule(RelativeLayout.LEFT_OF, 0);
                    titleParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                }
                break;
            }

            mIcon.setLayoutParams(iconParams);
            mTitle.setLayoutParams(titleParams);
        }
    }

    @Override
    public void setBannerPosition(BannerPosition position) {
        if (mHeaderView != null && mBodyView != null && mFooterView != null) {

            RelativeLayout.LayoutParams headerParams = (RelativeLayout.LayoutParams) mHeaderView.getLayoutParams();
            RelativeLayout.LayoutParams bodyParams = (RelativeLayout.LayoutParams) mBodyView.getLayoutParams();
            RelativeLayout.LayoutParams footerParams = (RelativeLayout.LayoutParams) mFooterView.getLayoutParams();

            switch (position) {

                case TOP: {
                    bodyParams.addRule(RelativeLayout.BELOW, 0);
                    bodyParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                    headerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    headerParams.addRule(RelativeLayout.BELOW, mBodyView.getId());

                    footerParams.addRule(RelativeLayout.BELOW, mHeaderView.getId());
                }
                break;
                case CENTER: {
                    headerParams.addRule(RelativeLayout.BELOW, 0);
                    headerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                    bodyParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    bodyParams.addRule(RelativeLayout.BELOW, mHeaderView.getId());

                    footerParams.addRule(RelativeLayout.BELOW, mBodyView.getId());
                }
                break;
                case BOTTOM: {
                    headerParams.addRule(RelativeLayout.BELOW, 0);
                    headerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                    footerParams.addRule(RelativeLayout.BELOW, mHeaderView.getId());

                    bodyParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    bodyParams.addRule(RelativeLayout.BELOW, mFooterView.getId());
                }
                break;
            }

            mHeaderView.setLayoutParams(headerParams);
            mBodyView.setLayoutParams(bodyParams);
            mFooterView.setLayoutParams(footerParams);
        }
    }

    protected RelativeLayout.LayoutParams getBodyContentParams() {

        return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                               RelativeLayout.LayoutParams.MATCH_PARENT);
    }

    public void loadWithAd(Context context, PNAdModel nativeAd) {

        mRootView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.pubnative_asset_group_5, this, true);

        mHeaderView = (RelativeLayout) mRootView.findViewById(R.id.pubnative_header);
        mBodyView = (RelativeLayout) mRootView.findViewById(R.id.pubnative_body);
        mFooterView = (RelativeLayout) mRootView.findViewById(R.id.pubnative_footer);

        mIcon = (ImageView) mHeaderView.findViewById(R.id.pubnative_icon);
        mTitle = (TextView) mHeaderView.findViewById(R.id.pubnative_title);
        mDescription = (TextView) mFooterView.findViewById(R.id.pubnative_description);
        mCallToAction = (TextView) mFooterView.findViewById(R.id.pubnative_callToAction);

        mContentInfoView = (RelativeLayout) mRootView.findViewById(R.id.pubnative_content_info_container);

        // Assign values
        nativeAd.withTitle(mTitle)
                .withDescription(mDescription)
                .withCallToAction(mCallToAction)
                .withBanner(mBodyView)
                .withIcon(mIcon)
                .withContentInfoContainer(mContentInfoView);
    }
}
