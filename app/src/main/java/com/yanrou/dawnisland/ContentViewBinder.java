package com.yanrou.dawnisland;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.drakeet.multitype.ItemViewBinder;
import com.yanrou.dawnisland.imageviewer.ImageViewerView;

public class ContentViewBinder extends ItemViewBinder<ContentItem, ContentViewBinder.ViewHolder> {

    private Activity callerActivity;

    public ContentViewBinder(Activity callerActivity){
        this.callerActivity = callerActivity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.seris_content_card, parent, false);
        return new ViewHolder(root);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ContentItem content) {
        holder.cookie.setText(content.cookie, TextView.BufferType.SPANNABLE);
        holder.time.setText(content.time);
        holder.number.setText(content.seriesId);
        holder.content.setText(content.content, TextView.BufferType.SPANNABLE);
        holder.sega.setVisibility(content.sega);
        if (content.hasTitleOrName) {
            holder.titleAndName.setVisibility(View.VISIBLE);
            holder.titleAndName.setText(content.titleAndName);
        } else {
            holder.titleAndName.setVisibility(View.GONE);
        }


        if (content.hasImage) {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.imageView.getContext())
                    .load("https://nmbimg.fastmirror.org/thumb/" + content.imgurl)
                    .override(250, 250)
                    .into(holder.imageView);

            holder.imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    Intent fullScreenImageViewer = new Intent(callerActivity, ImageViewerView.class);
                    fullScreenImageViewer.putExtra("imgurl", "https://nmbimg.fastmirror.org/image/" + content.imgurl);
                    callerActivity.startActivity(fullScreenImageViewer);

                    return false;
                }
            });
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sega;
        TextView number;
        TextView cookie;
        TextView content;
        TextView time;
        TextView titleAndName;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);

            sega = itemView.findViewById(R.id.sega);
            number = itemView.findViewById(R.id.number);
            cookie = itemView.findViewById(R.id.SeriesListCookie);
            content = itemView.findViewById(R.id.SeriesListContent);
            time = itemView.findViewById(R.id.SeriesListTime);
            titleAndName = itemView.findViewById(R.id.title_and_name);
            imageView = itemView.findViewById(R.id.series_content_imageView);


            content.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
