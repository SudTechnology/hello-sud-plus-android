package tech.sud.mgp.hello.ui.scenes.base.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;

/**
 * 火箭容器
 */
public class RocketContainer extends FrameLayout {

    private List<SudMGPMGState.MGCustomRocketSetClickRect.RocketClickRect> clickRectList;

    public RocketContainer(@NonNull Context context) {
        super(context);
    }

    public RocketContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RocketContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RocketContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
    }

}
