package net.pubnative.mediation.exceptions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class PubnativeException extends Exception {

    public static final String TAG = PubnativeException.class.getSimpleName();

    //==============================================================================================
    // Private fields
    //==============================================================================================
    protected int mErrorCode;
    protected Map mExtraMap;
    //==============================================================================================
    // Request Exceptions
    //==============================================================================================
    public static final PubnativeException REQUEST_NO_INTERNET             = new PubnativeException(1000, "Internet connection is not available");
    public static final PubnativeException REQUEST_PARAMETERS_INVALID      = new PubnativeException(1001, "Invalid execute parameters");
    public static final PubnativeException REQUEST_NO_FILL                 = new PubnativeException(1002, "No fill");
    //==============================================================================================
    // Adapter Exceptions
    //==============================================================================================
    public static final PubnativeException ADAPTER_UNKNOWN_ERROR           = new PubnativeException(2000, "Unknown error");
    public static final PubnativeException ADAPTER_MISSING_DATA            = new PubnativeException(2001, "Null context or adapter data provided");
    public static final PubnativeException ADAPTER_ILLEGAL_ARGUMENTS       = new PubnativeException(2002, "Invalid data provided");
    public static final PubnativeException ADAPTER_TIMEOUT                 = new PubnativeException(2003, "adapter timeout");
    public static final PubnativeException ADAPTER_NOT_FOUND               = new PubnativeException(2004, "adapter not found");
    public static final PubnativeException ADAPTER_TYPE_NOT_IMPLEMENTED    = new PubnativeException(2005, "adapter doesn't implements this type");
    //==============================================================================================
    // Interstitial Exceptions
    //==============================================================================================
    public static final PubnativeException INTERSTITIAL_PARAMETERS_INVALID = new PubnativeException(3000, "parameters configuring the interstitial are invalid");
    public static final PubnativeException INTERSTITIAL_LOADING            = new PubnativeException(3001, "interstitial is currently loading");
    public static final PubnativeException INTERSTITIAL_SHOWN              = new PubnativeException(3002, "interstitial is already shown");
    //==============================================================================================
    // Placement Exceptions
    //==============================================================================================
    public static final PubnativeException PLACEMENT_NO_FILL               = new PubnativeException(4000, "No fill");
    public static final PubnativeException PLACEMENT_FREQUENCY_CAP         = new PubnativeException(4001, "(frequency_cap) too many ads");
    public static final PubnativeException PLACEMENT_PACING_CAP            = new PubnativeException(4002, "(pacing_cap) too many ads");
    public static final PubnativeException PLACEMENT_DISABLED              = new PubnativeException(4003, "Placement is disabled");
    public static final PubnativeException PLACEMENT_CONFIG_INVALID        = new PubnativeException(4004, "Null or invalid config");
    public static final PubnativeException PLACEMENT_NOT_FOUND             = new PubnativeException(4005, "Placement not found");
    public static final PubnativeException PLACEMENT_EMPTY                 = new PubnativeException(4006, "Retrieved config contains null element");
    public static final PubnativeException PLACEMENT_PARAMETERS_INVALID    = new PubnativeException(4007, "Parameters invalid");
    //==============================================================================================
    // Network Exceptions
    //==============================================================================================
    public static final PubnativeException NETWORK_NO_INTERNET             = new PubnativeException(5001, "Internet connection is not available");
    public static final PubnativeException NETWORK_INVALID_RESPONSE        = new PubnativeException(5002, "Invalid response from server");
    public static final PubnativeException NETWORK_INVALID_STATUS_CODE     = new PubnativeException(5003, "Invalid status code from server");
    //==============================================================================================
    // FeedBanner Exceptions
    //==============================================================================================
    public static final PubnativeException FEED_BANNER_PARAMETERS_INVALID  = new PubnativeException(6000, "parameters configuring the feed banner are invalid");
    public static final PubnativeException FEED_BANNER_LOADING             = new PubnativeException(6001, "feedBanner is currently loading");
    public static final PubnativeException FEED_BANNER_SHOWN               = new PubnativeException(6002, "feedBanner is already shown");
    //==============================================================================================
    // Banner Exceptions
    //==============================================================================================
    public static final PubnativeException BANNER_PARAMETERS_INVALID       = new PubnativeException(7000, "Parameters configuring the banner are invalid");
    public static final PubnativeException BANNER_LOADING                  = new PubnativeException(7001, "Banner is currently loading");
    public static final PubnativeException BANNER_SHOWN                    = new PubnativeException(7002, "Banner is already shown");
    public static final PubnativeException BANNER_LOAD_FAILED              = new PubnativeException(7003, "Banner failed on load");
    //==============================================================================================
    // Video Exceptions
    //==============================================================================================
    public static final PubnativeException VIDEO_PARAMETERS_INVALID        = new PubnativeException(8000, "parameters configuring the video are invalid");
    public static final PubnativeException VIDEO_LOADING                   = new PubnativeException(8001, "video is currently loading");
    public static final PubnativeException VIDEO_SHOWN                     = new PubnativeException(8002, "video is already shown");
    //==============================================================================================
    // FeedBanner Exceptions
    //==============================================================================================
    public static final PubnativeException FEED_VIDEO_PARAMETERS_INVALID   = new PubnativeException(9000, "parameters configuring the feedVideo are invalid");
    public static final PubnativeException FEED_VIDEO_LOADING              = new PubnativeException(9001, "feedVideo is currently loading");
    public static final PubnativeException FEED_VIDEO_SHOWN                = new PubnativeException(9002, "feedVideo is already shown");

    /**
     * Constructor
     *
     * @param errorCode Error code
     * @param message   Error message
     */
    public PubnativeException(int errorCode, String message) {

        super(message);
        mErrorCode = errorCode;
    }

    /**
     * This will return this exception error code number
     *
     * @return valid int representing the error code
     */
    public int getErrorCode() {

        return mErrorCode;
    }

    @Override
    public boolean equals(Object o) {

        boolean result = false;
        if(o.getClass().isAssignableFrom(PubnativeException.class)){

            PubnativeException exception = (PubnativeException) o;
            result = exception.getErrorCode() == mErrorCode;
        }
        return result;
    }

    @Override
    public String getMessage() {

        return String.valueOf("PubnativeException (" + getErrorCode() + "): " + super.getMessage());
    }

    @Override
    public String toString() {

        String result;
        try {
            JSONObject json = new JSONObject();
            json.put("code", getErrorCode());
            json.put("message", super.getMessage());
            StackTraceElement[] stack = getStackTrace();
            if (stack != null && stack.length > 0) {
                StringBuilder stackTraceBuilder = new StringBuilder();
                for (StackTraceElement element : getStackTrace()) {
                    stackTraceBuilder.append(element.toString());
                    stackTraceBuilder.append('\n');
                }
                json.put("stackTrace", stackTraceBuilder.toString());
            }
            if(mExtraMap != null) {
                JSONObject extraDataObj = new JSONObject();
                for (Object key: mExtraMap.keySet()) {
                    extraDataObj.put(key.toString(), mExtraMap.get(key));
                }
                json.put("extraData", extraDataObj);
            }
            result = json.toString();
        } catch (JSONException e) {
            result = getMessage();
        }
        return result;
    }

    public static PubnativeException extraException(PubnativeException exception, Map extraMap) {

        Log.v(TAG, "extraException");
        PubnativeException extraException = new PubnativeException(exception.getErrorCode(), exception.getMessage());
        extraException.mExtraMap = extraMap;
        return extraException;
    }
}
