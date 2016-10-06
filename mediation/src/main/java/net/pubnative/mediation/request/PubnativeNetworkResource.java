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

package net.pubnative.mediation.request;

import android.util.Log;

import net.pubnative.mediation.request.model.PubnativeResourceCacheModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PubnativeNetworkResource {

    public static final String TAG = PubnativeNetworkBanner.class.getSimpleName();
    private final ExecutorService mExecutorService = Executors.newFixedThreadPool(2);
    private       List            mResourceList;
    private       Listener        mListener;

    public interface Listener {

        /**
         * called whenever resources finish loading from ad.
         *
         * @param resourceList result list for resource downloaded.
         */
        public void onPubnativeNetworkResourceLoaded(List resourceList);
    }

    /**
     * this method is used to download resources asynchronously.
     *
     * @param querySet set of request urls.
     */
    public void startDownload(Set querySet){

        Log.v(TAG, "startDownload");
        List pubnativeCacheModels = new ArrayList<PubnativeResourceCacheModel>();
        try{
            mResourceList = mExecutorService.invokeAll(querySet);
            Iterator<Future<PubnativeResourceCacheModel>> iterator = mResourceList.iterator();
            while(iterator.hasNext()){
                pubnativeCacheModels.add(iterator.next().get());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            mExecutorService.shutdown();
            if(mListener != null){
                mListener.onPubnativeNetworkResourceLoaded(pubnativeCacheModels);
            }
        }
    }

    /**
     * sets listener for this request.
     *
     * @param listener valid listener.
     */
    public void setListener(Listener listener){
        mListener = listener;
    }
}
