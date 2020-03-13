package com.yanrou.dawnisland;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static org.litepal.LitePalApplication.getContext;

/**
 * @author Rick
 */
public class ImageViewer extends AppCompatActivity {
  private static final String TAG = "ImageViewerActivity";
  Toolbar toolbar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_viewer);

    String imgUrl = getIntent().getStringExtra("imgurl");
    Log.d(TAG, "Downloading image at "+imgUrl);

    setupToolbar(imgUrl);


    // load image in Full Screen
    PhotoView photoView = findViewById(R.id.photo_view);
    Glide.with(getContext()).load(imgUrl).into(photoView);

  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  // TODO: enable image collections(within same thread) view
  private void setupToolbar(String imgUrl){
    toolbar = findViewById(R.id.content_toolbar);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    ImageButton saveButton = findViewById(R.id.save_button);


    saveButton.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        saveImageOnNewThread(imgUrl);
        Toast.makeText(getContext(), "Saving image", Toast.LENGTH_SHORT).show();
        return true;
      }

    });


  }


  private Boolean saveImage(String imgUrl) {
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    if (!storageDir.exists()) {
      Boolean success = storageDir.mkdirs();
      if (!success){
        Log.d(TAG, "Failed to create picture directory");
        return false;
      }
    }

    if (storageDir.exists()) {
      // TODO: can add duplicate check to avoid re-downloads(but filename can have duplicates)
      String name = imgUrl.substring(imgUrl.lastIndexOf("/")+1, imgUrl.lastIndexOf("."));
      String ext = imgUrl.substring(imgUrl.lastIndexOf(".")+1);

      String imageFileName = name + '.' + ext;
      File imageFile = new File(storageDir, imageFileName);
      String savedImagePath = imageFile.getAbsolutePath();

      Log.d(TAG, "Saving image " + imageFileName);
      try {
        OutputStream fOut = new FileOutputStream(imageFile);
        Bitmap image = Glide.with(getContext()).asBitmap().load(imgUrl).submit().get();
        Log.d(TAG, "Image has width: "+String.valueOf(image.getWidth()) + " and height "+String.valueOf(image.getHeight()) );

        switch (ext.toLowerCase()){
          case "png":
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            break;
            // TODO: gif require further handling
          case "gif":
            //            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            return false;

          default:
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            break;
        }

        fOut.close();

        Log.d(TAG, "Image saved at " + storageDir.getAbsolutePath());

        // TODO: Add the image to the system gallery

        // Publish
//        ContentResolver resolver = getApplicationContext().getContentResolver();
//        ContentValues newImageDetails = new ContentValues();
//        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, name);
//        newImageDetails.put(MediaStore.Images.ImageColumns.DATA, savedImagePath);
//
//        Uri imageCollection = MediaStore.Images.Media.getContentUri(
//            MediaStore.VOLUME_EXTERNAL);
//        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newImageDetails);






        return true;
      } catch (Exception e) {
        Log.d(TAG,"error on save");
        e.printStackTrace();
      }
    }

    return false;
  }


  private void saveImageOnNewThread(String imgUrl){
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        saveImage(imgUrl);
      }
    });

    t.start();
  }
}
