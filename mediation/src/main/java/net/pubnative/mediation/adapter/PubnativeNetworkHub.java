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

package net.pubnative.mediation.adapter;

import net.pubnative.mediation.adapter.network.PubnativeNetworkBannerAdapter;
import net.pubnative.mediation.adapter.network.PubnativeNetworkFeedBannerAdapter;
import net.pubnative.mediation.adapter.network.PubnativeNetworkFeedVideoAdapter;
import net.pubnative.mediation.adapter.network.PubnativeNetworkInterstitialAdapter;
import net.pubnative.mediation.adapter.network.PubnativeNetworkRequestAdapter;
import net.pubnative.mediation.adapter.network.PubnativeNetworkVideoAdapter;

import java.util.Map;

public abstract class PubnativeNetworkHub {

    protected Map mNetworkData;

    /**
     * Sets the network data to be used by the adapter hub to create the different formats adapters
     *
     * @param data map with the network data required
     */
    public void setNetworkData(Map data) {

        mNetworkData = data;
    }

    /**
     * This method will return the network dependent adapter for requests
     *
     * @return valid PubnativeNetworkRequestAdapter
     */
    public abstract PubnativeNetworkRequestAdapter getRequestAdapter();

    /**
     * Gets the network dependent adapter for interstitials
     *
     * @return valid PubnativeNetworkInterstitialAdapter
     */
    public abstract PubnativeNetworkInterstitialAdapter getInterstitialAdapter();

    /**
     * Gets the network dependent adapter for feedBanner
     *
     * @return valid PubnativeNetworkFeedBannerAdapter
     */
    public abstract PubnativeNetworkFeedBannerAdapter getFeedBannerAdapter();

    /**
     * Gets the network dependent adapter for banner
     *
     * @return valid PubnativeNetworkBannerAdapter
     */
    public abstract PubnativeNetworkBannerAdapter getBannerAdapter();

    /**
     * Gets the network dependent adapter for video
     *
     * @return valid PubnativeNetworkVideoAdapter
     */
    public abstract PubnativeNetworkVideoAdapter getVideoAdapter();

    /**
     * Gets the network dependent adapter for feed video
     *
     * @return valid PubnativeNetworkFeedVideoAdapter
     */
    public abstract PubnativeNetworkFeedVideoAdapter getFeedVideoAdapter();
}
