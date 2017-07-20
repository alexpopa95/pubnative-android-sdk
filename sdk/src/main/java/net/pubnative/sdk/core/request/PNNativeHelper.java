package net.pubnative.sdk.core.request;

import android.graphics.Bitmap;

/**
 * Created by erosgarciaponte on 18.07.17.
 */

public class PNNativeHelper {
    public static String getContentInfoIconUrl(PNAdModel model) {
        return model.getContentInfoImageUrl();
    }

    public static String getContentInfoClickUrl(PNAdModel model) {
        return model.getContentInfoClickUrl();
    }

    public static String getIconUrl(PNAdModel model) {
        return model.getIconUrl();
    }

    public static String getBannerUrl(PNAdModel model) {
        return model.getBannerUrl();
    }

    public static Bitmap getAsset(PNAdModel model, String url) {
        return model.getAsset(url);
    }
}
