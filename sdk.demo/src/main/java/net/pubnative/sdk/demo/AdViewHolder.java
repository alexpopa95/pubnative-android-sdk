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

package net.pubnative.sdk.demo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.pubnative.sdk.core.request.PNAdModel;
import net.pubnative.sdk.core.request.PNRequest;

/**
 * A class that holds the reference to all views in a cell.
 * This helps us to avoid redundant calls to "findViewById" each
 * time we initialize values into the cell.
 */
public class AdViewHolder implements PNRequest.Listener,
                                     View.OnClickListener {

    private static final String TAG = AdViewHolder.class.getSimpleName();

    protected Context          mContext;
    // Data
    protected CellRequestModel mCellRequestModel;
    // Behaviour
    protected ProgressBar      mAdLoading;
    protected ViewGroup        mAdViewContainer;
    protected Button           mRequestButton;
    // Ad info
    protected TextView         mPlacementID;
    protected TextView         mAdapterName;

    protected ViewGroup      mContentInfo;
    protected TextView       mDescription;
    protected TextView       mTitle;
    protected RatingBar      mRating;
    protected ImageView      mIcon;
    protected RelativeLayout mBanner;

    public AdViewHolder(Context context, View convertView) {
        mContext = context;
        mAdLoading = (ProgressBar) convertView.findViewById(R.id.ad_spinner);
        mAdViewContainer = (ViewGroup) convertView.findViewById(R.id.ad_view_container);
        mRequestButton = (Button) convertView.findViewById(R.id.request_button);
        mRequestButton.setOnClickListener(this);
        mAdapterName = (TextView) convertView.findViewById(R.id.ad_adapter_name_text);
        mPlacementID = (TextView) convertView.findViewById(R.id.placement_id_text);
        mContentInfo = (ViewGroup) convertView.findViewById(R.id.ad_disclosure);
        mTitle = (TextView) convertView.findViewById(R.id.ad_title_text);
        mDescription = (TextView) convertView.findViewById(R.id.ad_description_text);
        mPlacementID = (TextView) convertView.findViewById(R.id.placement_id_text);
        mRating = (RatingBar) convertView.findViewById(R.id.ad_rating);
        mIcon = (ImageView) convertView.findViewById(R.id.ad_icon_image);
        mBanner = (RelativeLayout) convertView.findViewById(R.id.media_container);
    }

    public void setCellRequestModel(CellRequestModel cellRequestModel) {
        if (mCellRequestModel != null && mCellRequestModel.adModel != null) {
            mCellRequestModel.adModel.stopTracking();
        }
        mCellRequestModel = cellRequestModel;
        cleanView();
        renderAd();
    }

    public void cleanView() {
        mContentInfo.removeAllViews();
        mTitle.setText("");
        mDescription.setText("");
        mAdapterName.setText("");
        mRating.setRating(0f);
        mRating.setVisibility(View.GONE);
        mBanner.removeAllViews();
        mIcon.setImageDrawable(null);
        mContentInfo.removeAllViews();
        mAdLoading.setVisibility(View.GONE);
    }

    public void renderAd() {
        // Placement data
        mPlacementID.setText("Placement ID: " + mCellRequestModel.placementID);
        PNAdModel model = mCellRequestModel.adModel;
        if (model != null) {
            // Privacy container
            String adapterNameText = model.getClass().getSimpleName();
            mAdapterName.setText(adapterNameText);
            mRating.setVisibility(View.VISIBLE);

            // Tracking with views
            model.withTitle(mTitle)
                 .withDescription(mDescription)
                 .withIcon(mIcon)
                 .withBanner(mBanner)
                 .withRating(mRating)
                 .withContentInfoContainer(mContentInfo)
                 .startTracking(mAdViewContainer);
        }
    }

    public void onRequestClick(View v) {
        cleanView();
        mAdLoading.setVisibility(View.VISIBLE);
        mCellRequestModel.request.start(mContext, Settings.appToken, mCellRequestModel.placementID, this);
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PNRequest.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPNRequestLoadFinish(PNRequest request, PNAdModel ad) {
        mAdLoading.setVisibility(View.GONE);
        if (mCellRequestModel.adModel != null) {
            mCellRequestModel.adModel.stopTracking();
        }
        mCellRequestModel.adModel = ad;
        renderAd();
    }

    @Override
    public void onPNRequestLoadFail(PNRequest request, Exception exception) {
        Toast.makeText(mContext, exception.toString(), Toast.LENGTH_LONG).show();
        mAdLoading.setVisibility(View.GONE);
        mCellRequestModel.adModel = null;
        cleanView();
    }

    @Override
    public void onClick(View v) {

        onRequestClick(v);
    }
}
