package com.yanrou.dawnisland.util;

import androidx.lifecycle.MutableLiveData;

public class ForumMapLiveData<T> extends MutableLiveData<T> {
    @Override
    public void setValue(T value) {
        if (value != null) {
            super.setValue(value);
        }
    }
}
