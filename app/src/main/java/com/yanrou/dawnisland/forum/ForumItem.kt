package com.yanrou.dawnisland.forum

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.json2class.ForumsBean

class ForumItem() : DslAdapterItem() {
    lateinit var forumsBean: ForumsBean
    lateinit var clickHandler: (fid: Int, fname: String) -> Unit

    init {
        itemLayoutId = R.layout.forum_list_item
    }

    override fun onItemBind(itemHolder: DslViewHolder, itemPosition: Int, adapterItem: DslAdapterItem) {
        itemHolder.apply {
            val forum = itemView.findViewById<TextView>(R.id.forum_name)
            val imageView = itemView.findViewById<ImageView>(R.id.icon)
            itemView.findViewById<View>(R.id.forum_layout)

            if (forumsBean.getShowName() != null && !forumsBean.getShowName().equals("")) {
                val displayName: Spanned = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Html.fromHtml(forumsBean.showName)
                } else {
                    Html.fromHtml(forumsBean.showName, Html.FROM_HTML_MODE_COMPACT)
                }
                forum.setText(displayName, TextView.BufferType.SPANNABLE)
            } else {
                forum.setText(forumsBean.name, TextView.BufferType.SPANNABLE)
            }

            // special handling for drawable resource ID, which cannot have -
            val biId = if (forumsBean.id > 0) forumsBean.getId() else 1
            val resourceId: Int = itemView.context.resources.getIdentifier("bi_$biId", "drawable",
                    itemView.context.getPackageName())
            if (resourceId != 0) {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(resourceId)
            } else {
                imageView.visibility = View.INVISIBLE
            }
            click(R.id.forum_layout) {
                clickHandler(forumsBean.id, forumsBean.name)
            }
        }
    }
}