package net.pubnative.sdk.layouts;

import android.content.Context;

public abstract class PNMediumLayoutView extends PNLayoutView {

    public PNMediumLayoutView(Context context) {
        super(context);
    }

    public enum IconPosition {
        LEFT,
        RIGHT
    }

    public enum BannerPosition {
        TOP,
        CENTER,
        BOTTOM
    }

    public void setIconPosition(IconPosition position) {}
    public void setBannerPosition(BannerPosition position) {}
}
