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

package net.pubnative.mediation.adapter.network;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;

public class YahooNetworkAdapter extends PubnativeNetworkHub {

    @Override
    public PubnativeNetworkRequestAdapter getRequestAdapter() {

        return new YahooNetworkRequestAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkInterstitialAdapter getInterstitialAdapter() {

        return new YahooNetworkInterstitialAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkFeedBannerAdapter getFeedBannerAdapter() {

        return null;
    }

    @Override
    public PubnativeNetworkBannerAdapter getBannerAdapter() {

        return new YahooNetworkBannerAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkVideoAdapter getVideoAdapter() {

        return new YahooNetworkVideoAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkFeedVideoAdapter getFeedVideoAdapter() {

        return new YahooNetworkFeedVideoAdapter(mNetworkData);
    }
}
