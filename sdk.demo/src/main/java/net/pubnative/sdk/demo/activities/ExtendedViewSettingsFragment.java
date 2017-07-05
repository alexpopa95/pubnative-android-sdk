package net.pubnative.sdk.demo.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import net.pubnative.sdk.layouts.PNMediumLayoutView;
import net.pubnative.sdk.demo.R;

public class ExtendedViewSettingsFragment extends Fragment {

    private static final String TAG = ExtendedViewSettingsFragment.class.getSimpleName();

    private int mTitleColor;
    private int mDescriptionColor;
    private int mCtaColor;
    private int mLayoutBgColor;

    private RadioGroup mIconPosition;
    private RadioGroup mBannerPosition;

    private onSaveSettingsListener mListener;

    public interface onSaveSettingsListener {
        void saveSettings(Bundle bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.ev_settings_layout, container, false);
        ColorSeekBar titleColorSeekBar = (ColorSeekBar) view.findViewById(R.id.titleColorSlider);
        titleColorSeekBar.setBarHeight(12);
        titleColorSeekBar.setColorSeeds(R.array.material_colors);
        titleColorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                mTitleColor = color;
            }
        });
        ColorSeekBar descColorSeekBar = (ColorSeekBar) view.findViewById(R.id.descColorSlider);
        descColorSeekBar.setBarHeight(12);
        descColorSeekBar.setColorSeeds(R.array.material_colors);
        descColorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                mDescriptionColor = color;
            }
        });
        ColorSeekBar ctaColorSeekBar = (ColorSeekBar) view.findViewById(R.id.ctaColorSlider);
        ctaColorSeekBar.setBarHeight(12);
        ctaColorSeekBar.setColorSeeds(R.array.material_colors);
        ctaColorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                mCtaColor = color;
            }
        });
        ColorSeekBar layoutBgColorSeekBar = (ColorSeekBar) view.findViewById(R.id.layoutBgColorSlider);
        layoutBgColorSeekBar.setBarHeight(12);
        layoutBgColorSeekBar.setColorSeeds(R.array.material_colors);
        layoutBgColorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                mLayoutBgColor = color;
            }
        });

        mIconPosition = (RadioGroup) view.findViewById(R.id.rg_icon_position);
        mBannerPosition = (RadioGroup) view.findViewById(R.id.rg_banner_position);

        view.findViewById(R.id.btn_ev_save_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveViewConfiguration();
            }
        });

        return view;
    }

    private void saveViewConfiguration() {
        Bundle args = new Bundle();
        args.putInt(LayoutsActivity.TITLE_COLOR, mTitleColor);
        args.putInt(LayoutsActivity.DESCRIPTION_COLOR, mDescriptionColor);
        args.putInt(LayoutsActivity.CTA_COLOR, mCtaColor);
        args.putInt(LayoutsActivity.BG_COLOR, mLayoutBgColor);

        switch (mIconPosition.getCheckedRadioButtonId()) {
            case R.id.rb_icon_left:
                args.putSerializable(LayoutsActivity.ICON_POSITION, PNMediumLayoutView.IconPosition.LEFT);
                break;
            case R.id.rb_icon_right:
            default:
                args.putSerializable(LayoutsActivity.ICON_POSITION, PNMediumLayoutView.IconPosition.RIGHT);
                break;
        }

        switch (mBannerPosition.getCheckedRadioButtonId()) {

            case R.id.rb_banner_top:
                args.putSerializable(LayoutsActivity.BANNER_POSITION, PNMediumLayoutView.BannerPosition.TOP);
                break;
            case R.id.rb_banner_center:
                args.putSerializable(LayoutsActivity.BANNER_POSITION, PNMediumLayoutView.BannerPosition.CENTER);
                break;
            case R.id.rb_banner_bottom:
                args.putSerializable(LayoutsActivity.BANNER_POSITION, PNMediumLayoutView.BannerPosition.BOTTOM);
                break;
        }

        mListener.saveSettings(args);
    }

    @Override
    public void onAttach(Context context) {
        Log.v(TAG, "onAttach");
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (onSaveSettingsListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: " + context.toString() + " must implement OnSaveSettingsListener!");
            e.printStackTrace();
        }
    }
}
