package com.yanrou.dawnisland;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yanrou.dawnisland.json2class.ForumJson;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    private List<ForumJson.ForumsBean> forumsList;
    private ChangeForum changeForum;
    private Context applicationContext;

    interface ChangeForum {
        void changeForum(int fid, String fname);
    }

    public void setChangeForum(ChangeForum changeForum) {
        this.changeForum = changeForum;
    }

    ForumAdapter(List<ForumJson.ForumsBean> forumsList, Context context) {
        this.forumsList = forumsList;
        this.applicationContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_list_item, parent, false);
        return new ViewHolder(view, changeForum);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForumJson.ForumsBean forum = forumsList.get(position);
        holder.fid = forum.getId();
        holder.fname = forum.getName();

        if (forum.getShowName() != null && (!forum.getShowName().equals(""))) {
            Spanned displayName;
            if (Build.VERSION.SDK_INT <  Build.VERSION_CODES.N) {
                displayName = Html.fromHtml(forum.getShowName());

            }else {
                displayName = Html.fromHtml(forum.getShowName(), Html.FROM_HTML_MODE_COMPACT);
            }

            holder.forum.setText(displayName,TextView.BufferType.SPANNABLE);
        } else {
            holder.forum.setText(forum.getName(),TextView.BufferType.SPANNABLE);
        }


        // special handling for drawable resource ID, which cannot have -
        int biId = (forum.getId() > 0)? forum.getId() : 1;
        int resourceId = applicationContext.getResources().getIdentifier("bi_"+String.valueOf(biId), "drawable",
            applicationContext.getPackageName());
        if (resourceId != 0){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(resourceId);
        } else {
            holder.imageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return forumsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        String fname;
        int fid;
        LinearLayout linearLayout;
        TextView forum;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView, final ChangeForum o) {
            super(itemView);
            forum = itemView.findViewById(R.id.forum_name);
            imageView = itemView.findViewById(R.id.icon);
            linearLayout = itemView.findViewById(R.id.forum_layout);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    o.changeForum(fid, fname);
                }
            });
        }
    }
}
