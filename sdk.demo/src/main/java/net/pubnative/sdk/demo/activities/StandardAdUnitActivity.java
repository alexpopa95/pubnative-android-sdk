package net.pubnative.sdk.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import net.pubnative.sdk.core.config.PNConfigManager;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.demo.R;
import net.pubnative.sdk.demo.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StandardAdUnitActivity extends Activity {

    private static final String TAG = StandardAdUnitActivity.class.getSimpleName();

    protected Spinner              mPlacementSpinner;
    protected RelativeLayout       mLoaderContainer;
    protected RelativeLayout       mBlankContainer;
    protected ArrayAdapter<String> mSpinnerAdapter;

    protected List<String> mPlacements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_ad_unit);
        mPlacementSpinner = (Spinner) findViewById(R.id.spinner_standard_unit_placement);
        mLoaderContainer = (RelativeLayout) findViewById(R.id.container_standard_ad_unit_loader);
        mBlankContainer = (RelativeLayout) findViewById(R.id.rl_blankContainer);
        loadSpinnerData();
    }

    private void loadSpinnerData() {

        Log.v(TAG, "loadSpinnerData");
        mPlacements.clear();
        mSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_row, mPlacements);
        mPlacementSpinner.setAdapter(mSpinnerAdapter);
        PNConfigManager.getConfig(this, Settings.appToken, new PNConfigManager.Listener() {
            @Override
            public void onConfigLoaded(PNConfigModel configModel) {
                List<String> placements = new ArrayList<String>();
                if (configModel != null && configModel.placements != null) {
                    Set<String> placementsSet = configModel.placements.keySet();
                    if (placementsSet != null) {
                        mPlacements.addAll(placementsSet);
                    }
                }
                mPlacements.addAll(Settings.placements);
                mSpinnerAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
