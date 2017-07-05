package net.pubnative.sdk.demo.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import net.pubnative.sdk.core.Pubnative;
import net.pubnative.sdk.core.config.PNConfigManager;
import net.pubnative.sdk.core.config.model.PNConfigModel;
import net.pubnative.sdk.layouts.PNMediumLayoutView;
import net.pubnative.sdk.demo.CardItem;
import net.pubnative.sdk.demo.CardsAdapter;
import net.pubnative.sdk.demo.R;
import net.pubnative.sdk.demo.Settings;
import net.pubnative.sdk.layouts.PNLayout;
import net.pubnative.sdk.layouts.PNMediumLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MediumLayoutListFragment extends Fragment implements PNMediumLayout.LoadListener, PNMediumLayout.TrackListener {

    private static final String TAG = MediumLayoutListFragment.class.getSimpleName();

    public static final int AD_PLACE_NUMBER = 5; // must be from 0 to max element count

    protected List<String> mPlacements = new ArrayList<>();

    protected Spinner                    mPlacementSpinner;
    protected RelativeLayout             mLoaderContainer;
    protected RelativeLayout             mBlankContainer;
    protected ArrayAdapter<String>       mSpinnerAdapter;
    protected PNMediumLayout             mMediumLayout;
    protected RecyclerView.Adapter       mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView               mRecyclerView;
    protected List<CardItem>             mCardItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ad_list_layout, container, false);
        mPlacementSpinner = (Spinner) view.findViewById(R.id.spinner_standard_unit_placement);
        mLoaderContainer = (RelativeLayout) view.findViewById(R.id.container_standard_ad_unit_loader);
        mBlankContainer = (RelativeLayout) view.findViewById(R.id.rl_blankContainer);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        inflater.inflate(R.layout.layout_infeed_banner, mBlankContainer);
        prepareRecyclerView();
        view.findViewById(R.id.btn_standard_unit_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoaderContainer.setVisibility(View.VISIBLE);

                if (mMediumLayout != null) {
                    mMediumLayout.stopTrackingView();
                }
                mMediumLayout = new PNMediumLayout();
                mMediumLayout.setLoadListener(MediumLayoutListFragment.this);
                if (mPlacementSpinner != null && mPlacementSpinner.getSelectedItem() != null) {
                    mMediumLayout.load(getActivity(), Settings.appToken, mPlacementSpinner.getSelectedItem().toString());
                } else {
                    Toast.makeText(getContext(), "Placement name is empty! Please, check Settings.", Toast.LENGTH_LONG).show();
                    mLoaderContainer.setVisibility(View.GONE);
                }
            }
        });
        loadSpinnerData();
        return view;
    }

    protected void prepareRecyclerView() {
        mCardItems = new ArrayList<CardItem>();
        for (int i = 0; i <= 20; i++) {
            CardItem item = new CardItem();
            item.content = getResources().getString(R.string.very_long_string);
            mCardItems.add(item);
        }
        mAdapter = new CardsAdapter(mCardItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadSpinnerData() {

        Log.v(TAG, "loadSpinnerData");
        mPlacements.clear();
        mSpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_row, mPlacements);
        mPlacementSpinner.setAdapter(mSpinnerAdapter);
        PNConfigManager.getConfig(getContext(), Settings.appToken, new PNConfigManager.Listener() {
            @Override
            public void onConfigLoaded(PNConfigModel configModel) {
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
        Context context = getActivity();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void removeAdView() {
        if (mCardItems.get(AD_PLACE_NUMBER).adView != null) {
            mCardItems.remove(AD_PLACE_NUMBER);
            mAdapter.notifyItemRemoved(AD_PLACE_NUMBER);
        }
    }

    @Override
    public void onPNLayoutLoadFinish(PNLayout layout) {
        showToast("Layout load finish");

        mLoaderContainer.setVisibility(View.INVISIBLE);
        removeAdView();

        PNMediumLayoutView adView = mMediumLayout.getView(getContext());
        CardItem item = new CardItem();
        item.adView = adView;
        mCardItems.add(AD_PLACE_NUMBER, item);
        mMediumLayout.setTrackListener(this);
        mMediumLayout.startTrackingView();
        mAdapter.notifyItemInserted(AD_PLACE_NUMBER);
        Bundle args = getArguments();
        if (args != null && item.adView != null) {
            adView.setTitleTextColor(args.getInt(LayoutsActivity.TITLE_COLOR));
            adView.setDescriptionTextColor(args.getInt(LayoutsActivity.DESCRIPTION_COLOR));
            adView.setCallToActionTextColor(args.getInt(LayoutsActivity.CTA_COLOR));
            adView.setIconPosition((PNMediumLayoutView.IconPosition) args.getSerializable(LayoutsActivity.ICON_POSITION));
            adView.setBannerPosition((PNMediumLayoutView.BannerPosition) args.getSerializable(LayoutsActivity.BANNER_POSITION));
            adView.setBackgroundColor(args.getInt(LayoutsActivity.BG_COLOR));
        }
    }

    @Override
    public void onPNLayoutLoadFail(PNLayout layout, Exception exception) {
        showToast("Layout load fail: " + exception.getMessage());
        mLoaderContainer.setVisibility(View.INVISIBLE);
        removeAdView();
        exception.printStackTrace();
    }

    @Override
    public void onPNLayoutTrackImpression(PNLayout layout) {
        showToast("Layout track impression");
    }

    @Override
    public void onPNLayoutTrackClick(PNLayout layout) {
        showToast("Layout track click");
    }
}
