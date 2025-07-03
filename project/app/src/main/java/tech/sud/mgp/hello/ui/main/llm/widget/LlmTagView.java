package tech.sud.mgp.hello.ui.main.llm.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.common.widget.MaxLinesFlexboxLayout;

public class LlmTagView extends FrameLayout {

    private List<String> mDatas;
    private List<String> mSelectedList = new ArrayList<>();
    private TextView mTvTitle;
    private TextView mTvMore;
    private MaxLinesFlexboxLayout mContainerTag;
    private boolean isCollapse = true; // 是否是收起的

    public LlmTagView(@NonNull Context context) {
        this(context, null);
    }

    public LlmTagView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LlmTagView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
        setListeners();
    }

    private void setListeners() {
        mTvMore.setOnClickListener(v -> {
            isCollapse = !isCollapse;
            initCollapse();
        });
    }

    private void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        View.inflate(context, R.layout.view_llm_tag, this);
        mTvTitle = findViewById(R.id.tv_title);
        mTvMore = findViewById(R.id.tv_more);
        mContainerTag = findViewById(R.id.container_tag);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LlmTagView, defStyleAttr, 0);
        String titleText = array.getString(R.styleable.LlmTagView_llm_tag_title);
        array.recycle();

        mTvTitle.setText(titleText);
    }

    public void initSelectedTags(List<String> datas) {
        if (datas == null || datas.size() == 0 || mSelectedList.size() != 0) {
            return;
        }
        mSelectedList.addAll(datas);
    }

    public void setDatas(List<String> datas) {
        if (datas == mDatas) {
            return;
        }
        mDatas = datas;
        mContainerTag.removeAllViews();
        initAllView();
        initCollapse();
    }

    private void initCollapse() {
        if (isCollapse) { // 收起
            mTvMore.setText(R.string.more);
            mContainerTag.setMaxLines(2);
        } else {
            mTvMore.setText(R.string.pack_up);
            mContainerTag.setMaxLines(Integer.MAX_VALUE);
        }
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
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = topMargin;
            params.setMarginEnd(startMargin);
            mContainerTag.addView(tv, params);

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
            tv.setBackgroundResource(R.drawable.shape_stroke_000000_1);
        } else {
            tv.setTypeface(Typeface.DEFAULT); // 恢复默认字体
            tv.setBackgroundResource(R.drawable.shape_stroke_r4_979797_0p5);
        }
    }

    public List<String> getSelectedTags() {
        return new ArrayList<>(mSelectedList);
    }
}
