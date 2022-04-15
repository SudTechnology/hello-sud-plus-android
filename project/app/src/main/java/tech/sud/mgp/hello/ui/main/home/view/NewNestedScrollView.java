package tech.sud.mgp.hello.ui.main.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;


public class NewNestedScrollView extends NestedScrollView implements NestedScrollView.OnScrollChangeListener {

    private ScrollStateChangeListener scrollStateChangeListener;

    /**
     * 滚动状态
     */
    public enum ScrollDirection {
        Scroll_Top,      // 滑动到顶部
        Scroll_Bottom,   // 滑动到底部
        Scroll_Up,      // 下滑
        Scroll_Down     // 上滑
    }

    /**
     * 整個滾動内容高度
     * 所有内容加起来的高度
     */
    public int totalHeight = 0;

    /**
     * 当前view的高度
     */
    public int viewHeight = 0;

    /**
     * 是否滚动到底了
     */
    private boolean bottom = false;

    /**
     * 是否滚动在顶部
     *
     * @param context
     */
    private boolean top = false;


    public NewNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public NewNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollChangeListener(this);
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollStateChangeListener != null) {
            /*实时滚动回调*/
            scrollStateChangeListener.onScrollChange(scrollX, scrollY, oldScrollX, oldScrollY);

            if (scrollY > oldScrollY) {
                scrollStateChangeListener.onScrollDirection(ScrollDirection.Scroll_Down);
            }

            if (scrollY < oldScrollY) {
                scrollStateChangeListener.onScrollDirection(ScrollDirection.Scroll_Up);
            }

            if (getScrollY() <= 0) {
                top = true;
                scrollStateChangeListener.onScrollDirection(ScrollDirection.Scroll_Top);
            } else {
                top = false;
            }

            if (totalHeight > viewHeight && (totalHeight - viewHeight) == scrollY) {
                bottom = true;
                scrollStateChangeListener.onScrollDirection(ScrollDirection.Scroll_Bottom);
            } else {
                bottom = false;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            totalHeight += view.getMeasuredHeight();
        }
        viewHeight = getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 是否动到底
     *
     * @return
     */
    public boolean isBottom() {
        return bottom;
    }

    /**
     * 是否滚动到了 顶部
     *
     * @return
     */
    public boolean isTop() {
        return top;
    }

    /**
     * 设置监听
     *
     * @param scrollStateChangeListener
     * @return
     */
    public NewNestedScrollView setScrollStateChangeListener(ScrollStateChangeListener scrollStateChangeListener) {
        this.scrollStateChangeListener = scrollStateChangeListener;
        return this;
    }

    public interface ScrollStateChangeListener {
        /**
         * 滚动监听
         *
         * @param scrollX
         * @param scrollY
         * @param oldScrollX
         * @param oldScrollY
         */
        void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY);

        /**
         * 滚动方向
         *
         * @param direction
         */
        void onScrollDirection(ScrollDirection direction);
    }
}


