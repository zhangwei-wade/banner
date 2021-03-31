package com.dzw.banner.view

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.viewpager2.widget.ViewPager2


/**
 * @author zhangwei on 2021/2/1.
 * viewpager banner 自动滚动
 */
class AutoScrollUtils(private val bannerView: BannerView) {
    /**是否停止自动滑动或者进入其它页面需要停止自动滚动*/
    var isStopScroll = false
    private val time: Long = bannerView.scrollIntervalTime
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val bannerRunnable: Runnable by lazy {
        Runnable {
            startAnimator(bannerView.viewPager2)
        }
    }


    //移动viewpager2
    private fun startAnimator(viewPager2: ViewPager2) {
        val sleepTime = 6
        val size = bannerView.autoScrollTime / sleepTime
        val rv = viewPager2.getChildAt(0)
        var width = if (bannerView.scrollOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            rv.width.toFloat() - rv.paddingLeft - rv.paddingLeft
        } else {
            rv.height.toFloat() - rv.paddingTop - rv.paddingBottom
        }
        if (bannerView.autoScrollDirector == 0) width = -width
        Thread {
            Handler(Looper.getMainLooper()).post { viewPager2.beginFakeDrag() }
            for (i in 1..size) {
//                Log.i("wade", "1241234231")
                if (isStopScroll) break
                Thread.sleep(sleepTime.toLong())
                Handler(Looper.getMainLooper()).post { viewPager2.fakeDragBy(width / size) }
            }
            if (!isStopScroll) mHandler.postDelayed(bannerRunnable, time)
            Handler(Looper.getMainLooper()).post { viewPager2.endFakeDrag() }
        }.start()
    }


    fun start(): AutoScrollUtils {
        isStopScroll = false
//        Log.i("AutoScrollUtils", "start")
        mHandler.removeCallbacks(bannerRunnable)
        mHandler.postDelayed(bannerRunnable, time)
        return this
    }

    fun stop() {
        isStopScroll = true
//        Log.i("AutoScrollUtils", "stop")
        mHandler.removeCallbacks(bannerRunnable)
    }
}
