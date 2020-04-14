package com.yanrou.dawnisland.mrecyclerview

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * PinnedHeader对应的ItemDecoration
 */
class PinnedHeaderItemDecoration : ItemDecoration(), IPinnedHeaderDecoration {
    override var pinnedHeaderRect: Rect? = null
        private set
    override var pinnedHeaderPosition = -1
        private set

    /**
     * 把要固定的View绘制在上层
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        //确保是PinnedHeaderAdapter的adapter,确保有View
        if (parent.adapter is PinnedHeaderAdapter && parent.childCount > 0) {
            val adapter = parent.adapter as PinnedHeaderAdapter
            //找到要固定的pin view
            //获取屏幕上显示的第一个view
            val firstView = parent.getChildAt(0)
            //获取该view在整个列表中的位置
            val firstAdapterPosition = parent.getChildAdapterPosition(firstView)
            //从这个view遍历到第一个view，如果有应该作为头布局的view，就返回它的index
            val pinnedHeaderPosition = getPinnedHeaderViewPosition(firstAdapterPosition, adapter)
            this.pinnedHeaderPosition = pinnedHeaderPosition
            //如果有需要固定到头部
            if (pinnedHeaderPosition != -1) {
                //创建该View
                val pinnedHeaderViewHolder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(pinnedHeaderPosition))
                adapter.onBindViewHolder(pinnedHeaderViewHolder, pinnedHeaderPosition)
                //要固定的view
                val pinnedHeaderView = pinnedHeaderViewHolder.itemView
                ensurePinnedHeaderViewLayout(pinnedHeaderView, parent)
                var sectionPinOffset = 0
                //这里是用来计算偏移量的
                for (index in 0 until parent.childCount) {
                    if (adapter.isPinnedPosition(parent.getChildAdapterPosition(parent.getChildAt(index)))) {
                        val sectionView = parent.getChildAt(index)
                        val sectionTop = sectionView.top
                        val pinViewHeight = pinnedHeaderView.height
                        if (sectionTop in 1 until pinViewHeight) {
                            sectionPinOffset = sectionTop - pinViewHeight
                        }
                    }
                }
                val saveCount = c.save()
                val layoutParams = pinnedHeaderView.layoutParams as RecyclerView.LayoutParams
                c.translate(layoutParams.leftMargin.toFloat(), sectionPinOffset.toFloat())
                c.clipRect(0, 0, parent.width, pinnedHeaderView.measuredHeight)
                pinnedHeaderView.draw(c)
                c.restoreToCount(saveCount)
                if (pinnedHeaderRect == null) {
                    pinnedHeaderRect = Rect()
                }
                pinnedHeaderRect!![0, 0, parent.width] = pinnedHeaderView.measuredHeight + sectionPinOffset
            } else {
                pinnedHeaderRect = null
            }
        }
    }

    /**
     * 要给每个item设置间距主要靠这个函数来实现
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {}

    /**
     * 根据第一个可见的adapter的位置去获取临近的一个要固定的position的位置
     *
     * @param adapterFirstVisible 第一个可见的adapter的位置
     * @return -1：未找到 >=0 找到位置
     */
    private fun getPinnedHeaderViewPosition(adapterFirstVisible: Int, adapter: PinnedHeaderAdapter): Int {
        for (index in adapterFirstVisible downTo 0) {
            if (adapter.isPinnedPosition(index)) {
                return index
            }
        }
        return -1
    }

    private fun ensurePinnedHeaderViewLayout(pinView: View, recyclerView: RecyclerView) {
        if (pinView.isLayoutRequested) {
            /**
             * 用的是RecyclerView的宽度测量，和RecyclerView的宽度一样
             */
            val layoutParams = pinView.layoutParams as RecyclerView.LayoutParams
            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                    recyclerView.measuredWidth - layoutParams.leftMargin - layoutParams.rightMargin, View.MeasureSpec.EXACTLY)
            val heightSpec: Int
            heightSpec = if (layoutParams.height > 0) {
                View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY)
            } else {
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            }
            pinView.measure(widthSpec, heightSpec)
            pinView.layout(0, 0, pinView.measuredWidth, pinView.measuredHeight)
        }
    }

}