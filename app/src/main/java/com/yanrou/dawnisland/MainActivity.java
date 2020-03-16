package com.yanrou.dawnisland;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.SubtitleCollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.susion.rabbit.Rabbit;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanrou.dawnisland.json2class.ForumJson;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Toolbar toolbar;
    RecyclerView forumList;
    ViewPager viewPager;

    AppBarLayout appBarLayout;

    List<ForumJson.ForumsBean> forumsList = new ArrayList<>();

    ForumAdapter forumAdapter;
    /**
     * 记录当前的页数
     */

    SharedPreferences sharedPreferences;
    DrawerLayout drawerLayout;

    SeriesFragment seriesFragment;
    TrandFragment trandFragment;

    String forumName;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                Intent intent = new Intent(MainActivity.this, CustomizeSizeActivity.class);
                startActivity(intent);
                break;
            case R.id.cookie_button:
                Intent intent1 = new Intent(MainActivity.this, CookiesManageActivity.class);
                startActivity(intent1);
                break;
            case R.id.crash:
                CrashReport.testJavaCrash();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("DawnIsland", MODE_PRIVATE);

        Rabbit.INSTANCE.open(true, this);

        forumAdapter = new ForumAdapter(forumsList, getApplicationContext());
        /**
         *读饼干
         */
        List<CookieData> cookieData = LitePal.findAll(CookieData.class);
        if (cookieData.size() > 0) {
            HttpUtil.cookie = cookieData.get(0).userHash;
        }
        /**
         * 初始化
         */
        ReadableTime.initialize(this);
        getView();
        /**
         * 新的状态栏透明方案
         */
        Window window = getWindow();
        //这一步最好要做，因为如果这两个flag没有清除的话下面没有生效
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏(StatusBar)颜色透明
        window.setStatusBarColor(Color.TRANSPARENT);
        //设置导航栏(NavigationBar)颜色透明
        //window.setNavigationBarColor(Color.TRANSPARENT);

        LitePal.getDatabase();
        Log.d(TAG, "onCreate: " + Arrays.toString(getResources().getStringArray(R.array.face)));

        /**
         * 设置抽屉滑动响应宽度
         */
        setDrawerLeftEdgeSize(this, drawerLayout, 1f);

        /**
         * 标题栏组件初始化
         */
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.toolbar_home_as_up);
        final SubtitleCollapsingToolbarLayout subtitleCollapsingToolbarLayout = findViewById(R.id.coolapsing_toolbar);


        Log.d(TAG, "onCreate: 重新调用了onCreat方法" + getSupportFragmentManager().getFragments());

        final MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if (getSupportFragmentManager().getFragments().size() == 0) {
            seriesFragment = new SeriesFragment();
            myViewPagerAdapter.addFragment(seriesFragment);
            trandFragment = TrandFragment.newInstance(this);
            myViewPagerAdapter.addFragment(trandFragment);
        } else {
            seriesFragment = (SeriesFragment) getSupportFragmentManager().getFragments().get(0);
            myViewPagerAdapter.addFragment(seriesFragment);
            trandFragment = (TrandFragment) getSupportFragmentManager().getFragments().get(1);
            myViewPagerAdapter.addFragment(trandFragment);
        }

        toolbar.setOnClickListener(v -> {
            seriesFragment.reFresh();
        });

        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                if (position == 0) {
                    subtitleCollapsingToolbarLayout.setTitle(forumName);

                }
                if (position == 1) {
                    appBarLayout.setExpanded(false);
                    forumName = subtitleCollapsingToolbarLayout.getTitle().toString();
                    subtitleCollapsingToolbarLayout.setTitle("A岛热榜");

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        forumAdapter.setChangeForum((id, name) -> {
            subtitleCollapsingToolbarLayout.setTitle(name);
            drawerLayout.closeDrawers();
            seriesFragment.changeForum(id);
            Log.d(TAG, "changeForum: " + seriesFragment);
        });
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        forumList.setLayoutManager(layoutManager2);
        forumList.setAdapter(forumAdapter);
        getForumList();
    }

    void getView() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        forumList = findViewById(R.id.forum_list);
        viewPager = findViewById(R.id.main_page_viewer);
        appBarLayout = findViewById(R.id.appbar_layout);
    }

    void getForumList() {
        //本地已有，直接读取
        Log.d(TAG, "getForumList: 开始读取");
        if (sharedPreferences.contains("ForumJson")) {
            Log.d(TAG, "getForumList: 本地已有");
            String ForumJson = sharedPreferences.getString("ForumJson", "");
            List<ForumJson> forumJsonList = new Gson().fromJson(ForumJson, new TypeToken<List<ForumJson>>() {
            }.getType());
            List<ForumJson.ForumsBean> allForum = new ArrayList<>();
            for (int i = 0; i < forumJsonList.size(); i++) {
                allForum.addAll(forumJsonList.get(i).getForums());
            }
            Log.d(TAG, "onResponse: " + allForum.size());
            DB.setDB(allForum);
            forumsList.addAll(allForum);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    forumAdapter.notifyDataSetChanged();
                }
            });
            Log.d(TAG, "getForumList: 调用碎片方法");
            //seriesFragment.getNewPage();
            //trandFragment.startGetTrend();
        } else {//否则从网络加载
            Log.d(TAG, "getForumList: 网络加载开始");
            HttpUtil.sendOkHttpRequest("https://nmb.fastmirror.org/Api/getForumList", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: 获取板块列表失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String json = response.body().string();
                    sharedPreferences.edit().putString("ForumJson", json).apply();
                    getForumList();
                }
            });
        }
    }


    private void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) return;
        try {
            // 找到 ViewDragHelper 并设置 Accessible 为true
            Field leftDraggerField =
                    drawerLayout.getClass().getDeclaredField("mLeftDragger");//Right
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);

            // 找到 edgeSizeField 并设置 Accessible 为true
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);

            // 设置新的边缘大小
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (displaySize.x *
                    displayWidthPercentage)));

            Field leftCallbackField = drawerLayout.getClass().getDeclaredField("mLeftCallback");
            leftCallbackField.setAccessible(true);

            ViewDragHelper.Callback leftCallback = (ViewDragHelper.Callback) leftCallbackField.get(drawerLayout);
            Field peekRunnableField = leftCallback.getClass().getDeclaredField("mPeekRunnable");
            peekRunnableField.setAccessible(true);
            Runnable nullRunnalbe = () -> {
            };
            peekRunnableField.set(leftCallback, nullRunnalbe);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
        }

    }
}
