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

import net.pubnative.api.core.request.model.PNAPIAdModel;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PubnativeLibraryCPICacheTest {

    @Before
    public void setUp() {
        PubnativeLibraryCPICache.getInstance().clear();
    }

    @Test
    public void enqueue_withEmptyQueue_shouldIncreaseCount() {

        PubnativeLibraryCPICache.getInstance().enqueue(mock(PNAPIAdModel.class));
        assertThat(PubnativeLibraryCPICache.getInstance().getQueueSize()).isEqualTo(1);
    }

    @Test
    public void enqueue_withNonEmptyQueue_shouldIncreaseCount() {

        // add item in queue
        PubnativeLibraryCPICache.getInstance().enqueue(mock(PNAPIAdModel.class));
        int queueSize = PubnativeLibraryCPICache.getInstance().getQueueSize();

        PubnativeLibraryCPICache.getInstance().enqueue(mock(PNAPIAdModel.class));

        assertThat(PubnativeLibraryCPICache.getInstance().getQueueSize()).isEqualTo(queueSize + 1);
    }

    @Test
    public void dequeue_withEmptyQueue_shouldReturnNull() {

        assertThat(PubnativeLibraryCPICache.getInstance().dequeue()).isNull();
    }

    @Test
    public void dequeue_withNonEmptyQueue_shouldReturnAd() {

        PNAPIAdModel adModel = mock(PNAPIAdModel.class);

        PubnativeLibraryCPICache.getInstance().enqueue(adModel);

        assertThat(PubnativeLibraryCPICache.getInstance().dequeue()).isEqualTo(adModel);
    }

    @Test
    public void dequeue_withNonEmptyQueue_shouldDecreaseCount() {

        PubnativeLibraryCPICache.getInstance().enqueue(mock(PNAPIAdModel.class));

        int queueSize = PubnativeLibraryCPICache.getInstance().getQueueSize();

        PubnativeLibraryCPICache.getInstance().dequeue();

        assertThat(PubnativeLibraryCPICache.getInstance().getQueueSize()).isEqualTo(queueSize - 1);
    }

    @Test
    public void dequeue_withNonEmptyQueue_shouldRemoveOutdatedAd() {

        // enqueue one ad with invalid timestamp
        PubnativeLibraryCPICache.getInstance().enqueue(mock(PubnativeLibraryCPICache.CacheItem.class));
        // enqueue one ad with valid timestamp
        PubnativeLibraryCPICache.getInstance().enqueue(mock(PNAPIAdModel.class));
        PubnativeLibraryCPICache.getInstance().dequeue();

        assertThat(PubnativeLibraryCPICache.getInstance().getQueueSize()).isZero();
    }

    @Test
    public void dequeue_shouldReturnAdsInSequence() {

        PNAPIAdModel ad1 = mock(PNAPIAdModel.class);
        PNAPIAdModel ad2 = mock(PNAPIAdModel.class);

        PubnativeLibraryCPICache.getInstance().enqueue(Arrays.asList(ad1, ad2));

        assertThat(PubnativeLibraryCPICache.getInstance().dequeue()).isEqualTo(ad1);
        assertThat(PubnativeLibraryCPICache.getInstance().dequeue()).isEqualTo(ad2);
    }
}
