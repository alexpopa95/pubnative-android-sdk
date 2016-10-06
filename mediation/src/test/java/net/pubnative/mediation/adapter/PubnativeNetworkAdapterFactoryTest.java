// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
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
//

package net.pubnative.mediation.adapter;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.utils.PubnativeTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PubnativeNetworkAdapterFactoryTest {

    private PubnativeNetworkModel model;


    @Test
    public void test_empty() {
        assertThat(true).isTrue();
    }

    @Before
    public void setUp() {
        model = new PubnativeNetworkModel();
    }

    @Test
    public void createsAdaptersForPresentAdapters() {
        List<String> adapters = PubnativeTestUtils.getClassesPackages(PubnativeNetworkAdapterFactory.NETWORK_PACKAGE);
        for (String adapterName : adapters) {
            model = new PubnativeNetworkModel();
            model.adapter = adapterName;
            model.params = mock(HashMap.class);
            PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(model);
            try {
                assertThat(adapterInstance).isInstanceOf(Class.forName(PubnativeNetworkAdapterFactory.getPackageName(adapterName)));
            } catch (ClassNotFoundException e) {
                fail("PubnativeNetworkAdapterFactory should be able to create all the network classes given the name");
            }
        }
    }

    @Test
    public void createAdapterWithInvalidClassString() {
        model.adapter = "invalid_class_string";
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(model);
        assertThat(adapterInstance).isNull();
    }

    @Test
    public void createAdapterWithEmptyString() {
        model.adapter = "";
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(model);
        assertThat(adapterInstance).isNull();
    }

    @Test
    public void createAdapterWithNullString() {
        model.adapter = null;
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(model);
        assertThat(adapterInstance).isNull();
    }
}
