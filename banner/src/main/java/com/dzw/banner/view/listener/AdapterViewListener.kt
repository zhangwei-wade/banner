package com.dzw.banner.view.listener

import android.widget.ImageView

/**
 * @author zhangwei on 2021/3/26.
 * 加载滚动图，以及点击图片的点击事件
 */
interface AdapterViewListener<T> {
    fun loadImage(data: T, imageView: ImageView, position: Int)

    fun onItemClick(data: T, position: Int)
}