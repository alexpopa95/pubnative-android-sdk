package net.pubnative.sdk.layouts.adapter;

import android.content.Context;
import android.view.View;

public abstract class PNLayoutFeedAdapter extends PNLayoutAdapter {

    public abstract View getView(Context context);

    public abstract void startTracking();

    public abstract void stopTracking();
}
