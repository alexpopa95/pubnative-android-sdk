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

package net.pubnative.sdk.core.request;

public class PNAdModelCache {

    private final int MINUTES_TO_MILLISECONDS = 60000;

    public    PNAdModel ad            = null;
    public    int       ad_expiration = 0;
    protected long      ad_timestamp  = 0;

    public PNAdModelCache() {
        ad_timestamp = System.currentTimeMillis();
    }

    /**
     * This method tells if the current fetchAssets item is valid or not depending on it's threshold
     *
     * @return true if it's still valid, false if not.
     */
    public boolean isValid() {
        boolean result = true;
        if (ad_expiration > 0) {
            int thresholdInMilliseconds = ad_expiration * MINUTES_TO_MILLISECONDS;
            result = ad_timestamp + thresholdInMilliseconds > System.currentTimeMillis();
        }
        return result;
    }
}
