package tech.sud.mgp.hello.ui.scenes.ticket.activity;

import android.view.View;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ClickUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.Serializable;
import java.util.ArrayList;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.common.widget.HSTopBar;
import tech.sud.mgp.hello.ui.main.base.constant.GameLevel;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;
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

    private final TicketLevelViewModel viewModel = new TicketLevelViewModel();

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
        ImmersionBar.setTitleBarMarginTop(this, topBar);
    }

    @Override
    protected void initData() {
        super.initData();
        topBar.setTitle(params.gameName);
        initNotices();

    }

    /** 初始化公告栏数据 */
    private void initNotices() {
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.ticket_level_notice, "沐辰", "900"));
        list.add(getString(R.string.ticket_level_notice, "安小六", "20"));
        list.add(getString(R.string.ticket_level_notice, "兔兔", "250"));
        list.add(getString(R.string.ticket_level_notice, "Toby", "900"));
        list.add(getString(R.string.ticket_level_notice, "Jennie", "900"));
        list.add(getString(R.string.ticket_level_notice, "Bell", "250"));
        tvNotice.setDatas(list);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        ClickUtils.applyGlobalDebouncing(new View[]{
                findViewById(R.id.level_view_primary),
                findViewById(R.id.level_view_middle),
                findViewById(R.id.level_view_high)
        }, this);
        viewModel.matchRoomLiveData.observe(this, new Observer<MatchRoomModel>() {
            @Override
            public void onChanged(MatchRoomModel matchRoomModel) {
                EnterRoomUtils.enterRoom(context, matchRoomModel.roomId);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.level_view_primary) { // 初级
            viewModel.matchRoom(params.sceneType, params.gameId, GameLevel.PRIMARY, this);
        } else if (id == R.id.level_view_middle) { // 中级
            viewModel.matchRoom(params.sceneType, params.gameId, GameLevel.MIDDLE, this);
        } else if (id == R.id.level_view_high) { // 高级
            viewModel.matchRoom(params.sceneType, params.gameId, GameLevel.HIGH, this);
        }
    }

}