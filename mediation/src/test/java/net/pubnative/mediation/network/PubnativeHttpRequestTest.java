package net.pubnative.mediation.network;

import android.os.Handler;

import net.pubnative.mediation.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeHttpRequestTest {

    @Test
    public void start_withNullContext_callbacksFail() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        PubnativeHttpRequest.Listener listener = mock(PubnativeHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(null, "url", listener);
        verify(listener).onPubnativeHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void start_withNullUrl_callbacksFail() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        PubnativeHttpRequest.Listener listener = mock(PubnativeHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(RuntimeEnvironment.application.getApplicationContext(), null, listener);
        verify(listener).onPubnativeHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void start_withEmptyUrl_callbacksFail() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        PubnativeHttpRequest.Listener listener = mock(PubnativeHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(RuntimeEnvironment.application.getApplicationContext(), "", listener);
        verify(listener).onPubnativeHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void invokeFinish_withNullListener_pass() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        request.mListener = null;
        request.mHandler = new Handler();
        request.invokeFinish("result");
    }

    @Test
    public void invokeStart_withNullListener_pass() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        request.mListener = null;
        request.mHandler = new Handler();
        request.invokeStart();
    }

    @Test
    public void invokeFail_withNullListener_pass() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        request.mListener = null;
        request.mHandler = new Handler();
        request.invokeFail(mock(Exception.class));
    }

    @Test
    public void invokeStart_withValidListener_callback() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        PubnativeHttpRequest.Listener listener = mock(PubnativeHttpRequest.Listener.class);
        request.mListener = listener;
        request.mHandler = new Handler();
        request.invokeStart();
        verify(listener).onPubnativeHttpRequestStart(eq(request));
    }

    @Test
    public void invokeLoad_WithValidListener_callbackAndNullsListener() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        PubnativeHttpRequest.Listener listener = mock(PubnativeHttpRequest.Listener.class);
        request.mListener = listener;
        request.mHandler = new Handler();
        request.invokeFinish("result");
        verify(listener).onPubnativeHttpRequestFinish(eq(request), eq("result"));
        assertThat(request.mListener).isNull();
    }

    @Test
    public void invokeFail_withValidListener_callbackAndNullsListener() {

        PubnativeHttpRequest request = spy(PubnativeHttpRequest.class);
        PubnativeHttpRequest.Listener listener = mock(PubnativeHttpRequest.Listener.class);
        request.mListener = listener;
        request.mHandler = new Handler();
        request.invokeFail(mock(Exception.class));
        verify(listener).onPubnativeHttpRequestFail(eq(request), any(Exception.class));
        assertThat(request.mListener).isNull();
    }
}
