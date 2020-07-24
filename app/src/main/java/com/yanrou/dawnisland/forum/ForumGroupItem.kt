package com.yanrou.dawnisland.forum

import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.yanrou.dawnisland.R
import com.yanrou.dawnisland.json2class.ForumJson


class ForumGroupItem : DslAdapterItem() {
    lateinit var forumJson: ForumJson

    init {
        itemLayoutId = R.layout.forum_group_item
        itemIsGroupHead = true
    }

    override fun onItemBind(itemHolder: DslViewHolder, itemPosition: Int, adapterItem: DslAdapterItem) {
        itemHolder.apply {
            itemView.findViewById<TextView>(R.id.group_name).apply { text = forumJson.name }
            val linearLayout = itemView.findViewById<View>(R.id.forum_layout)
            val arrow = itemView.findViewById<ImageView>(R.id.arrow).apply {
                rotation = if (itemGroupExtend) {
                    0f
                } else {
                    180f
                }
            }
            click(linearLayout) {
                itemGroupExtend = !itemGroupExtend
                if (itemGroupExtend) {
                    startPropertyAnim(arrow, 180f, 0f)
                } else {
                    startPropertyAnim(arrow, 0f, 180f)
                }
            }
        }
    }


    private fun startPropertyAnim(view: View, start: Float, end: Float) {
        // 第二个参数"rotation"表明要执行旋转
        // 0f -> 360f，从旋转360度，也可以是负值，负值即为逆时针旋转，正值是顺时针旋转。
        val anim = ObjectAnimator.ofFloat(view, "rotation", start, end)
        // 动画的持续时间，执行多久？
        anim.duration = 500
        // 回调监听
        anim.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
        }
        // 正式开始启动执行动画
        anim.start()
    }

}