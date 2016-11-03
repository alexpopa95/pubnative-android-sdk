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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeDeliveryManagerTest {

    private Context applicationContext;
    private static final String PLACEMENT_ID_VALID = "placement_id";

    @Before
    public void setUp() {
        applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testLastUpdateWithAllValues() {
        // Nothing is set
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        Calendar calendar = getMockedCalendar();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, null, null);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, "", null);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, null, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, "", calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(null, PLACEMENT_ID_VALID, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(applicationContext, null, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(applicationContext, "", calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();

        PubnativeDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNotNull();
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID).getTimeInMillis()).isEqualTo(calendar.getTimeInMillis());

        // Using a null removes the last setted up value
        PubnativeDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, null);
        assertThat(PubnativeDeliveryManager.getImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID)).isNull();
    }

    private Calendar getMockedCalendar() {
        Calendar calendar      = mock(Calendar.class);
        Long     currentMillis = System.currentTimeMillis();
        when(calendar.getTimeInMillis()).thenReturn(currentMillis);
        return calendar;
    }

    @Test
    public void testImpressionCountWithAllValues() {
        String trackingKeyString = "trackingKeyString";

        // Nothing is set
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        // Invalid arguments
        PubnativeDeliveryManager.setImpressionCount(null, null, null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(null, "", null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(null, null, "", 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(null, trackingKeyString, null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(null, null, PLACEMENT_ID_VALID, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(null, trackingKeyString, "", 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(null, "", PLACEMENT_ID_VALID, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(applicationContext, null, null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(applicationContext, "", null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(applicationContext, null, "", 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(applicationContext, trackingKeyString, null, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(applicationContext, null, PLACEMENT_ID_VALID, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(applicationContext, trackingKeyString, "", 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.setImpressionCount(applicationContext, "", PLACEMENT_ID_VALID, 0);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isZero();

        // Valid arguments
        PubnativeDeliveryManager.setImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID, 10);
        assertThat(PubnativeDeliveryManager.getImpressionCount(applicationContext, trackingKeyString, PLACEMENT_ID_VALID)).isEqualTo(10);
    }

    @Test
    public void getCurrentCountsReturnsZeroWithInvalidParameters() {
        PubnativeDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);

        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, "")).isZero();

        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, "")).isZero();
    }

    @Test
    public void logImpressionIncrementsCount() {
        PubnativeDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isEqualTo(1);
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isEqualTo(1);
    }

    @Test
    public void logImpressionDoesNothingWithNullParameters() {
        PubnativeDeliveryManager.logImpression(null, PLACEMENT_ID_VALID);
        PubnativeDeliveryManager.logImpression(null, "");
        PubnativeDeliveryManager.logImpression(applicationContext, null);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesCounts() {
        Calendar calendar = mock(Calendar.class);
        when(calendar.getTimeInMillis()).thenReturn((long) 10);
        PubnativeDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        // Null
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, "")).isZero();

        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(null, "")).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, null)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, "")).isZero();
        // Valid
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesWithMoreThanOneDay() {
        PubnativeDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        PubnativeDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        PubnativeDeliveryManager.updateImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionUpdatesWithMoreThanOneHour() {
        PubnativeDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        PubnativeDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        PubnativeDeliveryManager.updateImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();
    }

    @Test
    public void updateImpressionDontUpdatesWithValid() {
        PubnativeDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);
        Calendar calendar = Calendar.getInstance();
        PubnativeDeliveryManager.setImpressionLastUpdate(applicationContext, PLACEMENT_ID_VALID, calendar);
        PubnativeDeliveryManager.updateImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();
    }

    @Test
    public void resetMethodsWorksWithValidParams() {
        PubnativeDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);

        PubnativeDeliveryManager.resetHourlyImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();

        PubnativeDeliveryManager.resetDailyImpressionCount(applicationContext, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isZero();

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
        PubnativeDeliveryManager.logImpression(applicationContext, PLACEMENT_ID_VALID);

        // hourly count
        PubnativeDeliveryManager.resetHourlyImpressionCount(applicationContext, "");
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PubnativeDeliveryManager.resetHourlyImpressionCount(applicationContext, null);
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PubnativeDeliveryManager.resetHourlyImpressionCount(null, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentHourlyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        // daily count
        PubnativeDeliveryManager.resetDailyImpressionCount(applicationContext, "");
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PubnativeDeliveryManager.resetDailyImpressionCount(applicationContext, null);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();

        PubnativeDeliveryManager.resetDailyImpressionCount(null, PLACEMENT_ID_VALID);
        assertThat(PubnativeDeliveryManager.getCurrentDailyCount(applicationContext, PLACEMENT_ID_VALID)).isNotZero();
    }
}
