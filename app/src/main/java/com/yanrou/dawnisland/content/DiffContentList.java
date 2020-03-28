package com.yanrou.dawnisland.content;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DiffContentList extends DiffUtil.Callback {
    private List<Object> oldList;
    private List<Object> newList;

    public DiffContentList(List<Object> oldList, List<Object> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        boolean oldItem = oldList.get(oldItemPosition) instanceof ContentItem;
        boolean newItem = newList.get(newItemPosition) instanceof ContentItem;
        if (oldItem && newItem) {
            return ((ContentItem) oldList.get(oldItemPosition)).seriesId.equals(((ContentItem) newList.get(newItemPosition)).seriesId);
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}
