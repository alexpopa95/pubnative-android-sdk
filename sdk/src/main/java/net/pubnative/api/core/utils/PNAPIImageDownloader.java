// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
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

package net.pubnative.api.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.URL;

public class PNAPIImageDownloader {

    private static final String TAG = PNAPIImageDownloader.class.getSimpleName();

    private WeakReference<Bitmap> mImage;

    private Listener mListener;
    private Handler mHandler;

    /**
     * Interface for callbacks related to image downloader
     */
    public interface Listener {
        /**
         * Called whenever image is loaded either from cache or from network
         * @param bitmap Image
         * @param url Url of image
         */
        void onImageLoad(String url, Bitmap bitmap);

        /**
         * Called whenever image loading failed
         * @param url Url of image
         */
        void onImageFailed(String url, Exception exception);
    }

    /**
     * Load image
     * @param url valid url
     * @param listener valid listener
     */
    public void load(String url, Listener listener) {

        Log.v(TAG, "load");
        mHandler = new Handler(Looper.getMainLooper());
        if(listener == null) {
            Log.e(TAG, "Listener is not set, dropping call");
        } else if(TextUtils.isEmpty(url)) {
            invokeFail(url, new Exception("URL is not valid"));
        } else {
            mListener = listener;
            downloadImage(url);
        }
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    private void downloadImage(final String urlString) {

        Log.v(TAG, "downloadImage");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    final BitmapFactory.Options options = new BitmapFactory.Options();

                    // Fill options with data about bitmap only
                    // without allocating memory.
                    // decodeStream() will return null.
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(url.openConnection().getInputStream(), new Rect(), options);

                    // Calculate size of the image depends of free memory of device
                    int sampleSize = 0;
                    sampleSize = calculateInSampleSize(options);

                    if(sampleSize == 0) {
                        invokeFail(urlString, new Exception("Not enough memory"));
                    } else {
                        options.inSampleSize = sampleSize;
                        // Get image and allocate memory for it.
                        // WeakReference using for saving memory here.
                        options.inJustDecodeBounds = false;
                        mImage = new WeakReference<Bitmap>(BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options));
                        invokeLoad(urlString);
                    }
                } catch (Exception e) {
                    invokeFail(urlString, e);
                } catch (Error error) {
                    mImage = null;
                    invokeFail(urlString, new Exception("Out of memory during image downloading"));
                }
            }
        }).start();
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoad(final String url) {

        Log.v(TAG, "invokeLoad");
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if(mListener != null ) {
                    if(mImage == null || mImage.get() == null) {
                        mListener.onImageFailed(url, new Exception("Error: image gor released"));
                    } else {
                        mListener.onImageLoad(url, mImage.get());
                    }
                }
            }
        });
    }

    protected void invokeFail(final String url, final Exception exception) {

        Log.v(TAG, "invokeFail");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mListener != null) {
                    mListener.onImageFailed(url, exception);
                }
            }
        });
    }

    /**
     * This method calculates the inSampleSize which create a smaller image than original.
     * It's needed, because when we try to use decodeStream for the big image BitmapFactory
     * allocate memory not only for himself but also and for downloaded image and some processed data.
     *
     * @param options options for {@link android.graphics.BitmapFactory.Options BitmapFactory.Options}
     * @return int {@link android.graphics.BitmapFactory.Options#inSampleSize inSampleSize} which decoder use to subsample the original image, returning a smaller image to save memory
     */
    protected int calculateInSampleSize(BitmapFactory.Options options) {

        Log.v(TAG, "calculateInSampleSize");

        int result = 1;
        int freeMemory = (int) (Runtime.getRuntime().freeMemory());

        int bytesPerPixel = 4;
        int pictureSize = options.outWidth * options.outHeight * bytesPerPixel;

        if (freeMemory == 0) {
            result = 0;
        } else if (pictureSize > freeMemory && freeMemory > 0) {
            result = pictureSize / freeMemory;
        }

        return result;
    }
}