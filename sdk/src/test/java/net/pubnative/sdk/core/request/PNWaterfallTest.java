package net.pubnative.sdk.core.request;


import android.content.Context;

import net.pubnative.sdk.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNWaterfallTest {

    @Test
    public void initialize_withNullContext_pass() {

        PNWaterfall waterfall = spy(PNWaterfall.class);
        String appToken = "app_token";
        String placementName = "testPlacementName";
        Context context = null;
        doCallRealMethod().when(waterfall).initialize(null, appToken, placementName);
        waterfall.initialize(null, appToken, placementName);
        verify(waterfall).initialize(eq(context), eq("app_token"), eq("testPlacementName"));
    }

    @Test
    public void initialize_withNullAppToken_pass() {

        PNWaterfall waterfall = spy(PNWaterfall.class);
        String placementName = "testPlacementName";
        String appToken = null;
        Context context = RuntimeEnvironment.application.getApplicationContext();
        doCallRealMethod().when(waterfall).initialize(context, null, placementName);
        waterfall.initialize(context, null, placementName);
        verify(waterfall).initialize(eq(context), eq(appToken), eq("testPlacementName"));
    }

    @Test
    public void initialize_withNullPlacement_pass() {

        PNWaterfall waterfall = spy(PNWaterfall.class);
        String appToken = "app_token";
        String placementName = null;
        Context context = RuntimeEnvironment.application.getApplicationContext();
        doCallRealMethod().when(waterfall).initialize(context, appToken, null);
        waterfall.initialize(context, appToken, null);
        verify(waterfall).initialize(eq(context), eq("app_token"), eq(placementName));
    }
}
