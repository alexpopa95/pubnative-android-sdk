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
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.api.layouts.PNAPILayout;
import net.pubnative.api.layouts.PNAPILayoutView;
import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.adapter.request.PubnativeLibraryCPICache;
import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.layouts.adapter.PNLayoutFeedAdapter;
import net.pubnative.sdk.core.exceptions.PNException;
import net.pubnative.sdk.layouts.PNMediumLayoutView;

import java.util.Map;

public class PubnativeLibraryNetworkAdapter extends PNLayoutFeedAdapter implements PNAPILayout.LoadListener,
                                                                                   PNAPILayoutView.Listener,
                                                                                   PNAdModel.Listener {
    protected PNAPILayout          mLayout;
    protected PNAPILayoutView      mLayoutView;
    protected PNLibraryAdapterView mCachedView;

    @Override
    protected void request(Context context, Map<String, String> networkData) {
        if (context == null || networkData == null) {
            invokeLoadFail(PNException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {

            mCachedView = null;
            mLayoutView = null;
            mLayout = new PNAPILayout();
            for (String key :
                    networkData.keySet()) {
                String value = networkData.get(key);
                mLayout.setParameter(key, value);
            }
            if (PNSettings.targeting != null) {
                Map<String, String> targeting = PNSettings.targeting.toDictionary();
                for (String key : targeting.keySet()) {
                    String value = targeting.get(key);
                    mLayout.setParameter(key, value);
                }
            }
            mLayout.setTestMode(PNSettings.isTestModeEnabled);
            mLayout.setCoppaMode(PNSettings.isCoppaModeEnabled);
            mLayout.load(context, PNAPILayout.Size.MEDIUM, this);
        }
    }

    @Override
    public void startTracking() {
        if (mLayoutView != null) {
            mLayoutView.setListener(this);
            mLayoutView.startTracking();
        }
    }

    @Override
    public void stopTracking() {
        if (mLayoutView != null) {
            mLayoutView.stopTracking();
            mLayoutView.setListener(null);
        }
    }

    @Override
    public PNMediumLayoutView getView(Context context) {
        if (mCachedView == null && mLayoutView != null) {
            mCachedView = new PNLibraryAdapterView(mContext);
            mCachedView.loadWithView(mLayoutView);
        }
        return mCachedView;
    }

    protected void fetch() {

        mLayout.fetch(new PNAPILayout.FetchListener() {
            @Override
            public void onPubnativeLayoutFetchFinish(PNAPILayout layout, PNAPILayoutView view) {
                mLayoutView = view;
                invokeLoadSuccess();
            }

            @Override
            public void onPubnativeLayoutFetchFail(PNAPILayout layout, Exception exception) {
                invokeLoadFail(exception);
            }
        });
    }


    @Override
    public void onPubnativeLayoutLoadFinish(PNAPILayout layout, PNAPIAdModel model) {

        if (mIsCPICacheEnabled && model.isRevenueModelCPA()) {

            PNAPIAdModel cachedAdModel = PubnativeLibraryCPICache.get(mContext);
            if (cachedAdModel != null) {
                mLayout.setAdModel(model);
            }
        }

        fetch();
    }

    @Override
    public void onPubnativeLayoutLoadFail(PNAPILayout layout, Exception exception) {
        if (mIsCPICacheEnabled) {
            PNAPIAdModel cachedAdModel = PubnativeLibraryCPICache.get(mContext);
            if (cachedAdModel == null) {
                invokeLoadFail(exception);
            } else {
                mLayout.setAdModel(cachedAdModel);
                fetch();
            }
        } else {
            invokeLoadFail(exception);
        }
    }

    @Override
    public void onPubnativeLayoutViewImpressionConfirmed(PNAPILayoutView view) {
        invokeImpression();
    }

    @Override
    public void onPubnativeLayoutViewClick(PNAPILayoutView view) {
        invokeClick();
    }

    @Override
    public void onPNAdImpression(PNAdModel model) {
        invokeImpression();
    }

    @Override
    public void onPNAdClick(PNAdModel model) {
        invokeClick();
    }

    public class PNLibraryAdapterView extends PNMediumLayoutContainerView {
        protected ImageView mIcon;
        protected TextView  mTitle;
        protected TextView  mDescription;
        protected ImageView mBanner;
        protected TextView  mCallToAction;


        public PNLibraryAdapterView(Context context) {
            super(context);
        }

        @Override
        public void setIconPosition(IconPosition position) {
        }

        @Override
        public void setBackgroundColor(int color) {
            if (mLayoutView != null) {
                mLayoutView.setBackgroundColor(color);
            }
        }

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
        public void setCallToActionBackgroundColor(int color) {
            if (mCallToAction != null) {
                mCallToAction.setBackgroundColor(color);
            }
        }

        @Override
        protected void loadWithView(View view) {

            super.loadWithView(view);

            if (mLayoutView != null && mLayoutView.getIDMap() != null) {
                mIcon = (ImageView) mLayoutView.findViewById(mLayoutView.getIDMap().icon);
                mTitle = (TextView) mLayoutView.findViewById(mLayoutView.getIDMap().title);
                mDescription = (TextView) mLayoutView.findViewById(mLayoutView.getIDMap().description);
                mBanner = (ImageView) mLayoutView.findViewById(mLayoutView.getIDMap().banner);
                mCallToAction = (TextView) mLayoutView.findViewById(mLayoutView.getIDMap().callToAction);
            }
        }
    }
}
