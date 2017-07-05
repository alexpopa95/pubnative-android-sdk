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

package net.pubnative.sdk.core.adapter.request;

import net.pubnative.api.core.request.PNAPIRequest;
import net.pubnative.api.core.request.model.PNAPIAdModel;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PubnativeLibraryNetworkAdapterTest {

    @Test
    public void onPubnativeRequestSuccess_withCpiAdAndCacheUsageEnabled_shouldReturnCpiAdFromCache() {
        Map mapData = mock(Map.class);
        PubnativeLibraryNetworkAdapter requestAdapter = spy(new PubnativeLibraryNetworkAdapter(mapData));
        requestAdapter.mIsCPICacheEnabled = true;

        PNAPIAdModel returnedAd = mock(PNAPIAdModel.class);
        when(returnedAd.isRevenueModelCPA()).thenReturn(true);

        PNAPIAdModel cachedAd = mock(PNAPIAdModel.class);
        when(cachedAd.isRevenueModelCPA()).thenReturn(true);

        // add cpi ad
        PubnativeLibraryCPICache.getInstance().clear();
        PubnativeLibraryCPICache.getInstance().enqueue(cachedAd);

        requestAdapter.onPNAPIRequestFinish(mock(PNAPIRequest.class), Collections.singletonList(returnedAd));

        verify(requestAdapter).invokeLoadFinish(any(PubnativeLibraryAdModel.class));
        assertThat(PubnativeLibraryCPICache.getInstance().getQueueSize()).isZero();
    }

    @Test
    public void onPubnativeRequestSuccess_withCpiAdAndCacheUsageDisabled_shouldReturnCpiAdFromCache() {
        Map mapData = mock(Map.class);
        PubnativeLibraryNetworkAdapter requestAdapter = spy(new PubnativeLibraryNetworkAdapter(mapData));
        requestAdapter.mIsCPICacheEnabled = false;

        PNAPIAdModel returnedAd = mock(PNAPIAdModel.class);
        when(returnedAd.isRevenueModelCPA()).thenReturn(true);

        PNAPIAdModel cachedAd = mock(PNAPIAdModel.class);
        when(cachedAd.isRevenueModelCPA()).thenReturn(true);

        // add cpi ad
        PubnativeLibraryCPICache.getInstance().clear();
        PubnativeLibraryCPICache.getInstance().enqueue(cachedAd);

        requestAdapter.onPNAPIRequestFinish(mock(PNAPIRequest.class), Collections.singletonList(returnedAd));

        verify(requestAdapter).invokeLoadFinish(any(PubnativeLibraryAdModel.class));
        assertThat(PubnativeLibraryCPICache.getInstance().getQueueSize()).isGreaterThan(0);
    }

    @Test
    public void onPubnativeRequestSuccess_withNonCpiAdAndCacheUsageEnabled_shouldReturnCpiAdFromCache() {
        Map mapData = mock(Map.class);
        PubnativeLibraryNetworkAdapter requestAdapter = spy(new PubnativeLibraryNetworkAdapter(mapData));
        requestAdapter.mIsCPICacheEnabled = true;

        PNAPIAdModel returnedAd = mock(PNAPIAdModel.class);
        when(returnedAd.isRevenueModelCPA()).thenReturn(false);

        PNAPIAdModel cachedAd = mock(PNAPIAdModel.class);
        when(cachedAd.isRevenueModelCPA()).thenReturn(true);

        // add cpi ad
        PubnativeLibraryCPICache.getInstance().clear();
        PubnativeLibraryCPICache.getInstance().enqueue(cachedAd);

        requestAdapter.onPNAPIRequestFinish(mock(PNAPIRequest.class), Collections.singletonList(returnedAd));

        verify(requestAdapter).invokeLoadFinish(any(PubnativeLibraryAdModel.class));
        assertThat(PubnativeLibraryCPICache.getInstance().getQueueSize()).isGreaterThan(0);
    }

    @Test
    public void onPubnativeRequestSuccess_withNonCpiAdAndCacheUsageDisabled_shouldReturnCpiAdFromCache() {
        Map mapData = mock(Map.class);
        PubnativeLibraryNetworkAdapter requestAdapter = spy(new PubnativeLibraryNetworkAdapter(mapData));
        requestAdapter.mIsCPICacheEnabled = false;

        PNAPIAdModel returnedAd = mock(PNAPIAdModel.class);
        when(returnedAd.isRevenueModelCPA()).thenReturn(false);

        PNAPIAdModel cachedAd = mock(PNAPIAdModel.class);
        when(cachedAd.isRevenueModelCPA()).thenReturn(true);

        // add cpi ad
        PubnativeLibraryCPICache.getInstance().clear();
        PubnativeLibraryCPICache.getInstance().enqueue(cachedAd);

        requestAdapter.onPNAPIRequestFinish(mock(PNAPIRequest.class), Collections.singletonList(returnedAd));

        verify(requestAdapter).invokeLoadFinish(any(PubnativeLibraryAdModel.class));
        assertThat(PubnativeLibraryCPICache.getInstance().getQueueSize()).isGreaterThan(0);
    }

    @Test
    public void onPubnativeRequestFailed_withCachedAd_shouldInvokeLoad() {
        Map mapData = mock(Map.class);
        PubnativeLibraryNetworkAdapter requestAdapter = spy(new PubnativeLibraryNetworkAdapter(mapData));

        // enqueue ad
        PubnativeLibraryCPICache.getInstance().enqueue(mock(PNAPIAdModel.class));

        requestAdapter.onPNAPIRequestFail(mock(PNAPIRequest.class), mock(Exception.class));

        verify(requestAdapter).invokeLoadFinish(any(PubnativeLibraryAdModel.class));
    }

    @Test
    public void onPubnativeRequestFailed_withNoCachedAd_shouldInvokeFail() {
        Map mapData = mock(Map.class);
        PubnativeLibraryNetworkAdapter requestAdapter = spy(new PubnativeLibraryNetworkAdapter(mapData));

        // clear cached ads
        PubnativeLibraryCPICache.getInstance().clear();

        Exception exception = mock(Exception.class);
        requestAdapter.onPNAPIRequestFail(mock(PNAPIRequest.class), exception);

        verify(requestAdapter).invokeLoadFail(eq(exception));
    }
}
