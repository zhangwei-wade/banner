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

open class BannerAdapter<T>(private val data: MutableList<T>, private val layout: Int = R.layout.review_start_banner_item_layout) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    /**设置首尾循环*/
    var cycleSize = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val index = getRealPosition(position)
        loadImage(data[index], holder.bannerIv, index)
    }

    var loadImage: (T, View, Int) -> Unit = { _, _, _ -> }

    override fun getItemCount(): Int {
        return if (data.size > 1) data.size + cycleSize else data.size
    }

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var bannerIv: ImageView = view.findViewById(R.id.banner_iv)
    }

    fun getRealPosition(position: Int): Int {
        if (cycleSize == 0) {
            return position
        }
        return when (position) {
            0 -> {
                data.size - 1
            }
            data.size + 1 -> {
                0
            }
            else -> {
                position - 1
            }
        }
    }
}