package net.pubnative.sdk.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private List<CardItem> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mContent;
        RelativeLayout mAdContent;

        public ViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mAdContent = (RelativeLayout) itemView.findViewById(R.id.rl_content);
        }
    }

    public CardsAdapter(List<CardItem> dataset) {
        mDataset = dataset;
    }

    @Override
    public CardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_article_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardsAdapter.ViewHolder holder, int position) {
        CardItem cardItem = mDataset.get(position);
        if (cardItem.adView == null) {
            holder.mContent.setText(mDataset.get(position).content);
            holder.mContent.setVisibility(View.VISIBLE);
            holder.mAdContent.setVisibility(View.GONE);
        } else {
            holder.mAdContent.setVisibility(View.VISIBLE);
            holder.mAdContent.removeAllViews();
            ViewGroup parent = (ViewGroup) cardItem.adView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            holder.mAdContent.addView(cardItem.adView);
            holder.mContent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
