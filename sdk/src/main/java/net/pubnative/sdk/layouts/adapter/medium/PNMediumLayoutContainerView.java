package net.pubnative.sdk.layouts.adapter.medium;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.sdk.R;
import net.pubnative.sdk.layouts.PNMediumLayoutView;

public abstract class PNMediumLayoutContainerView extends PNMediumLayoutView {

    protected RelativeLayout mRootView;

    public PNMediumLayoutContainerView(Context context) {
        super(context);
    }

    protected void loadWithView(View view) {

        mRootView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_layout_medium_container, this, true);
        mRootView.addView(view);
    }
}
