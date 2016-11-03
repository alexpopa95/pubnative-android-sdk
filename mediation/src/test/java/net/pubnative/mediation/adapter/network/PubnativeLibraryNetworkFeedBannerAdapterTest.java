package net.pubnative.mediation.adapter.network;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.exceptions.PubnativeException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeLibraryNetworkFeedBannerAdapterTest {

    @Test
    public void load_withNullContext_shouldCallBackFail() {
        Map mapData = mock(Map.class);
        PubnativeLibraryNetworkFeedBannerAdapter adapter = spy(new PubnativeLibraryNetworkFeedBannerAdapter(mapData));
        adapter.load(null);

        verify(adapter).invokeLoadFail(eq(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS));
    }

    @Test
    public void load_withNullMapData_shouldCallBackFail() {
        PubnativeLibraryNetworkFeedBannerAdapter adapter = spy(new PubnativeLibraryNetworkFeedBannerAdapter(null));
        adapter.load(RuntimeEnvironment.application.getApplicationContext());

        verify(adapter).invokeLoadFail(eq(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS));
    }

    @Test
    public void load_withEmptyAppToken_shouldCallBackFail() {
        Map mapData = mock(Map.class);
        when(mapData.get(PubnativeNetworkBannerAdapter.KEY_APP_TOKEN)).thenReturn("");
        PubnativeLibraryNetworkFeedBannerAdapter adapter = spy(new PubnativeLibraryNetworkFeedBannerAdapter(mapData));
        adapter.load(RuntimeEnvironment.application.getApplicationContext());

        verify(adapter).invokeLoadFail(eq(PubnativeException.ADAPTER_MISSING_DATA));
    }
}
