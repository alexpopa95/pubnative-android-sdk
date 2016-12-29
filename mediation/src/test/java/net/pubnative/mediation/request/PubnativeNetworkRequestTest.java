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

package net.pubnative.mediation.request;

import android.os.Handler;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeNetworkRequestTest {

    @Test
    public void start_withNullListener_pass() {
        // This should not crash
        PubnativeNetworkRequest request = spy(PubnativeNetworkRequest.class);
        request.start(RuntimeEnvironment.application.getApplicationContext(), "app_token", "testPlacementName", null);
    }

    @Test
    public void invokeLoad_withNullListener_pass() {

        PubnativeNetworkRequest request = spy(PubnativeNetworkRequest.class);
        request.mHandler = new Handler();
        request.invokeLoad(null);
    }

    @Test
    public void invokeFail_withNullListener_pass() {

        PubnativeNetworkRequest request = spy(PubnativeNetworkRequest.class);
        request.mHandler = new Handler();
        request.invokeFail(null);
    }

    @Test
    public void invokeLoad_withValidListener_callbackLoad() {

        PubnativeNetworkRequest request = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequest.Listener listener = spy(PubnativeNetworkRequest.Listener.class);
        PubnativeAdModel model = mock(PubnativeAdModel.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeLoad(model);

        verify(listener).onPubnativeNetworkRequestLoaded(eq(request), eq(model));
    }

    @Test
    public void invokeFail_withValidListener_callbackFail() {

        PubnativeNetworkRequest request = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequest.Listener listener = spy(PubnativeNetworkRequest.Listener.class);
        Exception exception = mock(Exception.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeFail(exception);

        verify(listener).onPubnativeNetworkRequestFailed(eq(request), eq(exception));
    }
}
