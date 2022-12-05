package tech.sud.mgp.hello.ui.scenes.base.interaction.rocket.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;

/**
 * 火箭容器
 */
public class RocketContainer extends FrameLayout {

    private List<SudMGPMGState.MGCustomRocketSetClickRect.RocketClickRect> clickRectList;
    private FrameLayout clickRectContainer;

    public RocketContainer(@NonNull Context context) {
        this(context, null);
    }

    public RocketContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RocketContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RocketContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        clickRectContainer = new FrameLayout(getContext());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isInnerClickRect(ev)) { // 不在该点击区域的话，直接返回false
                return false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /** 判断当前点击区域是否在该区域内 */
    private boolean isInnerClickRect(MotionEvent ev) {
        if (clickRectList == null || clickRectList.size() == 0) {
            return false;
        }
        float x = ev.getX();
        float y = ev.getY();
        for (SudMGPMGState.MGCustomRocketSetClickRect.RocketClickRect rect : clickRectList) {
            if (x >= rect.x && x <= (rect.x + rect.width)) {
                if (y >= rect.y && y <= (rect.y + rect.height)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setClickRectList(List<SudMGPMGState.MGCustomRocketSetClickRect.RocketClickRect> clickRectList) {
        this.clickRectList = clickRectList;
        drawRectViewList();
    }

    private void drawRectViewList() {
        clickRectContainer.removeAllViews();
        if (clickRectList != null) {
            for (SudMGPMGState.MGCustomRocketSetClickRect.RocketClickRect rocketClickRect : clickRectList) {
                drawRectView(rocketClickRect);
            }
        }
    }

    private void drawRectView(SudMGPMGState.MGCustomRocketSetClickRect.RocketClickRect rocketClickRect) {
        View view = new View(getContext());
        LayoutParams layoutParams = new LayoutParams((int) rocketClickRect.width, (int) rocketClickRect.height);
        layoutParams.leftMargin = (int) rocketClickRect.x;
        layoutParams.topMargin = (int) rocketClickRect.y;
        view.setBackgroundResource(R.drawable.shape_stroke_2_red);
        clickRectContainer.addView(view, layoutParams);
    }

    public void bringToFrondClickRectView() {
        clickRectContainer.removeAllViews();
        if (clickRectContainer.getParent() == null) {
            addView(clickRectContainer, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
    }

}
