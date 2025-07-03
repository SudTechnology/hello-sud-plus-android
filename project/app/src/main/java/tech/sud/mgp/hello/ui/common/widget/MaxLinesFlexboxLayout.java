package tech.sud.mgp.hello.ui.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.flexbox.FlexLine;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

public class MaxLinesFlexboxLayout extends FlexboxLayout {

    private int maxLines = 2;

    public MaxLinesFlexboxLayout(Context context) {
        super(context);
    }

    public MaxLinesFlexboxLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxLinesFlexboxLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 先让系统测一遍，得到所有行信息
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        List<FlexLine> allLines = getFlexLinesInternal();

        if (allLines.size() <= maxLines) {
            return;
        }

        // 限制高度：只保留前两行的高度
        int totalHeight = 0;
        for (int i = 0; i < maxLines; i++) {
            totalHeight += allLines.get(i).getCrossSize();
        }

        setMeasuredDimension(getMeasuredWidth(), totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 先让系统正常 layout 一遍
        super.onLayout(changed, l, t, r, b);

        // 隐藏第3行及其以后的 View
        List<FlexLine> allLines = getFlexLinesInternal();

        if (allLines.size() > maxLines) {
            int countToShow = 0;
            for (int i = 0; i < maxLines; i++) {
                countToShow += allLines.get(i).getItemCount();
            }

            for (int i = countToShow; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.layout(0, 0, 0, 0); // 将其移到不可见区域
            }
        }
    }
}
