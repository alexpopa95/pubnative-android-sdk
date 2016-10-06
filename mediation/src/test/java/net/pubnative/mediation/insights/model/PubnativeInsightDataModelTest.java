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

package net.pubnative.mediation.insights.model;

import net.pubnative.mediation.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
@PrepareForTest(PubnativeInsightDataModel.class)
public class PubnativeInsightDataModelTest {

    private final String validString = "sampleText";

    @Test
    public void resetClearsLists() {
        PubnativeInsightDataModel dataModelSpy = spy(PubnativeInsightDataModel.class);
        dataModelSpy.network = validString;
        dataModelSpy.addAttemptedNetwork(validString);
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
        PubnativeInsightDataModel dataModelSpy = spy(PubnativeInsightDataModel.class);

        // the list is null at the beginning
        assertThat(dataModelSpy.interests).isNull();

        // valid string
        dataModelSpy.addInterest(validString);
        assertThat(dataModelSpy.interests).isNotNull();
        assertThat(dataModelSpy.interests.size()).isNotZero();

        // reset interests
        dataModelSpy.interests = null;

        // interest as empty string
        dataModelSpy.addInterest("");
        assertThat(dataModelSpy.interests).isNull();

        // interest as null
        dataModelSpy.addInterest(null);
        assertThat(dataModelSpy.interests).isNull();
    }

    @Test
    public void addAttemptedNetworkWithDifferentValues() {
        PubnativeInsightDataModel dataModelSpy = spy(PubnativeInsightDataModel.class);

        // the list is null at the beginning
        assertThat(dataModelSpy.attempted_networks).isNull();

        // valid string
        dataModelSpy.addAttemptedNetwork(validString);
        assertThat(dataModelSpy.attempted_networks).isNotNull();
        assertThat(dataModelSpy.attempted_networks.size()).isNotZero();

        // resets attempted_networks
        dataModelSpy.reset();

        // network as empty string
        dataModelSpy.addAttemptedNetwork("");
        assertThat(dataModelSpy.attempted_networks).isNull();

        // network as null
        dataModelSpy.addAttemptedNetwork(null);
        assertThat(dataModelSpy.attempted_networks).isNull();
    }
}
