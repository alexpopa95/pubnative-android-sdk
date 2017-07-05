package net.pubnative.api.layouts.asset_group;

import android.content.Context;
import android.util.Log;

import net.pubnative.api.core.request.model.PNAPIAdModel;

import java.lang.reflect.Constructor;

public class PNAPIAssetGroupFactory {

    private static final String TAG = PNAPIAssetGroupFactory.class.getSimpleName();
    private static final String PACKAGE = PNAPIAssetGroupFactory.class.getPackage().getName();
    private static final String CLASS_NAME_BASE = PNAPIAssetGroup.class.getSimpleName();

    public static PNAPIAssetGroup createView(Context context, PNAPIAdModel adModel) {

        PNAPIAssetGroup result = null;
        if (adModel.getAssetGroupId() == 0) {
            Log.e(TAG, "Error: Ad model used is not compatible with asset groups");
        } else {
            try {
                String classString = PACKAGE + "." + CLASS_NAME_BASE + String.valueOf(adModel.getAssetGroupId());
                Class<?> assetGroupClass = Class.forName(classString);
                Constructor<?> assetGroupConstructor = assetGroupClass.getConstructor(Context.class);
                result = (PNAPIAssetGroup) assetGroupConstructor.newInstance(context);
                if(result == null) {
                    Log.e(TAG, "Error: cannot initialise the view");
                } else {
                    result.mAdModel = adModel;
                }
            } catch (Exception e) {
                // Don't crash, just return null, log error and return null
                Log.e(TAG, "Error creating the asset group view", e);
            }
        }
        return result;
    }
}
