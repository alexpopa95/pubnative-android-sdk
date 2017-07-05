package net.pubnative.sdk.core.utils;

import android.content.Context;

import net.pubnative.sdk.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNAdvertisingIdTest {

    @Test
    public void request_withContextNull_pass() {
        PNAdvertisingId advertisingId = new PNAdvertisingId();
        PNAdvertisingId.Listener listener = mock(PNAdvertisingId.Listener.class);
        advertisingId.request(null, listener);

        verify(listener).onPubnativeAdvertisingIdFinish((String) isNull());
    }

    @Test
    public void request_withContext_onPNAPIAdvertisingIdFinish() {
        Context context = RuntimeEnvironment.application.getApplicationContext();

        PNAdvertisingId advertisingId = new PNAdvertisingId();
        PNAdvertisingId.Listener listener = mock(PNAdvertisingId.Listener.class);
        advertisingId.request(context, listener);

        verify(listener).onPubnativeAdvertisingIdFinish((String) isNull());
    }

}