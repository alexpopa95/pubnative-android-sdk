package net.pubnative.api.core.tracking;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import net.pubnative.sdk.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PNAPIImpressionTrackerTest {

    Context applicationContext;
    Activity activity;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        activity = Robolectric.buildActivity(Activity.class)
                              .create()
                              .resume()
                              .get();
    }

    @Test
    public void addView_withValidListener_invokeGetVisibilityTracker() {

        PNAPIVisibilityTracker.Listener listener = spy(PNAPIVisibilityTracker.Listener.class);
        PNAPIImpressionTracker impressionTracker = spy(PNAPIImpressionTracker.class);
        impressionTracker.mHandler = new Handler();
        impressionTracker.mVisibilityListener = listener;
        View view = new View(activity);
        impressionTracker.addView(view);
        verify(impressionTracker, times(1)).getVisibilityTracker();
    }

    @Test
    public void addView_withNullListener_invokeGetVisibilityTracker() {

        PNAPIImpressionTracker impressionTracker = spy(PNAPIImpressionTracker.class);
        impressionTracker.mHandler = new Handler();
        impressionTracker.mVisibilityListener = null;
        View view = new View(activity);
        impressionTracker.addView(view);
        verify(impressionTracker, times(1)).getVisibilityTracker();
    }
}
