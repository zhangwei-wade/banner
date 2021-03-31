package com.dzw.banner.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout


/**
 * @author zhangwei on 2021/3/27.
 */
class IndicatorLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?) : this(context, null)

    init {
        orientation = HORIZONTAL
    }
}