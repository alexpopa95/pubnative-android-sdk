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

package net.pubnative.sdk.core.insights.model;

import android.content.Context;

import net.pubnative.sdk.BuildConfig;
import net.pubnative.sdk.core.config.model.PNPriorityRuleModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNInsightModelTest {

    @Test
    public void createNew_shouldBeFilledWithDefaults() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightModel model = new PNInsightModel(context);

        assertNotNull(model.mData);
    }

    @Test
    public void trackSuccededNetwork_withValidData_shouldAddNetwork() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightModel model = new PNInsightModel(context);
        PNInsightDataModel data = spy(new PNInsightDataModel(context));
        model.setData(data);
        PNPriorityRuleModel ruleModel = spy(PNPriorityRuleModel.class);
        ruleModel.id = 1;
        ruleModel.network_code = "code";
        ruleModel.segment_ids = new ArrayList<Integer>();

        long currentTime = System.currentTimeMillis();
        model.trackSuccededNetwork(ruleModel, currentTime);

        verify(data).addNetwork(eq(ruleModel), eq(currentTime), (PNInsightCrashModel) isNull());
    }

    @Test
    public void trackUnreachableNetwork_withValidData_shouldAddNetwork() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightModel model = new PNInsightModel(context);
        PNInsightDataModel data = spy(new PNInsightDataModel(context));
        model.setData(data);
        PNPriorityRuleModel ruleModel = spy(PNPriorityRuleModel.class);
        Exception exception = new Exception();
        ruleModel.id = 1;
        ruleModel.network_code = "code";
        ruleModel.segment_ids = new ArrayList<Integer>();

        long currentTime = System.currentTimeMillis();
        model.trackUnreachableNetwork(ruleModel, currentTime, exception);

        verify(data).addNetwork(eq(ruleModel), eq(currentTime), (PNInsightCrashModel) isNotNull());
    }

    @Test
    public void trackAttemptedNetwork_withValidData_shouldAddNetwork() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightModel model = new PNInsightModel(context);
        PNInsightDataModel data = spy(new PNInsightDataModel(context));
        model.setData(data);
        PNPriorityRuleModel ruleModel = spy(PNPriorityRuleModel.class);
        Exception exception = new Exception();
        ruleModel.id = 1;
        ruleModel.network_code = "code";
        ruleModel.segment_ids = new ArrayList<Integer>();

        long currentTime = System.currentTimeMillis();
        model.trackAttemptedNetwork(ruleModel, currentTime, exception);

        verify(data).addNetwork(eq(ruleModel), eq(currentTime), (PNInsightCrashModel) isNotNull());
    }

}