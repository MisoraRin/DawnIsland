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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.yanrou.dawnisland.ListViewAdaptWidth
import com.yanrou.dawnisland.R
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
    private val autoTransition = AutoTransition()


    private lateinit var viewModel: ReplyViewModel

    init {
        autoTransition.duration = 150
        lifecycleScope.launchWhenCreated {
            viewModel = ViewModelProvider(this@ReplyDialog.requireActivity()).get(ReplyViewModel::class.java)
        }
    }
    /**
     * 用来标记邮件面板是否展开、是否全屏
     */
    private var isNameExpand = false
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
            TransitionManager.beginDelayedTransition(constraintLayout!!, autoTransition)
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
            TransitionManager.beginDelayedTransition(constraintLayout!!, autoTransition)
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

        viewModel.switchedCookie.observe(viewLifecycleOwner, Observer {
            cookie!!.text = it
        })

        contentText!!.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (isNameExpand && hasFocus) {
                TransitionManager.beginDelayedTransition(constraintLayout!!, autoTransition)
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
        constraintLayout!!.isClickable = true
        getDialog()!!.window!!.decorView.setOnTouchListener { _, event ->
            requireActivity().dispatchTouchEvent(event)
            false
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

        TransitionManager.beginDelayedTransition(constraintLayout!!, autoTransition)
        firstConstraintSet.clone(constraintLayout)
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
    }
}