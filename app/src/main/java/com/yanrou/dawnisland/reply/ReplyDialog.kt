package com.yanrou.dawnisland.reply

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.yanrou.dawnisland.ListViewAdaptWidth
import com.yanrou.dawnisland.R
import kotlinx.android.synthetic.main.dialog_reply.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ReplyDialog : DialogFragment() {
    private var imageWillSend: File? = null

    private var seriesId: String? = null

    private var firstConstraintSet = ConstraintSet()

    private lateinit var viewModel: ReplyViewModel

    init {
        lifecycleScope.launchWhenCreated {
            viewModel = ViewModelProvider(this@ReplyDialog.requireActivity()).get(ReplyViewModel::class.java)
        }
    }

    /**
     * 用来标记邮件面板是否展开、是否全屏
     */
    private var isInfoExpand = false
    private var isFullScreen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BottomReplyDialog)
        val bundle = requireArguments()
        seriesId = bundle.getString("seriesId")
        val provider = KeyboardHeightProvider(activity)
        provider.observer = object : KeyboardHeightObserver {
            override fun onKeyboardHeightChanged(height: Int) {
                changeMarginBottom(height)
            }
        }
        lifecycle.addObserver(provider)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_reply, container, false)
        val dialog = dialog!!
        val win = dialog.window
        val imageView = view.findViewById<ImageView>(R.id.send_reply)

        imageView.setOnClickListener { sendReply() }
        win?.let {
            it.decorView.setPadding(0, 0, 0, 0)
            it.attributes = it.attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
                softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                windowAnimations = R.style.Animation_Bottom
            }
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.statusBarColor = Color.TRANSPARENT
        }
        win!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    private fun changeMarginBottom(change: Int) {

        TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
        firstConstraintSet.clone(constraintLayout)
        if (change < 0) {
            firstConstraintSet.setMargin(R.id.tabLayout, ConstraintSet.BOTTOM, -change)
        } else if (change > 0) {
            firstConstraintSet.setMargin(R.id.tabLayout, ConstraintSet.BOTTOM, 0)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum()
            } else {
                Toast.makeText(context, "You denied the permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendReply() {
        lifecycleScope.launch {
            val builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("resto", seriesId!!)
                    .addFormDataPart("name", name_text.text.toString())
                    .addFormDataPart("title", title_text.text.toString())
                    .addFormDataPart("email", email_text.text.toString())
                    .addFormDataPart("content", edit_content.text.toString())
                    .addFormDataPart("water", "true")
            if (imageWillSend != null) {
                val image: RequestBody = imageWillSend!!.asRequestBody(("image/" + imageWillSend!!.name.substring(imageWillSend!!.name.lastIndexOf(".") + 1)).toMediaTypeOrNull())
                builder.addFormDataPart("image", imageWillSend!!.name, image)
            }
            val requestBody = builder.build()
            viewModel.sendReply(requestBody)
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        popup = null
    }

    //TODO 下面一堆方法都是选择图片相关，不知道能不能分到其他地方
    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        // 打开相册
        startActivityForResult(intent, CHOOSE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                handleImageOnKitKat(data)
            }
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        Log.d("TAG", "handleImageOnKitKat: uri is $uri")
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                // 解析出数字格式的id
                val id = docId.split(":").toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.path
        }
        // 根据图片路径显示图片
        displayImage(imagePath)
    }

    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        // 通过Uri和selection来获取真实的图片路径
        val cursor = requireActivity().contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            imageWillSend = File(imagePath)
            will_send_image.visibility = View.VISIBLE
            will_send_image.setImageBitmap(BitmapFactory.decodeFile(imagePath))
        } else {
            Toast.makeText(context, "failed to get image", Toast.LENGTH_SHORT).show()
        }
    }

    private var popup: PopupWindow? = null
    private fun popupWindow() {
        val view: View = LayoutInflater.from(context).inflate(R.layout.switch_cookie_list, null)
        val list: ListViewAdaptWidth = view.findViewById(R.id.cookie_list)
        popup = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        list.adapter = ArrayAdapter(requireContext(), R.layout.switch_cookie_in_popup, viewModel.getCookieNameList())
        list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ -> viewModel.switchCookie(position) }
        list.emptyView = view.findViewById(android.R.id.empty);
        popup!!.elevation = 20f
        popup!!.isOutsideTouchable = true
        popup!!.setBackgroundDrawable(BitmapDrawable())
        popup!!.showAsDropDown(cookie)
    }

    companion object {
        private val PERMISSIONS_STORAGE = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        private const val CHOOSE_PHOTO = 2
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val autoTransition = AutoTransition().apply { duration = 300 }
    }
}