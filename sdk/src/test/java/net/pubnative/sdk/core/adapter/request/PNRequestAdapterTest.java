// The MIT License (MIT)
//
// Copyright (c) 2017 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package net.pubnative.sdk.core.adapter.request;

import net.pubnative.sdk.core.request.PNAdModel;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PNRequestAdapterTest {

    @Test
    public void invokeLoaded_withNullListener_pass() {
        PNAdapter adapter = mock(PNAdapter.class);
        doCallRealMethod().when(adapter).invokeLoadFinish(any(PNAdModel.class));
        adapter.invokeLoadFinish(null);
    }

    @Test
    public void invokeLoaded_withValidListener_callbackLoaded() {
        PNAdapter adapter = mock(PNAdapter.class);
        doCallRealMethod().when(adapter).invokeLoadFinish(any(PNAdModel.class));
        PNAdapter.LoadListener loadListener = spy(PNAdapter.LoadListener.class);
        PNAdModel model = mock(PNAdModel.class);
        adapter.mLoadListener = loadListener;
        adapter.invokeLoadFinish(model);
        verify(loadListener).onAdapterLoadFinish(eq(adapter), eq(model));
    }

    @Test
    public void invokeFailed_withNullListener_pass() {
        PNAdapter adapter = mock(PNAdapter.class);
        doCallRealMethod().when(adapter).invokeLoadFail(any(Exception.class));
        adapter.invokeLoadFail(null);
    }

    @Test
    public void invokeFailed_withValidListener_callbackFailed() {
        PNAdapter adapter = mock(PNAdapter.class);
        doCallRealMethod().when(adapter).invokeLoadFail(any(Exception.class));
        PNAdapter.LoadListener loadListener = spy(PNAdapter.LoadListener.class);
        Exception exception = mock(Exception.class);
        adapter.mLoadListener = loadListener;
        adapter.invokeLoadFail(exception);
        verify(loadListener).onAdapterLoadFail(eq(adapter), eq(exception));
    }
}
