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

package net.pubnative.mediation.request.model;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.insights.PubnativeInsightsManager;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({PubnativeAdModel.class, PubnativeInsightsManager.class, PubnativeDeliveryManager.class})
public class PubnativeAdModelTest {

    private static final String SAMPLE_URL = "http://pubnative.net";

    private Context appContext = null;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() {
        this.appContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void callbacksOnlyFiresOnce() throws Exception {
        PowerMockito.spy(PubnativeInsightsManager.class);
        // stub the method trackNext to nothing.
        PowerMockito.doNothing().when(PubnativeInsightsManager.class, "trackData", any(Context.class), anyString(), any(Map.class), any(PubnativeInsightDataModel.class));

        PowerMockito.spy(PubnativeDeliveryManager.class);
        PowerMockito.doNothing().when(PubnativeDeliveryManager.class, "logImpression", any(Context.class), anyString());

        Map parametersMock = mock(Map.class);
        PubnativeAdModel modelSpy = spy(PubnativeAdModel.class);
        modelSpy.mContext = this.appContext;

        PubnativeInsightDataModel dataModel = new PubnativeInsightDataModel();
        modelSpy.setTrackingInfo(dataModel);
        modelSpy.setTrackingClickData(SAMPLE_URL, parametersMock);
        modelSpy.setTrackingImpressionData(SAMPLE_URL, parametersMock);
        
        // Callbacks the setted up listener
        PubnativeAdModel.Listener listenerSpy = spy(PubnativeAdModel.Listener.class);
        modelSpy.setListener(listenerSpy);

        // Calling invoke methods
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();

        // Calling them again don't increment the callback
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();

        verify(listenerSpy, times(1)).onAdImpressionConfirmed(eq(modelSpy));
        verify(listenerSpy, times(1)).onAdClick(eq(modelSpy));

        // track data called twice. once for each impression and click.
        PowerMockito.verifyStatic(times(2));
        PubnativeInsightsManager.trackData(eq(appContext), eq(SAMPLE_URL), eq(parametersMock), eq(dataModel));

        // log impression called only once.
        PowerMockito.verifyStatic(times(1));
        PubnativeDeliveryManager.logImpression(eq(appContext), anyString());
    }

    @Test
    public void callbackWithNullListenerDoesNothing() {
        PubnativeAdModel modelSpy = spy(PubnativeAdModel.class);

        // Does nothing with null listener
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();
    }
}
