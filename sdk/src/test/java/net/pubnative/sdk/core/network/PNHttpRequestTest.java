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

package net.pubnative.sdk.core.network;

import android.os.Handler;

import net.pubnative.sdk.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNHttpRequestTest {

    @Test
    public void start_withNullContext_callbacksFail() {

        PNHttpRequest request = spy(PNHttpRequest.class);
        PNHttpRequest.Listener listener = mock(PNHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(null, "url", listener);
        verify(listener).onPNHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void start_withNullUrl_callbacksFail() {

        PNHttpRequest request = spy(PNHttpRequest.class);
        PNHttpRequest.Listener listener = mock(PNHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(RuntimeEnvironment.application.getApplicationContext(), null, listener);
        verify(listener).onPNHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void start_withEmptyUrl_callbacksFail() {

        PNHttpRequest request = spy(PNHttpRequest.class);
        PNHttpRequest.Listener listener = mock(PNHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(RuntimeEnvironment.application.getApplicationContext(), "", listener);
        verify(listener).onPNHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void invokeFinish_withNullListener_pass() {

        PNHttpRequest request = spy(PNHttpRequest.class);
        request.mListener = null;
        request.mHandler = new Handler();
        request.invokeFinish("result");
    }
    @Test
    public void invokeFail_withNullListener_pass() {

        PNHttpRequest request = spy(PNHttpRequest.class);
        request.mListener = null;
        request.mHandler = new Handler();
        request.invokeFail(mock(Exception.class));
    }

    @Test
    public void invokeLoad_WithValidListener_callbackAndNullsListener() {

        PNHttpRequest request = spy(PNHttpRequest.class);
        PNHttpRequest.Listener listener = mock(PNHttpRequest.Listener.class);
        request.mListener = listener;
        request.mHandler = new Handler();
        request.invokeFinish("result");
        verify(listener).onPNHttpRequestFinish(eq(request), eq("result"));
        assertThat(request.mListener).isNull();
    }

    @Test
    public void invokeFail_withValidListener_callbackAndNullsListener() {

        PNHttpRequest request = spy(PNHttpRequest.class);
        PNHttpRequest.Listener listener = mock(PNHttpRequest.Listener.class);
        request.mListener = listener;
        request.mHandler = new Handler();
        request.invokeFail(mock(Exception.class));
        verify(listener).onPNHttpRequestFail(eq(request), any(Exception.class));
        assertThat(request.mListener).isNull();
    }
}
