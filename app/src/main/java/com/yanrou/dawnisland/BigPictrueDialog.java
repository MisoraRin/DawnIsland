package com.yanrou.dawnisland;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

public class BigPictrueDialog extends DialogFragment {

    private PhotoView photoView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomReplyDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        photoView = new PhotoView(this.getContext());
        Window win = getDialog().getWindow();
        if (win != null) {
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
            lp.windowAnimations = R.style.Animation_Bottom;
            win.setAttributes(lp);
            // dialog 布局位于底部
            //win.setGravity(Gravity.BOTTOM);
            // 设置进出场动画
            //win.setWindowAnimations(R.style.Animation_Bottom);

            //这一步最好要做，因为如果这两个flag没有清除的话下面没有生效
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏(StatusBar)颜色透明
            win.setStatusBarColor(Color.TRANSPARENT);
            //设置导航栏(NavigationBar)颜色透明
            //win.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        }
        return photoView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        String imgUrl = bundle.getString("imgurl");
        Glide.with(getContext()).load(imgUrl).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                photoView.setImageDrawable(resource);
            }
        });
    }
}
