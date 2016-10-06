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
import android.widget.RelativeLayout;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeNetworkFeedBannerTest {

    @Test
    public void invokeLoadFinish_withNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();
        request.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFail_withNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();
        request.invokeLoadFail(any(Exception.class));
    }

    @Test
    public void invokeShow_withNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();
        request.invokeShow();
    }

    @Test
    public void invokeImpressionConfirmed_withNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();
        request.invokeImpressionConfirmed();
    }

    @Test
    public void invokeClick_withNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();
        request.invokeClick();
    }

    @Test
    public void invokeHide_withNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();
        request.invokeHide();
    }

    @Test
    public void invokeLoadFinish_withValidListener_callbackLoadFinish() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeLoadFinish();

        verify(listener).onPubnativeNetworkFeedBannerLoadFinish(eq(request));
    }

    @Test
    public void invokeLoadFail_withValidListener_callbackLoadFail() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        Exception exception = mock(Exception.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeLoadFail(exception);

        verify(listener).onPubnativeNetworkFeedBannerLoadFail(eq(request), eq(exception));
    }

    @Test
    public void invokeShow_withValidListener_callbackShow() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeShow();

        verify(listener).onPubnativeNetworkFeedBannerShow(eq(request));
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_callbackImpressionConfirmed() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeImpressionConfirmed();

        verify(listener).onPubnativeNetworkFeedBannerImpressionConfirmed(eq(request));
    }

    @Test
    public void invokeClick_withValidListener_callbackClick() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeClick();

        verify(listener).onPubnativeNetworkFeedBannerClick(eq(request));
    }

    @Test
    public void load_withNullContext_callBackLoadFail() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(null, "app_token", "testPlacementName");

        verify(listener).onPubnativeNetworkFeedBannerLoadFail(eq(request), eq(PubnativeException.FEED_BANNER_PARAMETERS_INVALID));
    }

    @Test
    public void load_withNullAppToken_callBackLoadFail() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), null, "testPlacementName");

        verify(listener).onPubnativeNetworkFeedBannerLoadFail(eq(request), eq(PubnativeException.FEED_BANNER_PARAMETERS_INVALID));
    }

    @Test
    public void load_withNullPlacementName_callBackLoadFail() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", null);

        verify(listener).onPubnativeNetworkFeedBannerLoadFail(eq(request), eq(PubnativeException.FEED_BANNER_PARAMETERS_INVALID));
    }

    @Test
    public void load_withEmptyAppToken_callBackLoadFail() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "", "testPlacementName");

        verify(listener).onPubnativeNetworkFeedBannerLoadFail(eq(request), eq(PubnativeException.FEED_BANNER_PARAMETERS_INVALID));
    }

    @Test
    public void load_withEmptyPlacementName_callBackLoadFail() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        PubnativeNetworkFeedBanner.Listener listener = spy(PubnativeNetworkFeedBanner.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", "");

        verify(listener).onPubnativeNetworkFeedBannerLoadFail(eq(request), eq(PubnativeException.FEED_BANNER_PARAMETERS_INVALID));
    }

    @Test
    public void load_withNullContextNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();

        request.load(null, "app_token", "testPlacementName");
    }

    @Test
    public void load_withNullAppTokenNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();

        request.load(RuntimeEnvironment.application.getApplicationContext(), null, "testPlacementName");
    }

    @Test
    public void load_withNullPlacementNameNullListener_pass() {

        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.mHandler = new Handler();

        request.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", null);
    }

    @Test
    public void show_withNullContainer_pass() {
        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        request.show(null);
    }

    @Test
    public void show_withNotReadyAd_pass() {
        PubnativeNetworkFeedBanner request = spy(PubnativeNetworkFeedBanner.class);
        when(request.isReady()).thenReturn(false);
        request.show(mock(RelativeLayout.class));
    }
}
