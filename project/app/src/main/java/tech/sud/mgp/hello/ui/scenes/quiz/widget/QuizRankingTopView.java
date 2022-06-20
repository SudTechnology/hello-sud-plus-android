package tech.sud.mgp.hello.ui.scenes.quiz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;

/**
 * 竞猜排行榜里top3 显示的view
 */
public class QuizRankingTopView extends LinearLayout {

    private View viewRanking;
    private RoundedImageView ivIcon;
    private TextView tvName;
    private TextView tvTitle;

    public QuizRankingTopView(Context context) {
        this(context, null);
    }

    public QuizRankingTopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuizRankingTopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public QuizRankingTopView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.view_quiz_ranking_top, this);
        viewRanking = findViewById(R.id.view_ranking);
        ivIcon = findViewById(R.id.iv_icon);
        tvName = findViewById(R.id.tv_name);
        tvTitle = findViewById(R.id.tv_title);
    }

    public void setRankingIcon(int resId) {
        viewRanking.setBackgroundResource(resId);
    }

    public void setIcon(int resId) {
        ivIcon.setImageResource(resId);
    }

    public void setBorderColor(int color) {
        ivIcon.setBorderColor(color);
    }

    public void setName(String value) {
        tvName.setText(value);
    }

    public void setTitle(String value) {
        tvTitle.setText(value);
    }

}
