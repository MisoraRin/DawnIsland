package com.yanrou.dawnisland;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yanrou.dawnisland.json2class.ReplysBean;
import com.yanrou.dawnisland.json2class.SeriesContentJson;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author suche
 */
public class CookiesManageActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView addButton;
    ProgressDialog progressDialog;
    String testString, userid;
    private EditText cookieHash;
    private TextView cookieName;

    AlertDialog dialog;

    TextView enter, cancel, test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookies_manage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = View.inflate(this, R.layout.add_cookie_dialog, null);
        cookieHash = view1.findViewById(R.id.user_hash);
        cookieName = view1.findViewById(R.id.cookie_name);

        test = view1.findViewById(R.id.test);
        enter = view1.findViewById(R.id.yes);
        cancel = view1.findViewById(R.id.cancel);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                testString = getRandomString(17);
                try {
                    sendTestMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookieData cookieData = new CookieData();
                cookieData.userHash = cookieHash.getText().toString();
                cookieData.cookieName = cookieName.getText().toString();
                cookieData.save();
                dialog.dismiss();
            }
        });

        cookieName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                testString = getRandomString(17);
                try {
                    sendTestMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setView(view1).setTitle("添加饼干");
        //创建一个对话框
        dialog = builder.create();
        dialog.onWindowFocusChanged(true);

        progressDialog = new ProgressDialog(dialog.getContext());
        progressDialog.setTitle("检测饼干");
        progressDialog.setMessage("正在检测饼干可用性");
        progressDialog.setCancelable(false);


        recyclerView = findViewById(R.id.cookies_list);
        addButton = findViewById(R.id.add_button);

        List<CookieData> cookieDataList = LitePal.findAll(CookieData.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        CookiesListAdapter cookiesListAdapter = new CookiesListAdapter(cookieDataList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cookiesListAdapter);

        addButton.setOnClickListener(view -> showCustomerDialog());

    }

    /**
     * 自定义对话框事件按钮
     *
     * @param
     */
    public void showCustomerDialog() {
        //显示一个对话框
        dialog.show();
    }

    /**
     * 生成随机字符串用来查找饼干
     *
     * @param length 要生成的长度
     * @return
     */
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private static final String TAG = "CookiesManageActivity";

    public void sendTestMessage() throws IOException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 发送测试内容");
                FormBody.Builder builder = new FormBody.Builder()
                        .add("resto", "17735544")
                        .add("content", testString);
                RequestBody formBody = builder.build();
                Request request = new Request.Builder()
                        .url("https://adnmb2.com/Home/Forum/doReplyThread.html")
                        .header("Cookie", "userhash=" + cookieHash.getText().toString())
                        .post(formBody)
                        .build();
                Response response = null;
                try {
                    response = new OkHttpClient().newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    String s1 = response.body().string();
                    Log.d(TAG, "run: 检查内容是否发送成功" + s1);
                    if (s1.contains("成功")) {
                        int replycount = HttpUtil.getReplyCount("17735544");
                        int page = replycount / 19;
                        if ((replycount % 19) > 0) {
                            page++;
                        }
                        Log.d(TAG, "run: 获取串内容反查饼干");
                        OkHttpClient client = new OkHttpClient();
                        Request request1 = new Request.Builder().url("https://nmb.fastmirror.org/Api/thread?id=" + "17735544" + "&page=" + page).build();
                        String s = "";
                        try {
                            Response response1 = client.newCall(request1).execute();
                            s = response1.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "run: " + s);

                        SeriesContentJson seriesContentJson = new Gson().fromJson(s, SeriesContentJson.class);
                        Log.d(TAG, "run: 开始查找饼干");
                        for (ReplysBean replys : seriesContentJson.getReplys()) {
                            if (replys.getContent().equals(testString)) {
                                Log.d(TAG, "run: 找到了饼干");
                                userid = replys.getUserid();
                                runOnUiThread(() -> {
                                    cookieName.setText(replys.getUserid());
                                    progressDialog.dismiss();
                                });
                            }
                        }
                    } else {
                        runOnUiThread(() -> progressDialog.dismiss());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
}
