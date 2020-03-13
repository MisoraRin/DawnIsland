package com.yanrou.dawnisland;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yanrou.dawnisland.json2class.TimeLineJson;

import java.util.List;
import java.util.Map;

public class SeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SeriesAdapter";
    private List<TimeLineJson> allcontent;
    private Map<Integer, String> forumid = DB.getDb();
    private Context context;

    //是否展示订阅布局
    private boolean subscriberVisiable = false;

    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 订阅横滑
    private final int TYPE_SUB = 3;

    // 正在加载
    public final int LOADING = 1;
    // 加载失败
    public final int LOADING_FAIL = 3;
    // 当前加载状态，默认为加载中
    private int loadState = LOADING;

    SubscriberAdapter subscribeAdapter;
    LinearLayoutManager layoutManager;


    public void setSubscriberVisiable(boolean subscriberVisiable) {
        this.subscriberVisiable = subscriberVisiable;
    }

    public boolean isSubscriberVisiable() {
        return subscriberVisiable;
    }

    OnItemClick onItemClick;

    void setOnItemClick(OnItemClick o) {
        onItemClick = o;
    }

    public SeriesAdapter(List<TimeLineJson> allcontent, Context context, SubscriberAdapter subscribeAdapter) {
        this.allcontent = allcontent;
        this.context = context;
        this.subscribeAdapter = subscribeAdapter;
        layoutManager = new LinearLayoutManager(context);
    }


    @Override
    public int getItemViewType(int position) {
        if (subscriberVisiable && position == 0) {
            return TYPE_SUB;
        } else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seris_card, parent, false);
            return new ViewHolder(view, onItemClick);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_content_list_footer, parent, false);
            return new FootViewHolder(view);
        } else if (viewType == TYPE_SUB) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscriber, parent, false);
            return new SubscribeList(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewholder = (ViewHolder) holder;
            TimeLineJson temp;
            if (subscriberVisiable && position > 0) {
                temp = allcontent.get(position - 1);
            } else {
                temp = allcontent.get(position);
            }
            if (temp.getSage() == 1) {
                viewholder.sage.setVisibility(View.VISIBLE);
            } else {
                viewholder.sage.setVisibility(View.GONE);
            }
            viewholder.id = temp.getId();
            viewholder.content.setText(temp.getSpannedText());
            if (temp.getAdmin() == 1) {
                viewholder.cookie.setTextColor(Color.parseColor("#FF0F0F"));
            } else {
                viewholder.cookie.setTextColor(Color.parseColor("#7a7a7a"));
            }
            viewholder.cookie.setText(temp.getUserid());
            viewholder.time.setText(temp.getNow());

            viewholder.forumName = forumid.get(temp.getFid());
            Log.d(TAG, "onBindViewHolder: " + viewholder.forumName);
            if (temp.getFid() != 0) {
                viewholder.forumTextView.setVisibility(View.VISIBLE);
                viewholder.replycount.setVisibility(View.GONE);
                viewholder.forumTextView.setText(viewholder.forumName + " • " + temp.getReplyCount());
            } else {
                viewholder.forumTextView.setVisibility(View.GONE);
                viewholder.replycount.setVisibility(View.VISIBLE);
                viewholder.replycount.setText(String.valueOf(temp.getReplyCount()));
            }


            if (temp.getExt() != null && (!temp.getExt().equals(""))) {
                viewholder.image.setVisibility(View.VISIBLE);
                //设置图片圆角角度
                //RoundedCorners roundedCorners= new RoundedCorners(12);
                //通过RequestOptions扩展功能
                //RequestOptions options= RequestOptions.bitmapTransform(roundedCorners);

                Glide.with(context)
                        .load("https://nmbimg.fastmirror.org/thumb/" + temp.getImg() + temp.getExt())
                        .override(250, 250)
                        .into(viewholder.image);
            } else {
                viewholder.image.setVisibility(View.GONE);
            }
            Log.d("suyian", "onBindViewHolder: " + position);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.textView.setText("友谊魔法加载中~");
                    break;
                case LOADING_FAIL: // 加载到底
                    footViewHolder.textView.setText("加载大失败");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (subscriberVisiable) {
            return allcontent.size() + 2;
        } else {
            return allcontent.size() + 1;
        }
    }

    class SubscribeList extends RecyclerView.ViewHolder {
        RecyclerView subscribeRecycleView;

        public SubscribeList(@NonNull View itemView) {
            super(itemView);
            subscribeRecycleView = itemView.findViewById(R.id.subscribe_recycleview);

            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            subscribeRecycleView.setLayoutManager(layoutManager);
            subscribeRecycleView.setAdapter(subscribeAdapter);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        FootViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.waitting_text);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        String id;
        String forumName;
        TextView cookie;
        TextView content;
        TextView time;
        TextView forumTextView;
        TextView sage;
        TextView replycount;
        ImageView image;

        public ViewHolder(@NonNull View itemView, final OnItemClick o) {
            super(itemView);
            cookie = itemView.findViewById(R.id.SeriesListCookie);
            content = itemView.findViewById(R.id.SeriesListContent);
            time = itemView.findViewById(R.id.SeriesListTime);
            forumTextView = itemView.findViewById(R.id.SeriesListForum);
            image = itemView.findViewById(R.id.SeriesListImageView2);
            sage = itemView.findViewById(R.id.sage);
            replycount = itemView.findViewById(R.id.reply_count);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: " + forumName);
                    o.onItemClick(id, forumName);
                }
            });
            Log.d("suyian", "SeriesContentView: 初始化初始化");
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyItemChanged(getItemCount() - 1);
    }
}
