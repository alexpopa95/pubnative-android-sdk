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

package net.pubnative.mediation.network;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PubnativeHttpRequest {

    private static final String TAG = PubnativeHttpRequest.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    // Request properties
    protected int      mTimeoutInMillis = 4000; // 4 seconds
    protected String   mPOSTString      = null;
    // Inner
    protected Listener mListener        = null;
    protected Handler  mHandler         = null;

    //==============================================================================================
    // Listener
    //==============================================================================================

    public interface Listener {

        /**
         * Called when the HttpRequest is about to execute
         *
         * @param request request that is about to execute
         */
        void onPubnativeHttpRequestStart(PubnativeHttpRequest request);

        /**
         * Called when the HttpRequest has just finished with a valid String response
         *
         * @param request request that have just finished
         * @param result  string with the given response from the server
         */
        void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result);

        /**
         * Called when the HttpRequest fails, after this method the request will be stopped
         *
         * @param request   request that have just failed
         * @param exception exception with more info about the error
         */
        void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Sets timeout for connection and reading, if not specified default is 0 ms
     *
     * @param timeoutInMillis time in milliseconds
     */
    public void setTimeout(int timeoutInMillis) {

        Log.v(TAG, "setConnectionTimeout");
        mTimeoutInMillis = timeoutInMillis;
    }

    public void setPOSTString(String postString) {

        Log.v(TAG, "setPOSTString");
        mPOSTString = postString;
    }

    /**
     * This method will execute a new request to the given URL
     *
     * @param context   valid Context object
     * @param urlString URL where the request will be done
     * @param listener  valid Listener for callbacks
     */
    public void start(Context context, final String urlString, Listener listener) {

        Log.v(TAG, "execute: " + urlString);
        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper());
        if (mListener == null) {
            Log.w(TAG, "Warning: null listener specified, performing request without callbacks");
        }
        if (context == null) {
            invokeFail(new IllegalArgumentException("PubnativeHttpRequest - Error: null context provided, dropping call"));
        } else if (TextUtils.isEmpty(urlString)) {
            invokeFail(new IllegalArgumentException("PubnativeHttpRequest - Error: null or empty url, dropping call"));
        } else if (PubnativeDeviceUtils.isNetworkAvailable(context)) {
            invokeStart();
            new Thread(new Runnable() {

                @Override
                public void run() {

                    doRequest(urlString);
                }
            }).start();
        } else {
            invokeFail(PubnativeException.NETWORK_NO_INTERNET);
        }
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    protected void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    protected void doRequest(String urlString) {

        Log.v(TAG, "doRequest: " + urlString);
        HttpURLConnection connection = null;
        try {
            disableConnectionReuseIfNecessary();
            // 1. Create connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            // 2. Set connection properties
            connection.setDoInput(true);
            connection.setConnectTimeout(mTimeoutInMillis);
            if (TextUtils.isEmpty(mPOSTString)) {
                connection.setRequestMethod("GET");
            } else {
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", Integer.toString(mPOSTString.getBytes().length));
                OutputStream connectionOutputStream = connection.getOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(connectionOutputStream, "UTF-8");
                wr.write(mPOSTString);
                wr.flush();
                wr.close();
            }
            // 3. Do request
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                try {
                    invokeFinish(stringFromInputStream(inputStream));
                } catch (PubnativeException ex) {
                    invokeFail(ex);
                }
            } else {
                Map errorData = new HashMap();
                errorData.put("statusCode", responseCode+"");
                try {
                    errorData.put("errorString", stringFromInputStream(connection.getErrorStream()));
                } catch (PubnativeException ex) {
                    errorData.put("parsingException", ex.toString());
                }
                invokeFail(PubnativeException.extraException(PubnativeException.NETWORK_INVALID_STATUS_CODE, errorData));
            }
        } catch (Exception exception) {
            invokeFail(exception);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected String stringFromInputStream(InputStream inputStream) throws PubnativeException {

        Log.v(TAG, "stringFromInputStream");
        String result = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int length;
        try {
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byteArrayOutputStream.flush();
            result = byteArrayOutputStream.toString();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "stringFromInputStream - Error:" + e);

            Map errorData = new HashMap();
            if(result == null) {
                result = byteArrayOutputStream.toString();
            }
            errorData.put("serverResponse", result);
            errorData.put("IOException", e.getMessage());
            throw PubnativeException.extraException(PubnativeException.NETWORK_INVALID_RESPONSE, errorData);
        }
        return result;
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================

    protected void invokeStart() {

        Log.v(TAG, "invokeStart");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestStart(PubnativeHttpRequest.this);
                }
            }
        });
    }

    protected void invokeFinish(final String result) {

        Log.v(TAG, "invokeFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestFinish(PubnativeHttpRequest.this, result);
                }
                mListener = null;
            }
        });
    }

    protected void invokeFail(final Exception exception) {

        Log.v(TAG, "invokeFail: " + exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestFail(PubnativeHttpRequest.this, exception);
                }
                mListener = null;
            }
        });
    }
}