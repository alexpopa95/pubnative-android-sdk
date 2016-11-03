package net.pubnative.mediation.adapter.network;

import net.pubnative.mediation.request.model.PubnativeAdModel;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PubnativeNetworkRequestAdapterTest {

    @Test
    public void invokeStart_withNullListener_pass() {
        PubnativeNetworkRequestAdapter adapter = mock(PubnativeNetworkRequestAdapter.class);
        doCallRealMethod().when(adapter).invokeStart();
        adapter.invokeStart();
    }

    @Test
    public void invokeStart_withValidListener_callbackStart() {
        PubnativeNetworkRequestAdapter adapter = mock(PubnativeNetworkRequestAdapter.class);
        doCallRealMethod().when(adapter).invokeStart();
        PubnativeNetworkRequestAdapter.Listener listener = spy(PubnativeNetworkRequestAdapter.Listener.class);
        adapter.mListener = listener;
        adapter.invokeStart();
        verify(listener).onPubnativeNetworkAdapterRequestStarted(eq(adapter));
    }

    @Test
    public void invokeLoaded_withNullListener_pass() {
        PubnativeNetworkRequestAdapter adapter = mock(PubnativeNetworkRequestAdapter.class);
        doCallRealMethod().when(adapter).invokeLoaded(any(PubnativeAdModel.class));
        adapter.invokeLoaded(null);
    }

    @Test
    public void invokeLoaded_withValidListener_callbackLoaded() {
        PubnativeNetworkRequestAdapter adapter = mock(PubnativeNetworkRequestAdapter.class);
        doCallRealMethod().when(adapter).invokeLoaded(any(PubnativeAdModel.class));
        PubnativeNetworkRequestAdapter.Listener listener = spy(PubnativeNetworkRequestAdapter.Listener.class);
        PubnativeAdModel model = mock(PubnativeAdModel.class);
        adapter.mListener = listener;
        adapter.invokeLoaded(model);
        verify(listener).onPubnativeNetworkAdapterRequestLoaded(eq(adapter), eq(model));
    }

    @Test
    public void invokeFailed_withNullListener_pass() {
        PubnativeNetworkRequestAdapter adapter = mock(PubnativeNetworkRequestAdapter.class);
        doCallRealMethod().when(adapter).invokeFailed(any(Exception.class));
        adapter.invokeFailed(null);
    }

    @Test
    public void invokeFailed_withValidListener_callbackFailed() {
        PubnativeNetworkRequestAdapter adapter = mock(PubnativeNetworkRequestAdapter.class);
        doCallRealMethod().when(adapter).invokeFailed(any(Exception.class));
        PubnativeNetworkRequestAdapter.Listener listener = spy(PubnativeNetworkRequestAdapter.Listener.class);
        Exception exception = mock(Exception.class);
        adapter.mListener = listener;
        adapter.invokeFailed(exception);
        verify(listener).onPubnativeNetworkAdapterRequestFailed(eq(adapter), eq(exception));
    }
}
