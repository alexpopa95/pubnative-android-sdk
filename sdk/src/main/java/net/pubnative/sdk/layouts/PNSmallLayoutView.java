package net.pubnative.sdk.layouts;

import android.content.Context;

public abstract class PNSmallLayoutView extends PNLayoutView {

    public PNSmallLayoutView(Context context) {
        super(context);
    }

    public enum IconPosition {
        LEFT,
        RIGHT
    }

    public void setIconPosition(IconPosition position){}
}
