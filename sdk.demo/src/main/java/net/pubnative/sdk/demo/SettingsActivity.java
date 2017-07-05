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

package net.pubnative.sdk.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import net.pubnative.sdk.core.PNSettings;
import net.pubnative.sdk.core.Pubnative;
import net.pubnative.sdk.core.adapter.request.PNCPICacheResetHelper;
import net.pubnative.sdk.core.config.PNConfigManager;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.core.request.PNAdTargetingModel;
import net.pubnative.sdk.core.request.PNCacheManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingsActivity extends Activity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private EditText     mAppToken;
    private EditText     mPlacementID;
    private EditText     mTargeting;
    private Spinner      mTargetingSpinner;
    private Switch       mCoppaSwitch;
    private Switch       mTestModeSwitch;
    private Switch       mCacheAssetsSwitch;
    private Switch       mDevModeSwitch;
    private LinearLayout mPlacementContainer;
    private LinearLayout mTargetingContainer;
    private List<String> mTargetingKeyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAppToken = (EditText) findViewById(R.id.app_key_edit);
        mPlacementID = (EditText) findViewById(R.id.placement_id_edit);
        mTargetingSpinner = (Spinner) findViewById(R.id.targeting_key);
        mTargeting = (EditText) findViewById(R.id.targeting_value);
        mCoppaSwitch = (Switch) findViewById(R.id.coppa_switch);
        mTestModeSwitch = (Switch) findViewById(R.id.test_mode_switch);
        mCacheAssetsSwitch = (Switch) findViewById(R.id.caching_asset_switch);
        mDevModeSwitch = (Switch) findViewById(R.id.apptoken_switch);
        mPlacementContainer = (LinearLayout) findViewById(R.id.placement_container);
        mTargetingContainer = (LinearLayout) findViewById(R.id.targeting_container);
        setDefaultTargetingKeys();
        loadSpinnerData();
    }

    @Override
    protected void onResume() {

        Log.v(TAG, "onResume");
        super.onResume();
        mDevModeSwitch.setOnCheckedChangeListener(null);
        mDevModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean currentCheckValue = mDevModeSwitch.isChecked();
                if(currentCheckValue != Settings.isDevModeEnabled) {
                    Settings.isDevModeEnabled = currentCheckValue;
                    Settings.appToken = Settings.isDevModeEnabled ? Settings.DEFAULT_DEV_APP_TOKEN : Settings.DEFAULT_QA_APP_TOKEN;
                    setSettingValues();
                    PNConfigManager.clean(SettingsActivity.this);
                    refreshPlacementsView();
                    refreshTargetingView();
                }
            }
        });
        setSettingValues();
        refreshPlacementsView();
        refreshTargetingView();
    }

    @Override
    public void onBackPressed() {
        Settings.appToken = mAppToken.getText().toString();
        Settings.isAssetCacheEnabled = mCacheAssetsSwitch.isChecked();
        Settings.isDevModeEnabled = mDevModeSwitch.isChecked();
        PNConfigManager.clean(SettingsActivity.this);

        Pubnative.setTargeting(Settings.getTargeting());
        Pubnative.setCoppaMode(mCoppaSwitch.isChecked());
        Pubnative.setTestMode(mTestModeSwitch.isChecked());
        Pubnative.init(getApplicationContext(), mAppToken.getText().toString());
        super.onBackPressed();
    }

    public void onResetConfigClick(View view) {
        mPlacementID.setText("");
        mTargeting.setText("");
        PNConfigManager.clean(SettingsActivity.this);
        PNCacheManager.cleanCache();
        PNCPICacheResetHelper.resetCPICache();
        Toast.makeText(this, "Stored config reset!", Toast.LENGTH_SHORT).show();
    }

    protected void setSettingValues() {
        mCacheAssetsSwitch.setChecked(Settings.isAssetCacheEnabled);
        mTestModeSwitch.setChecked(PNSettings.isTestModeEnabled);
        mCoppaSwitch.setChecked(PNSettings.isCoppaModeEnabled);
        mAppToken.setText(Settings.appToken);
        mDevModeSwitch.setChecked(Settings.isDevModeEnabled);
        mDevModeSwitch.setText(Settings.isDevModeEnabled ? getResources().getString(R.string.dev_mode) : getResources().getString(R.string.qa_mode));
    }

    public void onAddPlacementClick(View view) {

        Log.v(TAG, "onAddPlacementClick");
        String placementID = mPlacementID.getText().toString();
        if (!TextUtils.isEmpty(placementID)) {

            Settings.placements.add(placementID);
            mPlacementID.setText("");
            refreshPlacementsView();
        }
    }

    public void onAddTargetingClick(View view) {

        Log.v(TAG, "onAddTargetingClick");
        String targetingKey = mTargetingKeyList.get(mTargetingSpinner.getSelectedItemPosition());
        String targetingValue = mTargeting.getText().toString();
        if (!TextUtils.isEmpty(targetingKey)) {

            Map<String, String> targetingMap = new HashMap<>();
            targetingMap.put(targetingKey, targetingValue);
            Settings.targeting.add(targetingMap);
            mTargeting.setText("");
            refreshTargetingView();
        }
    }
    private void loadSpinnerData() {

        Log.v(TAG, "loadSpinnerData");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, mTargetingKeyList);
        mTargetingSpinner.setAdapter(adapter);
    }

    protected void refreshPlacementsView() {

        mPlacementContainer.removeAllViews();
        PNConfigManager.getConfig(this, Settings.appToken, new PNConfigManager.Listener() {
            @Override
            public void onConfigLoaded(PNConfigModel configModel) {

                // Add predefined views
                if (configModel != null && configModel.placements != null) {
                    Set<String> placementsSet = configModel.placements.keySet();
                    if (placementsSet != null) {
                        addPlacementItems(new ArrayList<String>(placementsSet), false);
                    }
                }
                addPlacementItems(Settings.placements, true);
            }
        });
    }

    protected void addPlacementItems(List<String> placements, boolean removable) {

        for (final String placementId : placements) {
            View placementView = LayoutInflater.from(this).inflate(R.layout.placement_list_cell, null);
            // Name
            TextView placementNameTextView = (TextView) placementView.findViewById(R.id.placement_id_text);
            placementNameTextView.setText(placementId);
            // Remove button
            ImageButton removeButton = (ImageButton) placementView.findViewById(R.id.remove_placement_button);
            removeButton.setVisibility(View.GONE);
            removeButton.setOnClickListener(null);
            if (removable) {
                removeButton.setVisibility(View.VISIBLE);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Settings.placements.remove(placementId);
                        refreshPlacementsView();
                    }
                });
            }
            mPlacementContainer.addView(placementView);
            mPlacementContainer.addView(getDividerView());
        }
    }

    protected void refreshTargetingView() {

        Log.v(TAG, "refreshTargetingView");
        mTargetingContainer.removeAllViews();
        inflateTargetingView();
    }

    protected void inflateTargetingView() {

        for (int i = 0; i < Settings.targeting.size(); i++) {
            final int position = i;
            View targetingView = LayoutInflater.from(this).inflate(R.layout.target_list_cell, null);
            TextView targetingKey = (TextView) targetingView.findViewById(R.id.target_key_cell);
            TextView targetingVal = (TextView) targetingView.findViewById(R.id.target_value_cell);
            ImageButton remove = (ImageButton) targetingView.findViewById(R.id.remove_placement_button);

            Map<String, String> targetingMap = Settings.targeting.get(i);
            String key = targetingMap.keySet().iterator().next();
            targetingKey.setText(key);
            targetingVal.setText(targetingMap.get(key));
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Settings.targeting.remove(position);
                    refreshTargetingView();
                }
            });
            mTargetingContainer.addView(targetingView);
            mTargetingContainer.addView(getDividerView());
        }
    }

    protected View getDividerView() {

        Log.v(TAG, "getDividerView");
        View dividerView = new View(this);
        dividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        dividerView.setBackgroundColor(Color.BLACK);
        return dividerView;
    }

    protected void setDefaultTargetingKeys() {

        mTargetingKeyList = new ArrayList<>();
        mTargetingKeyList.add(PNAdTargetingModel.Keys.age);
        mTargetingKeyList.add(PNAdTargetingModel.Keys.education);
        mTargetingKeyList.add(PNAdTargetingModel.Keys.gender);
        mTargetingKeyList.add(PNAdTargetingModel.Keys.interests);
        mTargetingKeyList.add(PNAdTargetingModel.Keys.iap);
        mTargetingKeyList.add(PNAdTargetingModel.Keys.iap_total);
    }
}
