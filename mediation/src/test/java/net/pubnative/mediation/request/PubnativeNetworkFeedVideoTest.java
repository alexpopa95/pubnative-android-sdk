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
public class PubnativeNetworkFeedVideoTest {

    @Test
    public void invokeLoadFinish_withNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();
        request.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFail_withNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();
        request.invokeLoadFail(any(Exception.class));
    }

    @Test
    public void invokeShow_withNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();
        request.invokeShow();
    }

    @Test
    public void invokeImpressionConfirmed_withNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();
        request.invokeImpressionConfirmed();
    }

    @Test
    public void invokeClick_withNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();
        request.invokeClick();
    }

    @Test
    public void invokeHide_withNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();
        request.invokeHide();
    }

    @Test
    public void invokeVideoFinish_withNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();
        request.invokeVideoFinish();
    }

    @Test
    public void invokeVideoStart_withNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();
        request.invokeVideoStart();
    }

    @Test
    public void invokeLoadFinish_withValidListener_callbackLoadFinish() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeLoadFinish();

        verify(listener).onPubnativeNetworkFeedVideoLoadFinish(eq(request));
    }

    @Test
    public void invokeLoadFail_withValidListener_callbackLoadFail() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        Exception exception = mock(Exception.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeLoadFail(exception);

        verify(listener).onPubnativeNetworkFeedVideoLoadFail(eq(request), eq(exception));
    }

    @Test
    public void invokeShow_withValidListener_callbackShow() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeShow();

        verify(listener).onPubnativeNetworkFeedVideoShow(eq(request));
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_callbackImpressionConfirmed() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeImpressionConfirmed();

        verify(listener).onPubnativeNetworkFeedVideoImpressionConfirmed(eq(request));
    }

    @Test
    public void invokeClick_withValidListener_callbackClick() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeClick();

        verify(listener).onPubnativeNetworkFeedVideoClick(eq(request));
    }

    @Test
    public void invokeHide_withValidListener_callbackHide() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeHide();

        verify(listener).onPubnativeNetworkFeedVideoHide(eq(request));
    }

    @Test
    public void invokeVideoFinish_withValidListener_callbackVideoFinish() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeVideoFinish();

        verify(listener).onPubnativeNetworkFeedVideoFinish(eq(request));
    }

    @Test
    public void invokeVideoStart_withValidListener_callbackVideoStart() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeVideoStart();

        verify(listener).onPubnativeNetworkFeedVideoStart(eq(request));
    }

    @Test
    public void load_withNullContext_callBackLoadFail() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(null, "app_token", "testPlacementName");

        verify(listener).onPubnativeNetworkFeedVideoLoadFail(eq(request), eq(PubnativeException.FEED_VIDEO_PARAMETERS_INVALID));
    }

    @Test
    public void load_withNullAppToken_callBackLoadFail() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), null, "testPlacementName");

        verify(listener).onPubnativeNetworkFeedVideoLoadFail(eq(request), eq(PubnativeException.FEED_VIDEO_PARAMETERS_INVALID));
    }

    @Test
    public void load_withNullPlacementName_callBackLoadFail() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", null);

        verify(listener).onPubnativeNetworkFeedVideoLoadFail(eq(request), eq(PubnativeException.FEED_VIDEO_PARAMETERS_INVALID));
    }

    @Test
    public void load_withEmptyAppToken_callBackLoadFail() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "", "testPlacementName");

        verify(listener).onPubnativeNetworkFeedVideoLoadFail(eq(request), eq(PubnativeException.FEED_VIDEO_PARAMETERS_INVALID));
    }

    @Test
    public void load_withEmptyPlacementName_callBackLoadFail() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", "");

        verify(listener).onPubnativeNetworkFeedVideoLoadFail(eq(request), eq(PubnativeException.FEED_VIDEO_PARAMETERS_INVALID));
    }

    @Test
    public void load_withValidListnerAndTrueIsLoading_callBackLoadFail() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;
        request.mIsLoading = true;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", "testPlacementName");

        verify(listener).onPubnativeNetworkFeedVideoLoadFail(eq(request), eq(PubnativeException.FEED_VIDEO_LOADING));
    }

    @Test
    public void load_withValidListnerAndTrueIsShown_callBackLoadFail() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        PubnativeNetworkFeedVideo.Listener listener = spy(PubnativeNetworkFeedVideo.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;
        request.mIsShown = true;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", "testPlacementName");

        verify(listener).onPubnativeNetworkFeedVideoLoadFail(eq(request), eq(PubnativeException.FEED_VIDEO_SHOWN));
    }

    @Test
    public void load_withNullContextNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();

        request.load(null, "app_token", "testPlacementName");
    }

    @Test
    public void load_withNullAppTokenNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();

        request.load(RuntimeEnvironment.application.getApplicationContext(), null, "testPlacementName");
    }

    @Test
    public void load_withNullPlacementNameNullListener_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mHandler = new Handler();

        request.load(RuntimeEnvironment.application.getApplicationContext(), "app_token", null);
    }

    @Test
    public void show_withNullContainer_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.show(null);
    }

    @Test
    public void show_withoutListenerAndTrueIsLoading_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mIsLoading = true;
        request.show(mock(RelativeLayout.class));
    }

    @Test
    public void show_withoutListenerAndTrueIsShown_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        request.mIsShown = true;
        request.show(mock(RelativeLayout.class));
    }

    @Test
    public void show_withoutListenerAndFalseIsReady_pass() {

        PubnativeNetworkFeedVideo request = spy(PubnativeNetworkFeedVideo.class);
        when(request.isReady()).thenReturn(false);
        request.show(mock(RelativeLayout.class));
    }
}
