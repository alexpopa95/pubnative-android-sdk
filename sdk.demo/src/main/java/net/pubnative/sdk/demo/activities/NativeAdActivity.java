package net.pubnative.sdk.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import net.pubnative.sdk.core.config.PNConfigManager;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.demo.AdListAdapter;
import net.pubnative.sdk.demo.CellRequestModel;
import net.pubnative.sdk.demo.R;
import net.pubnative.sdk.demo.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NativeAdActivity extends Activity {

    private static final String                 TAG              = NativeAdActivity.class.getSimpleName();
    private              AdListAdapter          mRequestsAdapter = null;
    private              List<CellRequestModel> mRequests        = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad);
        mRequestsAdapter = new AdListAdapter(this, R.layout.ad_list_cell, mRequests);
        ListView listView = (ListView) findViewById(R.id.ad_list);
        listView.setAdapter(mRequestsAdapter);
    }

    @Override
    protected void onResume() {

        Log.v(TAG, "onResume");
        super.onResume();
        mRequestsAdapter.clear();
        PNConfigManager.getConfig(this, Settings.appToken, new PNConfigManager.Listener() {
            @Override
            public void onConfigLoaded(PNConfigModel configModel) {

                List<String> placements = new ArrayList<String>();
                if(configModel != null && configModel.placements != null) {
                    Set<String> placementsSet = configModel.placements.keySet();
                    if (placementsSet != null) {
                        placements.addAll(placementsSet);
                    }
                }
                placements.addAll(Settings.placements);

                List<CellRequestModel> requests = new ArrayList<>();
                for (String placementID : placements) {

                    CellRequestModel requestModel = null;
                    for (CellRequestModel model : mRequests) {
                        if(model.placementID.equals(placementID)) {
                            requestModel = model;
                            break;
                        }
                    }
                    if (requestModel == null) {
                        requestModel = new CellRequestModel(placementID);
                    }
                    requestModel.request.setCacheResources(Settings.isAssetCacheEnabled);
                    requests.add(requestModel);
                }
                mRequests = requests;
                for (CellRequestModel requestModel : mRequests) {
                    mRequestsAdapter.add(requestModel);
                }
                mRequestsAdapter.notifyDataSetChanged();
            }
        });
    }
}
