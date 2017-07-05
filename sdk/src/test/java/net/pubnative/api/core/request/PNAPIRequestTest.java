// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
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

package net.pubnative.api.core.request;

import android.content.Context;

import net.pubnative.sdk.BuildConfig;
import net.pubnative.api.PNAPITestUtils;
import net.pubnative.api.core.network.PNAPIHttpRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PNAPIRequestTest {

    private Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testWithValidListenerForSuccess() {

        PNAPIRequest.Listener listener = spy(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mListener = listener;
        request.invokeOnSuccess(mock(ArrayList.class));
        verify(listener, times(1)).onPNAPIRequestFinish(eq(request), any(List.class));
    }

    @Test
    public void testWithValidListenerForFailure() {

        Exception error = mock(Exception.class);
        PNAPIRequest.Listener listener = spy(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mListener = listener;
        request.invokeOnFail(error);
        verify(listener, times(1)).onPNAPIRequestFail(eq(request), eq(error));
    }

    @Test
    public void testWithNoListenerForSuccess() {

        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mListener = null;
        request.invokeOnSuccess(mock(ArrayList.class));
    }

    @Test
    public void testWithNoListenerForFailure() {

        PNAPIRequest request = spy(PNAPIRequest.class);
        Exception error = mock(Exception.class);
        request.mListener = null;
        request.invokeOnFail(error);
    }

    @Test
    public void testParameterIsSet() {

        String testKey = "testKey";
        String testValue = "testValue";
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.setParameter(testKey, testValue);
        assertThat(request.mRequestParameters.get(testKey)).isEqualTo(testValue);
    }

    @Test
    public void testWithNullParameters() {

        PNAPIRequest request = spy(PNAPIRequest.class);
        String testKey = "testKey";
        request.setParameter(testKey, null);
        assertThat(request.mRequestParameters.containsKey(testKey)).isFalse();
    }

    @Test
    public void testParameterSize() {

        PNAPIRequest request = spy(PNAPIRequest.class);
        request.setParameter("test1", "1");
        request.setParameter("test2", "2");
        assertThat(request.mRequestParameters.size() == 2).isTrue();
    }

    @Test
    public void testDuplicateParametersOverridesValue() {

        String testKey = "testKey";
        String testValue1 = "value1";
        String testValue2 = "value2";
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.setParameter(testKey, testValue1);
        request.setParameter(testKey, testValue2);
        assertThat(request.mRequestParameters.size()).isEqualTo(1);
        assertThat(request.mRequestParameters.get(testKey)).isEqualTo(testValue2);
    }

    @Test
    public void testNetworkRequestInitiatedOnStart() {

        PNAPIRequest.Listener listener = spy(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mListener = listener;
        request.start(this.applicationContext, listener);
        verify(request, times(1)).fillDefaultParameters();
    }

    @Test
    public void test_start_withNullContext_pass() {

        PNAPIRequest.Listener listener = mock(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.setParameter(PNAPIRequest.Parameters.ANDROID_ADVERTISER_ID, "test");
        request.start(null, listener);
    }

    @Test
    public void test_start_withNullListener_pass() {

        PNAPIRequest request = spy(PNAPIRequest.class);
        request.setParameter(PNAPIRequest.Parameters.ANDROID_ADVERTISER_ID, "test");
        request.start(RuntimeEnvironment.application.getApplicationContext(), null);
    }

    @Test
    public void test_start_withRunningRequest_pass() {

        PNAPIRequest.Listener listener = mock(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.setParameter(PNAPIRequest.Parameters.ANDROID_ADVERTISER_ID, "test");
        request.mIsRunning = true;
        request.start(RuntimeEnvironment.application.getApplicationContext(), listener);
    }

    @Test
    public void testSetsUpDefaultParametersAutomatically() {

        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mContext = this.applicationContext;
        request.fillDefaultParameters();
        assertThat(request.mRequestParameters.containsKey(PNAPIRequest.Parameters.OS)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PNAPIRequest.Parameters.OS_VERSION)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PNAPIRequest.Parameters.DEVICE_MODEL)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PNAPIRequest.Parameters.LOCALE)).isTrue();
    }

    @Test
    public void testRequestUrlValidity() {

        String testKey = "testKey";
        String testValue = "testValue";
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.setParameter(testKey, testValue);
        String url = request.getRequestURL();
        assertThat(url).isNotNull();
        assertThat(url).isNotEmpty();
        assertThat(url).startsWith(PNAPIRequest.BASE_URL);
        assertThat(url).contains(testKey);
    }

    @Test
    public void testOnResponseSuccess() {

        String result = PNAPITestUtils.getResponseJSON("success.json");
        PNAPIRequest.Listener listener = spy(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mListener = listener;
        request.onPNAPIHttpRequestFinish(null, result, PNAPIHttpRequest.HTTP_OK);
        verify(listener, times(1)).onPNAPIRequestFinish(eq(request), any(List.class));
    }

    @Test
    public void testOnResponseWithInvalidData() {


        String result = PNAPITestUtils.getResponseJSON("failure.json");
        PNAPIRequest.Listener listener = spy(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mListener = listener;
        request.onPNAPIHttpRequestFinish(null, result, PNAPIHttpRequest.HTTP_INVALID_REQUEST);
        verify(listener, times(1)).onPNAPIRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void testOnResponseWithNullData() {

        PNAPIRequest.Listener listener = spy(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mListener = listener;
        request.onPNAPIHttpRequestFinish(null, null, 0);
        verify(listener, times(1)).onPNAPIRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void testOnErrorResponseFromRequestManager() {

        Exception error = mock(Exception.class);
        PNAPIRequest.Listener listener = spy(PNAPIRequest.Listener.class);
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mListener = listener;
        request.onPNAPIHttpRequestFail(null, error);
        verify(listener, times(1)).onPNAPIRequestFail(eq(request), eq(error));
    }

    @Test
    public void setCoppaMode_true_shouldSet1() {
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mContext = this.applicationContext;
        request.setCoppaMode(true);
        assertThat(request.mRequestParameters).containsKey(PNAPIRequest.Parameters.COPPA);
        assertThat(request.mRequestParameters.get(PNAPIRequest.Parameters.COPPA)).isEqualTo("1");
    }

    @Test
    public void setCoppaMode_false_shouldSet0() {
        PNAPIRequest request = spy(PNAPIRequest.class);
        request.mContext = this.applicationContext;
        request.setCoppaMode(false);
        assertThat(request.mRequestParameters).containsKey(PNAPIRequest.Parameters.COPPA);
        assertThat(request.mRequestParameters.get(PNAPIRequest.Parameters.COPPA)).isEqualTo("0");
    }
}
