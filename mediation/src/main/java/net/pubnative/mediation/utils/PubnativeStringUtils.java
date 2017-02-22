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

package net.pubnative.mediation.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PubnativeStringUtils {

    private static String TAG = PubnativeStringUtils.class.getSimpleName();

    /**
     * Reads string from given InputStream object
     *
     * @param inputStream InputStream object from which we need to read the string
     * @return The string read from the inputStream object. Null if input stream is null or read fails.
     */
    public static String readStringFromInputStream(InputStream inputStream) {

        Log.v(TAG, "readStringFromInputStream(InputStream)");

        BufferedReader bufferReader  = null;
        StringBuilder  stringBuilder = new StringBuilder();
        try {
            String line;
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "readStringFromInputStream - Error:" + e);
        } finally {
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "readStringFromInputStream - Error:" + e);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static <T> List<T> convertStringToObject(String convertable, Class<T> object) {

        List<T> result;
        Gson gson = new Gson();
        JsonArray array = new JsonParser().parse(convertable).getAsJsonArray();
        result = new ArrayList<T>(array.size());
        for (JsonElement element : array) {
            result.add(gson.fromJson(element, object));
        }

        return result;
    }

    public static <T> String convertObjectsToJson(List<T> objects) {
        String result = null;
        Writer output = new StringWriter();
        Gson gson = new Gson();
        try {
            JsonWriter writer = new JsonWriter(output);
            writer.beginArray();
            for (T object : objects) {
                gson.toJson(object, object.getClass(), writer);
            }
            writer.endArray();
            writer.close();

            output.flush();
            result = output.toString();

            output.close();
        } catch (IOException exception) {
            Log.e(TAG, "convertObjectsToJson: ", exception);
        }
        return result;
    }
}
