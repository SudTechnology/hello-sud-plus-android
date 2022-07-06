package tech.sud.mgp.hello.ui.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * 跑马灯View
 */
public class MarqueeView extends HorizontalScrollView implements Runnable {

    private Context context;
    private LinearLayout mainLayout;//跑马灯滚动部分
    private int scrollSpeed = 1;//滚动速度
    private int scrollDirection = RIGHT_TO_LEFT;//滚动方向
    private int currentX;//当前x坐标
    private int viewMargin = 0;//View间距
    private int viewWidth;//View总宽度
    private int curGroupWidth; // 当前View的宽度

    public static final int LEFT_TO_RIGHT = 1;
    public static final int RIGHT_TO_LEFT = 2;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    void initView() {
        mainLayout = new LinearLayout(getContext());
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(mainLayout, LinearLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(getContext(), 24));
    }

    public void addViewInQueue(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginStart(viewMargin);
        mainLayout.addView(view, lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        curGroupWidth = getMeasuredWidth();
        viewWidth = 0;
        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            View view = mainLayout.getChildAt(i);
            viewWidth += view.getMeasuredWidth() + viewMargin;
        }
    }

    //开始滚动
    public void startScroll() {
        removeCallbacks(this);
        currentX = (scrollDirection == LEFT_TO_RIGHT ? viewWidth : -curGroupWidth);
        post(this);
    }

    //停止滚动
    public void stopScroll() {
        removeCallbacks(this);
    }

    //设置View间距
    public void setViewMargin(int viewMargin) {
        this.viewMargin = viewMargin;
    }

    //设置滚动速度
    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    //设置滚动方向 默认从左向右
    public void setScrollDirection(int scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    @Override
    public void run() {
        switch (scrollDirection) {
            case LEFT_TO_RIGHT:
                mainLayout.scrollTo(currentX, 0);
                currentX--;

                if (-currentX >= curGroupWidth) {
                    mainLayout.scrollTo(viewWidth, 0);
                    currentX = viewWidth;
                }
                break;
            case RIGHT_TO_LEFT:
                mainLayout.scrollTo(currentX, 0);
                currentX++;

                if (currentX >= viewWidth) {
                    mainLayout.scrollTo(-curGroupWidth, 0);
                    currentX = -curGroupWidth;
                }
                break;
            default:
                break;
        }

        postDelayed(this, 10 / scrollSpeed);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public LinearLayout getMainLayout() {
        return mainLayout;
    }
}