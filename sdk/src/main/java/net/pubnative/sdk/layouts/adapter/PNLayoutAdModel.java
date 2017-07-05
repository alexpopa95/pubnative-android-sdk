package net.pubnative.sdk.layouts.adapter;

import android.content.Context;

import net.pubnative.api.core.request.model.PNAPIAdModel;
import net.pubnative.sdk.core.adapter.request.PubnativeLibraryAdModel;
import net.pubnative.sdk.core.request.PNAdModel;

public class PNLayoutAdModel extends PubnativeLibraryAdModel {

    public interface FetchListener extends PNAdModel.FetchListener {}

    public PNLayoutAdModel(Context context, PNAPIAdModel adModel) {
        super(context, adModel);
    }

    public void fetchAssets(FetchListener listener) {
        super.fetchAssets(listener);
    }
}
