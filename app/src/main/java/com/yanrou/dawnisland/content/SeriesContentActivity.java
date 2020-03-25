package com.yanrou.dawnisland.content;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
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

import org.jetbrains.annotations.NotNull;

/**
 * @author suche
 */
public class SeriesContentActivity extends AppCompatActivity {
    private static final String TAG = "SeriesContentActivity";
    RecyclerView recyclerView;
    Toolbar toolbar;
    ActionBar actionBar;

    String id;
    String forumName;

    SmartRefreshLayout smartRefreshLayout;

    ReplyDialog replyDialog = null;

    SeriesData seriesData;

    private MultiTypeAdapter multiTypeAdapter;

    private SeriesContentViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.series_content_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_content);
        //TODO 应该更早初始化该类
        ReadableTime.initialize(this);
        //获取控件
        initView();

        actionBar.setDisplayHomeAsUpEnabled(true);
        //状态栏透明
        makeStatusBarTran(getWindow());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        //TODO 这里应该传入fid，然后通过DB类获取到板块名称，而不是直接传入板块名称
        forumName = intent.getStringExtra("forumTextView");
        Log.d(TAG, "onCreate: " + id);
        toolbar.setTitle("A岛 · " + forumName);
        toolbar.setSubtitle(">>No." + id + " · adnmb.com");

        viewModel = new ViewModelProvider(this).get(SeriesContentViewModel.class);

        multiTypeAdapter = new MultiTypeAdapter();
        
        multiTypeAdapter.register(ContentItem.class, new ContentViewBinder(SeriesContentActivity.this));

        multiTypeAdapter.register(FooterView.class, new FooterViewBinder());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new SeriesRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                viewModel.loadMore();
            }
        });

        recyclerView.setAdapter(multiTypeAdapter);
        smartRefreshLayout.setEnableAutoLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> viewModel.refresh());
        //TODO 首次获取放在view model中
        //presenter.loadFirstPage();


    }

    private void makeStatusBarTran(@NotNull Window window) {
        //这一步要做，因为如果这两个flag没有清除的话下面没有生效
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏(StatusBar)颜色透明
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        //设置导航栏(NavigationBar)颜色透明
        //window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
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
            case R.id.jump_page:
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
}
