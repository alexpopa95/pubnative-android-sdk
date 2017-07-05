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
import net.pubnative.sdk.core.request.PNAdTargetingModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNInsightDataModelTest {

    @Test
    public void resetClearsLists() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel dataModelSpy = spy(new PNInsightDataModel(context));
        dataModelSpy.network = "sampleText";
        dataModelSpy.addAttemptedNetwork("sampleText");
        // lists are not null.
        assertThat(dataModelSpy.network).isNotNull();
        assertThat(dataModelSpy.attempted_networks).isNotNull();
        // call reset and verify lists are null.
        dataModelSpy.reset();
        assertThat(dataModelSpy.network).isNull();
        assertThat(dataModelSpy.attempted_networks).isNull();
    }

    @Test
    public void addInterestWithDifferentValues() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel dataModelSpy = spy(new PNInsightDataModel(context));

        // the list is null at the beginning
        assertThat(dataModelSpy.interests).isNull();

        // valid string
        PNAdTargetingModel targeting = new PNAdTargetingModel();
        targeting.addInterest("sampleText");
        dataModelSpy.setTargeting(targeting);
        assertThat(dataModelSpy.interests).isNotNull();
        assertThat(dataModelSpy.interests.size()).isNotZero();

        // reset interests
        dataModelSpy.interests = null;

        // interest as empty string
        PNAdTargetingModel emptyInterest = new PNAdTargetingModel();
        emptyInterest.addInterest("");
        dataModelSpy.setTargeting(emptyInterest);
        assertThat(dataModelSpy.interests).isNull();

        // interest as null
        PNAdTargetingModel nullInterest = new PNAdTargetingModel();
        nullInterest.addInterest(null);
        dataModelSpy.setTargeting(nullInterest);
        assertThat(dataModelSpy.interests).isNull();
    }

    @Test
    public void addUnreachableNetwork_withValidValue_isNotNull() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel dataModelSpy = spy(new PNInsightDataModel(context));

        // valid string
        dataModelSpy.addUnreachableNetwork("sampleText");
        assertThat(dataModelSpy.unreachable_networks).isNotNull();
        assertThat(dataModelSpy.unreachable_networks.size()).isNotZero();

    }

    @Test
    public void addUnreachableNetwork_withEmptyValue_isNull() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel dataModelSpy = spy(new PNInsightDataModel(context));

        // network as empty string
        dataModelSpy.addUnreachableNetwork("");
        assertThat(dataModelSpy.unreachable_networks).isNull();
    }

    @Test
    public void addUnreachableNetwork_withNullValue_isNull() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel dataModelSpy = spy(new PNInsightDataModel(context));

        // network as null
        dataModelSpy.addUnreachableNetwork(null);
        assertThat(dataModelSpy.unreachable_networks).isNull();
    }

    @Test
    public void addAttemptedNetwork_withValidString_createsArray() {

        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel model = spy(new PNInsightDataModel(context));
        model.addAttemptedNetwork("valid string");
        assertThat(model.attempted_networks).isNotNull();
    }

    @Test
    public void addAttemptedNetwork_withValidString_addsItem() {

        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel model = spy(new PNInsightDataModel(context));
        String validString = "valid string";
        model.addAttemptedNetwork(validString);
        assertThat(model.attempted_networks.size()).isNotZero();
        assertThat(model.attempted_networks.get(0)).isEqualTo(validString);
    }

    @Test
    public void addAttemptedNetwork_withEmptyString_doesNothing() {

        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel model = spy(new PNInsightDataModel(context));
        model.addAttemptedNetwork("");
        assertThat(model.attempted_networks).isNull();
    }

    @Test
    public void addAttemptedNetwork_withNullString_doesNothing() {

        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel model = spy(new PNInsightDataModel(context));
        model.addAttemptedNetwork(null);
        assertThat(model.attempted_networks).isNull();
    }

    @Test
    public void reset_shouldNullifyAttemptedNetworks() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNInsightDataModel model = spy(new PNInsightDataModel(context));
        model.attempted_networks = Arrays.asList("attempted_network");
        model.reset();
        assertThat(model.attempted_networks).isNull();
    }
}
