package tech.sud.mgp.hello.ui.scenes.audio3d.widget.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.common.widget.SimpleAnimatorListener;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 3D语聊房的礼物效果展示
 */
public class Audio3DGiftEffectView extends ConstraintLayout {

    private Audio3DGiftEffectItemView mCurrentItemView;

    private long mEnterDuration = 300; // 进场动画时长
    private long mExitDuration = 300; // 出场动画时长
    private long mRetainDuration = 2000; // view展示多长时间之后再出场

    public Audio3DGiftEffectView(@NonNull Context context) {
        super(context);
    }

    public Audio3DGiftEffectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Audio3DGiftEffectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showGift(GiftModel giftModel, int giftCount, UserInfo fromUser, List<UserInfo> toUserList, boolean isAllSeat) {
        if (giftModel == null || toUserList == null || toUserList.size() == 0) {
            return;
        }
        for (UserInfo userInfo : toUserList) {
            addGiftEffectItemView(giftModel, giftCount, fromUser, userInfo, isAllSeat);
        }
    }

    private synchronized void addGiftEffectItemView(GiftModel giftModel, int giftCount, UserInfo fromUser, UserInfo toUser, boolean isAllSeat) {
        // 如果当前还有在场上的view，则提前触发给其清除
        removeCallbacks(mDelayExitTask);
        exitItemView();

        // 初始化
        Audio3DGiftEffectItemView view = new Audio3DGiftEffectItemView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.startToStart = LayoutParams.PARENT_ID;
        params.setMarginStart(DensityUtils.dp2px(getContext(), 16));
        view.initData(giftModel, giftCount, fromUser, toUser, isAllSeat);
        addView(view, params);

        int screenWidth = DensityUtils.getScreenWidth();
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.AT_MOST);
        view.measure(widthMeasureSpec, widthMeasureSpec);

        view.setTranslationX(-view.getMeasuredWidth());

        // 做进场动画
        startEnterAnimator(view);
        mCurrentItemView = view;

        postDelayed(mDelayExitTask, mRetainDuration); // 停留三秒退场
    }

    private Runnable mDelayExitTask = new Runnable() {
        @Override
        public void run() {
            exitItemView();
        }
    };

    private void startEnterAnimator(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", -view.getMeasuredWidth(), 0);
        animator.setDuration(mEnterDuration);
        view.setTag(R.id.animator, animator);
        animator.start();
    }

    private void exitItemView() {
        if (mCurrentItemView != null) {
            exitItemView(mCurrentItemView);
            mCurrentItemView = null;
        }
    }

    private void exitItemView(View view) {
        Object tag = view.getTag(R.id.animator);
        if (tag instanceof Animator) {
            ((Animator) tag).cancel();
        }

        AnimatorSet animatorSet = new AnimatorSet();

        ArrayList<Animator> animatorList = new ArrayList<>();
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(view, "translationY", 0, -view.getMeasuredHeight() * 1.5f);
        animatorList.add(alphaAnimator);
        animatorList.add(translationYAnim);

        if (view.getTranslationX() != 0) {
            ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX(), 0);
            animatorList.add(translationXAnim);
        }

        animatorSet.playTogether(animatorList);
        animatorSet.setDuration(mExitDuration);
        animatorSet.start();
        animatorSet.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(view);
            }
        });
    }

}
