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

package net.pubnative.mediation.exceptions;

import net.pubnative.mediation.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeExceptionTest {

    @Test
    public void extraException_withValidExtraData_extraMapIsNotNull() {
        Map mockExtraMap = mock(Map.class);
        PubnativeException extraException = PubnativeException.extraException(PubnativeException.NETWORK_INVALID_RESPONSE, mockExtraMap);
        assertThat(extraException.mExtraMap).isEqualTo(mockExtraMap);
    }

    @Test
    public void extraException_withValidExtraData_extraDataIsNotNull() throws JSONException{
        Map mockExtraMap = mock(Map.class);
        PubnativeException exception = PubnativeException.extraException(PubnativeException.NETWORK_INVALID_RESPONSE, mockExtraMap);
        String exceptionString = exception.toString();
        JSONObject exceptionJSON = new JSONObject(exceptionString);
        assertThat(exceptionJSON.get("extraData")).isNotNull();
    }
}
