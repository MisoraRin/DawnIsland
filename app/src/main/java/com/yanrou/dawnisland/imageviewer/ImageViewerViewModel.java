package com.yanrou.dawnisland.imageviewer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import static org.litepal.LitePalApplication.getContext;

/**
 * @author Rick
 */
public class ImageViewerViewModel extends ViewModel {
  private String TAG = "ImageViewerViewModel";

  public ImageViewerViewModel(){}

  public void loadImage(PhotoView photoView, String imgUrl){
    Log.d(TAG, "Downloading image at " + imgUrl);
    Glide.with(getContext()).load(imgUrl).into(photoView);
  }
  public void addPicToGallery(String imgUrl){
    new ImageDownloaderTask().execute(imgUrl);
  }

  private class ImageDownloaderTask extends AsyncTask<String, Void, Void>{
    @Override
    protected Void doInBackground(String... imgUrls) {
      String imgUrl = imgUrls[0];
      Log.d(TAG, "Saving image to Gallery... " );
      String relativeLocation = Environment.DIRECTORY_PICTURES+ File.separator+"Dawn";
      String name = imgUrl.substring(imgUrl.lastIndexOf("/")+1, imgUrl.lastIndexOf("."));
      String ext = imgUrl.substring(imgUrl.lastIndexOf(".")+1);
      OutputStream stream = null;
      Uri uri = null;
      try{
        ContentResolver resolver = getContext().getContentResolver();

        ContentValues newImageDetails = new ContentValues();
        newImageDetails.put(MediaStore.MediaColumns.DISPLAY_NAME, name + '.' + ext);
        newImageDetails.put(MediaStore.MediaColumns.MIME_TYPE, "image/"+ext);
        newImageDetails.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);

        final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Bitmap image = Glide.with(getContext()).asBitmap().load(imgUrl).submit().get();
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;

        uri = resolver.insert(contentUri, newImageDetails);
        if (uri == null){
          throw new IOException("Failed to create new MediaStore record.");
        }
        stream = resolver.openOutputStream(uri);

        if (stream == null){
          throw new IOException("Failed to get output stream.");
        }

        image.compress(format, 100, stream);
        Log.d(TAG, "Image saved");
      } catch (IOException | ExecutionException | InterruptedException e) {
        // currently omit exceptions
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      Toast.makeText(getContext(),"Image saved in Pictures/Dawn", Toast.LENGTH_SHORT).show();
    }
  }

}
