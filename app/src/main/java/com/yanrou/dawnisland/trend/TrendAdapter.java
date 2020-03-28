package com.yanrou.dawnisland.trend;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yanrou.dawnisland.OnItemClick;
import com.yanrou.dawnisland.R;

import java.util.List;

public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.ViewHolder> {
    private static final String TAG = "TrendAdapter";

    List<TrendItem> trendItems;

    OnItemClick onItemClick;

    void setOnItemClick(OnItemClick o) {
        onItemClick = o;
    }

    public TrendAdapter(List<TrendItem> trendItems) {
        this.trendItems = trendItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_item, parent, false);
        return new ViewHolder(view, onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrendItem trendItem = trendItems.get(position);
        holder.forum.setText(trendItem.getForum());
        holder.content.setText(trendItem.getContent());
        holder.trend.setText(trendItem.getTrend());
        holder.id.setText(trendItem.getId());
        holder.rank.setText(trendItem.getRank());
    }

    @Override
    public int getItemCount() {
        return trendItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank;
        TextView trend;
        TextView content;
        TextView forum;
        TextView id;

        public ViewHolder(@NonNull View itemView, final OnItemClick onItemClick) {
            super(itemView);
            rank = itemView.findViewById(R.id.rank);
            trend = itemView.findViewById(R.id.trend);
            content = itemView.findViewById(R.id.SeriesListContent);
            forum = itemView.findViewById(R.id.SeriesListForum);
            id = itemView.findViewById(R.id.id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onItemClick(id.getText().toString(), forum.getText().toString());
                }
            });
        }
    }
}
