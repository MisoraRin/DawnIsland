package com.yanrou.dawnisland;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubscriberAdapter extends RecyclerView.Adapter<SubscriberAdapter.ViewHolder> {

    List<SubscriberItem> subscriberItemList;
    OnItemClick onItemClick;

    void setOnItemClick(OnItemClick o) {
        onItemClick = o;
    }

    public SubscriberAdapter(List<SubscriberItem> subscriberItemList) {
        this.subscriberItemList = subscriberItemList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscribe_series_item, parent, false);
        return new SubscriberAdapter.ViewHolder(view, onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubscriberItem subscriberItem = subscriberItemList.get(position);
        holder.cookie.setText(subscriberItem.cookie);
        holder.content.setText(Html.fromHtml(subscriberItem.content));
        holder.newInfo.setText(subscriberItem.newInfo);
        holder.forum = subscriberItem.forum;
        holder.seriesid = subscriberItem.seriesid;
    }

    @Override
    public int getItemCount() {
        return subscriberItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        String forum;
        String seriesid;
        TextView cookie;
        TextView content;
        TextView newInfo;

        public ViewHolder(@NonNull View itemView, final OnItemClick onItemClick) {
            super(itemView);
            cookie = itemView.findViewById(R.id.SeriesListCookie);
            content = itemView.findViewById(R.id.SeriesListContent);
            newInfo = itemView.findViewById(R.id.new_info);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onItemClick(seriesid, forum);
                }
            });
        }
    }
}
