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

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PubnativeNetworkAdapterTest {

    private static final int TIMEOUT_HALF_SECOND = 500;

    @Test
    public void invokeFinalCallbacksNullifyListener() {
        HashMap<String, Object>         adapterConfigMock = mock(HashMap.class);
        PubnativeNetworkAdapter.Listener listenerSpy       = spy(PubnativeNetworkAdapter.Listener.class);

        PubnativeNetworkAdapter adapterInstance = spy(new PubnativeNetworkAdapter(adapterConfigMock) {
            @Override
            public void request(Context context) {
                // Do nothing
            }
        });

        // onRequestLoaded
        adapterInstance.mListener = listenerSpy;
        PubnativeAdModel adMock = mock(PubnativeAdModel.class);
        adapterInstance.invokeLoaded(adMock);
        assertThat(adapterInstance.mListener).isNull();

        // onRequestFailed
        adapterInstance.mListener = listenerSpy;
        Exception exceptionMock = mock(Exception.class);
        adapterInstance.invokeFailed(exceptionMock);
        assertThat(adapterInstance.mListener).isNull();
    }

    @Test
    public void invokeCallbacksWithValidListener() {
        HashMap<String, Object>         adapterConfigMock = mock(HashMap.class);
        PubnativeNetworkAdapter.Listener listenerSpy       = spy(PubnativeNetworkAdapter.Listener.class);

        PubnativeNetworkAdapter adapterInstance = spy(new PubnativeNetworkAdapter(adapterConfigMock) {
            @Override
            public void request(Context context) {
                // Do nothing
            }
        });

        adapterInstance.mListener = listenerSpy;

        // onRequestStarted
        adapterInstance.invokeStart();
        verify(listenerSpy, times(1)).onPubnativeNetworkAdapterRequestStarted(adapterInstance);

        // onRequestLoaded
        PubnativeAdModel adMock = mock(PubnativeAdModel.class);
        adapterInstance.invokeLoaded(adMock);
        verify(listenerSpy, times(1)).onPubnativeNetworkAdapterRequestLoaded(eq(adapterInstance), eq(adMock));

        adapterInstance.mListener = listenerSpy;

        // onRequestFailed
        Exception exceptionMock = mock(Exception.class);
        adapterInstance.invokeFailed(exceptionMock);
        verify(listenerSpy, times(1)).onPubnativeNetworkAdapterRequestFailed(eq(adapterInstance), eq(exceptionMock));
    }

    @Test
    public void invokeCallbacksWithNullListener() {
        HashMap<String, Object> adapterConfigMock = mock(HashMap.class);
        PubnativeNetworkAdapter adapterSpy = spy(new PubnativeNetworkAdapter(adapterConfigMock) {

            @Override
            public void request(Context context) {

            }
        });

        adapterSpy.mListener = null;
        adapterSpy.invokeStart();
        adapterSpy.invokeLoaded(mock(PubnativeAdModel.class));
        adapterSpy.invokeFailed(mock(Exception.class));
    }

    @Test
    public void adapterRequestsTimeoutCallsInvokeFailed() {
        PubnativeNetworkAdapter.Listener listenerSpy = spy(PubnativeNetworkAdapter.Listener.class);
        PubnativeNetworkAdapter adapterSpy = spy(new PubnativeNetworkAdapter(null) {
            @Override
            public void request(Context context) {
                // Do nothing, doRequest should timeout
            }
        });

        adapterSpy.doRequest(mock(Context.class), TIMEOUT_HALF_SECOND, listenerSpy);
        Robolectric.flushForegroundThreadScheduler();

        verify(listenerSpy, times(1)).onPubnativeNetworkAdapterRequestStarted(eq(adapterSpy));
        verify(listenerSpy, after(2 * TIMEOUT_HALF_SECOND).times(1)).onPubnativeNetworkAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
        verify(listenerSpy, after(2 * TIMEOUT_HALF_SECOND).never()).onPubnativeNetworkAdapterRequestLoaded(eq(adapterSpy), any(PubnativeAdModel.class));
    }

    // TODO: Ensure no more callbacks after fail or load
}
