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

package net.pubnative.sdk.core.request;

import net.pubnative.sdk.core.adapter.request.PNAdapter;
import net.pubnative.sdk.core.config.model.PNNetworkModel;

import java.util.HashMap;
import java.util.Map;

public class PNRequestCache extends PNRequest {

    @Override
    protected int getRequestTimeout(PNNetworkModel network) {

        int result = 0;
        if (network != null) {
            result = network.getAdCacheTimeout();
        }
        return result;
    }

    @Override
    protected boolean isCaching() {
        return true;
    }

    @Override
    protected boolean canUseCache(PNNetworkModel network) {
        // Ensure to return false so we never use cached ads when requesting for caching
        return false;
    }

    @Override
    protected void sendRequestInsight() {

        Map<String, String> extras = new HashMap<String, String>();
        extras.put("fetchAssets", "1");
        mInsight.sendRequestInsight(extras);
    }

    @Override
    protected void cache() {
        // Do nothing, avoid from fetchAssets when failed/finished
    }

    @Override
    protected void request(PNAdapter adapter, PNNetworkModel network, Map extras) {
        if (network.isAdCacheEnabled()) {
            // If the network allows ad fetchAssets, then we continue, otherwise we skip this
            super.request(adapter, network, extras);
        } else {
            getNextNetwork();
        }
    }
}
