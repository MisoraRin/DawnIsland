package com.yanrou.dawnisland;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class ReplyDialog extends DialogFragment {
    ImageView chosedImage;
    ImageView expandMore;
    File image_will_send;
    EditText contentText, nameText, titleText, emailText;
    String seriesId;
    TextView seriesIdTextView, cookie;

    Rect prerect;

    ImageView chooseImage;

    View partLine;


    ConstraintLayout constraintLayout;

    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int CHOOSE_PHOTO = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "ReplyDialog";
    /**
     * 用来标记邮件面板是否展开、是否全屏
     */
    boolean isNameExpand = false, isFullScreen = false;
    private List<CookieData> cookies;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomReplyDialog);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reply, container, false);

        Dialog dialog = getDialog();

        ImageView imageView = view.findViewById(R.id.send_reply);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReply();
            }
        });

        constraintLayout = view.findViewById(R.id.constrainLayout);
        applyConstraintSet.clone(constraintLayout);
        resetConstraintSet.clone(constraintLayout);

        contentText = view.findViewById(R.id.edit_content);
        nameText = view.findViewById(R.id.name_text);
        titleText = view.findViewById(R.id.title_text);
        emailText = view.findViewById(R.id.email_text);

        cookie = view.findViewById(R.id.cookie);
        cookie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow();
            }
        });


        Bundle bundle = getArguments();
        seriesId = bundle.getString("seriesId");
        seriesIdTextView = view.findViewById(R.id.series_number);
        seriesIdTextView.setText(seriesId);

        chosedImage = view.findViewById(R.id.will_send_image);
        chosedImage.setOnClickListener(view1 -> {
            image_will_send = null;

            TransitionManager.beginDelayedTransition(constraintLayout);
            applyConstraintSet.clone(constraintLayout);
            if (enpandKeyFlag == KEYBOARD_IMAGE) {
                int height = ((ConstraintLayout.LayoutParams) chosedImage.getLayoutParams()).bottomMargin;
                applyConstraintSet.setMargin(R.id.will_send_image, ConstraintSet.BOTTOM, dip2px(getContext(), 8));
                if (partLine.getVisibility() == View.VISIBLE) {
                    applyConstraintSet.setMargin(R.id.spart_line_4, ConstraintSet.BOTTOM, height);
                    enpandKeyFlag = KEYBOARD_NAME;
                } else {
                    applyConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, height - dip2px(getContext(), 8));
                    enpandKeyFlag = KEYBOARD_BUTTON;
                }
            }
            applyConstraintSet.setVisibility(R.id.will_send_image, ConstraintSet.GONE);
            applyConstraintSet.applyTo(constraintLayout);

        });

        expandMore = view.findViewById(R.id.expand_more_button);
        expandMore.setOnClickListener(view12 -> showMoreOrLess());


        Window win = dialog.getWindow();


        partLine = view.findViewById(R.id.spart_line_4);

        chooseImage = view.findViewById(R.id.choose_image_button);
        chooseImage.setOnClickListener(view13 -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //requestPermissions( new String[]{ Manifest.permission. READ_EXTERNAL_STORAGE }, 1);
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                openAlbum();
            }

        });

        ImageView fullScreen = view.findViewById(R.id.full_screen);
        fullScreen.setOnClickListener(view1 -> {
            if (!isFullScreen) {

                contentText.setMaxLines(1000);
                TransitionManager.beginDelayedTransition(constraintLayout);

                applyConstraintSet.clone(constraintLayout);
                applyConstraintSet.constrainHeight(R.id.edit_content, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
                applyConstraintSet.clear(R.id.textView3, ConstraintSet.BOTTOM);
                applyConstraintSet.clear(R.id.space_view, ConstraintSet.BOTTOM);
                applyConstraintSet.connect(R.id.edit_content, ConstraintSet.TOP, R.id.textView3, ConstraintSet.BOTTOM, dip2px(getContext(), 8));
                applyConstraintSet.connect(R.id.textView3, ConstraintSet.TOP, R.id.space_view, ConstraintSet.BOTTOM, 0);
                applyConstraintSet.connect(R.id.space_view, ConstraintSet.TOP, R.id.constrainLayout, ConstraintSet.TOP, 0);
                applyConstraintSet.applyTo(constraintLayout);
                isFullScreen = true;
            } else {

                contentText.setMaxLines(10);

                TransitionManager.beginDelayedTransition(constraintLayout);

                applyConstraintSet.clone(constraintLayout);
                applyConstraintSet.constrainHeight(R.id.edit_content, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                applyConstraintSet.clear(R.id.edit_content, ConstraintSet.TOP);
                applyConstraintSet.clear(R.id.textView3, ConstraintSet.TOP);
                applyConstraintSet.clear(R.id.space_view, ConstraintSet.TOP);
                applyConstraintSet.connect(R.id.textView3, ConstraintSet.BOTTOM, R.id.edit_content, ConstraintSet.TOP, dip2px(getContext(), 8));
                applyConstraintSet.connect(R.id.space_view, ConstraintSet.BOTTOM, R.id.textView3, ConstraintSet.TOP, 0);
                applyConstraintSet.applyTo(constraintLayout);
                isFullScreen = false;
            }
        });


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
        cookies = LitePal.findAll(CookieData.class);

        //TODO 当点击输入框时将其他元素隐藏
        contentText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        contentText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        return view;
    }

    AlertDialog alertDialog;
    final int KEYBOARD_NONE = 0;
    final int KEYBOARD_IMAGE = 1;
    final int KEYBOARD_NAME = 2;
    final int KEYBOARD_BUTTON = 3;
    int enpandKeyFlag = KEYBOARD_NONE;

    @Override
    public void onStart() {
        super.onStart();
        int eight = dip2px(getContext(), 8);
        Activity activity = getActivity();
        if (alertDialog == null) {

            alertDialog = new AlertDialog.Builder(getContext(), R.style.MyTransparent).create();
            TextView textView = new TextView(getContext());
            alertDialog.setView(textView);
            Window window = alertDialog.getWindow();
            //这一步最好要做，因为如果这两个flag没有清除的话下面没有生效
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置布局能够延伸到状态栏(StatusBar)和导航栏(NavigationBar)里面
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏(StatusBar)颜色透明
            window.setStatusBarColor(Color.TRANSPARENT);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            //核心代码是这个属性。
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;


            layoutParams.dimAmount = 0f;
            window.setAttributes(layoutParams);
            Rect rect = new Rect();
            textView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                AutoTransition autoTransition = new AutoTransition();
                autoTransition.setDuration(150);

                TransitionManager.beginDelayedTransition(constraintLayout, autoTransition);
                textView.getWindowVisibleDisplayFrame(rect);
                int change = 0;

                Log.d(TAG, "onGlobalLayout: " + rect + prerect);
                if (prerect != null) {
                    change = rect.bottom - prerect.bottom;
                }

                applyConstraintSet.clone(constraintLayout);
                Log.d(TAG, "onGlobalLayout: " + change);
                if (change < 0) {
                    if (chosedImage.getVisibility() == View.VISIBLE) {
                        applyConstraintSet.setMargin(R.id.will_send_image, ConstraintSet.BOTTOM, -change + eight);
                        enpandKeyFlag = KEYBOARD_IMAGE;
                    } else if (nameText.getVisibility() == View.VISIBLE) {
                        applyConstraintSet.setMargin(R.id.spart_line_4, ConstraintSet.BOTTOM, -change + eight);
                        enpandKeyFlag = KEYBOARD_NAME;
                    } else {
                        applyConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, -change);
                        enpandKeyFlag = KEYBOARD_BUTTON;
                    }
                    //applyConstraintSet.setMargin(R.id.choose_image_button,ConstraintSet.BOTTOM,741);
                } else if (change > 0) {
                    switch (enpandKeyFlag) {
                        case KEYBOARD_BUTTON:
                            applyConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, 0);
                            break;
                        case KEYBOARD_IMAGE:
                            applyConstraintSet.setMargin(R.id.will_send_image, ConstraintSet.BOTTOM, eight);
                            break;
                        case KEYBOARD_NAME:
                            applyConstraintSet.setMargin(R.id.spart_line_4, ConstraintSet.BOTTOM, eight);
                            break;
                        default:
                            break;
                    }
                    enpandKeyFlag = KEYBOARD_NONE;
                    //applyConstraintSet.setGoneMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, dip2px(getContext(), 8));
                }
                applyConstraintSet.applyTo(constraintLayout);
                prerect = new Rect(rect);
            });
        }
        alertDialog.show();

        Log.d(TAG, "onStart: ");
    }


    @Override
    public void onStop() {
        super.onStop();
        alertDialog.dismiss();
        alertDialog = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: " + grantResults.toString());
                    openAlbum();
                } else {
                    Toast.makeText(getContext(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    void sendReply() {


        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("resto", seriesId)
                .addFormDataPart("name", nameText.getText().toString())
                .addFormDataPart("title", titleText.getText().toString())
                .addFormDataPart("email", emailText.getText().toString())
                .addFormDataPart("content", contentText.getText().toString())
                .addFormDataPart("water", "true");
        if (image_will_send != null) {
            RequestBody image = RequestBody.create(image_will_send, MediaType.parse("image/" + image_will_send.getName().substring(image_will_send.getName().lastIndexOf(".") + 1)));
            builder.addFormDataPart("image", image_will_send.getName(), image);
        }

        RequestBody formBody = builder.build();


        Request request = new Request.Builder()
                .url("https://adnmb2.com/Home/Forum/doReplyThread.html")
                .header("Cookie", cookies.get(0).userHash)
                .post(formBody)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
                dismiss();
            }
        });
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        // 打开相册
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            image_will_send = new File(imagePath);
            chosedImage.setVisibility(View.VISIBLE);
            chosedImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        } else {
            Toast.makeText(getContext(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMoreOrLess() {
        if (!isNameExpand) {
            TransitionManager.beginDelayedTransition(constraintLayout);

            applyConstraintSet.clone(constraintLayout);

            applyConstraintSet.setVisibility(R.id.spart_line_1, View.VISIBLE);
            applyConstraintSet.setVisibility(R.id.name_text, View.VISIBLE);
            applyConstraintSet.setVisibility(R.id.spart_line_2, View.VISIBLE);
            applyConstraintSet.setVisibility(R.id.title_text, View.VISIBLE);
            applyConstraintSet.setVisibility(R.id.spart_line_3, View.VISIBLE);
            applyConstraintSet.setVisibility(R.id.email_text, View.VISIBLE);
            applyConstraintSet.setVisibility(R.id.spart_line_4, View.VISIBLE);

            //需要把操作栏下面的margin去掉改到name下面
            if (enpandKeyFlag == KEYBOARD_BUTTON) {
                int height = ((ConstraintLayout.LayoutParams) chooseImage.getLayoutParams()).bottomMargin;
                applyConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, 0);
                applyConstraintSet.setMargin(R.id.spart_line_4, ConstraintSet.BOTTOM, height + dip2px(getContext(), 8));
                enpandKeyFlag = KEYBOARD_NAME;
            }
            applyConstraintSet.applyTo(constraintLayout);
            nameText.requestFocus();
            isNameExpand = true;
        } else {
            TransitionManager.beginDelayedTransition(constraintLayout);

            applyConstraintSet.clone(constraintLayout);

            applyConstraintSet.setVisibility(R.id.spart_line_1, View.GONE);
            applyConstraintSet.setVisibility(R.id.name_text, View.GONE);
            applyConstraintSet.setVisibility(R.id.spart_line_2, View.GONE);
            applyConstraintSet.setVisibility(R.id.title_text, View.GONE);
            applyConstraintSet.setVisibility(R.id.spart_line_3, View.GONE);
            applyConstraintSet.setVisibility(R.id.email_text, View.GONE);
            applyConstraintSet.setVisibility(R.id.spart_line_4, View.GONE);

            //逆向操作一波
            if (enpandKeyFlag == KEYBOARD_NAME) {
                int height = ((ConstraintLayout.LayoutParams) partLine.getLayoutParams()).bottomMargin;
                applyConstraintSet.setMargin(R.id.spart_line_4, ConstraintSet.BOTTOM, 0);
                applyConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, height - dip2px(getContext(), 8));
                enpandKeyFlag = KEYBOARD_BUTTON;
            }

            applyConstraintSet.applyTo(constraintLayout);
            isNameExpand = false;
        }
    }

    PopupWindow popup;

    private void popupWindow() {

        popup = new PopupWindow(this.getActivity());
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);
        linearLayout.addView(genCookieView("cookie1", 1));
        linearLayout.addView(genCookieView("cookie2", 2));
        popup.setContentView(linearLayout);//设置显示内容
        popup.setOutsideTouchable(true);//点击PopupWindow以外的区域自动关闭该窗口
        popup.setBackgroundDrawable(new ColorDrawable(0));
        popup.showAsDropDown(cookie, 0, 0);//显示在edit控件的下面0,0代表偏移量
    }

    int cookieIndex;

    private TextView genCookieView(String s, int id) {
        TextView textView = new TextView(this.getContext());
        textView.setId(id + 1000);
        textView.setText(s);
        textView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                cookieIndex = v.getId() - 1000;
                popup.dismiss();
            }
        });
        return textView;
    }

    private int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
}
