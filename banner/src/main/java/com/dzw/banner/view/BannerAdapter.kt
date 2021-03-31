package com.dzw.banner.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dzw.banner.R


/**
 * @author zhangwei on 2020/12/22.
 * 引导页图片切换
 */

open class BannerAdapter<T>(private val bannerView: BannerView, private val data: MutableList<T>, private val layout: Int = R.layout.review_start_banner_item_layout) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val index = position % data.size
        loadImage(data[index], holder.bannerIv, index)
//        val index = position % data.size
//        GlideUtils.loadImage(holder.itemView.context, data[index].imgUrl, holder.bannerIv)
//        holder.bannerIv.singleClick {
//            RouteUtils.toBannerJumpActivity(data[index])
//        }
    }

    var loadImage: (T, View, Int) -> Unit = { _, _, _ -> }

    override fun getItemCount(): Int {
        return if (data.size > 1 && bannerView.isAutoScroll) Int.MAX_VALUE else data.size
    }

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var bannerIv: ImageView = view.findViewById(R.id.banner_iv)
    }

}