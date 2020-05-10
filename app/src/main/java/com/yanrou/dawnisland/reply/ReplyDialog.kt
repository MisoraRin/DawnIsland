package com.yanrou.dawnisland.reply

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.yanrou.dawnisland.Fid2Name
import com.yanrou.dawnisland.ListViewAdaptWidth
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.constant.Emoji
import com.yanrou.dawnisland.constant.TYPE_KEY
import com.yanrou.dawnisland.constant.TYPE_NEW
import com.yanrou.dawnisland.constant.TYPE_REPLY
import com.yanrou.dawnisland.io.FragmentIntentUtil
import com.yanrou.dawnisland.io.ImageUtil
import kotlinx.android.synthetic.main.dialog_reply.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File


class ReplyDialog : DialogFragment() {
    private var imageWillSend: File? = null

    private var type = -1

    private lateinit var targetId: String

    private var firstConstraintSet = ConstraintSet()

    private lateinit var viewModel: ReplyViewModel

    //记录键盘高度
    private var keyboardHeight = 0

    //当这个值为真的时候，ui将自动跟随键盘
    private var autoKeyboardHeight = true

    //用来表示是否为键盘留下了空白，如果为true则表示预留了键盘高度
    private var hasKeyboardSpace = false

    //用来标记邮件面板是否展开
    private var isInfoExpand = false

    //标记是否全屏
    private var isFullScreen = false

    //标记软件盘状态
    private var isSoftInputOpen = false

    init {
        lifecycleScope.launchWhenCreated {
            viewModel = ViewModelProvider(this@ReplyDialog.requireActivity()).get(ReplyViewModel::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BottomReplyDialog)

        val bundle = requireArguments()
        type = bundle.getInt(TYPE_KEY)
        if (type == TYPE_REPLY) {
            targetId = bundle.getString("seriesId")!!
        } else if (type == TYPE_REPLY) {
            targetId = bundle.getString("forumId")!!
        }

        val provider = KeyboardHeightProvider(activity)
        provider.observer = object : KeyboardHeightObserver {
            override fun onKeyboardHeightChanged(height: Int) {
                keyboardChangeHandle(height)
            }
        }
        lifecycle.addObserver(provider)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //点击事件设置
        full_screen.setOnClickListener { fullScreenSwitch() }

        expand_more_button.setOnClickListener { changeInfoSpanState() }

        emoji_keyboard.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = EmojiKeyboardAdapter {
                edit_content.text.insert(edit_content.selectionStart, Emoji.EMOJI_VALUE[it])
            }
        }
        fun toggleExpandContainer() {
            when {
                hasKeyboardSpace && isSoftInputOpen -> {
                    autoKeyboardHeight = false
                    hideKeyboardFrom(requireContext(), edit_content)
                }
                hasKeyboardSpace && !isSoftInputOpen -> {
                    removeMarginBottom()
                }
                !hasKeyboardSpace -> {
                    autoKeyboardHeight = false
                    addMarginBottom()
                }
            }
        }
        open_emoji_button.setOnClickListener {
            toggleExpandContainer()
            //如果有其他view的话，应该隐藏其他view，然后显示这一个
        }
        // TODO 需要修一下，权限检测有一点问题
        choose_image_button.setOnClickListener {
            if (!FragmentIntentUtil.checkReadStoragePermission(this)) {
                return@setOnClickListener
            }
            FragmentIntentUtil.getImageFromGallery(this, "image/*") { uri: Uri? ->
                if (uri != null) {
                    imageWillSend = ImageUtil.getImageFileFromUri(this, uri)
                    try {
                        ImageUtil.loadImageThumbnailToImageView(
                                this,
                                uri,
                                150,
                                150,
                                will_send_image
                        )
//                        attachmentContainer!!.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        Timber.e(e, "Cannot load thumbnail from image...")
                    }
                }
            }
        }
        send_reply.setOnClickListener { sendReply() }

        state_text_view.text = when (type) {
            TYPE_REPLY -> requireContext().getString(R.string.post_title_reply_state)
            TYPE_NEW -> requireContext().getString(R.string.post_title_new_thread_state)
            else -> throw IllegalStateException("遇到了意外的状态，请检查传入的type参数")
        }

        series_number.text = when (type) {
            TYPE_REPLY -> targetId
            TYPE_NEW -> Fid2Name.db.value!![targetId.toInt()]
            else -> throw IllegalStateException("遇到了意外的状态，请检查传入的type参数")
        }
        //下面两行是默认弹出键盘
        edit_content.requestFocus()
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_reply, container, false)
        val win = dialog!!.window
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
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    //切换信息面板展开和收缩
    private fun changeInfoSpanState() {
        TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
        firstConstraintSet.clone(constraintLayout)
        if (isInfoExpand) {
            firstConstraintSet.setVisibility(R.id.feifei_info_container, View.GONE)
            isInfoExpand = false
        } else {
            firstConstraintSet.setVisibility(R.id.feifei_info_container, View.VISIBLE)
            isInfoExpand = true
        }
        firstConstraintSet.applyTo(constraintLayout)
    }

    private fun changeInfoSpanState(expand: Boolean) {
        TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
        firstConstraintSet.clone(constraintLayout)
        if (!expand) {
            firstConstraintSet.setVisibility(R.id.feifei_info_container, View.GONE)
            isInfoExpand = false
        } else {
            firstConstraintSet.setVisibility(R.id.feifei_info_container, View.VISIBLE)
            isInfoExpand = true
        }
        firstConstraintSet.applyTo(constraintLayout)
    }

    private fun addMarginBottom() {
        TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
        firstConstraintSet.clone(constraintLayout)
        firstConstraintSet.setVisibility(R.id.expand_container, View.VISIBLE)
        firstConstraintSet.setMargin(R.id.tabLayout, ConstraintSet.BOTTOM, keyboardHeight)
        firstConstraintSet.applyTo(constraintLayout)
        hasKeyboardSpace = true
        autoKeyboardHeight = hasKeyboardSpace == isSoftInputOpen
    }

    private fun removeMarginBottom() {
        TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
        firstConstraintSet.clone(constraintLayout)
        firstConstraintSet.setVisibility(R.id.expand_container, View.GONE)
        firstConstraintSet.setMargin(R.id.tabLayout, ConstraintSet.BOTTOM, 0)
        firstConstraintSet.applyTo(constraintLayout)
        hasKeyboardSpace = false
        autoKeyboardHeight = hasKeyboardSpace == isSoftInputOpen
    }

    private fun keyboardChangeHandle(change: Int) {
        initKeyboardHeight(change)
        isSoftInputOpen = (change < 0)
        if (autoKeyboardHeight) {
            TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
            firstConstraintSet.clone(constraintLayout)
            if (change < 0) {
                addMarginBottom()
//                firstConstraintSet.setMargin(R.id.tabLayout, ConstraintSet.BOTTOM, -change)
//                hasKeyboardSpace = true
            } else if (change > 0) {
                removeMarginBottom()
//                firstConstraintSet.setMargin(R.id.tabLayout, ConstraintSet.BOTTOM, 0)
//                hasKeyboardSpace = false
            }
            firstConstraintSet.applyTo(constraintLayout)
        }
        autoKeyboardHeight = hasKeyboardSpace == isSoftInputOpen
    }

    fun initKeyboardHeight(height: Int) {
        if (keyboardHeight == 0) {
            keyboardHeight = -height
        }
    }

    private fun fullScreenSwitch() {
        Timber.d("执行")
        TransitionManager.beginDelayedTransition(constraintLayout, autoTransition)
        firstConstraintSet.clone(constraintLayout)
        if (isFullScreen) {
            firstConstraintSet.clear(R.id.inputLayout, ConstraintSet.TOP)
            isFullScreen = false
        } else {
            firstConstraintSet.connect(R.id.inputLayout, ConstraintSet.TOP, R.id.constraintLayout, ConstraintSet.TOP)
            isFullScreen = true
        }
        firstConstraintSet.applyTo(constraintLayout)
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

    private fun hideKeyboardFrom(context: Context, view: View) {
        (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)

    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun sendReply() {
        lifecycleScope.launch {
            val builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(if (type == TYPE_REPLY) {
                        "resto"
                    } else {
                        "fid"
                    }, targetId)
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
        private val autoTransition = AutoTransition().apply {
            duration = 150
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
}