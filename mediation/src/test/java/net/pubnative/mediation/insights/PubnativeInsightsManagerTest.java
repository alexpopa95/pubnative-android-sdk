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

package net.pubnative.mediation.insights;

import android.content.Context;

import com.google.gson.Gson;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.insights.model.PubnativeInsightRequestModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.task.PubnativeHttpTask;

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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(PubnativeInsightsManager.class)
public class PubnativeInsightsManagerTest {

    private PubnativeInsightDataModel insightDataModel = null;
    private Context                   appContext       = null;
    private String                    sampleURL        = "http://pubnative.net";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() {
        this.appContext = RuntimeEnvironment.application.getApplicationContext();
        // Gson() gives error when mock/spy of PubnativeInsightDataModel
        this.insightDataModel = new PubnativeInsightDataModel();
    }

    @Test
    public void trackDataWithVariousArguments() throws Exception {
        PowerMockito.spy(PubnativeInsightsManager.class);

        // stub the method trackNext to nothing.
        PowerMockito.doNothing().when(PubnativeInsightsManager.class, "trackNext", any(Context.class));

        PubnativeInsightDataModel dataModel = this.insightDataModel;
        Map<String, String> parametersMock = mock(Map.class);

        // valid arguments
        PubnativeInsightsManager.trackData(this.appContext, this.sampleURL, parametersMock, dataModel);

        // invalid arguments
        PubnativeInsightsManager.trackData(null, null, null, null);

        // 1 valid
        PubnativeInsightsManager.trackData(this.appContext, null, null, null);
        PubnativeInsightsManager.trackData(null, this.sampleURL, null, null);
        PubnativeInsightsManager.trackData(null, null, parametersMock, null);
        PubnativeInsightsManager.trackData(null, null, null,  dataModel);

        // 2 valid
        PubnativeInsightsManager.trackData(this.appContext, this.sampleURL, null, null);
        PubnativeInsightsManager.trackData(this.appContext, null, parametersMock,  null);
        PubnativeInsightsManager.trackData(this.appContext, null, null,  dataModel);
        PubnativeInsightsManager.trackData(null, this.sampleURL, parametersMock, null);
        PubnativeInsightsManager.trackData(null, this.sampleURL, null, dataModel);
        PubnativeInsightsManager.trackData(null, null, parametersMock, dataModel);
        PubnativeInsightsManager.trackData(null, this.sampleURL, parametersMock,  dataModel);
        PubnativeInsightsManager.trackData(this.appContext, null, parametersMock,  dataModel);
        PubnativeInsightsManager.trackData(this.appContext, this.sampleURL, null,  dataModel);
        PubnativeInsightsManager.trackData(this.appContext, this.sampleURL, parametersMock,  null);
    }

    @Test
    public void trackingDataIsQueued() throws Exception {
        PowerMockito.spy(PubnativeInsightsManager.class);

        // assert that the queue is empty at the beginning
        assertThat(PubnativeInsightsManager.dequeueInsightItem(this.appContext, PubnativeInsightsManager.INSIGHTS_PENDING_DATA)).isNull();

        // stub the method trackNext to nothing.
        PowerMockito.doNothing().when(PubnativeInsightsManager.class, "trackNext", any(Context.class));

        // call trackData
        PubnativeInsightDataModel dataModel = this.insightDataModel;
        PubnativeInsightsManager.trackData(this.appContext, this.sampleURL, mock(Map.class), dataModel);

        // assert that the queue is not empty
        assertThat(PubnativeInsightsManager.dequeueInsightItem(this.appContext, PubnativeInsightsManager.INSIGHTS_PENDING_DATA)).isNotNull();
    }

    @Test
    public void pendingAndFailedQueueIsEmptyAtBeginning() {
        // assert that the failed items queue is empty at the beginning
        assertThat(PubnativeInsightsManager.dequeueInsightItem(this.appContext, PubnativeInsightsManager.INSIGHTS_FAILED_DATA)).isNull();
        // assert that the pending items queue is empty at the beginning
        assertThat(PubnativeInsightsManager.dequeueInsightItem(this.appContext, PubnativeInsightsManager.INSIGHTS_PENDING_DATA)).isNull();
    }

    // TODO: Test that ensures that a failing request is re-queued

    @Test
    public void trackingRequestQueuesClearedOnTrackingSuccess() throws Exception {
        PowerMockito.spy(PubnativeInsightsManager.class);

        // stub the network call for success case.
        PowerMockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PubnativeHttpTask.Listener        listener = (PubnativeHttpTask.Listener) invocation.getArguments()[3];
                PubnativeInsightsAPIResponseModel model    = new PubnativeInsightsAPIResponseModel();
                model.status = PubnativeInsightsAPIResponseModel.Status.OK;
                String result = new Gson().toJson(model);
                listener.onHttpTaskSuccess(mock(PubnativeHttpTask.class), result);
                return null;
            }
        }).when(PubnativeInsightsManager.class,
                "sendTrackingDataToServer",
                (any(Context.class)), any(String.class), any(String.class), any(PubnativeHttpTask.Listener.class));

        PubnativeInsightDataModel dataModel = this.insightDataModel;
        PubnativeInsightsManager.trackData(this.appContext, this.sampleURL, mock(Map.class), dataModel);

        // verify the trackingFinished is called
        PowerMockito.verifyStatic(times(1));
        PubnativeInsightsManager.trackingFinished(eq(this.appContext), any(PubnativeInsightRequestModel.class));

        // assert that pending/failed queues are empty after a successful tracking.
        assertThat(PubnativeInsightsManager.dequeueInsightItem(this.appContext, PubnativeInsightsManager.INSIGHTS_PENDING_DATA)).isNull();
        assertThat(PubnativeInsightsManager.dequeueInsightItem(this.appContext, PubnativeInsightsManager.INSIGHTS_FAILED_DATA)).isNull();
    }

    @Test
    public void networkCallNotHappeningWhenTrackerIsBusy() throws Exception {
        PowerMockito.spy(PubnativeInsightsManager.class);
        // marking tracker as busy
        PubnativeInsightsManager.sIdle = false;

        // skipping the network call
        PowerMockito.doNothing()
                    .when(PubnativeInsightsManager.class,
                          "sendTrackingDataToServer",
                          any(Context.class), any(String.class), any(String.class), any(PubnativeHttpTask.Listener.class));

        PubnativeInsightDataModel dataModel = new PubnativeInsightDataModel();
        PubnativeInsightsManager.trackData(this.appContext, this.sampleURL, mock(Map.class), dataModel);

        // verify that the network call inside trackNext is not called.
        PowerMockito.verifyStatic(never());
        PubnativeInsightsManager.sendTrackingDataToServer(any(Context.class), any(String.class), any(String.class), any(PubnativeHttpTask.Listener.class));

        // marking tracker as available (avoiding issue caused with static variable)
        PubnativeInsightsManager.sIdle = true;
    }
}
