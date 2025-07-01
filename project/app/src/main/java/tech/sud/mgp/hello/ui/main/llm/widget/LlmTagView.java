package tech.sud.mgp.hello.ui.main.llm.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;

public class LlmTagView extends FlexboxLayout {

    private List<String> mDatas;
    private List<String> mSelectedList = new ArrayList<>();

    public LlmTagView(Context context) {
        super(context);
    }

    public LlmTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LlmTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDatas(List<String> datas) {
        if (datas == mDatas) {
            return;
        }
        mDatas = datas;
        removeAllViews();
        initAllView();
    }

    private void initAllView() {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        Context context = getContext();
        int paddingHorizontal = DensityUtils.dp2px(context, 20);
        int paddingVertical = DensityUtils.dp2px(context, 8);
        int topMargin = DensityUtils.dp2px(context, 8);
        int startMargin = DensityUtils.dp2px(context, 9);
        for (int i = 0; i < mDatas.size(); i++) {
            String tag = mDatas.get(i);
            if (TextUtils.isEmpty(tag)) {
                continue;
            }
            TextView tv = new TextView(context);
            tv.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            tv.setTextColor(getResources().getColor(R.color.c_1a1a1a));
            tv.setTextSize(14);
            tv.setText(tag);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.topMargin = topMargin;
            params.setMarginEnd(startMargin);
            addView(tv, params);
            setIsSelectedStyle(tv, tag);
            tv.setOnClickListener(v -> {
                if (mSelectedList.contains(tag)) {
                    mSelectedList.remove(tag);
                } else {
                    mSelectedList.add(tag);
                }
                setIsSelectedStyle(tv, tag);
            });
        }
    }

    private void setIsSelectedStyle(TextView tv, String tag) {
        boolean isSelected = mSelectedList.contains(tag);
        if (isSelected) {
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            tv.setBackgroundResource(R.drawable.shape_stroke_000000_1p5);
        } else {
            tv.setTypeface(Typeface.DEFAULT); // 恢复默认字体
            tv.setBackgroundResource(R.drawable.shape_stroke_979797_0p5);
        }
    }

    public List<String> getSelectedTags() {
        return new ArrayList<>(mSelectedList);
    }

}
