package com.yanrou.dawnisland.reply

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.entities.Cookie
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ReplyDialog : DialogFragment() {
    private var chosedImage: ImageView? = null
    private var expandMore: ImageView? = null
    private var imageWillSend: File? = null
    private var contentText: EditText? = null
    private var nameText: EditText? = null
    private var titleText: EditText? = null
    private var emailText: EditText? = null
    private var seriesId: String? = null
    private var seriesIdTextView: TextView? = null
    private var cookie: TextView? = null
    private var chooseImage: ImageView? = null
    private var constraintLayout: ConstraintLayout? = null
    private val firstConstraintSet = ConstraintSet()
    private val fullScreenConstraintSet = ConstraintSet()

    private lateinit var viewModel: ReplyViewModel

    init {
        lifecycleScope.launchWhenCreated {
            viewModel = ViewModelProvider(this@ReplyDialog).get(ReplyViewModel::class.java)
        }
    }
    /**
     * 用来标记邮件面板是否展开、是否全屏
     */
    private var isNameExpand = false
    private var isFullScreen = false
    private var cookies: List<Cookie>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.BottomReplyDialog)

        val bundle = arguments!!

        seriesId = bundle.getString("seriesId")

        val provider = KeyboardHeightProvider(activity)

        provider.observer = object : KeyboardHeightObserver {
            override fun onKeyboardHeightChanged(height: Int) {
                changeMarginBottom(height)
            }
        }
        lifecycle.addObserver(provider)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_reply_first, container, false)
        val dialog = dialog!!
        val win = dialog.window
        val imageView = view.findViewById<ImageView>(R.id.send_reply)
        constraintLayout = view.findViewById(R.id.constrainLayout)
        contentText = view.findViewById(R.id.edit_content)
        nameText = view.findViewById(R.id.name_text)
        titleText = view.findViewById(R.id.title_text)
        emailText = view.findViewById(R.id.email_text)
        firstConstraintSet.clone(constraintLayout)
        fullScreenConstraintSet.clone(this.activity, R.layout.reply_dialog_full_screen)
        imageView.setOnClickListener { sendReply() }
        cookie = view.findViewById(R.id.cookie)
        cookie!!.setOnClickListener { popupWindow() }
        seriesIdTextView = view.findViewById(R.id.series_number)
        seriesIdTextView!!.text = seriesId
        chosedImage = view.findViewById(R.id.will_send_image)
        expandMore = view.findViewById(R.id.expand_more_button)
        expandMore!!.setOnClickListener {
            TransitionManager.beginDelayedTransition(constraintLayout!!)
            showMoreOrLess()
            if (isFullScreen) {
                fullScreenConstraintSet.applyTo(constraintLayout)
            } else {
                firstConstraintSet.applyTo(constraintLayout)
            }
            if (isNameExpand) {
                nameText!!.requestFocus()
            } else {
                contentText!!.requestFocus()
            }
        }
        chooseImage = view.findViewById(R.id.choose_image_button)
        chooseImage!!.setOnClickListener {
            if (context?.let { it1 -> ContextCompat.checkSelfPermission(it1, Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            } else {
                openAlbum()
            }
        }
        val fullScreen = view.findViewById<ImageView>(R.id.full_screen)
        fullScreen.setOnClickListener {
            TransitionManager.beginDelayedTransition(constraintLayout!!)
            isFullScreen = if (!isFullScreen) {
                fullScreenConstraintSet.applyTo(constraintLayout)
                contentText!!.maxLines = 65535
                true
            } else {
                firstConstraintSet.applyTo(constraintLayout)
                contentText!!.maxLines = 4
                false
            }
        }
        if (win != null) {
            win.decorView.setPadding(0, 0, 0, 0)
            val lp = win.attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
            lp.windowAnimations = R.style.Animation_Bottom
            win.attributes = lp
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            win.statusBarColor = Color.TRANSPARENT
        }

        viewModel.cookies.observe(viewLifecycleOwner, Observer {
            cookies = it
            if (cookies!!.isNotEmpty()) {
            cookie!!.text = cookies!![0].cookieName
            } else {
                cookie!!.text = "没有饼干"
            }
        })

        contentText!!.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (isNameExpand && hasFocus) {
                TransitionManager.beginDelayedTransition(constraintLayout!!)
                showMoreOrLess()
                if (isFullScreen) {
                    fullScreenConstraintSet.applyTo(constraintLayout)
                } else {
                    firstConstraintSet.applyTo(constraintLayout)
                }
                if (isNameExpand) {
                    nameText!!.requestFocus()
                } else {
                    contentText!!.requestFocus()
                }
            }
        }
        contentText!!.requestFocus()
        win!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return view
    }


    override fun onDestroy() {
        super.onDestroy()
        chosedImage = null
        expandMore = null
        contentText = null
        nameText = null
        titleText = null
        emailText = null
        seriesIdTextView = null
        cookie = null
        chooseImage = null
    }

    private fun changeMarginBottom(change: Int) {
        val autoTransition = AutoTransition()
        autoTransition.duration = 150
        TransitionManager.beginDelayedTransition(constraintLayout!!, autoTransition)
        firstConstraintSet.clone(constraintLayout)
        Log.d(TAG, "onGlobalLayout: $change")
        if (change < 0) {
            firstConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, -change)
            fullScreenConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, -change)
        } else if (change > 0) {
            firstConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, 0)
            fullScreenConstraintSet.setMargin(R.id.choose_image_button, ConstraintSet.BOTTOM, 0)
        }
        if (isFullScreen) {
            fullScreenConstraintSet.applyTo(constraintLayout)
        } else {
            firstConstraintSet.applyTo(constraintLayout)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: " + grantResults.contentToString())
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
                    .addFormDataPart("name", nameText!!.text.toString())
                    .addFormDataPart("title", titleText!!.text.toString())
                    .addFormDataPart("email", emailText!!.text.toString())
                    .addFormDataPart("content", contentText!!.text.toString())
                    .addFormDataPart("water", "true")
            if (imageWillSend != null) {
                val image: RequestBody = imageWillSend!!.asRequestBody(("image/" + imageWillSend!!.name.substring(imageWillSend!!.name.lastIndexOf(".") + 1)).toMediaTypeOrNull())
                builder.addFormDataPart("image", imageWillSend!!.name, image)
            }
            val requestBody = builder.build()
            viewModel.sendReply(requestBody)
        }
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
        val cursor = activity!!.contentResolver.query(uri!!, null, selection, null, null)
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
            chosedImage!!.visibility = View.VISIBLE
            chosedImage!!.setImageBitmap(BitmapFactory.decodeFile(imagePath))
        } else {
            Toast.makeText(context, "failed to get image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMoreOrLess() {
        isNameExpand = if (!isNameExpand) {
            firstConstraintSet.setVisibility(R.id.spart_line_1, View.VISIBLE)
            firstConstraintSet.setVisibility(R.id.name_text, View.VISIBLE)
            firstConstraintSet.setVisibility(R.id.spart_line_2, View.VISIBLE)
            firstConstraintSet.setVisibility(R.id.title_text, View.VISIBLE)
            firstConstraintSet.setVisibility(R.id.spart_line_3, View.VISIBLE)
            firstConstraintSet.setVisibility(R.id.email_text, View.VISIBLE)
            firstConstraintSet.setVisibility(R.id.spart_line_4, View.VISIBLE)
            fullScreenConstraintSet.setVisibility(R.id.spart_line_1, View.VISIBLE)
            fullScreenConstraintSet.setVisibility(R.id.name_text, View.VISIBLE)
            fullScreenConstraintSet.setVisibility(R.id.spart_line_2, View.VISIBLE)
            fullScreenConstraintSet.setVisibility(R.id.title_text, View.VISIBLE)
            fullScreenConstraintSet.setVisibility(R.id.spart_line_3, View.VISIBLE)
            fullScreenConstraintSet.setVisibility(R.id.email_text, View.VISIBLE)
            fullScreenConstraintSet.setVisibility(R.id.spart_line_4, View.VISIBLE)
            true
        } else {
            firstConstraintSet.setVisibility(R.id.spart_line_1, View.GONE)
            firstConstraintSet.setVisibility(R.id.name_text, View.GONE)
            firstConstraintSet.setVisibility(R.id.spart_line_2, View.GONE)
            firstConstraintSet.setVisibility(R.id.title_text, View.GONE)
            firstConstraintSet.setVisibility(R.id.spart_line_3, View.GONE)
            firstConstraintSet.setVisibility(R.id.email_text, View.GONE)
            firstConstraintSet.setVisibility(R.id.spart_line_4, View.GONE)
            fullScreenConstraintSet.setVisibility(R.id.spart_line_1, View.GONE)
            fullScreenConstraintSet.setVisibility(R.id.name_text, View.GONE)
            fullScreenConstraintSet.setVisibility(R.id.spart_line_2, View.GONE)
            fullScreenConstraintSet.setVisibility(R.id.title_text, View.GONE)
            fullScreenConstraintSet.setVisibility(R.id.spart_line_3, View.GONE)
            fullScreenConstraintSet.setVisibility(R.id.email_text, View.GONE)
            fullScreenConstraintSet.setVisibility(R.id.spart_line_4, View.GONE)
            false
        }
    }

    //TODO 这里是选择饼干，替换成 Xpopup会比较好 ，并且把数据放到 VM 里 ，让 Model 从数据库中读取数据
    private var popup: PopupWindow? = null
    private fun popupWindow() {
        popup = PopupWindow(this.activity)
        popup!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        popup!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        val linearLayout = LinearLayout(this.context)
        linearLayout.setBackgroundColor(Color.WHITE)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(20, 20, 20, 20)
        for (i in cookies!!.indices) {
            linearLayout.addView(genCookieView(cookies!![i].cookieName, i))
        }
        //设置显示内容
        popup!!.contentView = linearLayout
        //点击PopupWindow以外的区域自动关闭该窗口
        popup!!.isOutsideTouchable = true
        popup!!.setBackgroundDrawable(ColorDrawable(0))
        //显示在edit控件的下面0,0代表偏移量
        popup!!.showAsDropDown(cookie, 0, 0)
    }

    @SuppressLint("ResourceType")
    private fun genCookieView(s: String, id: Int): TextView {
        val textView = TextView(this.context)
        textView.id = id + 1000
        textView.text = s
        textView.setOnClickListener {
            cookie!!.text = textView.text
            popup!!.dismiss()
        }
        return textView
    }

    companion object {
        private val PERMISSIONS_STORAGE = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        private const val CHOOSE_PHOTO = 2
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private const val TAG = "ReplyDialog"
    }
}