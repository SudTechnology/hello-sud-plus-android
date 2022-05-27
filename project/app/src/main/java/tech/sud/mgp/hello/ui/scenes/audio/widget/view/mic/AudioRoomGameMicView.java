package tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.AnimUtils;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.common.widget.SimpleAnimatorListener;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.BaseMicView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.OnMicItemClickListener;

/**
 * 语聊房开启游戏时显示的麦位
 */
public class AudioRoomGameMicView extends BaseMicView<AudioRoomGameMicItemView> {

    private View scrollView;
    private LinearLayout container;
    private View viewBg;
    private ImageView ivShirnk;
    private View viewSpread;
    private View viewLeftArrow;

    private final long duration = 500;
    private final GameMicMode micMode = new GameMicMode();

    private AnimatorSet mAnimatorSet;

    public AudioRoomGameMicView(@NonNull Context context) {
        this(context, null);
    }

    public AudioRoomGameMicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRoomGameMicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initListener();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_room_game_mic, this);
        container = findViewById(R.id.ll_container);
        scrollView = findViewById(R.id.scroll_view);
        viewBg = findViewById(R.id.view_bg);
        ivShirnk = findViewById(R.id.iv_shirnk);
        viewSpread = findViewById(R.id.view_spread);
        viewLeftArrow = findViewById(R.id.view_left_arrow);
        int radiu = DensityUtils.dp2px(getContext(), 100);
        ivShirnk.setBackground(getBackgroundDrawable(radiu));
        viewBg.setBackground(getBackgroundDrawable(radiu));
        initMicView();
    }

    @NonNull
    private GradientDrawable getBackgroundDrawable(int radiu) {
        return ShapeUtils.createShape(null, null,
                new float[]{radiu, radiu, 0, 0, 0, 0, radiu, radiu},
                GradientDrawable.RECTANGLE, null,
                Color.parseColor("#99000000"));
    }

    private void initMicView() {
        for (int i = 0; i < 9; i++) {
            AudioRoomGameMicItemView itemView = new AudioRoomGameMicItemView(getContext());
            itemView.setMicMode(micMode);
            container.addView(itemView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mItemViews.add(itemView);
            int finalI = i;
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnMicItemClickListener listener = mOnMicItemClickListener;
                    if (listener != null) {
                        listener.onItemClick(v, finalI);
                    }
                }
            });
        }
    }

    private void initListener() {
        ivShirnk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shirnkMicView();
            }
        });
        viewSpread.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                spreadMicView();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        scrollView.setPivotX(getMeasuredWidth());
        scrollView.setPivotY(getMeasuredHeight());
    }

    /** 展开麦位 */
    @Override
    public void spreadMicView() {
        if (!micMode.isShirnk) return;
        cancelCurAnim();
        micMode.isShirnk = false;
        viewSpread.setVisibility(View.GONE);

        changeItemSize();
        ViewUtils.setPaddingHorizontal(scrollView, 8);
        notifyDataSetChanged();

        ArrayList<Animator> list = new ArrayList<>();
        AnimatorSet animatorSet = new AnimatorSet();

        viewBg.setAlpha(0);
        viewLeftArrow.setAlpha(0);

        list.add(AnimUtils.createTranslationXAnimator(scrollView, (int) scrollView.getTranslationX(), 0));
        list.add(ObjectAnimator.ofFloat(scrollView, "scaleX", scrollView.getScaleX(), 1f));
        list.add(ObjectAnimator.ofFloat(scrollView, "scaleY", scrollView.getScaleY(), 1f));

        animatorSet.playTogether(list);
        animatorSet.setDuration(duration);
        animatorSet.start();

        animatorSet.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorSet = null;
                ivShirnk.setVisibility(View.VISIBLE);
            }
        });
        mAnimatorSet = animatorSet;
    }

    /** 缩放麦位 */
    @Override
    public void shirnkMicView() {
        if (micMode.isShirnk) return;
        cancelCurAnim();
        micMode.isShirnk = true;
        ivShirnk.setVisibility(View.GONE);

        changeItemSize();
        ViewUtils.setPaddingHorizontal(scrollView, 0);
        notifyDataSetChanged();

        ArrayList<Animator> list = new ArrayList<>();
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgAlphaAnimator = ObjectAnimator.ofFloat(viewBg, "alpha", 0, 1);
        bgAlphaAnimator.setStartDelay(duration / 2);
        list.add(bgAlphaAnimator);
        ObjectAnimator leftArrowAlphaAnimator = ObjectAnimator.ofFloat(viewLeftArrow, "alpha", 0, 1);
        leftArrowAlphaAnimator.setStartDelay(duration / 2);
        list.add(leftArrowAlphaAnimator);

        list.add(AnimUtils.createTranslationXAnimator(scrollView, (int) scrollView.getTranslationX(), DensityUtils.dp2px(getContext(), 40)));
        list.add(ObjectAnimator.ofFloat(scrollView, "scaleX", scrollView.getScaleX(), 0.375f));
        list.add(ObjectAnimator.ofFloat(scrollView, "scaleY", scrollView.getScaleY(), 0.375f));

        animatorSet.playTogether(list);
        animatorSet.setDuration(duration);
        animatorSet.start();

        viewSpread.setVisibility(View.VISIBLE);

        animatorSet.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorSet = null;
            }
        });
        mAnimatorSet = animatorSet;
    }

    private void cancelCurAnim() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    public void changeItemSize() {
        for (AudioRoomGameMicItemView view : mItemViews) {
            view.changeSize();
        }
    }

    public static class GameMicMode {
        public boolean isShirnk;
    }

}
