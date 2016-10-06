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

package net.pubnative.mediation.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PubnativeHttpTask extends AsyncTask<String, Void, String> {

    private static final   String TAG              = PubnativeHttpTask.class.getSimpleName();
    protected final static String ERROR_CONNECTION = "conection error, internet not reachable";
    protected final static String ERROR_URL_FORMAT = "request url not provided or is null or empty";
    protected final static String ERROR_CANCELLED  = "request cancelled";
    protected Context                    mContext;
    protected String                     mURL;
    protected String                     mPostString;
    protected PubnativeHttpTask.Listener mListener;
    protected boolean                    mSuccess;

    public PubnativeHttpTask(Context context) {

        mContext = context;
        mSuccess = false;
    }

    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface for callbacks about the PubnativeHTTPTask behaviour
     */
    public interface Listener {

        /**
         * Invoked whenever the HTTP request is succeeded
         * @param task task where the request was succeeded
         * @param result result of the request
         */
        void onHttpTaskSuccess(PubnativeHttpTask task, String result);

        /**
         * Invoked whenver the HTTP request failed
         * @param task task where the request failed
         * @param errorMessage error message exaplining the reason of the error
         */
        void onHttpTaskFailed(PubnativeHttpTask task, String errorMessage);
    }

    /**
     * Sets the listener for the HTTP task
     * @param listener
     */
    public void setListener(PubnativeHttpTask.Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    //==============================================================================================
    // AsyncTask
    //==============================================================================================
    @Override
    protected String doInBackground(String... params) {

        Log.v(TAG, "doInBackground");
        String result = null;
        if (params.length > 0) {
            mURL = params[0];
            if (TextUtils.isEmpty(mURL)) {
                result = ERROR_URL_FORMAT;
            } else {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    try {
                        HttpURLConnection connection;
                        connection = (HttpURLConnection) new URL(mURL).openConnection();
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(1000);
                        if (!TextUtils.isEmpty(mPostString)) {
                            connection.setRequestMethod("POST");
                            connection.setUseCaches(false);
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.setRequestProperty("Content-Length", Integer.toString(mPostString.getBytes().length));
                            OutputStream connectionOutputStream = connection.getOutputStream();
                            OutputStreamWriter wr = new OutputStreamWriter(connectionOutputStream, "UTF-8");
                            wr.write(mPostString);
                            wr.flush();
                            wr.close();
                        }
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (HttpURLConnection.HTTP_OK == responseCode) {
                            result = PubnativeStringUtils.readStringFromInputStream(connection.getInputStream());
                            mSuccess = true;
                        }
                    } catch (Exception e) {
                        result = e.toString();
                    }
                } else {
                    result = ERROR_CONNECTION;
                }
            }
        } else {
            result = ERROR_URL_FORMAT;
        }
        return result;
    }

    @Override
    protected void onCancelled() {

        Log.v(TAG, "onCancelled");
        invokeFailed(ERROR_CANCELLED);
    }

    @Override
    protected void onPostExecute(String result) {

        Log.v(TAG, "onPostExecute");
        if (mSuccess) {
            invokeSuccess(result);
        } else {
            invokeFailed(result);
        }
    }

    //==============================================================================================
    // Fields
    //==============================================================================================

    /**
     * Sets post data, if set this task will do a POST request, otherwise it will be GET
     * @param postString valid string
     */
    public void setPOSTData(String postString) {

        Log.v(TAG, "setPOSTData");
        mPostString = postString;
    }

    /**
     * Gets the setted post data
     * @return valid String or null
     */
    public String getPOSTData() {

        Log.v(TAG, "getPOSTData");
        return mPostString;
    }

    /**
     * Gets the url of the http request
     * @return valid String
     */
    public String getURL() {

        Log.v(TAG, "getURL");
        return mURL;
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeSuccess(String result) {

        Log.v(TAG, "invokeSuccess");
        if (mListener != null) {
            mListener.onHttpTaskSuccess(this, result);
        }
    }

    protected void invokeFailed(String errorMessage) {

        Log.v(TAG, "invokeFailed: " + errorMessage);
        if (mListener != null) {
            mListener.onHttpTaskFailed(this, errorMessage);
        }
    }
}
