package tech.sud.mgp.hello.ui.scenes.ticket.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ClickUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.AnimUtils;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.common.widget.HSTopBar;
import tech.sud.mgp.hello.ui.main.constant.GameLevel;
import tech.sud.mgp.hello.ui.main.home.MatchRoomModel;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;
import tech.sud.mgp.hello.ui.scenes.ticket.model.TicketLevelParams;
import tech.sud.mgp.hello.ui.scenes.ticket.viewmodel.TicketLevelViewModel;
import tech.sud.mgp.hello.ui.scenes.ticket.widget.TicketLevelNoticeView;

/**
 * 选择门票场景级别的页面
 */
public class TicketLevelActivity extends BaseActivity implements View.OnClickListener {

    private TicketLevelParams params;
    private HSTopBar topBar;
    private TicketLevelNoticeView tvNotice;
    private View clSmall;
    private TextView tvWinAwardSmall;
    private FrameLayout flAvatarsSmall;
    private TextView tvCountSmall;
    private TextView tvJoinSmall;
    private View clMiddle;
    private TextView tvWinAwardMiddle;
    private FrameLayout flAvatarsMiddle;
    private TextView tvCountMiddle;
    private TextView tvJoinMiddle;
    private View clHigh;
    private TextView tvWinAwardHigh;
    private FrameLayout flAvatarsHigh;
    private TextView tvCountHigh;
    private TextView tvJoinHigh;

    private TicketLevelViewModel viewModel = new TicketLevelViewModel();

    @Override
    protected boolean beforeSetContentView() {
        Serializable serializableParams = getIntent().getSerializableExtra(RequestKey.TICKET_LEVEL_PARAMS);
        if (serializableParams instanceof TicketLevelParams) {
            params = (TicketLevelParams) serializableParams;
        }
        if (params == null) {
            return true;
        }
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ticket_level;
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(false).init();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topBar = findViewById(R.id.top_bar);
        tvNotice = findViewById(R.id.tv_notice);
        clSmall = findViewById(R.id.cl_small);
        tvWinAwardSmall = findViewById(R.id.tv_win_award_small);
        flAvatarsSmall = findViewById(R.id.fl_avatars_small);
        tvCountSmall = findViewById(R.id.tv_count_people_small);
        tvJoinSmall = findViewById(R.id.tv_join_small);
        clMiddle = findViewById(R.id.cl_middle);
        tvWinAwardMiddle = findViewById(R.id.tv_win_award_middle);
        flAvatarsMiddle = findViewById(R.id.fl_avatars_middle);
        tvCountMiddle = findViewById(R.id.tv_count_people_middle);
        tvJoinMiddle = findViewById(R.id.tv_join_middle);
        clHigh = findViewById(R.id.cl_high);
        tvWinAwardHigh = findViewById(R.id.tv_win_award_high);
        flAvatarsHigh = findViewById(R.id.fl_avatars_high);
        tvCountHigh = findViewById(R.id.tv_count_people_high);
        tvJoinHigh = findViewById(R.id.tv_join_high);
        ImmersionBar.setTitleBarMarginTop(this, topBar);
        startJoinAnim();
    }

    private void startJoinAnim() {
        AnimUtils.breathe(tvJoinSmall);
        AnimUtils.breathe(tvJoinMiddle);
        AnimUtils.breathe(tvJoinHigh);
    }

    @Override
    protected void initData() {
        super.initData();
        topBar.setTitle(params.gameName);
        int avatarSize = DensityUtils.dp2px(context, 24);
        tvNotice.setDatas(getResources().getStringArray(R.array.ticket_level_notices));
        // 初级
        tvWinAwardSmall.setText(getString(R.string.win_multiple_award, 10));
        addAvatars(flAvatarsSmall, avatarSize);
        tvCountSmall.setText(getString(R.string.count_people_play, "87367"));
        // 中级
        tvWinAwardMiddle.setText(getString(R.string.win_multiple_award, 50));
        addAvatars(flAvatarsMiddle, avatarSize);
        tvCountMiddle.setText(getString(R.string.count_people_play, "85787"));
        // 高级
        tvWinAwardHigh.setText(getString(R.string.win_multiple_award, 90));
        addAvatars(flAvatarsHigh, avatarSize);
        tvCountHigh.setText(getString(R.string.count_people_play, "98759"));
    }

    private void addAvatars(FrameLayout container, int avatarSize) {
        for (int i = 0; i < 4; i++) {
            View view = new View(this);
            switch (i) {
                case 0:
                    view.setBackgroundResource(R.drawable.ic_ticket_avatar_1);
                    break;
                case 1:
                    view.setBackgroundResource(R.drawable.ic_ticket_avatar_2);
                    break;
                case 2:
                    view.setBackgroundResource(R.drawable.ic_ticket_avatar_3);
                    break;
                case 3:
                    view.setBackgroundResource(R.drawable.ic_ticket_avatar_4);
                    break;
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(avatarSize, avatarSize);
            params.setMarginStart(DensityUtils.dp2px(context, 16) * i);
            container.addView(view, 0, params);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        ClickUtils.applyGlobalDebouncing(new View[]{clSmall, clMiddle, clHigh}, this);
        viewModel.matchRoomLiveData.observe(this, new Observer<MatchRoomModel>() {
            @Override
            public void onChanged(MatchRoomModel matchRoomModel) {
                EnterRoomUtils.enterRoom(context, matchRoomModel.roomId);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == clSmall) { // 初级
            viewModel.matchRoom(params.sceneType, params.gameId, GameLevel.PRIMARY, this);
        } else if (v == clMiddle) { // 中级
            viewModel.matchRoom(params.sceneType, params.gameId, GameLevel.MIDDLE, this);
        } else if (v == clHigh) { // 高级
            viewModel.matchRoom(params.sceneType, params.gameId, GameLevel.HIGH, this);
        }
    }

}