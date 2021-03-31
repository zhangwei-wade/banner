package com.dzw.banner.view.listener

import android.view.View

/**
 * @author zhangwei on 2021/3/26.
 * 自定义适配器界面
 * 加载滚动图，以及点击图片的点击事件
 */
interface AdapterCustomViewListener<T> {
    fun loadImage(data: T, view: View, position: Int)

    fun onItemClick(data: T, position: Int)
}