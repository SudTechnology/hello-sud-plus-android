package tech.sud.mgp.hello.ui.scenes.quiz.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.ui.common.utils.FormatUtils;
import tech.sud.mgp.hello.ui.common.utils.SceneUtils;
import tech.sud.mgp.hello.ui.scenes.quiz.model.QuizRoomPkModel;

/**
 * 竞猜场景 更多活动顶部View
 */
public class MoreQuizHeadView extends ConstraintLayout {

    private RecyclerView recyclerView;
    private TextView tvLimitedQuiz;
    private MyAdapter adapter;
    private OnGuessListener onGuessListener;

    public MoreQuizHeadView(@NonNull Context context) {
        this(context, null);
    }

    public MoreQuizHeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreQuizHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MoreQuizHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        setListeners();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_more_quiz_head, this);
        recyclerView = findViewById(R.id.recycler_view);
        tvLimitedQuiz = findViewById(R.id.tv_limited_quiz);
        adapter = new MyAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void setListeners() {
        adapter.addChildClickViewIds(R.id.tv_left_guess, R.id.tv_right_guess);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                itemChildClick(view, position);
            }
        });
    }

    private void itemChildClick(@NonNull View view, int position) {
        QuizRoomPkModel model = adapter.getItem(position);
        if (model.status != 0) {
            return;
        }
        int id = view.getId();
        QuizRoomPkModel.RoomPkInfoModel roomPkInfoModel;
        if (id == R.id.tv_left_guess) {
            roomPkInfoModel = model.leftInfo;
        } else if (id == R.id.tv_right_guess) {
            roomPkInfoModel = model.rightInfo;
        } else {
            return;
        }
        if (onGuessListener != null) {
            onGuessListener.onGuess(roomPkInfoModel);
        }
    }

    /** 设置数据集 */
    public void setDatas(List<QuizRoomPkModel> list) {
        adapter.setList(list);
    }

    /** 刷新数据 */
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    /** 设置"限时竞猜活动"标题是否显示 */
    public void setQuizTitleVisible(boolean show) {
        tvLimitedQuiz.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /** 设置猜TA赢的监听器 */
    public void setOnGuessListener(OnGuessListener onGuessListener) {
        this.onGuessListener = onGuessListener;
    }

    public interface OnGuessListener {
        void onGuess(QuizRoomPkModel.RoomPkInfoModel model);
    }

    private static class MyAdapter extends BaseQuickAdapter<QuizRoomPkModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_more_quiz_head);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, QuizRoomPkModel model) {
            View viewVS = holder.getView(R.id.view_vs);
            TextView tvStatus = holder.getView(R.id.tv_status);
            TextView tvWinResult = holder.getView(R.id.tv_win_result);

            ImageView ivLeftIcon = holder.getView(R.id.iv_left_icon);
            TextView tvLeftName = holder.getView(R.id.tv_left_name);
            TextView tvLeftId = holder.getView(R.id.tv_left_id);
            View viewLeftStar = holder.getView(R.id.view_left_start);
            TextView tvLeftBetNumber = holder.getView(R.id.tv_left_bet_number);
            View viewLeftResult = holder.getView(R.id.view_left_result);
            TextView tvLeftGuess = holder.getView(R.id.tv_left_guess);

            ImageView ivRightIcon = holder.getView(R.id.iv_right_icon);
            TextView tvRightName = holder.getView(R.id.tv_right_name);
            TextView tvRightId = holder.getView(R.id.tv_right_id);
            View viewRightStar = holder.getView(R.id.view_right_start);
            TextView tvRightBetNumber = holder.getView(R.id.tv_right_bet_number);
            View viewRightResult = holder.getView(R.id.view_right_result);
            TextView tvRightGuess = holder.getView(R.id.tv_right_guess);

            setRoomInfo(model.leftInfo, ivLeftIcon, tvLeftName, tvLeftId, viewLeftStar, tvLeftBetNumber, tvLeftGuess, viewLeftResult,
                    model.rightInfo.betNumber <= 0);
            setRoomInfo(model.rightInfo, ivRightIcon, tvRightName, tvRightId, viewRightStar, tvRightBetNumber, tvRightGuess, viewRightResult,
                    model.leftInfo.betNumber <= 0);

            float alpha;
            if (model.status == 0) { // 进行中
                viewLeftResult.setVisibility(View.GONE);
                viewRightResult.setVisibility(View.GONE);
                tvLeftGuess.setVisibility(View.VISIBLE);
                tvRightGuess.setVisibility(View.VISIBLE);
                alpha = 1;
                startCountdown(model.countdownCycle, tvStatus);
                tvWinResult.setText(R.string.quiz_win_result);
            } else { // 已结束
                viewLeftResult.setVisibility(View.VISIBLE);
                viewRightResult.setVisibility(View.VISIBLE);
                tvLeftGuess.setVisibility(View.GONE);
                tvRightGuess.setVisibility(View.GONE);
                alpha = 0.7f;
                cancelCountdown(tvStatus);
                tvStatus.setText(R.string.finished);
                tvWinResult.setText(getContext().getString(R.string.quiz_win_result_detail, model.winNumber + ""));
            }

            viewVS.setAlpha(alpha);

            tvLeftName.setAlpha(alpha);
            tvLeftId.setAlpha(alpha);
            viewLeftStar.setAlpha(alpha);
            tvLeftBetNumber.setAlpha(alpha);

            tvRightName.setAlpha(alpha);
            tvRightId.setAlpha(alpha);
            viewRightStar.setAlpha(alpha);
            tvRightBetNumber.setAlpha(alpha);

            // 固定的参与者头像
            FrameLayout flActor = holder.getView(R.id.fl_actor);
            if (flActor.getChildCount() == 0) {
                int ivSize = DensityUtils.dp2px(getContext(), 24);
                int baseMarginStart = DensityUtils.dp2px(getContext(), 18);
                addActor(flActor, R.drawable.ic_avatar_1, ivSize, baseMarginStart);
                addActor(flActor, R.drawable.ic_avatar_2, ivSize, baseMarginStart);
                addActor(flActor, R.drawable.ic_avatar_3, ivSize, baseMarginStart);
                addActor(flActor, R.drawable.ic_avatar_4, ivSize, baseMarginStart);
                addActor(flActor, R.drawable.ic_avatar_5, ivSize, baseMarginStart);
                addActor(flActor, R.drawable.ic_avatar_6, ivSize, baseMarginStart);
            }
            // 多少人参与了竞猜
            holder.setText(R.id.tv_member_number, getContext().getString(R.string.quiz_number, model.memberNumber + ""));

            // 奖池数量
            holder.setText(R.id.tv_jackpot, model.jackpotCount);
        }

        // 开启倒计时
        private void startCountdown(int cycle, TextView tv) {
            cancelCountdown(tv);
            int totalCount = SceneUtils.getTotalCount(cycle);
            CustomCountdownTimer timer = new CustomCountdownTimer(totalCount) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void onTick(int count) {
                    String info = getContext().getString(R.string.from_start) + " " + FormatUtils.formatTimeDay(count);
                    tv.setText(info);
                }

                @Override
                protected void onFinish() {
                    startCountdown(cycle, tv);
                }
            };
            timer.start();
            tv.setTag(R.id.obj, timer);
        }

        private void cancelCountdown(TextView tv) {
            CustomCountdownTimer oldTimer = (CustomCountdownTimer) tv.getTag(R.id.obj);
            if (oldTimer != null) {
                oldTimer.cancel();
            }
        }

        // 设置房间信息
        @SuppressLint("SetTextI18n")
        private void setRoomInfo(QuizRoomPkModel.RoomPkInfoModel model, ImageView ivIcon, TextView tvName, TextView tvId, View viewStar,
                                 TextView tvBetNumber, TextView tvGuess, View viewResult, boolean enableGuess) {
            ivIcon.setImageResource(model.icon);
            tvName.setText(model.roomName);
            tvId.setText("ID " + model.roomId);
            if (model.betNumber > 0) {
                viewStar.setVisibility(View.VISIBLE);
                tvBetNumber.setText(getContext().getString(R.string.have_support) + " " + model.betNumber);
                tvGuess.setText(R.string.add_cast);
                tvGuess.setBackground(ShapeUtils.createShape(DensityUtils.dp2px(getContext(), 1),
                        (float) DensityUtils.dp2px(getContext(), 2), null, GradientDrawable.RECTANGLE,
                        Color.parseColor("#ffbf3a"), Color.parseColor("#ffe373")));
                tvGuess.setTextColor(Color.parseColor("#6c3800"));
            } else {
                viewStar.setVisibility(View.GONE);
                tvBetNumber.setText("");
                tvGuess.setText(R.string.guess_he_win);
                if (enableGuess) {
                    tvGuess.setBackground(ShapeUtils.createShape(DensityUtils.dp2px(getContext(), 1),
                            (float) DensityUtils.dp2px(getContext(), 2), null, GradientDrawable.RECTANGLE,
                            Color.parseColor("#ffbf3a"), Color.parseColor("#ffe373")));
                    tvGuess.setTextColor(Color.parseColor("#6c3800"));
                } else {
                    tvGuess.setBackground(ShapeUtils.createShape(DensityUtils.dp2px(getContext(), 1),
                            (float) DensityUtils.dp2px(getContext(), 2), null, GradientDrawable.RECTANGLE,
                            Color.parseColor("#ffe8b9"), Color.parseColor("#fbf2d0")));
                    tvGuess.setTextColor(Color.parseColor("#4d6c3800"));
                }
                tvGuess.setEnabled(enableGuess);
            }
            if (model.isWin) {
                viewResult.setBackgroundResource(R.drawable.ic_quiz_win);
            } else {
                viewResult.setBackgroundResource(R.drawable.ic_quiz_lose);
            }
        }

        // 添加一个参与者头像
        private void addActor(FrameLayout flActor, int resId, int ivSize, int baseMarginStart) {
            RoundedImageView iv = new RoundedImageView(getContext());
            iv.setOval(true);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(resId);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ivSize, ivSize);
            params.setMarginStart(baseMarginStart * flActor.getChildCount());
            flActor.addView(iv, 0, params);
        }

    }

}
