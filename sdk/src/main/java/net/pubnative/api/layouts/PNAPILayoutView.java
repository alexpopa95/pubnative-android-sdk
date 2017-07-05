package net.pubnative.api.layouts;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class PNAPILayoutView extends RelativeLayout {

    public PNAPILayoutView(Context context) {
        super(context);
    }

    public class IDMap {

        public int title;
        public int description;
        public int icon;
        public int banner;
        public int callToAction;
        public int starRating;
    }

    public interface Listener {

        void onPubnativeLayoutViewImpressionConfirmed(PNAPILayoutView view);

        void onPubnativeLayoutViewClick(PNAPILayoutView view);
    }
    protected Listener mListener;

    protected void invokeOnImpressionConfirmed() {
        if (mListener != null) {
            mListener.onPubnativeLayoutViewImpressionConfirmed(this);
        }
    }

    protected void invokeOnClick() {
        if (mListener != null) {
            mListener.onPubnativeLayoutViewClick(this);
        }
    }

    //==============================================================================================
    // PUBLIC
    //==============================================================================================
    public void setListener(Listener listener) {
        mListener = listener;
    }

    // Abstract
    //----------------------------------------------------------------------------------------------
    public abstract void startTracking();
    public abstract void stopTracking();
    public abstract IDMap getIDMap();
}