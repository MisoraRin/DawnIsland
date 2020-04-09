package com.yanrou.dawnisland;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yanrou.dawnisland.entities.Cookie;

import java.util.List;

public class CookiesListAdapter extends RecyclerView.Adapter<CookiesListAdapter.ViewHolder> {
  public CookiesListAdapter(List<Cookie> cookieList) {
    this.cookieList = cookieList;
    }

  List<Cookie> cookieList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cookie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.userHash.setText(cookieList.get(position).getUserHash());
      holder.cookieName.setText(cookieList.get(position).getCookieName());

    }

    @Override
    public int getItemCount() {
      return cookieList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cookieName;
        TextView userHash;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cookieName = itemView.findViewById(R.id.cookie_name);
            userHash = itemView.findViewById(R.id.cookie_hash);
        }
    }
}
