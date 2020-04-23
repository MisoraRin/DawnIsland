package com.yanrou.dawnisland.feed

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.drakeet.multitype.ViewDelegate
import com.yanrou.dawnisland.content.SeriesContentActivity
import com.yanrou.dawnisland.json2class.FeedJson
import com.yanrou.dawnisland.serieslist.CardViewFactory
import com.yanrou.dawnisland.serieslist.CardViewFactory.MyCardView

/**
 * @author suche
 */
class FeedItemViewBinder(private val mContext: Context, private val viewModel: FeedViewModel) : ViewDelegate<FeedJson, MyCardView>() {
    override fun onBindView(myCardView: MyCardView, feedJson: FeedJson) {
        myCardView.forum = "收藏夹"
        myCardView.id = feedJson.id
        myCardView.contentView.text = feedJson.content
        myCardView.cookieView.text = feedJson.userid
        if (feedJson.ext != null && feedJson.ext != "") {
            myCardView.imageContent.visibility = View.VISIBLE
            Glide.with(myCardView.context)
                    .load("https://nmbimg.fastmirror.org/thumb/" + feedJson.img + feedJson.img)
                    .override(250, 250)
                    .into(myCardView.imageContent)
        } else {
            myCardView.imageContent.visibility = View.GONE
        }
    }

    override fun onCreateView(context: Context): MyCardView {
        Log.d(TAG, "onBindView: 创建对象")
        val seriesCardView = CardViewFactory.getInstance(context).getSeriesCardView(context)
        seriesCardView.setOnClickListener { v: View? ->
            val intent = Intent(context, SeriesContentActivity::class.java)
            intent.putExtra("id", seriesCardView.id)
            intent.putExtra("forumTextView", seriesCardView.forum)
            context.startActivity(intent)
        }
        return seriesCardView
    }

    companion object {
        private const val TAG = "FeedItemViewBinder"
    }

}