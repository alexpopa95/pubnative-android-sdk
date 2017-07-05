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

package net.pubnative.sdk.core.config;

import android.content.Context;

import net.pubnative.sdk.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNDeliveryManagerTest {

    private static final String PLACEMENT_ID_VALID = "placement_id";
    private Context applicationContext;

    @Before
    public void setUp() {
        applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testLastUpdateWithAllValues() {
        // Nothing is set
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        Calendar calendar = getMockedCalendar();

        PNDeliveryManager.setImpressionLastUpdate(null, null, null);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PNDeliveryManager.setImpressionLastUpdate(null, "", null);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PNDeliveryManager.setImpressionLastUpdate(null, null, calendar);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PNDeliveryManager.setImpressionLastUpdate(null, "", calendar);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PNDeliveryManager.setImpressionLastUpdate(null, PLACEMENT_ID_VALID, calendar);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PNDeliveryManager.setImpressionLastUpdate(applicationContext, null, calendar);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PNDeliveryManager.setImpressionLastUpdate(applicationContext, "", calendar);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PNDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNotNull();
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID).getTimeInMillis()).isEqualTo(calendar.getTimeInMillis());

        // Using a null removes the last setted up value
        PNDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, null);
        assertThat(PNDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();
    }

    private Calendar getMockedCalendar() {
        Calendar calendar = mock(Calendar.class);
        Long currentMillis = System.currentTimeMillis();
        when(calendar.getTimeInMillis()).thenReturn(currentMillis);
        return calendar;
    }

    @Test
    public void testImpressionCountWithAllValues() {
        String trackingKeyString = "trackingKeyString";

        // Nothing is set
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        // Invalid arguments
        PNDeliveryManager.setImpressionCount(null, null, null, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(null, "", null, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(null, null, "", 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(null, trackingKeyString, null, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(null, null, PLACEMENT_ID_VALID, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(null, trackingKeyString, "", 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(null, "", PLACEMENT_ID_VALID, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(applicationContext, null, null, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(applicationContext, "", null, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(applicationContext, null, "", 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(applicationContext, trackingKeyString, null, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(applicationContext, null, PLACEMENT_ID_VALID, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(applicationContext, trackingKeyString, "", 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.setImpressionCount(applicationContext, "", PLACEMENT_ID_VALID, 0);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        // Valid arguments
        PNDeliveryManager.setImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID, 10);
        assertThat(PNDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isEqualTo(10);
    }

    @Test
    public void getCurrentCountsReturnsZeroWithInvalidParameters() {
        PNDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);

        assertThat(PNDeliveryManager.getCurrentDailyCount(null, null)).isZero();
        assertThat(PNDeliveryManager.getCurrentDailyCount(null, "")).isZero();
        assertThat(PNDeliveryManager.getCurrentDailyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, null)).isZero();
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, "")).isZero();

        assertThat(PNDeliveryManager.getCurrentHourlyCount(null, null)).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(null, "")).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, null)).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, "")).isZero();
    }

    @Test
    public void logImpressionIncrementsCount() {
        PNDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isEqualTo(1);
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isEqualTo(1);
    }

    @Test
    public void logImpressionDoesNothingWithNullParameters() {
        PNDeliveryManager.logImpression(null, PLACEMENT_ID_VALID);
        PNDeliveryManager.logImpression(null, "");
        PNDeliveryManager.logImpression(applicationContext, null);
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesCounts() {
        Calendar calendar = mock(Calendar.class);
        when(calendar.getTimeInMillis()).thenReturn((long) 10);
        PNDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        // Null
        assertThat(PNDeliveryManager.getCurrentDailyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PNDeliveryManager.getCurrentDailyCount(null, "")).isZero();
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, null)).isZero();
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, "")).isZero();

        assertThat(PNDeliveryManager.getCurrentHourlyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(null, "")).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, null)).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, "")).isZero();
        // Valid
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesWithMoreThanOneDay() {
        PNDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        PNDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        PNDeliveryManager.updateImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesWithMoreThanOneHour() {
        PNDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        PNDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        PNDeliveryManager.updateImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionDontUpdatesWithValid() {
        PNDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        PNDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        PNDeliveryManager.updateImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();
    }

    @Test
    public void resetMethodsWorksWithValidParams() {
        PNDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);

        PNDeliveryManager.resetHourlyImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.resetDailyImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();

        PNDeliveryManager.updatePacingCalendar(PLACEMENT_ID_VALID);

        PNDeliveryManager.resetPacingCalendar(PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getPacingCalendar(PLACEMENT_ID_VALID)).isNull();
    }

    @Test
    public void pacingCalendarDoNotResetWithInvalidParams() {
        PNDeliveryManager.updatePacingCalendar(PLACEMENT_ID_VALID);

        // pacing calendar
        PNDeliveryManager.resetPacingCalendar("");
        assertThat(PNDeliveryManager.getPacingCalendar(PLACEMENT_ID_VALID)).isNotNull();

        PNDeliveryManager.resetPacingCalendar(null);
        assertThat(PNDeliveryManager.getPacingCalendar(PLACEMENT_ID_VALID)).isNotNull();
    }

    @Test
    public void impressionCountDoNotResetWithInvalidParams() {
        PNDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);

        // hourly count
        PNDeliveryManager.resetHourlyImpressionCount(applicationContext, "");
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PNDeliveryManager.resetHourlyImpressionCount(applicationContext, null);
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PNDeliveryManager.resetHourlyImpressionCount(null, PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        // daily count
        PNDeliveryManager.resetDailyImpressionCount(applicationContext, "");
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PNDeliveryManager.resetDailyImpressionCount(applicationContext, null);
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PNDeliveryManager.resetDailyImpressionCount(null, PLACEMENT_ID_VALID);
        assertThat(PNDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();
    }
}
