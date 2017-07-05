package net.pubnative.api.core.tracking;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import net.pubnative.sdk.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PNAPIVisibilityTrackerTest {

    @Test
    public void addView_withValidListener_shouldScheduleVisibilityCheck() {

        PNAPIVisibilityTracker.Listener listener = spy(PNAPIVisibilityTracker.Listener.class);
        PNAPIVisibilityTracker pnapiVisibilityTracker = spy(PNAPIVisibilityTracker.class);
        pnapiVisibilityTracker.mHandler = new Handler();
        pnapiVisibilityTracker.setListener(listener);
        View view = new View(RuntimeEnvironment.application.getApplicationContext());
        pnapiVisibilityTracker.addView(view, 100);
        verify(pnapiVisibilityTracker, times(1)).scheduleVisibilityCheck();
    }

    @Test
    public void addView_withNullListener_shouldScheduleVisibilityCheck() {

        PNAPIVisibilityTracker pnapiVisibilityTracker = spy(PNAPIVisibilityTracker.class);
        pnapiVisibilityTracker.mHandler = new Handler();
        pnapiVisibilityTracker.setListener(null);
        View view = new View(RuntimeEnvironment.application.getApplicationContext());
        pnapiVisibilityTracker.addView(view, 100);
        verify(pnapiVisibilityTracker, times(1)).scheduleVisibilityCheck();
    }
}
