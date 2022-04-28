package com.tinytiger.hooworld.ui.common.widget.view.refresh

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * 状态View，待实现
 * 1,显示错误
 */
class StatusView : ConstraintLayout {

    // 刷新数据的监听
    var refreshSubscriber: (() -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs)

    /** 设置是否可以刷新 */
    fun setRefreshEnable(enable: Boolean) {

    }

}