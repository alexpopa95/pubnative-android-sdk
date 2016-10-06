package net.pubnative.mediation.adapter.network;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;

public class AdMobNetworkAdapter extends PubnativeNetworkHub {

    @Override
    public PubnativeNetworkRequestAdapter getRequestAdapter() {

        return new AdMobNetworkRequestAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkInterstitialAdapter getInterstitialAdapter() {

        return new AdMobNetworkInterstitialAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkFeedBannerAdapter getFeedBannerAdapter() {
        return null;
    }

    @Override
    public PubnativeNetworkBannerAdapter getBannerAdapter() {

        return new AdMobNetworkBannerAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkVideoAdapter getVideoAdapter() {
        return new AdMobNetworkVideoAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkFeedVideoAdapter getFeedVideoAdapter() {
        return null;
    }
}
