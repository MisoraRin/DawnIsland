package com.yanrou.dawnisland;

/*
public class SeriesContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;

    // 正在加载
    public final int LOADING = 1;

    // 全部已加载
    public final int LOADING_ALL = 2;

    // 加载失败
    public final int LOADING_FAIL = 3;

    // 加载完成
    public final int LOADING_COMPLETE = 4;

    // 串被删了，将就这个加载条用用
    public final int DELETED = 5;

    // 当前加载状态，默认为加载中
    private int loadState = LOADING_COMPLETE;

    public void setLoadState(int loadState) {
        this.loadState = loadState;
    }

    public int getLoadState() {
        return loadState;
    }

    List<SeriesContentJson.ReplysBean> seriesContents;

    NeedGetNext needGetNext;
    Activity context;

    Set<String> po=new HashSet<>();

    public void setPo(String userid){
        po.add(userid);
    }

    interface NeedGetNext{
        void getNext();
    }

    FragmentManager fragmentManager;

    public void setNeedGetNext(NeedGetNext needGetNext) {
        this.needGetNext = needGetNext;
    }

    public SeriesContentAdapter(List<SeriesContentJson.ReplysBean> seriesContents, Activity context, FragmentManager supportFragmentManager) {
        this.seriesContents = seriesContents;
        this.context = context;
        this.fragmentManager = supportFragmentManager;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seris_content_card, parent, false);
            return new SeriesContentView(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_content_list_footer, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SeriesContentView) {
            SeriesContentView contentHolder = (SeriesContentView) holder;
            SeriesContentJson.ReplysBean replys = seriesContents.get(position);

            if (po.contains(replys.getUserid())) {
                contentHolder.cookie.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                contentHolder.cookie.setTextColor(Color.parseColor("#000000"));
            } else {
                contentHolder.cookie.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                contentHolder.cookie.setTextColor(Color.parseColor("#B0B0B0"));
            }
            contentHolder.cookie.setText(replys.getUserid());

            contentHolder.time.setText(replys.getNow());
            //contentHolder.content.setText(replys.getSpannableStringBuilder(), TextView.BufferType.SPANNABLE);
            contentHolder.number.setText(replys.getSeriesId());


            if (replys.getSage() == 1) {
                contentHolder.sega.setVisibility(View.VISIBLE);
            } else {
                contentHolder.sega.setVisibility(View.GONE);
            }

            if (replys.getAdmin() == 1) {
                contentHolder.cookie.setTextColor(Color.parseColor("#FF0F0F"));
            }


            if (replys.getExt() != null && (!replys.getExt().equals(""))) {
                contentHolder.imageView.setVisibility(View.VISIBLE);
                //设置图片圆角角度
                //RoundedCorners roundedCorners= new RoundedCorners(12);
                //通过RequestOptions扩展功能
                //RequestOptions options= RequestOptions.bitmapTransform(roundedCorners);

                Glide.with(context)
                        .load("https://nmbimg.fastmirror.org/thumb/" + replys.getImg() + replys.getExt())
                        .override(250, 250)
                        .into(contentHolder.imageView);
                contentHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BigPictrueDialog dialog = new BigPictrueDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("imgurl", "https://nmbimg.fastmirror.org/image/" + replys.getImg() + replys.getExt());
                        dialog.setArguments(bundle);
                        dialog.show(fragmentManager, "imageView");
                    }
                });
            } else {
                contentHolder.imageView.setVisibility(View.GONE);
            }

        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.textView.setText("友谊魔法加载中~");
                    break;
                case LOADING_FAIL: // 加载失败
                    footViewHolder.textView.setText("加载失败");
                    break;
                case LOADING_ALL:
                    footViewHolder.textView.setText("已经加载全部内容");
                    break;
                case LOADING_COMPLETE:
                    footViewHolder.textView.setText("加载完毕");
                    break;
                case DELETED:
                    footViewHolder.textView.setText("该串已被删除");
                    break;
                default:

                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return seriesContents.size() + 1;
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        FootViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.waitting_text);
        }
    }

    static class SeriesContentView extends RecyclerView.ViewHolder {
        TextView sega;
        TextView number;
        TextView cookie;
        TextView content;
        TextView time;
        TextView titleAndName;
        ImageView imageView;

        public SeriesContentView(@NonNull View itemView) {
            super(itemView);
            sega=itemView.findViewById(R.id.sega);
            number=itemView.findViewById(R.id.number);
            cookie = itemView.findViewById(R.id.SeriesListCookie);
            content = itemView.findViewById(R.id.SeriesListContent);
            time = itemView.findViewById(R.id.SeriesListTime);
            titleAndName=itemView.findViewById(R.id.title_and_name);
            imageView = itemView.findViewById(R.id.series_content_imageView);
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}

 */
