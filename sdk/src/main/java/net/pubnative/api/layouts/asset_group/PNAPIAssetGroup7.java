package net.pubnative.api.layouts.asset_group;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.pubnative.sdk.R;
import net.pubnative.api.core.request.PNAPIAsset;
import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.api.core.utils.PNAPIImageDownloader;

public class PNAPIAssetGroup7 extends PNAPIAssetGroup implements PNAPIAdModel.Listener {

    protected RelativeLayout mRoot;
    protected ImageView      mBanner;
    protected RelativeLayout mContentInfo;

    public PNAPIAssetGroup7(Context context) {
        super(context);
    }

    @Override
    public void load() {

        mRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_asset_group_7, this, true);
        mBanner = (ImageView) mRoot.findViewById(R.id.pubnative_banner);
        mContentInfo = (RelativeLayout) mRoot.findViewById(R.id.pubnative_content_info_container);
        new PNAPIImageDownloader().load(mAdModel.getAssetUrl(PNAPIAsset.STANDARD_BANNER), new PNAPIImageDownloader.Listener() {
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

        View contentInfo = mAdModel.getContentInfo(mContext);
        if (contentInfo != null) {
            mContentInfo.addView(contentInfo);
        }
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

        return null;
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

    }
}
