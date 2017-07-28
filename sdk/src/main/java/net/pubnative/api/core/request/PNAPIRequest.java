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

package net.pubnative.api.core.request;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.api.core.network.PNAPIHttpRequest;
import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.api.core.utils.PNAPISystemUtils;
import net.pubnative.api.core.request.model.api.PNAPIV3AdModel;
import net.pubnative.api.core.request.model.api.PNAPIV3ResponseModel;
import net.pubnative.api.core.utils.PNAPICrypto;
import net.pubnative.sdk.core.PNSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PNAPIRequest implements PNAPIHttpRequest.Listener {

    private static String TAG = PNAPIRequest.class.getSimpleName();

    @Deprecated
    protected static final String              BASE_URL           = "https://api.pubnative.net/api/v3/native";
    protected              Context             mContext           = null;
    protected              Map<String, String> mRequestParameters = new HashMap<String, String>();
    protected              Listener            mListener          = null;
    protected              PNAPIHttpRequest    mRequest           = null;
    protected              boolean             mIsRunning         = false;

    //==============================================================================================
    // REQUEST PARAMETERS
    //==============================================================================================

    /**
     * Interface with all possible request parameters
     */
    public interface Parameters {

        String APP_TOKEN                  = "apptoken";
        String ANDROID_ADVERTISER_ID      = "gid";
        String ANDROID_ADVERTISER_ID_SHA1 = "gidsha1";
        String ANDROID_ADVERTISER_ID_MD5  = "gidmd5";
        String OS                         = "os";
        String OS_VERSION                 = "osver";
        String DEVICE_MODEL               = "devicemodel";
        String NO_USER_ID                 = "dnt";
        String LOCALE                     = "locale";
        String AD_COUNT                   = "adcount";
        String ZONE_ID                    = "zoneid";
        String LAT                        = "lat";
        String LONG                       = "long";
        String GENDER                     = "gender";
        String AGE                        = "age";
        String KEYWORDS                   = "keywords";
        String APP_VERSION                = "appver";
        String TEST                       = "test";
        String COPPA                      = "coppa";
        String VIDEO                      = "video";
        String META_FIELDS                = "mf";
        String ASSET_FIELDS               = "af";
        String ASSET_LAYOUT               = "al";
    }

    //==============================================================================================
    // LISTENER
    //==============================================================================================

    /**
     * Listener interface used to start Pubnative request with success and failure callbacks.
     */
    public interface Listener {

        /**
         * Invoked when PNAPIRequest request is success
         *
         * @param request Request object used for making the request
         * @param ads     List of ads received
         */
        void onPNAPIRequestFinish(PNAPIRequest request, List<PNAPIAdModel> ads);

        /**
         * Invoked when PNAPIRequest request fails
         *
         * @param request Request object used for making the request
         * @param ex      Exception that caused the failure
         */
        void onPNAPIRequestFail(PNAPIRequest request, Exception ex);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Sets parameters required to make the pub native request
     *
     * @param key   key name of parameter
     * @param value actual value of parameter
     */
    public void setParameter(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            Log.w(TAG, "Invalid key passed for parameter");
        } else if (TextUtils.isEmpty(value)) {
            mRequestParameters.remove(key);
        } else {
            mRequestParameters.put(key, value);
        }
    }

    /**
     * Sets parameters required to make the pub native request
     *
     * @param key   key name of parameter
     * @param value actual value of parameter
     */
    public void setParameterArray(String key, String[] value) {
        if (TextUtils.isEmpty(key)) {
            Log.w(TAG, "Invalid key passed for parameter");
        } else if (value == null) {
            mRequestParameters.remove(key);
        } else {
            mRequestParameters.put(key, TextUtils.join(",", value));
        }
    }

    /**
     * Starts pubnative request, This function make the ad request to the pubnative server. It makes asynchronous network request in the background.
     *
     * @param context  valid Context object
     * @param listener valid nativeRequestListener to track ad request callbacks.
     */
    public void start(Context context, Listener listener) {
        if (listener == null) {
            Log.w(TAG, "start - listener is null and required, dropping call");
        } else if (context == null) {
            Log.w(TAG, "start - context is null and required, dropping call");
        } else if (mIsRunning) {
            Log.w(TAG, "start - this request is already running, dropping the call");
        } else {
            mIsRunning = true;
            mListener = listener;
            mContext = context;
            fillDefaultParameters();
            doRequest();
        }
    }

    /**
     * Sets test mode to the status passed in the parameter
     *
     * @param enabled true if you want to enable test mode false if you want to get production ads
     */
    public void setTestMode(boolean enabled) {
        setParameter(Parameters.TEST, enabled ? "1" : "0");
    }

    /**
     * Sets COPPA mode to the status enabled in the parameter
     *
     * @param enabled true if you want to enable COPPA mode
     */
    public void setCoppaMode(boolean enabled) {
        setParameter(Parameters.COPPA, enabled ? "1" : "0");
    }

    /**
     * Sets the timeout for the request to the specified timeout
     *
     * @param timeout int value of timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        PNAPIHttpRequest.setConnectionTimeout(timeout);
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    protected void fillDefaultParameters() {

        mRequestParameters.put(Parameters.OS, PNSettings.os);
        mRequestParameters.put(Parameters.OS_VERSION, PNSettings.osVersion);
        mRequestParameters.put(Parameters.DEVICE_MODEL, PNSettings.deviceName);
        mRequestParameters.put(Parameters.LOCALE, PNSettings.locale);

        setAdvertisingID();
        setLocation();
        setDefaultAssetFields();
        setDefaultMetaFields();
    }

    protected void setAdvertisingID() {
        if (TextUtils.isEmpty(PNSettings.advertisingId)) {
            mRequestParameters.put(Parameters.NO_USER_ID, "1");
        } else {
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID, PNSettings.advertisingId);
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID_SHA1, PNAPICrypto.sha1(PNSettings.advertisingId));
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID_MD5, PNAPICrypto.md5(PNSettings.advertisingId));
        }
    }

    protected void setLocation() {
        Location location = PNSettings.location;
        if (location != null) {
            mRequestParameters.put(Parameters.LONG, String.valueOf(location.getLongitude()));
            mRequestParameters.put(Parameters.LAT, String.valueOf(location.getLatitude()));
        }
    }

    protected void setDefaultAssetFields() {
        if (!mRequestParameters.containsKey(Parameters.ASSET_LAYOUT) &&
            !mRequestParameters.containsKey(Parameters.ASSET_FIELDS)) {

            setParameterArray(PNAPIRequest.Parameters.ASSET_FIELDS, new String[]{
                    PNAPIAsset.TITLE,
                    PNAPIAsset.DESCRIPTION,
                    PNAPIAsset.ICON,
                    PNAPIAsset.BANNER,
                    PNAPIAsset.CALL_TO_ACTION,
                    PNAPIAsset.RATING
            });
        }
    }

    protected void setDefaultMetaFields() {
        String metaString = mRequestParameters.get(Parameters.META_FIELDS);
        List<String> metaList = new ArrayList<String>();
        if (metaString != null) {
            Arrays.asList(TextUtils.split(metaString, ","));
        }
        metaList.add(PNAPIMeta.REVENUE_MODEL);
        metaList.add(PNAPIMeta.CONTENT_INFO);
        setParameterArray(Parameters.META_FIELDS, metaList.toArray(new String[0]));
    }

    protected String getRequestURL() {
        // Base URL
        Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
        // Appending parameters
        for (String key : mRequestParameters.keySet()) {
            String value = mRequestParameters.get(key);
            if (key != null && value != null) {
                uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().toString();
    }

    protected void doRequest() {
        String url = getRequestURL();
        if (url == null) {
            invokeOnFail(new Exception("PNAPIRequest - Error: invalid request URL"));
        } else {
            mRequest = new PNAPIHttpRequest();
            mRequest.start(mContext, url, this);
        }
    }

    protected boolean isCoppaModeEnabled() {
        boolean result = false;
        String coppa = mRequestParameters.get(Parameters.COPPA);
        if (!TextUtils.isEmpty(coppa)) {
            result = coppa.equalsIgnoreCase("1");
        }
        return result;
    }

    protected void processStream(String result) {
        PNAPIV3ResponseModel apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new Gson().fromJson(result, PNAPIV3ResponseModel.class);
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception("Response can not be parsed!", error);
        }
        if (parseException != null) {
            invokeOnFail(parseException);
        } else if (apiResponseModel == null) {
            invokeOnFail(new Exception("PNAPIRequest - Parse error"));
        } else if (PNAPIV3ResponseModel.Status.OK.equals(apiResponseModel.status)) {
            // STATUS 'OK'
            List<PNAPIAdModel> resultModels = null;
            if (apiResponseModel.ads != null) {
                for (PNAPIV3AdModel adModel : apiResponseModel.ads) {
                    if (resultModels == null) {
                        resultModels = new ArrayList<PNAPIAdModel>();
                    }
                    resultModels.add(PNAPIAdModel.create(adModel));
                }
            }
            invokeOnSuccess(resultModels);
        } else {
            // STATUS 'ERROR'
            invokeOnFail(new Exception("PNAPIRequest - Server error: " + apiResponseModel.error_message));
        }
    }

    //==============================================================================================
    // Listener Helpers
    //==============================================================================================

    protected void invokeOnSuccess(List<PNAPIAdModel> ads) {
        mIsRunning = false;
        if (mListener != null) {
            mListener.onPNAPIRequestFinish(this, ads);
        }
        mListener = null;
    }

    protected void invokeOnFail(Exception exception) {
        mIsRunning = false;
        if (mListener != null) {
            mListener.onPNAPIRequestFail(this, exception);
        }
        mListener = null;
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PNAPIHttpRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPNAPIHttpRequestFinish(PNAPIHttpRequest request, String result, int statusCode) {
        if (PNAPIHttpRequest.HTTP_OK == statusCode
            || PNAPIHttpRequest.HTTP_INVALID_REQUEST == statusCode) {

            // STATUS CODE VALID
            processStream(result);

        } else {

            // STATUS CODE INVALID
            invokeOnFail(new Exception("PNAPIRequest - Response error: " + statusCode));
        }
    }

    @Override
    public void onPNAPIHttpRequestFail(PNAPIHttpRequest request, Exception exception) {
        invokeOnFail(exception);
    }
}
