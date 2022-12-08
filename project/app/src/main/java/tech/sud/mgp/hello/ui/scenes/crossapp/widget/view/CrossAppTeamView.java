package tech.sud.mgp.hello.ui.scenes.crossapp.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;

/**
 * 跨域 组队状态View
 */
public class CrossAppTeamView extends ConstraintLayout {

    private ImageView ivIcon;
    private TextView tvTitle;
    private CrossAppStallView crossAppStallView;
    private View viewExitTeam;
    private View viewFastMatch;
    private View viewJoin;
    private View viewWaiting;

    public CrossAppTeamView(@NonNull Context context) {
        this(context, null);
    }

    public CrossAppTeamView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossAppTeamView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CrossAppTeamView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_cross_app_team, this);
        ivIcon = findViewById(R.id.team_iv_icon);
        tvTitle = findViewById(R.id.team_tv_title);
        crossAppStallView = findViewById(R.id.team_cross_app_stall_view);
        viewExitTeam = findViewById(R.id.team_tv_exit_team);
        viewFastMatch = findViewById(R.id.team_container_fast_match);
        viewJoin = findViewById(R.id.team_tv_join);
        viewWaiting = findViewById(R.id.team_tv_wait_start);
    }

    private void cycleTestData(int count) {
        List<UserInfoResp> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new UserInfoResp());
        }
        setStallDatas(list);
        postDelayed(() -> {
            cycleTestData(count + 1);
        }, 5000);
    }

    /** 设置跨域 数据 */
    public void setCrossAppModel(CrossAppModel model) {
        setStallDatas(model.userList);
        setGameModel(model.gameModel);
        setBtnStatus(model);
    }

    private void setBtnStatus(CrossAppModel model) {
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

        if (isJoined) {
            if (isCaptain) { // 已加入-队长
                viewExitTeam.setVisibility(View.VISIBLE);
                viewFastMatch.setVisibility(View.VISIBLE);
                viewJoin.setVisibility(View.GONE);
                viewWaiting.setVisibility(View.GONE);
            } else { // 已加入-队员
                viewExitTeam.setVisibility(View.VISIBLE);
                viewFastMatch.setVisibility(View.GONE);
                viewJoin.setVisibility(View.GONE);
                viewWaiting.setVisibility(View.VISIBLE);
            }
        } else { // 未加入
            viewExitTeam.setVisibility(View.GONE);
            viewFastMatch.setVisibility(View.GONE);
            viewJoin.setVisibility(View.VISIBLE);
            viewWaiting.setVisibility(View.GONE);
        }
    }

    /** 设置当前匹配的游戏 */
    private void setGameModel(GameModel model) {
        if (model != null) {
            setGameIcon(model.gamePic);
            setGameName(model.gameName);
        }
    }

    /** 设置车位数据 */
    private void setStallDatas(List<UserInfoResp> list) {
        crossAppStallView.setDatas(list);
    }

    private void setGameIcon(String url) {
        ImageLoader.loadImage(ivIcon, url);
    }

    private void setGameName(String name) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (name != null) {
            builder.append(name);
        }
        builder.append(" ");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#99ffffff"));
        builder.append(getContext().getString(R.string.game_team), colorSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTitle.setText(builder);
    }

    public void setOnClickStallListener(CrossAppStallView.OnClickStallListener onClickStallListener) {
        crossAppStallView.setOnClickStallListener(onClickStallListener);
    }

    public void setExitTeamOnClickListener(OnClickListener listener) {
        viewExitTeam.setOnClickListener(listener);
    }

    public void setTeamFastMatchOnClickListener(OnClickListener listener) {
        viewFastMatch.setOnClickListener(listener);
    }

    public void setJoinTeamOnClickListener(OnClickListener listener) {
        viewJoin.setOnClickListener(listener);
    }

}
