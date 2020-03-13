package com.yanrou.dawnisland;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yanrou.dawnisland.json2class.ForumJson;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    private List<ForumJson.ForumsBean> forumsList;
    private ChangeForum changeForum;

    interface ChangeForum {
        void changeForum(int fid, String fname);
    }

    public void setChangeForum(ChangeForum changeForum) {
        this.changeForum = changeForum;
    }

    ForumAdapter(List<ForumJson.ForumsBean> forumsList) {
        this.forumsList = forumsList;
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
            holder.forum.setText(Html.fromHtml(forum.getShowName()));
        } else {
            holder.forum.setText(forum.getName());
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

        public ViewHolder(@NonNull View itemView, final ChangeForum o) {
            super(itemView);
            forum = itemView.findViewById(R.id.forum_name);
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
