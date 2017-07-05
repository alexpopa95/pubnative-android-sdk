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

package net.pubnative.sdk.core.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class PNException extends Exception {

    public static final String      TAG                          = PNException.class.getSimpleName();
    //==============================================================================================
    // Request Exceptions
    //==============================================================================================
    public static final PNException REQUEST_NO_INTERNET          = new PNException(1000, "Internet connection is not available");
    public static final PNException REQUEST_PARAMETERS_INVALID   = new PNException(1001, "Invalid execute parameters");
    public static final PNException REQUEST_NO_FILL              = new PNException(1002, "No fill");
    public static final PNException REQUEST_CONFIG_INVALID       = new PNException(1005, "Null or invalid config");
    public static final PNException REQUEST_CONFIG_EMPTY         = new PNException(1006, "Retrieved config contains null element");
    public static final PNException REQUEST_LOADING              = new PNException(1007, "Currently loading");
    public static final PNException REQUEST_SHOWN                = new PNException(1008, "Already shown");
    //==============================================================================================
    // Adapter Exceptions
    //==============================================================================================
    public static final PNException ADAPTER_MISSING_DATA         = new PNException(2000, "Null context or adapter data provided");
    public static final PNException ADAPTER_ILLEGAL_ARGUMENTS    = new PNException(2001, "Invalid data provided");
    public static final PNException ADAPTER_TIMEOUT              = new PNException(2002, "adapter timeout");
    public static final PNException ADAPTER_NOT_FOUND            = new PNException(2003, "adapter not found");
    public static final PNException ADAPTER_TYPE_NOT_IMPLEMENTED = new PNException(2004, "adapter doesn't implements this type");
    public static final PNException ADAPTER_NO_FILL              = new PNException(2005, "adapter did not fill the request");
    //==============================================================================================
    // Placement Exceptions
    //==============================================================================================
    public static final PNException PLACEMENT_FREQUENCY_CAP      = new PNException(3001, "Too many ads: frequency");
    public static final PNException PLACEMENT_PACING_CAP         = new PNException(3002, "Too many ads: pacing");
    public static final PNException PLACEMENT_DISABLED           = new PNException(3003, "Placement is disabled");
    public static final PNException PLACEMENT_NOT_FOUND          = new PNException(3004, "Placement not found");
    //==============================================================================================
    // Private fields
    //==============================================================================================
    protected int mErrorCode;
    protected Map mExtraMap;


    /**
     * Constructor
     *
     * @param errorCode Error code
     * @param message   Error message
     */
    public PNException(int errorCode, String message) {

        super(message);
        mErrorCode = errorCode;
    }

    /**
     * Constructor
     *
     * @param errorCode Error code
     * @param exception Base exception
     */
    public PNException(int errorCode, Exception exception) {

        super(exception);
        mErrorCode = errorCode;
    }

    public static PNException extraException(Map extraMap) {
        PNException extraException = new PNException(0, "extra exception");
        extraException.mExtraMap = extraMap;
        return extraException;
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
        if (o.getClass().isAssignableFrom(PNException.class)) {

            PNException exception = (PNException) o;
            result = exception.getErrorCode() == mErrorCode;
        }
        return result;
    }

    @Override
    public String getMessage() {

        return String.valueOf("PNException (" + getErrorCode() + "): " + super.getMessage());
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
            if (mExtraMap != null) {
                JSONObject extraDataObj = new JSONObject();
                for (Object key : mExtraMap.keySet()) {
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
}
