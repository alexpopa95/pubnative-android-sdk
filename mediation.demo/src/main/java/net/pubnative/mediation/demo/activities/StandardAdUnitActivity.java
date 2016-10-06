package net.pubnative.mediation.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.demo.Settings;

import java.util.List;

public class StandardAdUnitActivity extends Activity {

    private static final String         TAG                = StandardAdUnitActivity.class.getSimpleName();
    protected            Spinner        mPlacementSpinner;
    protected            RelativeLayout mLoaderContainer;
    protected            RelativeLayout mBlankContainer;

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
        List<String> placements = Settings.getPlacements(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, placements);
        mPlacementSpinner.setAdapter(adapter);
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
