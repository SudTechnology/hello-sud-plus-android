package tech.sud.mgp.hello.ui.scenes.ticket.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

/**
 * 门票等级选择页面的通知栏
 */
public class TicketLevelNoticeView extends ConstraintLayout {

    private String[] datas;
    private int position;
    private long changeDuration = 3000; // 切换的间隔
    private boolean isShowFirst = true;
    private TextView tvFirst = new TextView(getContext());
    private TextView tvSecond = new TextView(getContext());

    public TicketLevelNoticeView(@NonNull Context context) {
        this(context, null);
    }

    public TicketLevelNoticeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TicketLevelNoticeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        int textColor = Color.parseColor("#ffe77d");
        tvFirst.setTextSize(14);
        tvFirst.setTextColor(textColor);
        tvFirst.setGravity(Gravity.CENTER);
        addView(tvFirst, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        tvSecond.setTextSize(14);
        tvSecond.setTextColor(textColor);
        tvSecond.setGravity(Gravity.CENTER);
        addView(tvSecond, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
        setNotice();
        startChangeTask();
    }

    private void startChangeTask() {
        removeChangeTask();
        postDelayed(changeTask, changeDuration);
    }

    private void removeChangeTask() {
        removeCallbacks(changeTask);
    }

    private Runnable changeTask = new Runnable() {
        @Override
        public void run() {
            setNotice();
            startChangeTask();
        }
    };

    private void setNotice() {
        TextView showTv;
        TextView hideTv;
        if (isShowFirst) {
            showTv = tvFirst;
            hideTv = tvSecond;
        } else {
            showTv = tvSecond;
            hideTv = tvFirst;
        }
        showTv.setText(findNotice());
        startTranslationTopShowAnim(showTv);
        startTranslationTopHideAnim(hideTv);
        isShowFirst = !isShowFirst;
        position++;
    }

    private String findNotice() {
        if (datas == null || datas.length == 0) return null;
        if (position >= datas.length) {
            position = 0;
        }
        return datas[position];
    }

    /**
     * 移动自身一个身位，消失
     */
    private void startTranslationTopHideAnim(View view) {
        ArrayList<Animator> animatorList = new ArrayList<>();
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, -view.getMeasuredHeight() * 1.0f / 2);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);
        animatorList.add(translationYAnimator);
        animatorList.add(alphaAnimator);
        animatorSet.playTogether(animatorList);
        animatorSet.setDuration(300L);
        animatorSet.start();
    }

    /**
     * 移动自身一个身位，显示
     */
    private void startTranslationTopShowAnim(View view) {
        ArrayList<Animator> animatorList = new ArrayList<>();
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight() * 1.0f / 2, 0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1.0f);
        animatorList.add(translationYAnimator);
        animatorList.add(alphaAnimator);
        animatorSet.playTogether(animatorList);
        animatorSet.setDuration(300L);
        animatorSet.start();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            startChangeTask();
        } else {
            removeChangeTask();
        }
    }

}
