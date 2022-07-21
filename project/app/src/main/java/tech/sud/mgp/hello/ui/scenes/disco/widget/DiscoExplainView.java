package tech.sud.mgp.hello.ui.scenes.disco.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.common.widget.MarqueeView;

/**
 * 蹦迪 说明View
 */
public class DiscoExplainView extends ConstraintLayout {

    private LinearLayout llSpread;
    private MarqueeView marqueeView;
    private final List<TextView> textViews = new ArrayList<>();
    private TextView tvFirst; // 第一个TextView，用于动态更新倒计时
    private boolean isSpread; // 是否是展开的

    public DiscoExplainView(Context context) {
        this(context, null);
    }

    public DiscoExplainView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscoExplainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DiscoExplainView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        setListeners();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_disco_explain, this);
        llSpread = findViewById(R.id.ll_spread);
        marqueeView = findViewById(R.id.marquee_view);

        llSpread.setVisibility(View.GONE);

        marqueeView.setViewMargin(DensityUtils.dp2px(getContext(), 8));
        marqueeView.startScroll();

        initTextView();
        tvFirst = textViews.get(0);

        applySpread();
    }

    private void setListeners() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSpread(!isSpread);
            }
        });
    }

    private void changeSpread(boolean isSpread) {
        if (this.isSpread == isSpread) {
            return;
        }
        this.isSpread = isSpread;
        applySpread();
    }

    /** 应用缩放或展开 */
    private void applySpread() {
        if (isSpread) {
            LinearLayout mainLayout = marqueeView.getMainLayout();
            mainLayout.removeAllViews();
            for (TextView tv : textViews) {
                tv.setSingleLine(false);
                if (tv.getParent() == null) {
                    llSpread.addView(tv);
                }
            }
            marqueeView.setVisibility(View.GONE);
            marqueeView.stopScroll();
            llSpread.setVisibility(View.VISIBLE);
        } else {
            llSpread.removeAllViews();
            for (TextView tv : textViews) {
                tv.setSingleLine(true);
                if (tv.getParent() == null) {
                    marqueeView.addViewInQueue(tv);
                }
            }
            marqueeView.setVisibility(View.VISIBLE);
            marqueeView.startScroll();
            llSpread.setVisibility(View.GONE);
        }
    }

    private void initTextView() {
        CharSequence text1 = getCountdownText("0");
        createTextView(text1);

        createTextView(getContext().getString(R.string.disco_explain_2));

        String text5Keyword1 = getContext().getString(R.string.focus);
        CharSequence text3 = ViewUtils.getTextKeywordColor(getContext(),
                getContext().getString(R.string.disco_explain_3, text5Keyword1),
                R.color.c_ffd731, text5Keyword1);
        createTextView(text3);
    }

    private CharSequence getCountdownText(String text1Keyword2) {
        String text1Keyword1 = getContext().getString(R.string.disco_explain_top_5);
        return ViewUtils.getTextKeywordColor(getContext(), getContext().getString(R.string.disco_explain_1, text1Keyword1, text1Keyword2),
                R.color.c_ffd731, text1Keyword1);
    }

    private void createTextView(CharSequence text) {
        TextView tv = new TextView(getContext());
        tv.setTextSize(12);
        tv.setTextColor(Color.WHITE);
        tv.setText(text);
        marqueeView.addViewInQueue(tv);
        textViews.add(tv);
    }

    /** 设置第一个元素的倒计时，秒 */
    public void setCountdown(int countdown) {
        CharSequence countdownText = getCountdownText(countdown + "");
        tvFirst.setText(countdownText);
    }

}
