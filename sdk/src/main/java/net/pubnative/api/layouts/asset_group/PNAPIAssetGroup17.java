package net.pubnative.api.layouts.asset_group;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.sdk.R;
import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.api.core.utils.PNAPIImageDownloader;

public class PNAPIAssetGroup17 extends PNAPIAssetGroup implements PNAPIAdModel.Listener {

    protected RelativeLayout mRoot;
    protected RelativeLayout mHeader;
    protected RelativeLayout mBody;
    protected TextView       mTitle;
    protected TextView       mDescription;
    protected TextView       mCallToAction;
    protected ImageView      mIcon;
    protected ImageView      mBanner;
    protected IDMap          mIdMap;
    protected RelativeLayout mContentInfo;

    public PNAPIAssetGroup17(Context context) {
        super(context);
    }

    @Override
    public void load() {

        loadView();
        mTitle.setText(mAdModel.getTitle());
        mDescription.setText(mAdModel.getDescription());
        mCallToAction.setText(mAdModel.getCtaText());

        new PNAPIImageDownloader().load(mAdModel.getIconUrl(), new PNAPIImageDownloader.Listener() {
            @Override
            public void onImageLoad(String url, Bitmap bitmap) {

                mIcon.setImageBitmap(bitmap);
                new PNAPIImageDownloader().load(mAdModel.getBannerUrl(), new PNAPIImageDownloader.Listener() {

                    @Override
                    public void onImageLoad(String url, Bitmap bitmap) {
                        mBanner.setImageBitmap(bitmap);
                        invokeOnLoadFinish();
                    }

                    @Override
                    public void onImageFailed(String url, Exception exception) {
                        invokeOnLoadFail(exception);
                    }
                });
            }

            @Override
            public void onImageFailed(String url, Exception exception) {
                invokeOnLoadFail(exception);
            }
        });

        View contentInfo = mAdModel.getContentInfo(mContext);
        if (contentInfo != null) {
            mContentInfo.addView(contentInfo);
        }
    }

    protected void loadView() {

        mRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_asset_group_17, this, true);

        mHeader = (RelativeLayout) mRoot.findViewById(R.id.pubnative_header);
        mBody = (RelativeLayout) mRoot.findViewById(R.id.pubnative_body);

        mTitle = (TextView) mBody.findViewById(R.id.pubnative_title);
        mIcon = (ImageView) mBody.findViewById(R.id.pubnative_icon);
        mBanner = (ImageView) mHeader.findViewById(R.id.pubnative_banner);

        mDescription = (TextView) mRoot.findViewById(R.id.pubnative_description);
        mCallToAction = (TextView) mRoot.findViewById(R.id.pubnative_callToAction);
        mContentInfo = (RelativeLayout) mRoot.findViewById(R.id.pubnative_content_info_container);
    }

    @Override
    public void startTracking() {

        mAdModel.startTracking(mRoot, this);
    }

    @Override
    public void stopTracking() {

        mAdModel.stopTracking();
    }

    @Override
    public IDMap getIDMap() {

        if (mIdMap == null) {
            mIdMap = new IDMap();
            mIdMap.title = mTitle.getId();
            mIdMap.description = mDescription.getId();
            mIdMap.icon = mIcon.getId();
            mIdMap.banner = mBanner.getId();
            mIdMap.callToAction = mCallToAction.getId();
        }
        return mIdMap;
    }

    @Override
    public void onPNAPIAdModelImpression(PNAPIAdModel PNAPIAdModel, View view) {
        invokeOnImpressionConfirmed();
    }

    @Override
    public void onPNAPIAdModelClick(PNAPIAdModel PNAPIAdModel, View view) {
        invokeOnClick();
    }

    @Override
    public void onPNAPIAdModelOpenOffer(PNAPIAdModel PNAPIAdModel) {
        // Do nothing
    }
}
