package net.pubnative.sdk.demo.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import net.pubnative.sdk.demo.R;

public class LayoutsActivity extends FragmentActivity implements ExtendedViewSettingsFragment.onSaveSettingsListener {

    private static final String TAG = LayoutsActivity.class.getSimpleName();

    public static final String TITLE_COLOR = "title_color";
    public static final String DESCRIPTION_COLOR = "desc_color";
    public static final String CTA_COLOR = "cta_color";
    public static final String BG_COLOR = "bg_color";
    public static final String TITLE_SIZE = "title_size";
    public static final String DESCRIPTION_SIZE = "desc_size";
    public static final String ICON_POSITION = "icon_position";
    public static final String BANNER_POSITION = "banner_position";

    private int    mCheckedRadioButtonId;
    private Bundle mViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layouts);
        mCheckedRadioButtonId = ((RadioGroup)findViewById(R.id.radio_grp)).getCheckedRadioButtonId();
        mViewSettings = getIntent().getExtras();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            loadView();
        }

        findViewById(R.id.btn_ev_config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick");

                v.setVisibility(View.GONE);
                ExtendedViewSettingsFragment settingsFragment = new ExtendedViewSettingsFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, settingsFragment);
                transaction.commit();
            }
        });
    }

    @Override
    public void saveSettings(Bundle bundle) {
        Log.v(TAG, "saveSettings");
        findViewById(R.id.btn_ev_config).setVisibility(View.VISIBLE);
        mViewSettings = bundle;
        loadView();
    }

    public void onRadioButtonClicked(View v) {
        mCheckedRadioButtonId = v.getId();
        loadView();
    }

    private void loadView() {
        switch (mCheckedRadioButtonId) {
            case R.id.radio_small:
                SmallLayoutListFragment smallLayoutListFragment = new SmallLayoutListFragment();
                smallLayoutListFragment.setArguments(mViewSettings);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, smallLayoutListFragment).commit();
                break;
            case R.id.radio_medium:
                MediumLayoutListFragment mediumLayoutListFragment = new MediumLayoutListFragment();
                mediumLayoutListFragment.setArguments(mViewSettings);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mediumLayoutListFragment).commit();
                break;
            case R.id.radio_large:
                LargeLayoutListFragment largreLayoutListFragment = new LargeLayoutListFragment();
                largreLayoutListFragment.setArguments(mViewSettings);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, largreLayoutListFragment).commit();
                break;
        }
    }
}
