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

package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;

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

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(PubnativeDeliveryManager.class)
public class PubnativeDeliveryManagerTest {

    private Context applicationContext;
    private static final String PLACEMENT_ID_VALID = "placement_id";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void getlastUpdateWithAllValues() {
        // Returns null when not setted up or bad parameters
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();

        // Returns previous setted value and nothing
        Calendar calendar = this.getMockedCalendar();
        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(null, PLACEMENT_ID_VALID)).isNull();
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(null, "")).isNull();
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, null)).isNull();
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, "")).isNull();
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID).getTimeInMillis()).isEqualTo(calendar.getTimeInMillis());
    }

    @Test
    public void setLastUpdateWithAllValues() {
        Calendar calendar = this.getMockedCalendar();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, null, null);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, "", null);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, null, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, "", calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, PLACEMENT_ID_VALID, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, null, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, "", calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNotNull();
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID).getTimeInMillis()).isEqualTo(calendar.getTimeInMillis());

        // Using a null removes the last setted up value
        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID, null);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID)).isNull();
    }

    private Calendar getMockedCalendar() {
        Calendar calendar      = mock(Calendar.class);
        Long     currentMillis = System.currentTimeMillis();
        when(calendar.getTimeInMillis()).thenReturn(currentMillis);
        return calendar;
    }

    @Test
    public void getImpressionCountWithAllValues() {
        String trackingKeyString = "trackingKey";

        // Returns 0 when not setted up
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        // Returns previous setted value and 0 when wrong parameters
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID, 10);
        assertThat(PubnativeDeliveryManager.getImpressionCount(null, null, null)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(null, null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(null, "", null)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(null, "", "")).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(null, trackingKeyString, null)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(null, null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(null, trackingKeyString, "")).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(null, "", PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, "", null)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, "", "")).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, null)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, "")).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, "", PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isEqualTo(10);
    }

    @Test
    public void setImpressionCountWithAllValues() {
        String trackingKeyString = "trackingKeyString";

        // Invalid arguments
        PubnativeDeliveryManager.setImpressionCount(null, null, null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(null, "", null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(null, null, "", 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(null, trackingKeyString, null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(null, null, PLACEMENT_ID_VALID, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(null, trackingKeyString, "", 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(null, "", PLACEMENT_ID_VALID, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, null, null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, "", null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, null, "", 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, trackingKeyString, null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, null, PLACEMENT_ID_VALID, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, trackingKeyString, "", 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, "", PLACEMENT_ID_VALID, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        // Valid arguments
        PubnativeDeliveryManager.setImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID, 10);
        assertThat(PubnativeDeliveryManager.getImpressionCount(this.applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isEqualTo(10);
    }

    @Test
    public void publicMethodsUpdatesData() {
        PowerMockito.spy(PubnativeDeliveryManager.class);
        // Updates once
        PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID);
        // Updates once
        PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID);
        // Updates twice
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);
        PowerMockito.verifyStatic(times(4));
        PubnativeDeliveryManager.updateImpressionCount(any(Context.class), anyString());
    }

    @Test
    public void getCurrentCountsReturnsZeroWithInvalidParameters() {
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);

        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, "")).isZero();

        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, "")).isZero();
    }

    @Test
    public void logImpressionIncrementsCount() {
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isEqualTo(1);
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isEqualTo(1);
    }

    @Test
    public void logImpressionDoesNothingWithNullParameters() {
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
        PubnativeDeliveryManager.logImpression(null, PLACEMENT_ID_VALID);
        PubnativeDeliveryManager.logImpression(null, "");
        PubnativeDeliveryManager.logImpression(this.applicationContext, null);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesCounts() {
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        Calendar calendar = mock(Calendar.class);
        when(calendar.getTimeInMillis()).thenReturn((long) 10);
        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID, calendar);
        // Null
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, "")).isZero();
        // Valid
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesWithMoreThanOneDay() {
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID, calendar);
        PubnativeDeliveryManager.updateImpressionCount(this.applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesWithMoreThanOneHour() {
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID, calendar);
        PubnativeDeliveryManager.updateImpressionCount(this.applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionDontUpdatesWithValid() {
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        PubnativeDeliveryManager.setImpressionLastUpdate(this.applicationContext, PLACEMENT_ID_VALID, calendar);
        PubnativeDeliveryManager.updateImpressionCount(this.applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();
    }

    @Test
    public void resetMethodCalledWithImpHourCapChange() {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", PLACEMENT_ID_VALID);

        PowerMockito.spy(PubnativeDeliveryManager.class);
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);

        String configJson = PubnativeConfigTestUtils.getConfigApiResponseJsonFromResource("valid_config_imp_cap_hour_changed.json");
        PubnativeConfigManager.processConfigDownloadResponse(this.applicationContext, PLACEMENT_ID_VALID, configJson);

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetPacingCalendar(anyString());

        PowerMockito.verifyStatic(times(1));
        PubnativeDeliveryManager.resetHourlyImpressionCount(eq(this.applicationContext), anyString());

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetDailyImpressionCount(eq(this.applicationContext), anyString());
    }

    @Test
    public void resetMethodCalledWithImpDayCapChange() {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", PLACEMENT_ID_VALID);

        PowerMockito.spy(PubnativeDeliveryManager.class);
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);

        String configJson = PubnativeConfigTestUtils.getConfigApiResponseJsonFromResource("valid_config_imp_cap_day_changed.json");
        PubnativeConfigManager.processConfigDownloadResponse(this.applicationContext, PLACEMENT_ID_VALID, configJson);

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetPacingCalendar(anyString());

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetHourlyImpressionCount(eq(this.applicationContext), anyString());

        PowerMockito.verifyStatic(times(1));
        PubnativeDeliveryManager.resetDailyImpressionCount(eq(this.applicationContext), anyString());
    }

    @Test
    public void resetMethodCalledWhenPacingCapHourChanged() {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", PLACEMENT_ID_VALID);

        PowerMockito.spy(PubnativeDeliveryManager.class);
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);

        String configJson = PubnativeConfigTestUtils.getConfigApiResponseJsonFromResource("valid_config_pacing_cap_hour_changed.json");
        PubnativeConfigManager.processConfigDownloadResponse(this.applicationContext, PLACEMENT_ID_VALID, configJson);

        PowerMockito.verifyStatic(times(1));
        PubnativeDeliveryManager.resetPacingCalendar(anyString());

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetHourlyImpressionCount(eq(this.applicationContext), anyString());

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetDailyImpressionCount(eq(this.applicationContext), anyString());
    }

    @Test
    public void resetMethodCalledWhenPacingCapMinuteChanged() {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", PLACEMENT_ID_VALID);

        PowerMockito.spy(PubnativeDeliveryManager.class);
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);

        String configJson = PubnativeConfigTestUtils.getConfigApiResponseJsonFromResource("valid_config_pacing_cap_minute_changed.json");
        PubnativeConfigManager.processConfigDownloadResponse(this.applicationContext, PLACEMENT_ID_VALID, configJson);

        PowerMockito.verifyStatic(times(1));
        PubnativeDeliveryManager.resetPacingCalendar(anyString());

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetHourlyImpressionCount(eq(this.applicationContext), anyString());

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetDailyImpressionCount(eq(this.applicationContext), anyString());
    }

    @Test
    public void resetMethodsNotCalledForSameDeliveryRule() {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", PLACEMENT_ID_VALID);

        PowerMockito.spy(PubnativeDeliveryManager.class);
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);

        String configJson = PubnativeConfigTestUtils.getConfigApiResponseJsonFromResource("valid_config.json");
        PubnativeConfigManager.processConfigDownloadResponse(this.applicationContext, PLACEMENT_ID_VALID, configJson);

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetPacingCalendar(anyString());

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetHourlyImpressionCount(eq(this.applicationContext), anyString());

        PowerMockito.verifyStatic(never());
        PubnativeDeliveryManager.resetDailyImpressionCount(eq(this.applicationContext), anyString());
    }

    @Test
    public void resetMethodsWorksWithValidParams() {
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);

        PubnativeDeliveryManager.resetHourlyImpressionCount(this.applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.resetDailyImpressionCount(this.applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.updatePacingCalendar(PLACEMENT_ID_VALID);

        PubnativeDeliveryManager.resetPacingCalendar(PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getPacingCalendar(PLACEMENT_ID_VALID)).isNull();
    }

    @Test
    public void pacingCalendarDoNotResetWithInvalidParams() {
        PubnativeDeliveryManager.updatePacingCalendar(PLACEMENT_ID_VALID);

        // pacing calendar
        PubnativeDeliveryManager.resetPacingCalendar("");
        assertThat(PubnativeDeliveryManager.getPacingCalendar(PLACEMENT_ID_VALID)).isNotNull();

        PubnativeDeliveryManager.resetPacingCalendar(null);
        assertThat(PubnativeDeliveryManager.getPacingCalendar(PLACEMENT_ID_VALID)).isNotNull();
    }

    @Test
    public void impressionCountDoNotResetWithInvalidParams() {
        PubnativeDeliveryManager.logImpression(this.applicationContext, PLACEMENT_ID_VALID);

        // hourly count
        PubnativeDeliveryManager.resetHourlyImpressionCount(this.applicationContext, "");
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PubnativeDeliveryManager.resetHourlyImpressionCount(this.applicationContext, null);
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PubnativeDeliveryManager.resetHourlyImpressionCount(null, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        // daily count
        PubnativeDeliveryManager.resetDailyImpressionCount(this.applicationContext, "");
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PubnativeDeliveryManager.resetDailyImpressionCount(this.applicationContext, null);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PubnativeDeliveryManager.resetDailyImpressionCount(null, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(this.applicationContext, PLACEMENT_ID_VALID)).isNotZero();
    }
}
