package com.yanrou.dawnisland.reply;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.yanrou.dawnisland.R;
import com.yanrou.dawnisland.database.CookieData;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class ReplyDialog extends DialogFragment {
    private ImageView chosedImage;
    private ImageView expandMore;
    private File imageWillSend;
    private EditText contentText, nameText, titleText, emailText;
    private String seriesId;
    private TextView seriesIdTextView, cookie;
    private ImageView chooseImage;

    private ConstraintLayout constraintLayout;

    private ConstraintSet firstConstraintSet = new ConstraintSet();
    private ConstraintSet fullScreenConstraintSet = new ConstraintSet();

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int CHOOSE_PHOTO = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "ReplyDialog";
    /**
     * 用来标记邮件面板是否展开、是否全屏
     */
    private boolean isNameExpand = false, isFullScreen = false;
    private List<CookieData> cookies;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomReplyDialog);
        Bundle bundle = getArguments();
        assert bundle != null;
        seriesId = bundle.getString("seriesId");
        KeyboardHeightProvider provider = new KeyboardHeightProvider(getActivity());
        provider.setObserver(this::changeMarginBottom);
        getLifecycle().addObserver(provider);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView的开头");
        inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reply_first, container, false);

        Dialog dialog = getDialog();
        assert dialog != null;
        Window win = dialog.getWindow();

        ImageView imageView = view.findViewById(R.id.send_reply);
        constraintLayout = view.findViewById(R.id.constrainLayout);
        contentText = view.findViewById(R.id.edit_content);
        nameText = view.findViewById(R.id.name_text);
        titleText = view.findViewById(R.id.title_text);
        emailText = view.findViewById(R.id.email_text);

        firstConstraintSet.clone(constraintLayout);
        fullScreenConstraintSet.clone(this.getActivity(), R.layout.reply_dialog_full_screen);

        imageView.setOnClickListener(view14 -> sendReply());

        cookie = view.findViewById(R.id.cookie);
        cookie.setOnClickListener(v -> popupWindow());

        seriesIdTextView = view.findViewById(R.id.series_number);
        seriesIdTextView.setText(seriesId);

        chosedImage = view.findViewById(R.id.will_send_image);

        expandMore = view.findViewById(R.id.expand_more_button);
        expandMore.setOnClickListener(view12 -> {
            TransitionManager.beginDelayedTransition(constraintLayout);
            showMoreOrLess();
            if (isFullScreen) {
                fullScreenConstraintSet.applyTo(constraintLayout);
            } else {
                firstConstraintSet.applyTo(constraintLayout);
            }
            if (isNameExpand) {
                nameText.requestFocus();
            } else {
                contentText.requestFocus();
            }
        });


        chooseImage = view.findViewById(R.id.choose_image_button);
        chooseImage.setOnClickListener(view13 -> {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                openAlbum();
            }

        });

        ImageView fullScreen = view.findViewById(R.id.full_screen);
        fullScreen.setOnClickListener(view1 -> {
            TransitionManager.beginDelayedTransition(constraintLayout);
            if (!isFullScreen) {
                fullScreenConstraintSet.applyTo(constraintLayout);
                contentText.setMaxLines(65535);
                isFullScreen = true;
            } else {
                firstConstraintSet.applyTo(constraintLayout);
                contentText.setMaxLines(4);
                isFullScreen = false;
            }
        });

        if (!isFullScreen) {
            contentText.setMaxLines(4);
        } else {
            contentText.setMaxLines(65535);
        }

        if (win != null) {
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
            lp.windowAnimations = R.style.Animation_Bottom;
            win.setAttributes(lp);

            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(Color.TRANSPARENT);

        }
        cookies = LitePal.findAll(CookieData.class);
        if (cookies.size() > 0) {
            cookie.setText(cookies.get(0).getCookieName());
        } else {
            cookie.setText("没有饼干");
        }

        contentText.setOnFocusChangeListener((v, hasFocus) -> {
            if (isNameExpand && hasFocus) {
                TransitionManager.beginDelayedTransition(constraintLayout);
                showMoreOrLess();
                if (isFullScreen) {
                    fullScreenConstraintSet.applyTo(constraintLayout);
                } else {
                    firstConstraintSet.applyTo(constraintLayout);
                }
                if (isNameExpand) {
                    nameText.requestFocus();
                } else {
                    contentText.requestFocus();
                }
            }
        });
        contentText.requestFocus();
        assert win != null;
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        Log.d(TAG, "onCreateView的结尾");
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroy");
        chosedImage = null;
        expandMore = null;
        contentText = null;
        nameText = null;
        titleText = null;
        emailText = null;
        seriesIdTextView = null;
        cookie = null;
        chooseImage = null;
    }


    private void changeMarginBottom(int change) {
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(150);
        TransitionManager.beginDelayedTransition(constraintLayout, autoTransition);
        firstConstraintSet.clone(constraintLayout);
        Log.d(TAG, "onGlobalLayout: " + change);
        if (change < 0) {
            firstConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, -change);
            fullScreenConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, -change);
        } else if (change > 0) {
            firstConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, 0);
            fullScreenConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, 0);
        }
        if (isFullScreen) {
            fullScreenConstraintSet.applyTo(constraintLayout);
        } else {
            firstConstraintSet.applyTo(constraintLayout);
        }
    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: " + Arrays.toString(grantResults));
                openAlbum();
            } else {
                Toast.makeText(getContext(), "You denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendReply() {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("resto", seriesId)
                .addFormDataPart("name", nameText.getText().toString())
                .addFormDataPart("title", titleText.getText().toString())
                .addFormDataPart("email", emailText.getText().toString())
                .addFormDataPart("content", contentText.getText().toString())
                .addFormDataPart("water", "true");
        if (imageWillSend != null) {
            RequestBody image = RequestBody.create(imageWillSend, MediaType.parse("image/" + imageWillSend.getName().substring(imageWillSend.getName().lastIndexOf(".") + 1)));
            builder.addFormDataPart("image", imageWillSend.getName(), image);
        }

        RequestBody formBody = builder.build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        popup = null;
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
        if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                handleImageOnKitKat(data);
            }
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
                // 解析出数字格式的id
                String id = docId.split(":")[1];
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
            imageWillSend = new File(imagePath);
            chosedImage.setVisibility(View.VISIBLE);
            chosedImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        } else {
            Toast.makeText(getContext(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMoreOrLess() {
        if (!isNameExpand) {
            firstConstraintSet.setVisibility(R.id.spart_line_1, View.VISIBLE);
            firstConstraintSet.setVisibility(R.id.name_text, View.VISIBLE);
            firstConstraintSet.setVisibility(R.id.spart_line_2, View.VISIBLE);
            firstConstraintSet.setVisibility(R.id.title_text, View.VISIBLE);
            firstConstraintSet.setVisibility(R.id.spart_line_3, View.VISIBLE);
            firstConstraintSet.setVisibility(R.id.email_text, View.VISIBLE);
            firstConstraintSet.setVisibility(R.id.spart_line_4, View.VISIBLE);

            fullScreenConstraintSet.setVisibility(R.id.spart_line_1, View.VISIBLE);
            fullScreenConstraintSet.setVisibility(R.id.name_text, View.VISIBLE);
            fullScreenConstraintSet.setVisibility(R.id.spart_line_2, View.VISIBLE);
            fullScreenConstraintSet.setVisibility(R.id.title_text, View.VISIBLE);
            fullScreenConstraintSet.setVisibility(R.id.spart_line_3, View.VISIBLE);
            fullScreenConstraintSet.setVisibility(R.id.email_text, View.VISIBLE);
            fullScreenConstraintSet.setVisibility(R.id.spart_line_4, View.VISIBLE);

            isNameExpand = true;
        } else {
            firstConstraintSet.setVisibility(R.id.spart_line_1, View.GONE);
            firstConstraintSet.setVisibility(R.id.name_text, View.GONE);
            firstConstraintSet.setVisibility(R.id.spart_line_2, View.GONE);
            firstConstraintSet.setVisibility(R.id.title_text, View.GONE);
            firstConstraintSet.setVisibility(R.id.spart_line_3, View.GONE);
            firstConstraintSet.setVisibility(R.id.email_text, View.GONE);
            firstConstraintSet.setVisibility(R.id.spart_line_4, View.GONE);

            fullScreenConstraintSet.setVisibility(R.id.spart_line_1, View.GONE);
            fullScreenConstraintSet.setVisibility(R.id.name_text, View.GONE);
            fullScreenConstraintSet.setVisibility(R.id.spart_line_2, View.GONE);
            fullScreenConstraintSet.setVisibility(R.id.title_text, View.GONE);
            fullScreenConstraintSet.setVisibility(R.id.spart_line_3, View.GONE);
            fullScreenConstraintSet.setVisibility(R.id.email_text, View.GONE);
            fullScreenConstraintSet.setVisibility(R.id.spart_line_4, View.GONE);
            isNameExpand = false;
        }
    }

    private PopupWindow popup;

    private void popupWindow() {
        popup = new PopupWindow(this.getActivity());
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);
        for (int i = 0; i < cookies.size(); i++) {
            linearLayout.addView(genCookieView(cookies.get(i).getCookieName(), i));
        }
        //设置显示内容
        popup.setContentView(linearLayout);
        //点击PopupWindow以外的区域自动关闭该窗口
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new ColorDrawable(0));
        //显示在edit控件的下面0,0代表偏移量
        popup.showAsDropDown(cookie, 0, 0);
    }

    @SuppressLint("ResourceType")
    private TextView genCookieView(String s, int id) {
        TextView textView = new TextView(this.getContext());
        textView.setId(id + 1000);
        textView.setText(s);
        textView.setOnClickListener(v -> {
            cookie.setText(textView.getText());
            popup.dismiss();
        });
        return textView;
    }
}
