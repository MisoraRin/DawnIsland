package com.yanrou.dawnisland;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.ItemViewBinder;

import org.jetbrains.annotations.NotNull;


public class FooterViewBinder extends ItemViewBinder<FooterView, FooterViewBinder.ViewHolder> {
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull LayoutInflater layoutInflater, @NotNull ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.series_content_list_footer, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, FooterView footerView) {
        viewHolder.textView.setText(footerView.text);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.waitting_text);
        }
    }
}
