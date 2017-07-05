// The MIT License (MIT)
//
// Copyright (c) 2017 PubNative GmbH
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

package net.pubnative.sdk.demo.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import net.pubnative.sdk.demo.CardItem;
import net.pubnative.sdk.demo.CardsAdapter;
import net.pubnative.sdk.demo.R;
import net.pubnative.sdk.demo.Settings;
import net.pubnative.sdk.layouts.PNLargeLayout;
import net.pubnative.sdk.layouts.PNLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LargeLayoutListFragment extends Fragment implements PNLargeLayout.ViewListener, PNLargeLayout.LoadListener, PNLargeLayout.TrackListener {

    private static final String TAG = LargeLayoutListFragment.class.getSimpleName();

    protected List<String> mPlacements = new ArrayList<>();

    protected Spinner                      mPlacementSpinner;
    protected RelativeLayout               mLoaderContainer;
    protected RelativeLayout               mBlankContainer;
    protected ArrayAdapter<String>         mSpinnerAdapter;
    protected PNLargeLayout                mLargeLayout;
    protected RecyclerView.Adapter         mAdapter;
    protected RecyclerView.LayoutManager   mLayoutManager;
    protected RecyclerView                 mRecyclerView;
    protected List<CardItem>               mCardItems;

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

                mLargeLayout = new PNLargeLayout();
                mLargeLayout.setLoadListener(LargeLayoutListFragment.this);
                if (mPlacementSpinner != null && mPlacementSpinner.getSelectedItem() != null) {
                    mLargeLayout.load(getActivity(), Settings.appToken, mPlacementSpinner.getSelectedItem().toString());
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

    @Override
    public void onPNLayoutLoadFinish(PNLayout layout) {
        showToast("Layout load finish");
        mLoaderContainer.setVisibility(View.INVISIBLE);
        mLargeLayout.setViewListener(this);
        mLargeLayout.setTrackListener(this);
        mLargeLayout.show();
    }

    @Override
    public void onPNLayoutLoadFail(PNLayout layout, Exception exception) {
        showToast("Layout load fail: " + exception.getMessage());
        mLoaderContainer.setVisibility(View.INVISIBLE);
        exception.printStackTrace();
    }

    @Override
    public void onPNLayoutViewShown(PNLayout layout) {
        showToast("Layout view shown");
    }

    @Override
    public void onPNLayoutViewHidden(PNLayout layout) {
        showToast("Layout view hidden");
    }

    @Override
    public void onPNLayoutTrackImpression(PNLayout layout) {
        showToast("Layout track impression");
    }

    @Override
    public void onPNLayoutTrackClick(PNLayout layout) {
        showToast("Layout track click");
        if (mLargeLayout != null) {
            mLargeLayout.hide();
        }
    }

    protected void showToast(String message) {
        Context context = getActivity();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
