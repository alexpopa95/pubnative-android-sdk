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

package net.pubnative.sdk.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import java.net.URL;

public class PNBitmapDownloader {

    private static final String TAG = PNBitmapDownloader.class.getSimpleName();
    private DownloadListener mDownloadListener;
    private String mURL;

    /**
     * Interface for callbacks related to image downloader
     */
    public interface DownloadListener {
        /**
         * Called whenever image is loaded either from fetchAssets or from network
         *
         * @param bitmap Image
         */
        void onDownloadFinish(String url, Bitmap bitmap);

        /**
         * Called whenever image loading failed
         *
         * @param url Url that failed
         */
        void onDownloadFailed(String url, Exception exception);
    }

    public void download(String url, DownloadListener listener) {

        if (listener == null) {
            Log.w(TAG, "download won't start since there is no assigned listener to It");
        } else {
            mDownloadListener = listener;
            mURL = url;
            if (TextUtils.isEmpty(url)) {
                invokeFail(new Exception("image download "));
            } else if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                downloadImage();
            } else if (URLUtil.isFileUrl(url)) {
                loadCachedImage();
            } else {
                invokeFail(new Exception("Wrong file URL!"));
            }
        }
    }


    //==============================================================================================
    // Private methods
    //==============================================================================================
    private void downloadImage() {
        try {
            URL url = new URL(mURL);
            final BitmapFactory.Options options = new BitmapFactory.Options();

            // Fill options with data about bitmap only
            // without allocating memory.
            // decodeStream() will return null.
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(url.openConnection().getInputStream(), new Rect(), options);

            // Calculate size of the image depends of free memory of device
            int sampleSize = calculateInSampleSize(options);

            if (sampleSize == 0) {
                invokeFail(new Exception("Not enough memory"));
            } else {
                // Calculate size of the image depends of free memory of device
                options.inSampleSize = sampleSize;

                // Get image and allocate memory for it.
                // WeakReference using for saving memory here.
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                invokeLoad(bitmap);
            }
        } catch (Error error) {
            invokeFail(new Exception(error.toString()));
        } catch (Exception e) {
            invokeFail(e);
        }
    }


    private void loadCachedImage() {
        try {
            Uri uri = Uri.parse(mURL);
            final BitmapFactory.Options options = new BitmapFactory.Options();

            // Calculate size of the image depends of free memory of device
            int sampleSize = calculateInSampleSize(options);

            if (sampleSize == 0) {
                invokeFail(new Exception("Not enough memory"));
            } else {
                // Calculate size of the image depends of free memory of device
                options.inSampleSize = sampleSize;

                // Get image and allocate memory for it.
                // WeakReference using for saving memory here.
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(uri.getEncodedPath(), options);
                invokeLoad(bitmap);
            }
        } catch (Error error) {
            invokeFail(new Exception(error.toString()));
        } catch (Exception e) {
            invokeFail(e);
        }
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

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoad(Bitmap bitmap) {
        final DownloadListener listener = mDownloadListener;
        mDownloadListener = null;
        if (listener != null) {
            listener.onDownloadFinish(mURL, bitmap);
        }
    }

    protected void invokeFail(Exception exception) {
        final DownloadListener listener = mDownloadListener;
        mDownloadListener = null;
        if (listener != null) {
            listener.onDownloadFailed(mURL, exception);
        }
    }
}
