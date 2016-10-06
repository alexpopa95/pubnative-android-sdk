package net.pubnative.mediation.adapter.network;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PubnativeNetworkBannerAdapterTest {

    @Test
    public void invokeLoadFinish_withNullListener_pass() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeLoadFinish();
        adapter.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFail_withNullListener_pass() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeLoadFail(any(Exception.class));
        adapter.invokeLoadFail(any(Exception.class));
    }

    @Test
    public void invokeShow_withNullListener_pass() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeShow();
        adapter.invokeShow();
    }

    @Test
    public void invokeImpressionConfirmed_withNullListener_pass() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeImpressionConfirmed();
        adapter.invokeImpressionConfirmed();
    }

    @Test
    public void invokeClick_withNullListener_pass() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeClick();
        adapter.invokeClick();
    }

    @Test
    public void invokehide_withNullListener_pass() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeHide();
        adapter.invokeHide();
    }

    @Test
    public void invokeLoadFinish_withValidListener_callbackLoadFinish() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeLoadFinish();
        PubnativeNetworkBannerAdapter.LoadListener listener = spy(PubnativeNetworkBannerAdapter.LoadListener.class);
        adapter.mLoadListener = listener;
        adapter.invokeLoadFinish();
        verify(listener).onAdapterLoadFinish(eq(adapter));
    }

    @Test
    public void invokeLoadFail_withValidListener_callbackLoadFail() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        Exception exception = mock(Exception.class);
        doCallRealMethod().when(adapter).invokeLoadFail(exception);
        PubnativeNetworkBannerAdapter.LoadListener listener = spy(PubnativeNetworkBannerAdapter.LoadListener.class);
        adapter.mLoadListener = listener;
        adapter.invokeLoadFail(exception);
        verify(listener).onAdapterLoadFail(eq(adapter), eq(exception));
    }

    @Test
    public void invokeShow_withValidListener_callbackShow() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeShow();
        PubnativeNetworkBannerAdapter.AdListener listener = spy(PubnativeNetworkBannerAdapter.AdListener.class);
        adapter.mAdListener = listener;
        adapter.invokeShow();
        verify(listener).onAdapterShow(eq(adapter));
    }

    @Test
    public void invokeHide_withValidListener_callbackHide() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeHide();
        PubnativeNetworkBannerAdapter.AdListener listener = spy(PubnativeNetworkBannerAdapter.AdListener.class);
        adapter.mAdListener = listener;
        adapter.invokeHide();
        verify(listener).onAdapterHide(eq(adapter));
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_callbackImpressionConfirmed() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeImpressionConfirmed();
        PubnativeNetworkBannerAdapter.AdListener listener = spy(PubnativeNetworkBannerAdapter.AdListener.class);
        adapter.mAdListener = listener;
        adapter.invokeImpressionConfirmed();
        verify(listener).onAdapterImpressionConfirmed(eq(adapter));
    }

    @Test
    public void invokeClick_withValidListener_callbackClick() {
        PubnativeNetworkBannerAdapter adapter = mock(PubnativeNetworkBannerAdapter.class);
        doCallRealMethod().when(adapter).invokeClick();
        PubnativeNetworkBannerAdapter.AdListener listener = spy(PubnativeNetworkBannerAdapter.AdListener.class);
        adapter.mAdListener = listener;
        adapter.invokeClick();
        verify(listener).onAdapterClick(eq(adapter));
    }
}
