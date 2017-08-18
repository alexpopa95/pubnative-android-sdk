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

package net.pubnative.api.core.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.api.core.utils.PNAPISystemUtils;
import net.pubnative.sdk.core.utils.PNDeviceUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PNAPIHttpRequest {

    private static final String TAG = PNAPIHttpRequest.class.getSimpleName();

    public static final int HTTP_OK              = HttpURLConnection.HTTP_OK;
    public static final int HTTP_INVALID_REQUEST = 422;
    protected static    int sConnectionTimeout   = 4000; // 4 seconds

    protected Listener mListener;
    protected Handler  mHandler;

    //==============================================================================================
    // Listener
    //==============================================================================================
    public interface Listener {

        /**
         * Called when the HttpRequest has just finished with a valid String response
         *
         * @param request    request that have just finished
         * @param result     resulting string from the http response
         * @param statusCode status code from the response
         */
        void onPNAPIHttpRequestFinish(PNAPIHttpRequest request, String result, int statusCode);

        /**
         * Called when the HttpRequest fails, after this method the request will be stopped
         *
         * @param request   request that have just failed
         * @param exception exception with more info about the error
         */
        void onPNAPIHttpRequestFail(PNAPIHttpRequest request, Exception exception);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Sets a timeout for establishing connection with the server, if not specified default is 4000 ms
     *
     * @param connectionTimeout time in milliseconds
     */
    public static void setConnectionTimeout(int connectionTimeout) {
        sConnectionTimeout = connectionTimeout;
    }

    /**
     * This method will start a new request to the given URL
     *
     * @param context   valid Context object
     * @param urlString URL where the request will be done
     * @param listener  valid Listener for callbacks
     */
    public void start(final Context context, final String urlString, Listener listener) {
        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper());
        if (mListener == null) {
            Log.w(TAG, "Warning: null listener specified");
        }
        if (TextUtils.isEmpty(urlString)) {
            invokeFail(new IllegalArgumentException("PNAPIHttpRequest - Error: null or empty url, dropping call"));
        } else {

            if (PNDeviceUtils.isNetworkAvailable(context)) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        String userAgent = PNAPISystemUtils.getWebViewUserAgent();
                        if (TextUtils.isEmpty(userAgent)) {
                            invokeFail(new Exception("PNAPIHttpRequest - Error: User agent cannot be retrieved"));
                        } else {
                            initiateRequest(urlString, userAgent);
                        }
                    }
                });
            } else {
                invokeFail(new Exception("PNAPIHttpRequest - Error: internet connection not detected, dropping call"));
            }
        }
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private void initiateRequest(final String urlString, final String userAgent) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                doRequest(urlString, userAgent);
            }
        }).start();
    }

    protected void doRequest(String urlString, String userAgent) {

        HttpURLConnection connection = null;

        try {
            // 1. Create connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            // 2. Set connection properties
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(sConnectionTimeout);
            connection.setDoInput(true);
            // 3. Do request
            connection.connect();

            int statusCode = connection.getResponseCode();
            InputStream input = connection.getInputStream();
            String result = getString(input);
            input.close();

            invokeFinish(result, statusCode);

        } catch (Exception exception) {
            invokeFail(exception);
        } catch (Error error) {
            invokeFail(new Exception("Request finished with the error!", error));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected String getString(InputStream inputStream) {
        String result = null;
        BufferedReader bufferReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {

            stringBuilder = null;
        } finally {
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                }
            }
        }
        if (stringBuilder != null) {
            result = stringBuilder.toString();
        }
        return result;
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================
    protected void invokeFinish(final String result, final int statusCode) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPNAPIHttpRequestFinish(PNAPIHttpRequest.this, result, statusCode);
                }
            }
        });
    }

    protected void invokeFail(final Exception exception) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPNAPIHttpRequestFail(PNAPIHttpRequest.this, exception);
                }
            }
        });
    }
}
