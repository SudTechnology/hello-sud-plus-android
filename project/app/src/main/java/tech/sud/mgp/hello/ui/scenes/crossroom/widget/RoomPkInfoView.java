package tech.sud.mgp.hello.ui.scenes.crossroom.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.service.room.model.PkStatus;
import tech.sud.mgp.hello.service.room.response.RoomPkModel;
import tech.sud.mgp.hello.service.room.response.RoomPkRoomInfo;
import tech.sud.mgp.hello.ui.common.utils.FormatUtils;

/**
 * 跨房pk顶部，显示pk信息的View
 */
public class RoomPkInfoView extends ConstraintLayout {

    private RoundedImageView leftIvIcon;
    private TextView leftTvName;
    private TextView leftTvScore;
    private View leftViewProgress;
    private View leftViewResult;

    private View middleTopContainer;
    private TextView tvStatus;
    private View viewIssue;
    private View viewVS;
    private View viewResultDraw;

    private RoundedImageView rightIvIcon;
    private TextView rightTvName;
    private TextView rightTvScore;
    private View rightViewProgress;
    private View rightViewResult;

    private CustomCountdownTimer countdownTimer;
    private final int progressMinWidth = DensityUtils.dp2px(getContext(), 147); // 进度显示，至少保留的宽度
    private int progressCalcTotalWidth = DensityUtils.getScreenWidth() - progressMinWidth * 2; // 用于计算进度值的总可用宽度
    private RoomPkModel roomPkModel;
    private PkStatusChangeListener pkStatusChangeListener;

    public RoomPkInfoView(@NonNull Context context) {
        this(context, null);
    }

    public RoomPkInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomPkInfoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initStyles();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_room_pk_info, this);
        leftIvIcon = findViewById(R.id.left_iv_icon);
        leftTvName = findViewById(R.id.left_tv_name);
        leftTvScore = findViewById(R.id.left_tv_score);
        leftViewProgress = findViewById(R.id.left_view_progress);
        leftViewResult = findViewById(R.id.left_view_result);

        middleTopContainer = findViewById(R.id.middle_top_container);
        tvStatus = findViewById(R.id.tv_status);
        viewIssue = findViewById(R.id.view_issue);
        viewVS = findViewById(R.id.view_vs);
        viewResultDraw = findViewById(R.id.view_result_draw);

        rightIvIcon = findViewById(R.id.right_iv_icon);
        rightTvName = findViewById(R.id.right_tv_name);
        rightTvScore = findViewById(R.id.right_tv_score);
        rightViewProgress = findViewById(R.id.right_view_progress);
        rightViewResult = findViewById(R.id.right_view_result);
    }

    private void initStyles() {
        int radius = DensityUtils.dp2px(getContext(), 4);
        GradientDrawable topContainerBg = ShapeUtils.createShape(null, null,
                new float[]{0, 0, 0, 0, radius, radius, radius, radius},
                GradientDrawable.RECTANGLE, null, Color.parseColor("#80000000"));
        middleTopContainer.setBackground(topContainerBg);
    }

    /** 设置数据源 */
    public void setRoomPkModel(RoomPkModel model) {
        roomPkModel = model;
        notifyDataSetChange();
    }

    /** 刷新数据 */
    private void notifyDataSetChange() {
        // 更新左右房间信息
        RoomPkRoomInfo leftRoomInfo = getLeftRoomInfo();
        updateRoomInfo(leftRoomInfo, leftIvIcon, leftTvName, leftTvScore, leftViewProgress);
        RoomPkRoomInfo rightRoomInfo = getRightRoomInfo();
        updateRoomInfo(rightRoomInfo, rightIvIcon, rightTvName, rightTvScore, rightViewProgress);

        // 分数处理
        long leftScore = getScore(leftRoomInfo);
        long rightScore = getScore(rightRoomInfo);
        updateProgress(leftScore, rightScore);

        updateMiddleStatus();
    }

    private long getScore(RoomPkRoomInfo pkRoomInfo) {
        long rightScore;
        if (pkRoomInfo == null) {
            rightScore = 0;
        } else {
            rightScore = pkRoomInfo.score;
        }
        return rightScore;
    }

    /** 更新中间的状态显示 */
    private void updateMiddleStatus() {
        RoomPkModel model = roomPkModel;
        if (model == null) {
            tvStatus.setVisibility(View.GONE);
            viewVS.setVisibility(View.GONE);
            return;
        }
        tvStatus.setVisibility(View.VISIBLE);

        switch (model.pkStatus) {
            case PkStatus.MATCHING: // 匹配中
            case PkStatus.MATCHED: // 已匹配
                tvStatus.setText(R.string.wait_start);
                viewVS.setVisibility(View.VISIBLE);
                leftViewResult.setVisibility(View.GONE);
                rightViewResult.setVisibility(View.GONE);
                viewResultDraw.setVisibility(View.GONE);
                break;
            case PkStatus.STARTED: // 已开始
                startCountdown(model.remainSecond);
                viewVS.setVisibility(View.GONE);
                leftViewResult.setVisibility(View.GONE);
                rightViewResult.setVisibility(View.GONE);
                viewResultDraw.setVisibility(View.GONE);
                break;
            case PkStatus.PK_END: // pk结束
                tvStatus.setText(R.string.finished);
                viewVS.setVisibility(View.GONE);
                showPkResult();
                break;
            default:
                tvStatus.setVisibility(View.GONE);
                viewVS.setVisibility(View.GONE);
                leftViewResult.setVisibility(View.GONE);
                rightViewResult.setVisibility(View.GONE);
                viewResultDraw.setVisibility(View.GONE);
                break;
        }
    }

    /** 展示Pk结果 */
    private void showPkResult() {
        long leftScore = getScore(getLeftRoomInfo());
        long rightScore = getScore(getRightRoomInfo());
        if (leftScore == rightScore) { // 平局
            leftViewResult.setVisibility(View.GONE);
            rightViewResult.setVisibility(View.GONE);
            viewResultDraw.setVisibility(View.VISIBLE);
        } else if (leftScore > rightScore) { // 左边赢
            leftViewResult.setVisibility(View.VISIBLE);
            leftViewResult.setBackgroundResource(R.drawable.ic_pk_win);
            rightViewResult.setVisibility(View.VISIBLE);
            rightViewResult.setBackgroundResource(R.drawable.ic_pk_lose);
            viewResultDraw.setVisibility(View.GONE);
        } else { // 右边赢
            leftViewResult.setVisibility(View.VISIBLE);
            leftViewResult.setBackgroundResource(R.drawable.ic_pk_lose);
            rightViewResult.setVisibility(View.VISIBLE);
            rightViewResult.setBackgroundResource(R.drawable.ic_pk_win);
            viewResultDraw.setVisibility(View.GONE);
        }
    }

    /** 开启倒计时 */
    private void startCountdown(int count) {
        cancelCountdown();
        countdownTimer = new CustomCountdownTimer(count) {
            @Override
            protected void onTick(int count) {
                tvStatus.setText(FormatUtils.formatTime(count));
                if (roomPkModel != null) {
                    roomPkModel.remainSecond = count;
                }
            }

            @Override
            protected void onFinish() {
                if (roomPkModel != null) {
                    roomPkModel.pkStatus = PkStatus.PK_END;
                }
                if (pkStatusChangeListener != null) {
                    pkStatusChangeListener.onStatusChanged(PkStatus.PK_END);
                }
            }
        };
        countdownTimer.start();
    }

    private void cancelCountdown() {
        if (countdownTimer == null) return;
        countdownTimer.cancel();
        countdownTimer = null;
    }

    /** 更新房间信息 */
    private void updateRoomInfo(RoomPkRoomInfo info, RoundedImageView ivIcon, TextView tvName, TextView tvScore, View viewProgress) {
        if (info == null) {
            ivIcon.setBorderColor(ContextCompat.getColor(getContext(), R.color.c_39bdff));
            ImageLoader.loadAvatar(ivIcon, null);
            tvName.setText(R.string.seat_empty);
            tvScore.setText("0");
            viewProgress.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.c_39bdff));
        } else {
            ImageLoader.loadAvatar(ivIcon, info.roomOwnerHeader);
            tvName.setText(info.roomOwnerNickname);
            tvScore.setText(info.score + "");
            if (info.isInitiator) {
                viewProgress.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.c_ff2959));
            } else {
                viewProgress.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.c_39bdff));
            }
        }
    }

    /** 获取左边的房间信息 */
    public RoomPkRoomInfo getLeftRoomInfo() {
        if (roomPkModel == null) return null;
        if (roomPkModel.srcRoomInfo.isSelfRoom) {
            return roomPkModel.srcRoomInfo;
        }
        if (roomPkModel.destRoomInfo.isSelfRoom) {
            return roomPkModel.destRoomInfo;
        }
        return null;
    }

    /** 获取右边的房间信息 */
    public RoomPkRoomInfo getRightRoomInfo() {
        if (roomPkModel == null) return null;
        if (roomPkModel.srcRoomInfo.isSelfRoom) {
            return roomPkModel.destRoomInfo;
        }
        if (roomPkModel.destRoomInfo.isSelfRoom) {
            return roomPkModel.srcRoomInfo;
        }
        return null;
    }

    private void updateProgress(long leftScore, long rightScore) {
        //计算左边所占的比例值宽度
        float leftPercent;
        long totalNumber = leftScore + rightScore;
        if (totalNumber > 0) {
            leftPercent = (leftScore / 10000f) / (totalNumber / 10000f);
        } else {
            leftPercent = 0.5f;
        }
        ViewUtils.setWidth(leftViewProgress, (int) (progressMinWidth + progressCalcTotalWidth * leftPercent));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        progressCalcTotalWidth = getMeasuredWidth() - progressMinWidth * 2;
    }

    /** 设置PK状态变更的监听器 */
    public void setPkStatusChangeListener(PkStatusChangeListener pkStatusChangeListener) {
        this.pkStatusChangeListener = pkStatusChangeListener;
    }

    /** 设置问题按钮的点击事件 */
    public void setIssueOnClickListener(OnClickListener listener) {
        viewIssue.setOnClickListener(listener);
    }

    public interface PkStatusChangeListener {
        void onStatusChanged(int status);
    }

}
