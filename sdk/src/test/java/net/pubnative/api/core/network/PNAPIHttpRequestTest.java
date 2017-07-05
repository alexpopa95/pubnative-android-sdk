package net.pubnative.api.core.network;


import android.os.Handler;

import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class PNAPIHttpRequestTest {

    @Test
    public void invokeFinish_withNullListener_pass(){
        PNAPIHttpRequest pnapiHttpRequest = mock(PNAPIHttpRequest.class);
        pnapiHttpRequest.mHandler = new Handler();
        pnapiHttpRequest.mListener = null;
        pnapiHttpRequest.invokeFinish("Test", PNAPIHttpRequest.HTTP_OK);
    }

    @Test
    public void invokeFinish_withValidListenerResult_pass(){
        PNAPIHttpRequest pnapiHttpRequest = mock(PNAPIHttpRequest.class);
        PNAPIHttpRequest.Listener listener = spy(PNAPIHttpRequest.Listener.class);
        pnapiHttpRequest.mHandler = new Handler();
        pnapiHttpRequest.mListener = listener;
        pnapiHttpRequest.invokeFinish("Test", PNAPIHttpRequest.HTTP_OK);
    }

    @Test
    public void invokeFinish_withNullResult_pass(){
        PNAPIHttpRequest pnapiHttpRequest = mock(PNAPIHttpRequest.class);
        PNAPIHttpRequest.Listener listener = spy(PNAPIHttpRequest.Listener.class);
        pnapiHttpRequest.mHandler = new Handler();
        pnapiHttpRequest.mListener = listener;
        pnapiHttpRequest.invokeFinish(null, PNAPIHttpRequest.HTTP_OK);
    }

    @Test
    public void invokeFinish_withNullResult_AndNullListener_pass(){
        PNAPIHttpRequest pnapiHttpRequest = mock(PNAPIHttpRequest.class);
        pnapiHttpRequest.mHandler = new Handler();
        pnapiHttpRequest.mListener = null;
        pnapiHttpRequest.invokeFinish(null, PNAPIHttpRequest.HTTP_OK);
    }

    @Test
    public void invokeFail_withNullListener_pass(){
        PNAPIHttpRequest pnapiHttpRequest = mock(PNAPIHttpRequest.class);
        pnapiHttpRequest.mHandler = new Handler();
        pnapiHttpRequest.mListener = null;
        pnapiHttpRequest.invokeFail(any(Exception.class));
    }

    @Test
    public void invokeFail_withValidListenerResult_pass(){
        PNAPIHttpRequest pnapiHttpRequest = mock(PNAPIHttpRequest.class);
        PNAPIHttpRequest.Listener listener = spy(PNAPIHttpRequest.Listener.class);
        pnapiHttpRequest.mHandler = new Handler();
        pnapiHttpRequest.mListener = listener;
        pnapiHttpRequest.invokeFail(any(Exception.class));
    }
}
