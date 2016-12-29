package net.pubnative.mediation.insights.model;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeInsightModelTest {

    @Test
    public void createNew_shouldBeFilledWithDefaults() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PubnativeInsightModel model = new PubnativeInsightModel(context);

        assertNotNull(model.mData);
    }

    @Test
    public void trackSuccededNetwork_withValidData_shouldAddNetwork() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PubnativeInsightModel model = new PubnativeInsightModel(context);
        PubnativeInsightDataModel dataModel = spy(PubnativeInsightDataModel.class);
        model.mData = dataModel;
        PubnativePriorityRuleModel ruleModel = spy(PubnativePriorityRuleModel.class);
        ruleModel.id = 1;
        ruleModel.network_code = "code";
        ruleModel.segment_ids = new ArrayList<Integer>();

        model.trackSuccededNetwork(ruleModel, 0);

        verify(dataModel).addNetwork(eq(ruleModel), any(Integer.class), any(PubnativeInsightCrashModel.class));
    }

}