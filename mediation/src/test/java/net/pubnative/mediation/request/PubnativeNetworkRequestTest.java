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

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.config.PubnativeConfigTestUtils;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({PubnativeNetworkRequest.class, PubnativeNetworkAdapterFactory.class, PubnativeConfigManager.class})
public class PubnativeNetworkRequestTest {

    @Rule
    public       PowerMockRule rule                      = new PowerMockRule();
    final static String        TEST_PLACEMENT_ID_INVALID = "mPlacement";
    final static String        TEST_PLACEMENT_ID_VALID   = "1";
    final static String        TEST_APP_TOKEN            = "app_token";
    final static int           TEST_TIMEOUT              = 500;
    Context                          applicationContext;
    // Mocks
    PubnativeNetworkRequest          requestSpy;
    PubnativeNetworkRequest.Listener listenerMock;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        this.requestSpy = spy(PubnativeNetworkRequest.class);
        this.listenerMock = mock(PubnativeNetworkRequest.Listener.class);
    }

    @Test
    public void requestWithCorrectParameters() {

        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", TEST_APP_TOKEN);
        final PubnativeAdModel modelMock = spy(PubnativeAdModel.class);
        final PubnativeNetworkAdapter adapterMock = mock(PubnativeNetworkAdapter.class);
        // Stub Adapter doRequest to callback the mListener directly
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                PubnativeNetworkAdapter adapterMock = (PubnativeNetworkAdapter) invocation.getMock();
                PubnativeNetworkAdapter.Listener adapterListener = (PubnativeNetworkAdapter.Listener) invocation.getArgumentAt(2, PubnativeNetworkAdapter.Listener.class);
                adapterListener.onPubnativeNetworkAdapterRequestStarted(adapterMock);
                adapterListener.onPubnativeNetworkAdapterRequestLoaded(adapterMock, modelMock);
                return null;
            }
        }).when(adapterMock).doRequest(any(Context.class), anyInt(), any(PubnativeNetworkAdapter.Listener.class));
        // Stub Factory create to return my adapter mock
        PowerMockito.mockStatic(PubnativeNetworkAdapterFactory.class);
        when(PubnativeNetworkAdapterFactory.createAdapter(any(PubnativeNetworkModel.class))).thenReturn(adapterMock);
        final PubnativeNetworkRequest networkRequest = spy(PubnativeNetworkRequest.class);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                listenerMock.onPubnativeNetworkRequestLoaded(networkRequest, modelMock);
                return null;
            }
        }).when(networkRequest).getConfig(anyString(), any(PubnativeConfigManager.Listener.class));
        networkRequest.start(this.applicationContext, TEST_APP_TOKEN, TEST_PLACEMENT_ID_VALID, listenerMock);
        verify(listenerMock, after(TEST_TIMEOUT).times(1)).onPubnativeNetworkRequestStarted(eq(networkRequest));
        verify(listenerMock, after(TEST_TIMEOUT).never()).onPubnativeNetworkRequestFailed(eq(networkRequest), any(Exception.class));
        verify(listenerMock, after(TEST_TIMEOUT).times(1)).onPubnativeNetworkRequestLoaded(eq(networkRequest), eq(modelMock));
    }

    @Test
    public void requestWithNullListenerDrops() {
        // This should not crash
        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, TEST_PLACEMENT_ID_INVALID, null);
    }

    @Test
    public void freqDayLimitResets() {

        this.frequencyResets(Calendar.DAY_OF_MONTH, "delivery_freq_day.json");
    }

    @Test
    public void freqHourLimitResets() {

        this.frequencyResets(Calendar.HOUR_OF_DAY, "delivery_freq_hour.json");
    }

    public void frequencyResets(int field, String configFileName) {

        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, configFileName, TEST_APP_TOKEN);
        PubnativeConfigModel configModel = PubnativeConfigManager.getStoredConfig(this.applicationContext);
        PubnativeNetworkRequest.Listener listenerMock = mock(PubnativeNetworkRequest.Listener.class);

        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        doNothing().when(networkRequestSpy).doNextNetworkRequest();
        networkRequestSpy.mConfig = configModel;
        networkRequestSpy.mPlacementID = TEST_PLACEMENT_ID_VALID;
        networkRequestSpy.mContext = this.applicationContext;
        networkRequestSpy.mListener = listenerMock;

        PubnativeDeliveryManager.logImpression(this.applicationContext, TEST_PLACEMENT_ID_VALID);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.add(field, -1);
        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, TEST_PLACEMENT_ID_VALID, currentCalendar);
        networkRequestSpy.startRequest();

        verify(listenerMock, never()).onPubnativeNetworkRequestFailed(eq(networkRequestSpy), any(Exception.class));
    }
}
