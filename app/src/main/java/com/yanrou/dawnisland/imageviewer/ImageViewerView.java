package com.yanrou.dawnisland.imageviewer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;
import com.yanrou.dawnisland.R;

/**
 * @author Rick
 */
public class ImageViewerView extends AppCompatActivity {
  private static final String TAG = "ImageViewerView";
  Toolbar toolbar;
  private ImageViewerViewModel viewModel;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_viewer);
    viewModel = new ImageViewerViewModel();

    String imgUrl = getIntent().getStringExtra("imgurl");

    setupToolbar(imgUrl);

    // load image in Full Screen
    PhotoView photoView = findViewById(R.id.photo_view);
    viewModel.loadImage(this, photoView, imgUrl);

  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  // TODO: enable image collections(within same thread) view
  private void setupToolbar(String imgUrl) {
      toolbar = findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    ImageButton saveButton = findViewById(R.id.save_button);

    saveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        viewModel.addPicToGallery(getBaseContext(), imgUrl);
      }
    });
  }
}

