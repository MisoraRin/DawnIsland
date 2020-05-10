package com.yanrou.dawnisland.io

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Size
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.yanrou.dawnisland.util.GlideApp

import timber.log.Timber
import java.io.*


object ImageUtil {
    fun addPlaceholderImageUriToGallery(
            callerActivity: Activity,
            fileName: String,
            fileExt: String,
            relativeLocation: String
    ): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "$fileName.$fileExt")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/$fileExt")

            // without this part causes "Failed to create new MediaStore record" exception to be invoked (uri is null below)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.ImageColumns.RELATIVE_PATH, relativeLocation)
            }
        }
        return callerActivity.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
        )
                ?: throw IOException("Failed to create new MediaStore record.")
    }

    fun removePlaceholderImageUriToGallery(
            callerActivity: Activity,
            uri: Uri
    ): Int {
        return callerActivity.contentResolver.delete(uri, null, null)
    }

    fun loadImageThumbnailToImageView(
            caller: Fragment,
            uri: Uri,
            width: Int,
            height: Int,
            imageView: ImageView
    ) {
        if (Build.VERSION.SDK_INT >= 29) {
            caller.requireActivity().contentResolver
                    .loadThumbnail(uri, Size(width, height), null)
                    .run { imageView.setImageBitmap(this) }
        } else {
            GlideApp.with(caller)
                    .load(uri).override(width, height).into(imageView)
        }
    }

    fun getImageFileFromUri(caller: Fragment, uri: Uri): File? {
        val parcelFileDescriptor =
                caller.requireActivity().contentResolver.openFileDescriptor(uri, "r", null)

        parcelFileDescriptor?.let {
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(
                    caller.requireContext().cacheDir,
                    caller.requireContext().contentResolver.getFileName(uri)
            )
            if (file.exists()) {
                Timber.i("File exists..Reusing the old file")
                return file
            }
            Timber.i("File not found. Making a new one...")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            return file
        }
        return null
    }

    fun getFileFromDrawable(caller: Fragment, fileName: String, resId: Int): File {
        val file = File(
                caller.requireContext().cacheDir,
                "$fileName.png"
        )
        if (file.exists()) {
            Timber.i("File exists..Reusing the old file")
            return file
        }
        Timber.i("File not found. Making a new one...")
        val inputStream: InputStream = caller.requireContext().resources.openRawResource(resId)

        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file
    }

    private fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }
}