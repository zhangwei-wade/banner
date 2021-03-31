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
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL
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
    private var adapterLayoutRes: Int = R.layout.review_start_banner_item_layout
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

    //是否循环
    var isCycle: Boolean = true
        set(value) {
            if (!value) {
                isAutoScroll = false
            }
            mStartPosition = (if (value) 1 else 0)
            field = value
        }

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

    /**轮播开始位置*/
    var mStartPosition = 1

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val types = context.obtainStyledAttributes(attrs, R.styleable.BannerView)
        isAutoScroll = types.getBoolean(R.styleable.BannerView_isAutoScroll, false)
        isCycle = types.getBoolean(R.styleable.BannerView_isCycle, true)
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
        private var mTempPosition: Int = -1
        private var isScrolled = false
        override fun onPageSelected(position: Int) {
            //for-each循环将所有的dot设置为dot_normal
            if (isScrolled) {
                mTempPosition = position
                val realPosition: Int = bannerAdapter?.getRealPosition(position) ?: 0
                if (realSize > 1) {
                    for (imageView in mDots) {
                        imageView.setImageResource(indicatorDotArray[0])
                    }
                    //设置当前显示的页面的dot设置为dot_focused
                    mDots[realPosition].setImageResource(indicatorDotArray[1])
                }
                pagerChangeListener?.onPagerChange(realPosition)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            //手势滑动中,代码执行滑动中
            if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
                isScrolled = true
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                //滑动闲置或滑动结束
                isScrolled = false
                if (mTempPosition != -1 && isCycle) {
                    if (mTempPosition == 0) {
                        setCurrentItem(realSize, false)
                    } else if (mTempPosition == getItemCount() - 1) {
                        setCurrentItem(1, false)
                    }
                }
            }
        }
    }


    open fun getCurrentItem(): Int {
        return viewPager2.currentItem
    }

    open fun getItemCount(): Int {
        return bannerAdapter?.itemCount ?: 0
    }

    /**
     * 跳转到指定位置（最好在设置了数据后在调用，不然没有意义）
     * @param position
     * @return
     */
    open fun setCurrentItem(position: Int) {
        return setCurrentItem(position, true)
    }

    /**
     * 跳转到指定位置（最好在设置了数据后在调用，不然没有意义）
     * @param position
     * @param smoothScroll
     * @return
     */
    open fun setCurrentItem(position: Int, smoothScroll: Boolean) {
        viewPager2.setCurrentItem(position, smoothScroll)
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

    /**绑定view的生命周期*/
    fun addLifecycleObserver(owner: FragmentActivity) {
        owner.lifecycle.addObserver(this)
    }

    /**绑定view的生命周期*/
    fun addLifecycleObserver(owner: Fragment) {
        owner.lifecycle.addObserver(this)
    }


    /**
     *设置图片数据源**/
    fun <T : Any> setData(list: MutableList<T>, adapterListener: AdapterViewListener<T>?) {
        initAdapter(list) { data, view, position ->
            adapterListener?.loadImage(data, view as ImageView, position)
            view.setOnClickListener {
                adapterListener?.onItemClick(data, position)
            }
        }
    }


    /**
     *设置图片数据源
     *自定义page页面
     * @param adapterCustomLayoutRes 自定义显示页面
     * **/
    fun <T : Any> setData(list: MutableList<T>, adapterCustomLayoutRes: Int? = R.layout.review_start_banner_item_layout, adapterListener: AdapterCustomViewListener<T>?) {
        if (adapterCustomLayoutRes == null) return
        adapterLayoutRes = adapterCustomLayoutRes
        initAdapter(list) { data, view, position ->
            adapterListener?.loadImage(data, view, position)
            view.setOnClickListener {
                adapterListener?.onItemClick(data, position)
            }
        }
    }


    private fun <T : Any> initAdapter(list: MutableList<T>, loadImageListener: (T, View, Int) -> Unit) {
        this.realSize = list.size
        createDots()
        if (bannerAdapter == null) {
            bannerAdapter = BannerAdapter(list, adapterLayoutRes).apply {
                loadImage = loadImageListener
                viewPager2.adapter = this
                viewPager2.registerOnPageChangeCallback(listener)
            }
        }
        if (list.size == 1) {
            isCycle = false
        }
        if (!isCycle) {
            bannerAdapter?.cycleSize = 0
        }
        viewPager2.setCurrentItem(mStartPosition, false)
        startScrolling()
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