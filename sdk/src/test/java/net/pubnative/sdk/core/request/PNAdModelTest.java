package net.pubnative.sdk.core.request;

import net.pubnative.sdk.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNAdModelTest {

    @Test
    public void invokeImpressionConfirmed_withNullListener_pass() {

        PNAdModel adModel = mock(PNAdModel.class);
        adModel.mListener = null;
        doCallRealMethod().when(adModel).invokeImpressionConfirmed();
        adModel.invokeImpressionConfirmed();
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_callBackImpressionConfirmed() {

        PNAdModel adModel = mock(PNAdModel.class);
        PNAdModel.Listener listener = spy(PNAdModel.Listener.class);
        adModel.mListener = listener;
        doCallRealMethod().when(adModel).invokeImpressionConfirmed();
        adModel.invokeImpressionConfirmed();
        verify(listener).onPNAdImpression(eq(adModel));
    }

    @Test
    public void invokeClick_withNullListener_pass() {

        PNAdModel adModel = mock(PNAdModel.class);
        adModel.mListener = null;
        doCallRealMethod().when(adModel).invokeClick();
        adModel.invokeClick();
    }

    @Test
    public void invokeClick_withValidListener_callBackClick() {

        PNAdModel adModel = mock(PNAdModel.class);
        PNAdModel.Listener listener = spy(PNAdModel.Listener.class);
        adModel.mListener = listener;
        doCallRealMethod().when(adModel).invokeClick();
        adModel.invokeClick();
        verify(listener).onPNAdClick(eq(adModel));
    }

    @Test
    public void invokeFetchFinish_withNullListener_pass() {

        PNAdModel adModel = mock(PNAdModel.class);
        adModel.mListener = null;
        doCallRealMethod().when(adModel).invokeFetchFinish();
        adModel.invokeFetchFinish();
    }

    @Test
    public void invokeFetchFinish_withValidListener_callBackFetchFinished() {

        PNAdModel adModel = mock(PNAdModel.class);
        PNAdModel.Listener listener = spy(PNAdModel.Listener.class);
        adModel.mListener = listener;
        PNAdModel.FetchListener fetchListener = spy(PNAdModel.FetchListener.class);
        adModel.mFetchListeners = new ArrayList<PNAdModel.FetchListener>();
        adModel.mFetchListeners.add(fetchListener);
        doCallRealMethod().when(adModel).invokeFetchFinish();
        adModel.invokeFetchFinish();
        verify(fetchListener).onFetchFinish(eq(adModel));
    }

    @Test
    public void invokeFetchFail_withNullListener_pass() {
        PNAdModel adModel = mock(PNAdModel.class);
        adModel.mListener = null;
        doCallRealMethod().when(adModel).invokeFetchFail(any(Exception.class));
        adModel.invokeFetchFail(any(Exception.class));
    }

    @Test
    public void invokeFetchFail_withValidListener_callBackFetchFailed() {
        PNAdModel adModel = mock(PNAdModel.class);
        PNAdModel.Listener listener = spy(PNAdModel.Listener.class);
        adModel.mListener = listener;
        PNAdModel.FetchListener fetchListener = spy(PNAdModel.FetchListener.class);
        adModel.mFetchListeners = new ArrayList<PNAdModel.FetchListener>();
        adModel.mFetchListeners.add(fetchListener);
        doCallRealMethod().when(adModel).invokeFetchFail(any(Exception.class));
        Exception exception = mock(Exception.class);
        adModel.invokeFetchFail(exception);
        verify(fetchListener).onFetchFail(eq(adModel), any(Exception.class));
    }
}
