package tech.sud.mgp.hello.ui.scenes.league.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.LeaguePlayingResp;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.common.widget.HSTopBar;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

/**
 * 联赛场景 入口
 */
public class LeagueEntranceActivity extends BaseActivity {

    private HSTopBar topBar;
    private TextView tvExample;
    private View viewPromoted;
    private TextView tvEntrance;
    private GameModel gameModel;
    private LeaguePlayingResp leaguePlayingResp;

    @Override
    protected boolean beforeSetContentView() {
        Serializable gameModelSeri = getIntent().getSerializableExtra(RequestKey.KEY_GAME_MODEL);
        if (gameModelSeri instanceof GameModel) {
            gameModel = (GameModel) gameModelSeri;
        } else {
            return true;
        }
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_league_entrance;
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(false).init();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topBar = findViewById(R.id.top_bar);
        tvExample = findViewById(R.id.tv_example);
        viewPromoted = findViewById(R.id.view_promoted);
        tvEntrance = findViewById(R.id.tv_entrance);

        TextView tvAwardInfo = findViewById(R.id.tv_award_info);
        tvAwardInfo.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(20),
                null, GradientDrawable.RECTANGLE, null, Color.parseColor("#4d000000")));

        int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
        ViewUtils.addMarginTop(topBar, statusBarHeight);

        viewPromoted.setVisibility(View.GONE);

        topBar.setTitle(gameModel.leagueSceneName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshIsPlaying();
    }

    private void refreshIsPlaying() {
        RoomRepository.leaguePlaying(this, gameModel.gameId, new RxCallback<LeaguePlayingResp>() {
            @Override
            public void onSuccess(LeaguePlayingResp resp) {
                super.onSuccess(resp);
                leaguePlayingResp = resp;
                updatePageStyle();
            }
        });
    }

    // 更新页面样式
    private void updatePageStyle() {
        Long playingRoomId = getPlayingRoomId();
        if (playingRoomId == null) {
            tvEntrance.setText(R.string.immediate_entry);
            viewPromoted.setVisibility(View.GONE);
        } else {
            tvEntrance.setText(R.string.continue_play);
            viewPromoted.setVisibility(View.VISIBLE);
        }
    }

    // 获取进行中的房间id
    private Long getPlayingRoomId() {
        if (leaguePlayingResp != null) {
            return leaguePlayingResp.roomId;
        }
        return null;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LeagueExampleActivity.class));
            }
        });
        tvEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEntrance();
            }
        });
    }

    // 点击了进入
    private void onClickEntrance() {
        Long playingRoomId = getPlayingRoomId();
        if (playingRoomId == null) {
            matchRoom();
        } else {
            EnterRoomUtils.enterRoom(this, playingRoomId);
            refreshIsPlaying();
        }
    }

    private void matchRoom() {
        HomeRepository.matchGame(SceneType.LEAGUE, gameModel.gameId, null, this, new RxCallback<MatchRoomModel>() {
            @Override
            public void onSuccess(MatchRoomModel matchRoomModel) {
                super.onSuccess(matchRoomModel);
                if (matchRoomModel != null) {
                    EnterRoomUtils.enterRoom(context, matchRoomModel.roomId);
                }
            }
        });
    }

}
