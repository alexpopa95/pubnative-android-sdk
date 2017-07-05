package net.pubnative.api.layouts.asset_group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import net.pubnative.api.core.request.PNAPIAsset;
import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.sdk.R;

public class PNAPIAssetGroup21 extends PNAPIAssetGroup implements PNAPIAdModel.Listener {

    protected RelativeLayout mRoot;
    protected WebView        mBanner;
    protected RelativeLayout mContentInfo;

    public PNAPIAssetGroup21(Context context) {
        super(context);
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
    public void load() {
        mRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_asset_group_21, this, true);
        mBanner = (WebView) mRoot.findViewById(R.id.pubnative_banner);
        mContentInfo = (RelativeLayout) mRoot.findViewById(R.id.pubnative_content_info_container);
        mBanner.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                invokeOnLoadFinish();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                invokeOnLoadFail(new Exception("Error " + errorCode + " - " + description));
            }
        });
        mBanner.getSettings().setJavaScriptEnabled(true);
        mBanner.loadData(mAdModel.getAssetHtml(PNAPIAsset.HTML_BANNER).replace("\\", ""), "text/html", "UTF-8");

        View contentInfo = mAdModel.getContentInfo(mContext);
        if (contentInfo != null) {
            mContentInfo.addView(contentInfo);
        }
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
