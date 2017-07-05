package net.pubnative.api.layouts.asset_group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import net.pubnative.sdk.R;
import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.player.VASTParser;
import net.pubnative.player.VASTPlayer;
import net.pubnative.player.model.VASTModel;

public class PNAPIAssetGroup15 extends PNAPIAssetGroup implements PNAPIAdModel.Listener, VASTPlayer.Listener {

    protected RelativeLayout mRoot;
    protected VASTPlayer     mPlayer;
    protected IDMap          mIdMap;
    protected RelativeLayout mContentInfo;

    public PNAPIAssetGroup15(Context context) {
        super(context);
    }

    @Override
    public void load() {

        mRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_asset_group_15, this, true);
        mPlayer = (VASTPlayer) mRoot.findViewById(R.id.pubnative_player);
        mContentInfo = (RelativeLayout) mRoot.findViewById(R.id.pubnative_content_info_container);

        mPlayer.setListener(this);

        new VASTParser(getContext()).setListener(new VASTParser.Listener() {
            @Override
            public void onVASTParserError(int error) {
                invokeOnLoadFail(new Exception("Cannot load VAST, parsing error " + error));
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {
                mPlayer.load(model);
            }
        }).execute(mAdModel.getVast());

        View contentInfo = mAdModel.getContentInfo(mContext);
        if (contentInfo != null) {
            mContentInfo.addView(contentInfo);
        }
    }

    @Override
    public void startTracking() {

        mAdModel.startTracking(mRoot, this);
        mPlayer.play();
    }

    @Override
    public void stopTracking() {

        mPlayer.stop();
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
        // Do nothing
    }

    @Override
    public void onVASTPlayerLoadFinish() {
        invokeOnLoadFinish();
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {
        invokeOnLoadFail(exception);
    }

    @Override
    public void onVASTPlayerPlaybackStart() {
        // Do nothing
    }

    @Override
    public void onVASTPlayerPlaybackFinish() {
        // Do nothing
    }

    @Override
    public void onVASTPlayerOpenOffer() {
        invokeOnClick();
    }
}
