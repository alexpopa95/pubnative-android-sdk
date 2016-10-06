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

package net.pubnative.mediation.network;

import android.graphics.BitmapFactory;
import android.util.Log;

import net.pubnative.mediation.request.model.PubnativeResourceCacheModel;

import java.net.URL;
import java.util.concurrent.Callable;

public class PubnativeResourceRequest
        implements Callable<PubnativeResourceCacheModel> {

    private static final String TAG = PubnativeResourceRequest.class.getSimpleName();
    private String              mUrlString;
    private PubnativeResourceCacheModel mPubnativeResourceCacheModel;

    public PubnativeResourceRequest(String urlString, ResourceType type){

        Log.v(TAG, "PubnativeResourceRequest");
        mUrlString                       = urlString;
        mPubnativeResourceCacheModel     = new PubnativeResourceCacheModel();
        mPubnativeResourceCacheModel.key = type;
    }

    @Override
    public PubnativeResourceCacheModel call() throws Exception {

        Log.v(TAG, "call");
        try {
            URL url = new URL(mUrlString);
            mPubnativeResourceCacheModel.image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return mPubnativeResourceCacheModel;
    }

    public enum ResourceType {
        ICON,
        BANNER;
    }
}
