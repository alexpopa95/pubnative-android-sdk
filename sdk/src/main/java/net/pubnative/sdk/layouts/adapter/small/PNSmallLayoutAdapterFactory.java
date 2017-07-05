package net.pubnative.sdk.layouts.adapter.small;

import android.util.Log;

import net.pubnative.sdk.layouts.adapter.PNLayoutAdapter;
import net.pubnative.sdk.core.config.model.PNNetworkModel;

import java.lang.reflect.Constructor;

public class PNSmallLayoutAdapterFactory {

    private   static final String TAG             = PNSmallLayoutAdapterFactory.class.getSimpleName();
    protected static final String NETWORK_PACKAGE = PNSmallLayoutAdapterFactory.class.getPackage().getName();

    public static PNLayoutAdapter getAdapter(PNNetworkModel model) {
        PNLayoutAdapter result = null;
        try {
            Class<?> adapterClass = Class.forName(getPackageName(model.adapter));
            Constructor<?> adapterConstructor = adapterClass.getConstructor();
            result = (PNLayoutAdapter) adapterConstructor.newInstance();
            if (result != null) {
                result.setNetworkData(model.params);
            }
        } catch (Exception e) {
            // Don't crash, just return null, log error and return null
            Log.e(TAG, "Error creating adapter: " + e);
        } catch (Error e) {
            // Don't crash, just return null, log error and return null
            Log.e(TAG, "Error creating adapter: " + e);
        }
        return result;
    }

    protected static String getPackageName(String classSimpleName) {
        String result = null;
        if (classSimpleName != null) {
            result = NETWORK_PACKAGE + "." + classSimpleName;
        }
        return result;
    }
}
