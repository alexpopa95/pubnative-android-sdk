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

package net.pubnative.sdk.core.adapter;

import net.pubnative.sdk.core.adapter.request.PNAdapter;
import net.pubnative.sdk.core.adapter.request.PNAdapterFactory;
import net.pubnative.sdk.core.config.model.PNNetworkModel;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class PubnativeNetworkFactoryTest {

    @Test
    public void create_withInvalidAdapterName_returnsNull() {

        PNNetworkModel model = spy(PNNetworkModel.class);
        model.adapter = "invalid_class_string";
        PNAdapter adapter = PNAdapterFactory.create(model);
        assertThat(adapter).isNull();
    }

    @Test
    public void create_withNullAdapterName_returnsNull() {

        PNNetworkModel model = spy(PNNetworkModel.class);
        model.adapter = null;
        PNAdapter adapter = PNAdapterFactory.create(model);
        assertThat(adapter).isNull();
    }

    @Test
    public void create_withNormalAdapterName_returnsNotNull() {

        PNNetworkModel model = spy(PNNetworkModel.class);
        model.adapter = "PubnativeLibraryNetworkAdapter";
        PNAdapter adapter = PNAdapterFactory.create(model);
        assertThat(adapter).isNotNull();
    }
}
