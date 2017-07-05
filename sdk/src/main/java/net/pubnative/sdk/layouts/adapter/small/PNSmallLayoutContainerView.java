package net.pubnative.sdk.layouts.adapter.small;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.sdk.R;
import net.pubnative.sdk.layouts.PNSmallLayoutView;

public abstract class PNSmallLayoutContainerView extends PNSmallLayoutView {

    protected RelativeLayout mRootView;

    public PNSmallLayoutContainerView(Context context) {
        super(context);
    }

    protected void loadWithView(View view) {

        mRootView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_layout_small_container, this, true);
        mRootView.addView(view);
    }
}
