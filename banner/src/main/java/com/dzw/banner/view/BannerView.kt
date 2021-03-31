package com.dzw.banner.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.*
import com.dzw.banner.R
import com.dzw.banner.view.listener.AdapterCustomViewListener
import com.dzw.banner.view.listener.AdapterViewListener
import com.dzw.banner.view.listener.IBaseLifecycle
import com.dzw.banner.view.listener.PagerChangeListener
import java.util.*

/**
 * @author zhangwei on 2021/3/25.
 */
open class BannerView : FrameLayout, IBaseLifecycle {
    private var adapterLayoutRes: Int? = null
    lateinit var viewPager2: ViewPager2
    private lateinit var dotLayout: LinearLayout
    private var mDots: ArrayList<ImageView> = arrayListOf()
    private var autoScrollUtils: AutoScrollUtils? = null

    //滚动间隔时间
    var scrollIntervalTime: Long = 3500

    //自动滚动时间
    var autoScrollTime: Long = 400

    //是否自动滚动
    var isAutoScroll: Boolean = false

    //设置自动滚动方向 0往左滚动  1往右滚动
    var autoScrollDirector = 0

    //数据真实个数
    var realSize: Int = 0

    /*设置指示器的间距单位dp*/
    private var indicatorPadding = 0

    //指示器图片 未选择中颜色 选中颜色 背景颜色
    var indicatorDotArray = arrayOf(R.drawable.review_dot_unfocused, R.drawable.review_dot_focused, R.drawable.review_dot_bg)

    private var bannerAdapter: BannerAdapter<*>? = null

    /**是否隐藏指示器*/
    private var indicatorVisibility = VISIBLE


    /**设置滚动的方向*/
    var scrollOrientation = ORIENTATION_HORIZONTAL

    /**是否禁止手动滑动 true 可以滑动 false 不能手动滑动*/
    private var fingerSwipeEnable = true

    var pagerChangeListener: PagerChangeListener? = null


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val types = context.obtainStyledAttributes(attrs, R.styleable.BannerView)
        isAutoScroll = types.getBoolean(R.styleable.BannerView_isAutoScroll, false)
        autoScrollTime = types.getInt(R.styleable.BannerView_autoScrollTime, 400).toLong()
        scrollOrientation = types.getInt(R.styleable.BannerView_scrollOrientation, ORIENTATION_HORIZONTAL)
        autoScrollDirector = types.getInt(R.styleable.BannerView_autoScrollDirector, 0)
        fingerSwipeEnable = types.getBoolean(R.styleable.BannerView_fingerSwipeEnable, true)
        scrollIntervalTime = types.getInt(R.styleable.BannerView_scrollIntervalTime, 3500).toLong()
        indicatorPadding = types.getDimensionPixelOffset(R.styleable.BannerView_indicatorPadding, 0)
        indicatorVisibility = types.getInt(R.styleable.BannerView_indicatorVisibility, VISIBLE)
        types.recycle()
        initView()
    }

    private fun initView() {
        setBackgroundColor(Color.WHITE)
        inflate(context, R.layout.banne_view, this)
        viewPager2 = findViewById(R.id.banner_vp)
        dotLayout = findViewById(R.id.review_dot_layout)
        dotLayout.visibility = indicatorVisibility
        viewPager2.isUserInputEnabled = fingerSwipeEnable
        Log.d(javaClass.simpleName, "$indicatorVisibility")
        viewPager2.getChildAt(0).setOnTouchListener { view, event ->
            view.performClick()
            if (fingerSwipeEnable) {
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        if (autoScrollUtils?.isStopScroll == false) {
                            stopScrolling()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (autoScrollUtils?.isStopScroll == true) {
                            autoScrollUtils?.isStopScroll = false
                            startScrolling()
                        }
                    }
                }
            }
            false
        }
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        ensureCustomDot()
        viewPager2.orientation = scrollOrientation
        dotLayout.orientation = scrollOrientation
    }

    /**设置圆点位置*/
    private fun ensureCustomDot() {
        // Don't bother getting the parent height if the parent hasn't been laid
        if (dotLayout.javaClass.name == IndicatorLayout::class.java.name || indicatorVisibility != View.VISIBLE) return
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.javaClass.name == IndicatorLayout::class.java.name) {
                dotLayout = child as IndicatorLayout
                break
            }
        }
    }


    /**创建选择焦点*/
    private fun createDots() {
        if (indicatorDotArray.size > 2) {
            dotLayout.setBackgroundResource(indicatorDotArray[2])
        }
        if (realSize <= 1) return
        if (dotLayout.childCount > 0) dotLayout.removeAllViews()
        mDots.clear()
        for (i in 1..realSize) {
            val imageView = ImageView(context)
            imageView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            imageView.setImageResource(indicatorDotArray[0]) //设置图片
            if (indicatorPadding > 0) {
                if (scrollOrientation == ORIENTATION_HORIZONTAL) {
                    imageView.setPadding(indicatorPadding / 2, 0, indicatorPadding / 2, 0)
                }
                if (scrollOrientation == ORIENTATION_VERTICAL) {
                    imageView.setPadding(0, indicatorPadding / 2, 0, indicatorPadding / 2)
                }
            }
            dotLayout.addView(imageView) //将图片添加到布局中
            //将dot添加到dots集合中
            mDots.add(imageView)
        }
        mDots[0].setImageResource(indicatorDotArray[1]) //设置启动后显示的第一个点
    }


    private val listener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            //for-each循环将所有的dot设置为dot_normal
            val index = position % mDots.size
            if (realSize > 1) {
                for (imageView in mDots) {
                    imageView.setImageResource(indicatorDotArray[0])
                }
                //设置当前显示的页面的dot设置为dot_focused
                mDots[index].setImageResource(indicatorDotArray[1])
            }
            pagerChangeListener?.onPagerChange(position)
        }
    }


    override fun onCreate() {
//        Log.i("wade", "onCreate")
    }

    override fun onStart() {
//        Log.i("wade", "onStart")
    }

    override fun onResume() {
//        Log.i("wade", "onResume")
        startScrolling()
    }

    override fun onPause() {
//        Log.i("wade", "onPause")
        stopScrolling()
    }

    override fun onStop() {
//        Log.i("wade", "onStop")
    }

    override fun onDestroy() {
        viewPager2.unregisterOnPageChangeCallback(listener)
    }

    /*自定义page页面*/
    fun setAdapterView(@LayoutRes adapterLayoutRes: Int) {
        this.adapterLayoutRes = adapterLayoutRes
    }

    /**绑定view的生命周期*/
    fun addLifecycleObserver(owner: FragmentActivity) {
        owner.lifecycle.addObserver(this)
    }

    /**绑定view的生命周期*/
    fun addLifecycleObserver(owner: Fragment) {
        owner.lifecycle.addObserver(this)
    }


    /**
     *设置图片数据源和绑定list生命周期**/
    fun <T : Any> setData(list: MutableList<T>, adapterListener: AdapterViewListener<T>?) {
        //绑定生命周期
        this.realSize = list.size
        createDots()
        if (bannerAdapter == null) {
            bannerAdapter = BannerAdapter(this, list).apply {
                loadImage = { data, view, position ->
                    adapterListener?.loadImage(data, view as ImageView, position)
                    view.setOnClickListener {
                        adapterListener?.onItemClick(data, position)
                    }
                }
                viewPager2.adapter = this
                viewPager2.registerOnPageChangeCallback(listener)
            }
        }
        if (realSize > 1 && isAutoScroll) {
            viewPager2.setCurrentItem(realSize * 100000, false)
            startScrolling()
        }
    }


    /**
     *设置图片数据源和绑定list生命周期**/
    fun <T : Any> setData(list: MutableList<T>, adapterListener: AdapterCustomViewListener<T>?) {
        //绑定生命周期
        if (adapterLayoutRes == null) return
        this.realSize = list.size
        createDots()
        if (bannerAdapter == null) {
            bannerAdapter = BannerAdapter(this, list, adapterLayoutRes!!).apply {
                loadImage = { data, view, position ->
                    adapterListener?.loadImage(data, view, position)
                    view.setOnClickListener {
                        adapterListener?.onItemClick(data, position)
                    }
                }
                viewPager2.adapter = this
                viewPager2.registerOnPageChangeCallback(listener)
            }
        }
        if (realSize > 1 && isAutoScroll) {
            viewPager2.setCurrentItem(realSize * 100000, false)
            startScrolling()
        }
    }


    /**启动滚动*/
    private fun startScrolling() {
        if (autoScrollUtils == null) autoScrollUtils = AutoScrollUtils(this)
        if (realSize > 1 && isAutoScroll) {
            autoScrollUtils?.start()
        }
    }

    /**启动滚动*/
    private fun stopScrolling() {
        autoScrollUtils?.stop()
    }
}