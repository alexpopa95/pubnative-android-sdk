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

package net.pubnative.mediation.config.model;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class PubnativeDeliveryRuleModelTest {

    PubnativeDeliveryRuleModel modelSpy;

    @Before
    public void setUp() {
        modelSpy = spy(PubnativeDeliveryRuleModel.class);
    }

    @Test
    public void isActiveWithValues() {
        modelSpy.no_ads = false;
        assertThat(modelSpy.isDisabled()).isFalse();
        modelSpy.no_ads = true;
        assertThat(modelSpy.isDisabled()).isTrue();
    }

    @Test
    public void isDayImpressionCapActiveWithValues() {
        modelSpy.imp_cap_day = -1;
        assertThat(modelSpy.isDayImpressionCapActive()).isFalse();
        modelSpy.imp_cap_day = 0;
        assertThat(modelSpy.isDayImpressionCapActive()).isFalse();
        modelSpy.imp_cap_day = 1;
        assertThat(modelSpy.isDayImpressionCapActive()).isTrue();
    }

    @Test
    public void isHourImpressionCapActiveWithValues() {
        modelSpy.imp_cap_hour = -1;
        assertThat(modelSpy.isHourImpressionCapActive()).isFalse();
        modelSpy.imp_cap_hour = 0;
        assertThat(modelSpy.isHourImpressionCapActive()).isFalse();
        modelSpy.imp_cap_hour = 1;
        assertThat(modelSpy.isHourImpressionCapActive()).isTrue();
    }

    @Test
    public void isPacingCapActiveWithValues() {
        modelSpy.pacing_cap_minute = -1;
        modelSpy.pacing_cap_hour = -1;
        assertThat(modelSpy.isPacingCapActive()).isFalse();

        modelSpy.pacing_cap_minute = -1;
        modelSpy.pacing_cap_hour = 0;
        assertThat(modelSpy.isPacingCapActive()).isFalse();

        modelSpy.pacing_cap_minute = 0;
        modelSpy.pacing_cap_hour = -1;
        assertThat(modelSpy.isPacingCapActive()).isFalse();

        modelSpy.pacing_cap_minute = 0;
        modelSpy.pacing_cap_hour = 0;
        assertThat(modelSpy.isPacingCapActive()).isFalse();

        modelSpy.pacing_cap_minute = 1;
        modelSpy.pacing_cap_hour = 0;
        assertThat(modelSpy.isPacingCapActive()).isTrue();

        modelSpy.pacing_cap_minute = 0;
        modelSpy.pacing_cap_hour = 1;
        assertThat(modelSpy.isPacingCapActive()).isTrue();

        modelSpy.pacing_cap_minute = 1;
        modelSpy.pacing_cap_hour = 1;
        assertThat(modelSpy.isPacingCapActive()).isTrue();
    }
}
