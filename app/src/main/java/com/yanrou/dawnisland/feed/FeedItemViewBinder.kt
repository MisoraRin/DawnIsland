package com.yanrou.dawnisland.feed

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.drakeet.multitype.ViewDelegate
import com.yanrou.dawnisland.json2class.FeedJson
import com.yanrou.dawnisland.serieslist.CardViewFactory
import com.yanrou.dawnisland.serieslist.CardViewFactory.MyCardView

/**
 * @author suche
 */
class FeedItemViewBinder(private val clickListener: View.OnClickListener,
                         private val longClickListener: View.OnLongClickListener) : ViewDelegate<FeedJson, MyCardView>() {
    override fun onBindView(view: MyCardView, item: FeedJson) {
        view.forum = "收藏夹"
        view.id = item.id
        view.contentView.text = item.content
        view.cookieView.text = item.userid
        if (item.ext != null && item.ext != "") {
            view.imageContent.visibility = View.VISIBLE
            Glide.with(view.context)
                    .load("https://nmbimg.fastmirror.org/thumb/" + item.img + item.img)
                    .override(250, 250)
                    .into(view.imageContent)
        } else {
            view.imageContent.visibility = View.GONE
        }
        view.setOnClickListener(clickListener)
        view.setOnLongClickListener(longClickListener)
    }

    override fun onCreateView(context: Context): MyCardView {
        return CardViewFactory.getInstance(context).getSeriesCardView(context)
    }

    companion object {
        private const val TAG = "FeedItemViewBinder"
    }

}