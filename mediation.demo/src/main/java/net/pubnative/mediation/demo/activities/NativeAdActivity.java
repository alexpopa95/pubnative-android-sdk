package net.pubnative.mediation.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import net.pubnative.mediation.demo.AdListAdapter;
import net.pubnative.mediation.demo.CellRequestModel;
import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.demo.Settings;

import java.util.ArrayList;
import java.util.List;

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
        List<String> placements = Settings.getPlacements(this);
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
                requests.add(new CellRequestModel(placementID));
            } else {
                requests.add(requestModel);
            }
        }
        mRequests = requests;
        for (CellRequestModel requestModel : mRequests) {
            mRequestsAdapter.add(requestModel);
        }
        mRequestsAdapter.notifyDataSetChanged();
    }
}
