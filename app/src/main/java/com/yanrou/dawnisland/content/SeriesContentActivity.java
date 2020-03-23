package com.yanrou.dawnisland.content;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.MultiTypeAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanrou.dawnisland.FooterView;
import com.yanrou.dawnisland.FooterViewBinder;
import com.yanrou.dawnisland.R;
import com.yanrou.dawnisland.ReplyDialog;
import com.yanrou.dawnisland.SeriesRecyclerOnScrollListener;
import com.yanrou.dawnisland.database.SeriesData;
import com.yanrou.dawnisland.util.ReadableTime;

import java.util.List;
import java.util.Objects;

/**
 * @author suche
 */
public class SeriesContentActivity extends AppCompatActivity implements SeriesContentView {
    private static final String TAG = "SeriesContentActivity";
    RecyclerView recyclerView;
    Toolbar toolbar;
    ActionBar actionBar;

    String id;
    String forumName;

    SmartRefreshLayout smartRefreshLayout;

    ReplyDialog replyDialog = null;

    //JumpPageDialog jumpPageDialog=null;

    SeriesData seriesData;

    private MultiTypeAdapter multiTypeAdapter;
    private SeriesContentPresenter presenter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.series_content_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_content);

        ReadableTime.initialize(this);

        initView();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        //这一步要做，因为如果这两个flag没有清除的话下面没有生效
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏(StatusBar)颜色透明
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        //设置导航栏(NavigationBar)颜色透明
        //window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        forumName = intent.getStringExtra("forumTextView");
        Log.d(TAG, "onCreate: " + id);
        toolbar.setTitle("A岛 · " + forumName);
        toolbar.setSubtitle(">>No." + id + " · adnmb.com");

        presenter = new SeriesContentPresenter(id, this);
        multiTypeAdapter = new MultiTypeAdapter();
        
        multiTypeAdapter.register(ContentItem.class, new ContentViewBinder(SeriesContentActivity.this));

        multiTypeAdapter.register(FooterView.class, new FooterViewBinder());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new SeriesRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                presenter.loadMore();
            }
        });

        recyclerView.setAdapter(multiTypeAdapter);
        smartRefreshLayout.setFooterTriggerRate(2);
        smartRefreshLayout.setEnableAutoLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> presenter.refresh());
        //swipeRefreshLayout.setOnRefreshListener(presenter::refresh);
        presenter.loadFirstPage();
    }

    void initView() {
        recyclerView = findViewById(R.id.series_content_recycleview);
        toolbar = findViewById(R.id.cotent_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        smartRefreshLayout = findViewById(R.id.smart_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //左上角的箭头按钮的动作
            case android.R.id.home:
                finish();
                break;
            case R.id.new_reply_count:

                break;
            case R.id.delete_all_note:

                break;
            case R.id.jump_page:
                /*
                if (jumpPageDialog == null) {
                    jumpPageDialog=new JumpPageDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("seriesId", id);
                    jumpPageDialog.setArguments(bundle);

                }
                jumpPageDialog.show(getSupportFragmentManager(), "reply");

                 */
                int pageCount = (int) Math.ceil((presenter.getReplyCount() * 1.0f) / 19);
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));

                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.jump_page_dialog, null);
                TextView textView = view.findViewById(R.id.total_page_count);
                textView.setText(String.valueOf(pageCount));
                EditText editText = view.findViewById(R.id.editText);
                builder.setTitle("跳页");
                builder.setView(view);
                builder.setPositiveButton("跳页", (dialog, which) -> {
                    int page = Integer.parseInt(editText.getText().toString());
                    presenter.jumpPage(page);
                    dialog.dismiss();
                });
                builder.create().show();
                break;
            case R.id.reply:
                //防止启动多次
                if (replyDialog == null) {
                    replyDialog = new ReplyDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("seriesId", id);
                    replyDialog.setArguments(bundle);

                }

                replyDialog.show(getSupportFragmentManager(), "reply");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setFirstPage(List<Object> items) {
        multiTypeAdapter.setItems(items);
        runOnUiThread(() -> {
            multiTypeAdapter.notifyItemInserted(0);
        });
    }

    @Override
    public void loadMoreSuccess() {
        runOnUiThread(() -> {
            multiTypeAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void refreshSuccess(int itemCount) {
        runOnUiThread(() -> {
            if (itemCount != 0) {
                multiTypeAdapter.notifyItemRangeInserted(0, itemCount);
                recyclerView.smoothScrollToPosition(itemCount - 1);
            }
            smartRefreshLayout.finishRefresh(0);
        });
    }

    @Override
    public void jumpSuccess() {
        runOnUiThread(() -> {
            multiTypeAdapter.notifyDataSetChanged();
        });
    }
}
