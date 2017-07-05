package net.pubnative.sdk.core.config;


import android.content.Context;

import net.pubnative.sdk.BuildConfig;
import net.pubnative.sdk.core.config.PNPlacement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNPlacementTest {

    @Test
    public void load_withNullListener_pass() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNPlacement placement = new PNPlacement();
        placement.load(context, "", "", new HashMap(), null);
    }

    @Test
    public void load_withNullContext_onPlacementLoadFail() {
        PNPlacement placement = new PNPlacement();
        PNPlacement.Listener listener = spy(PNPlacement.Listener.class);

        placement.load(null, "", "", new HashMap(), listener);

        verify(listener).onPlacementLoadFail(eq(placement), any(Exception.class));
    }

    @Test
    public void load_withEmptyAppToken_onPlacementLoadFail() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNPlacement placement = new PNPlacement();
        PNPlacement.Listener listener = spy(PNPlacement.Listener.class);

        placement.load(context, "", "test", new HashMap(), listener);

        verify(listener).onPlacementLoadFail(eq(placement), any(Exception.class));
    }

    @Test
    public void load_withEmptyPlacementName_onPlacementLoadFail() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNPlacement placement = new PNPlacement();
        PNPlacement.Listener listener = spy(PNPlacement.Listener.class);

        placement.load(context, "12345", "", new HashMap(), listener);

        verify(listener).onPlacementLoadFail(eq(placement), any(Exception.class));
    }

    @Test
    public void load_withRightParams_pass() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        PNPlacement placement = new PNPlacement();
        PNPlacement.Listener listener = spy(PNPlacement.Listener.class);

        placement.load(context, "12345", "placement", new HashMap(), listener);

    }

}