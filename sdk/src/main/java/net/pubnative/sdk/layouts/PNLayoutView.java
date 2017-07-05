package net.pubnative.sdk.layouts;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.RelativeLayout;

public class PNLayoutView extends RelativeLayout {
    public PNLayoutView(Context context) {
        super(context);
    }

    public void setContainerBackgroundColor(int color) {
        getRootView().setBackgroundColor(color);
    }
    public void setAdBackgroundColor(int color){}

    // TITLE
    public void setTitleTextColor(int color){}
    public void setTitleTextSize(float size){}
    public void setTitleTextFont(Typeface font){}

    // DESCRIPTION
    public void setDescriptionTextColor(int color){}
    public void setDescriptionTextSize(float size){}
    public void setDescriptionTextFont(Typeface font){}

    // CTA
    public void setCallToActionBackgroundColor(int color){}
    public void setCallToActionTextColor(int color){}
    public void setCallToActionTextSize(float size){}
    public void setCallToActionTextFont(Typeface font){}
}
