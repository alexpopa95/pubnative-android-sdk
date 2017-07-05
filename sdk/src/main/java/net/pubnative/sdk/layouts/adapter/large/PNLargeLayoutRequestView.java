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

package net.pubnative.sdk.layouts.adapter.large;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.api.core.utils.PNAPIImageDownloader;
import net.pubnative.sdk.R;
import net.pubnative.sdk.layouts.PNLayoutView;
import net.pubnative.sdk.core.request.PNAdModel;

public class PNLargeLayoutRequestView extends PNLayoutView {

    private static final String TAG = PNLargeLayoutRequestView.class.getSimpleName();

    protected RelativeLayout mHeader;
    protected RelativeLayout mBody;
    protected ImageView      mIcon;
    protected TextView       mTitle;
    protected TextView       mDescription;
    protected TextView       mCallToAction;
    protected RatingBar      mRating;
    protected RelativeLayout mRootView;
    protected RelativeLayout mContentInfoView;

    public PNLargeLayoutRequestView(Context context) {
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

    public void loadWithAd(Context context, PNAdModel nativeAd) {

        mRootView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.pubnative_asset_group_14, this, true);

        mBody = (RelativeLayout) mRootView.findViewById(R.id.pubnative_body);

        mHeader = (RelativeLayout) mRootView.findViewById(R.id.pubnative_header);

        mIcon = (ImageView) mBody.findViewById(R.id.pubnative_icon);
        mRating = (RatingBar) mBody.findViewById(R.id.pubnative_rating);
        mTitle = (TextView) mBody.findViewById(R.id.pubnative_title);

        mDescription = (TextView) mRootView.findViewById(R.id.pubnative_description);
        mCallToAction = (TextView) mRootView.findViewById(R.id.pubnative_callToAction);

        mContentInfoView = (RelativeLayout) mRootView.findViewById(R.id.pubnative_content_info_container);

        // Assign values
        nativeAd.withTitle(mTitle)
                .withRating(mRating)
                .withDescription(mDescription)
                .withCallToAction(mCallToAction)
                .withIcon(mIcon)
                .withBanner(mHeader)
                .withContentInfoContainer(mContentInfoView);
    }
}
