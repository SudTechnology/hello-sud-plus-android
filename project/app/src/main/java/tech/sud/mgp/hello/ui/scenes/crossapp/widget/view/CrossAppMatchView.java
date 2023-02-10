package tech.sud.mgp.hello.ui.scenes.crossapp.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.ui.common.widget.SizeForegroundColorSpan;

/**
 * 跨域 匹配状态View
 */
public class CrossAppMatchView extends ConstraintLayout {

    private TextView tvTitle;
    private TextView tvNumber;
    private TextView tvStatus;
    private TextView tvCancelMatch;
    private TextView tvChangeGame;
    private TextView tvAnewMatch;
    private View viewOb;
    private View progressBar;

    public CrossAppMatchView(@NonNull Context context) {
        this(context, null);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setClipChildren(false);
        inflate(getContext(), R.layout.view_cross_app_match, this);
        tvTitle = findViewById(R.id.match_tv_title);
        tvNumber = findViewById(R.id.match_tv_number);
        tvStatus = findViewById(R.id.match_tv_status);
        tvCancelMatch = findViewById(R.id.match_tv_cancel_match);
        tvChangeGame = findViewById(R.id.match_tv_change_game);
        tvAnewMatch = findViewById(R.id.match_tv_anew_match);
        viewOb = findViewById(R.id.match_container_ob);
        progressBar = findViewById(R.id.match_progress_bar);
    }

    public void setCrossAppModel(CrossAppModel model) {
        boolean isJoined = false;
        boolean isCaptain = false;
        if (model.userList != null) {
            for (UserInfoResp userInfoResp : model.userList) {
                if (userInfoResp.userId == HSUserInfo.userId) {
                    isJoined = true;
                    isCaptain = userInfoResp.isCaptain;
                }
            }
        }
        switch (model.matchStatus) {
            case CrossAppMatchStatus.MATCHING:
                setMatchingData(isJoined, isCaptain, model);
                break;
            case CrossAppMatchStatus.MATCH_FAILED:
                setMatchFailedData(isJoined, isCaptain, model);
                break;
        }
    }

    private void setMatchingData(boolean isJoined, boolean isCaptain, CrossAppModel model) {
        String gameName;
        if (model.gameModel == null) {
            gameName = "";
        } else {
            gameName = model.gameModel.gameName;
        }
        tvTitle.setText(getContext().getString(R.string.matching_title, gameName));

        setMatchNumber(model);

        tvStatus.setText(R.string.looking_for_a_playmate);

        if (isJoined) {
            tvCancelMatch.setVisibility(View.VISIBLE);
            viewOb.setVisibility(View.GONE);
        } else {
            tvCancelMatch.setVisibility(View.GONE);
            viewOb.setVisibility(View.VISIBLE);
        }

        tvChangeGame.setVisibility(View.GONE);
        tvAnewMatch.setVisibility(View.GONE);
        startProgressBar();
    }

    private void setMatchFailedData(boolean isJoined, boolean isCaptain, CrossAppModel model) {
        tvNumber.setVisibility(View.GONE);
        tvStatus.setText(R.string.match_failed_hint);

        if (isJoined) {
            if (isCaptain) {
                tvTitle.setText(getContext().getString(R.string.match_failed_captain_title));
                tvCancelMatch.setVisibility(View.GONE);
                tvChangeGame.setVisibility(View.VISIBLE);
                tvAnewMatch.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setText(getContext().getString(R.string.match_failed_member_title));
                tvCancelMatch.setVisibility(View.GONE);
                tvChangeGame.setVisibility(View.GONE);
                tvAnewMatch.setVisibility(View.VISIBLE);
            }
            viewOb.setVisibility(View.GONE);
        } else {
            tvTitle.setText(getContext().getString(R.string.match_failed_captain_title));
            tvCancelMatch.setVisibility(View.GONE);
            tvChangeGame.setVisibility(View.GONE);
            tvAnewMatch.setVisibility(View.GONE);
            viewOb.setVisibility(View.VISIBLE);
        }
        stopProgressBar();
    }

    private void startProgressBar() {
        RotateAnimation animation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1000);
        progressBar.startAnimation(animation);
    }

    private void stopProgressBar() {
        Animation animation = progressBar.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
    }

    private void setMatchNumber(CrossAppModel model) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SizeForegroundColorSpan colorSpan = new SizeForegroundColorSpan(Color.parseColor("#ffffff"));
        colorSpan.setTextSize(DensityUtils.sp2px(getContext(), 18));
        builder.append(model.curNum + "", colorSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(" /");
        builder.append(model.totalNum + "");
        builder.append(getContext().getString(R.string.person));
        tvNumber.setText(builder);
        tvNumber.setVisibility(View.VISIBLE);
    }

    public void setCancelMatchOnClickListener(OnClickListener listener) {
        tvCancelMatch.setOnClickListener(listener);
    }

    public void setChangeGameOnClickListener(OnClickListener listener) {
        tvChangeGame.setOnClickListener(listener);
    }

    public void setAnewMatchOnClickListener(OnClickListener listener) {
        tvAnewMatch.setOnClickListener(listener);
    }

}
