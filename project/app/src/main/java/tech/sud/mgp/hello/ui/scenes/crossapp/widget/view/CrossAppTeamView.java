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
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;

/**
 * 跨域 组队状态View
 */
public class CrossAppTeamView extends ConstraintLayout {

    private ImageView ivIcon;
    private TextView tvTitle;
    private CrossAppStallView crossAppStallView;
    private View viewExitMatch;
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
        viewExitMatch = findViewById(R.id.team_tv_exit_team);
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

    /** 设置当前匹配的游戏 */
    public void setGameModel(GameModel model) {
        if (model != null) {
            setIcon(model.gamePic);
            setGameName(model.gameName);
        }
    }

    /** 设置车位数据 */
    public void setStallDatas(List<UserInfoResp> list) {
        crossAppStallView.setDatas(list);
    }

    private void setIcon(String url) {
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

}
