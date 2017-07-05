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

package net.pubnative.sdk.core.insights;

import android.content.Context;

import net.pubnative.sdk.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNInsightsManagerTest {

    @Test
    public void pendingAndFailedQueueIsEmptyAtBeginning() {

        Context context = RuntimeEnvironment.application.getApplicationContext();
        // assert that the failed items queue is empty at the beginning
        assertThat(PNInsightsManager.dequeueInsightItem(context, PNInsightsManager.INSIGHTS_FAILED_DATA)).isNull();
        // assert that the pending items queue is empty at the beginning
        assertThat(PNInsightsManager.dequeueInsightItem(context, PNInsightsManager.INSIGHTS_PENDING_DATA)).isNull();
    }

    @Test
    public void trackData_withNullContext_pass() {
        PNInsightsManager.trackData(null, null, null, null);
    }

    @Test
    public void trackData_withInvalidUrl_pass() {

        PNInsightsManager.trackData(RuntimeEnvironment.application.getApplicationContext(), null, null, null);
    }
}
