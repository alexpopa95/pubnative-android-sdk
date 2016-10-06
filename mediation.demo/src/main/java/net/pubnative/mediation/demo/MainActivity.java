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

package net.pubnative.mediation.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String                 TAG              = MainActivity.class.getSimpleName();
    private static final String                 APP_TOKEN        = "7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c";
    private              AdListAdapter          mRequestsAdapter = null;
    private              List<CellRequestModel> mRequests        = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDefaults();
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

    public void onSettingsClick(View v) {

        Log.v(TAG, "onSettingsClick");
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    protected void setDefaults() {
        Log.v(TAG, "setDefaults");
        // App token
        Settings.setAppToken(this, APP_TOKEN);
        // Placements
        List<String> placements = new ArrayList<>();
        placements.add("facebook_only");
        placements.add("pubnative_only");
        placements.add("yahoo_only");
        placements.add("admob_only");
        placements.add("waterfall");
        placements.add("imp_day_cap_10");
        placements.add("imp_hour_cap_10");
        placements.add("pacing_cap_hour_1");
        placements.add("pacing_cap_min_1");
        placements.add("disabled");
        Settings.setPlacements(this, placements);
    }
}
