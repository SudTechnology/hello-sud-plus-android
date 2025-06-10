package tech.sud.mgp.hello.ui.scenes.league.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ViewUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState.MGCommonGameSettle;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.LeagueModel;

/**
 * 联赛 游戏结算状态 弹窗
 */
public class LeagueGameSettleDialog extends BaseDialogFragment {

    private TextView tvBtn;
    private View viewOb;
    private View viewExit;
    private View viewTop;
    private View viewWin;
    private TextView tvOBContinue;
    private TextView tvTitle;

    public MGCommonGameSettle gameSettle; // 游戏结算状态
    public LeagueModel leagueModel; // 联赛信息
    private MyAdapter adapter;
    private CustomCountdownTimer countdownTimer;
    private int countdowmCount = 10;

    private LeagueGameSettleEventListener eventListener;

    public static LeagueGameSettleDialog newInstance(MGCommonGameSettle gameSettle, LeagueModel leagueModel) {
        Bundle args = new Bundle();
        MyParams params = new MyParams();
        params.gameSettle = gameSettle;
        params.leagueModel = leagueModel;
        args.putSerializable("params", params);
        LeagueGameSettleDialog fragment = new LeagueGameSettleDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            MyParams params = (MyParams) arguments.getSerializable("params");
            if (params != null) {
                gameSettle = params.gameSettle;
                leagueModel = params.leagueModel;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_league_game_settle;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(296);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView tvCoin = findViewById(R.id.tv_coin);
        tvBtn = findViewById(R.id.tv_btn);
        viewOb = findViewById(R.id.view_ob);
        viewExit = findViewById(R.id.tv_ob_exit);
        tvOBContinue = findViewById(R.id.tv_ob_continue);
        tvTitle = findViewById(R.id.tv_title);
        viewTop = findViewById(R.id.view_top);
        viewWin = findViewById(R.id.view_win);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        tvCoin.setText(getString(R.string.gold_coin_count, "1000000"));
    }

    @Override
    protected void initData() {
        super.initData();
        if (gameSettle == null || leagueModel == null) {
            dismiss();
            return;
        }
        initRankingList();
        viewWin.setVisibility(View.GONE);
        if (isPlayer()) {
            initPlayerStyle();
        } else {
            initOBStyle();
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvOBContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    // 玩家展示的样式
    private void initPlayerStyle() {
        if (leagueModel.schedule == 0) { // 第一局
            if (isWin()) {
                viewTop.setBackgroundResource(R.drawable.ic_league_settle_top_win);
                tvTitle.setText(R.string.enter_top_three);
                startContinueGameCountdown();
                setBtnDismissOnClickListener();
            } else {
                viewTop.setBackgroundResource(R.drawable.ic_league_settle_top_end);
                tvTitle.setText(R.string.enter_top_three_failed);
                btnSetBackHomePage();
            }
        } else { // 决赛
            if (isWin()) {
                viewTop.setBackgroundResource(R.drawable.ic_league_settle_top_win);
                tvTitle.setText(R.string.win_the_first);
                viewWin.setVisibility(View.VISIBLE);
            } else {
                viewTop.setBackgroundResource(R.drawable.ic_league_settle_top_win);
                tvTitle.setText(R.string.win_the_first_failed);
            }
            btnSetBackHomePage();
        }
    }

    private void setBtnDismissOnClickListener() {
        tvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void btnSetBackHomePage() {
        tvBtn.setText(R.string.back_home_page);
        tvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callbackBackHomePage();
            }
        });
    }

    private void callbackBackHomePage() {
        if (eventListener != null) {
            eventListener.onBackHomePage();
        }
    }

    // 继续比赛及继续观看倒计时
    private void startContinueGameCountdown() {
        cancelCountdown();
        countdownTimer = new CustomCountdownTimer(countdowmCount) {
            @Override
            protected void onTick(int count) {
                String countStr = count + "";
                tvBtn.setText(getString(R.string.continue_game_countdown, countStr));
                tvOBContinue.setText(getString(R.string.continue_watch, countStr));
            }

            @Override
            protected void onFinish() {
                dismissAllowingStateLoss();
            }
        };
        countdownTimer.start();
    }

    private void cancelCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountdown();
    }

    // ob视角展示的样式
    private void initOBStyle() {
        viewTop.setBackgroundResource(R.drawable.ic_league_settle_top_end);
        // 如果前三都是机器人，那么观察者提示比赛已结束
        if (leagueModel.schedule > 0 || top3IsRobot()) {
            tvTitle.setText(R.string.game_over);
            btnSetBackHomePage();
        } else {
            tvTitle.setText(R.string.enter_next_game);
            viewOb.setVisibility(View.VISIBLE);
            tvBtn.setVisibility(View.GONE);
            viewExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (eventListener != null) {
                        eventListener.onFinish();
                    }
                }
            });
            startContinueGameCountdown();
        }
    }

    private boolean top3IsRobot() {
        if (gameSettle.results == null || gameSettle.results.size() == 0) {
            return true;
        }
        boolean isRobot1 = true;
        boolean isRobot2 = true;
        boolean isRobot3 = true;
        for (MGCommonGameSettle.PlayerResult result : gameSettle.results) {
            switch (result.rank) {
                case 1:
                    if (result.isAI != 1) {
                        isRobot1 = false;
                    }
                    break;
                case 2:
                    if (result.isAI != 1) {
                        isRobot2 = false;
                    }
                    break;
                case 3:
                    if (result.isAI != 1) {
                        isRobot3 = false;
                    }
                    break;
            }
        }
        return isRobot1 && isRobot2 && isRobot3;
    }

    // 判断是否赢了比赛
    private boolean isWin() {
        String userId = HSUserInfo.userId + "";
        if (gameSettle.results != null) {
            for (MGCommonGameSettle.PlayerResult result : gameSettle.results) {
                if (userId.equals(result.uid)) {
                    if (leagueModel.schedule == 0) {
                        return result.rank <= 3;
                    } else {
                        return result.rank <= 1;
                    }
                }
            }
        }
        return false;
    }

    private boolean isPlayer() {
        String userId = HSUserInfo.userId + "";
        if (gameSettle.results != null) {
            for (MGCommonGameSettle.PlayerResult result : gameSettle.results) {
                if (userId.equals(result.uid)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initRankingList() {
        if (gameSettle.results == null || gameSettle.results.size() == 0) {
            return;
        }
        List<Long> list = new ArrayList<>();
        for (MGCommonGameSettle.PlayerResult result : gameSettle.results) {
            try {
                list.add(Long.parseLong(result.uid));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        UserInfoRepository.getUserInfoList(this, list, new UserInfoRepository.UserInfoListResultListener() {
            @Override
            public void userInfoList(List<UserInfoResp> userInfos) {
                LifecycleUtils.safeLifecycle(LeagueGameSettleDialog.this, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        getUserInfoListCompleted(userInfos);
                    }
                });
            }
        });
    }

    private void getUserInfoListCompleted(List<UserInfoResp> userInfos) {
        List<SettleRankingModel> list = new ArrayList<>();
        String selfUserId = HSUserInfo.userId + "";
        for (MGCommonGameSettle.PlayerResult result : gameSettle.results) {
            UserInfoResp user = findUser(userInfos, result.uid);
            SettleRankingModel model = new SettleRankingModel();
            model.userId = result.uid;
            model.rank = result.rank;
            model.award = result.award;
            model.score = result.score;
            if (user != null) {
                model.avatar = user.avatar;
                model.name = user.nickname;
            }
            model.isSelf = selfUserId.equals(result.uid);
            if (leagueModel.schedule == 0) {
                model.isEliminate = model.rank > 3;
            } else {
                model.isEliminate = model.rank > 1;
            }
            list.add(model);
        }
        adapter.setList(list);
    }

    private UserInfoResp findUser(List<UserInfoResp> userInfos, String uid) {
        if (userInfos == null || userInfos.size() == 0) {
            return null;
        }
        for (UserInfoResp userInfo : userInfos) {
            if ((userInfo.userId + "").equals(uid)) {
                return userInfo;
            }
        }
        return null;
    }

    private static class MyAdapter extends BaseQuickAdapter<SettleRankingModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_league_settle);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, SettleRankingModel model) {
            View viewRanking = holder.getView(R.id.view_ranking);
            boolean layoutRtl = ViewUtils.isLayoutRtl();
            if (layoutRtl) {
                viewRanking.setRotation(60);
            } else {
                viewRanking.setRotation(0);
            }
            switch (model.rank) {
                case 1:
                    viewRanking.setBackgroundResource(R.drawable.ic_quiz_settle_ranking_1);
                    break;
                case 2:
                    viewRanking.setBackgroundResource(R.drawable.ic_quiz_settle_ranking_2);
                    break;
                case 3:
                    viewRanking.setBackgroundResource(R.drawable.ic_quiz_settle_ranking_3);
                    break;
                default:
                    viewRanking.setBackground(null);
                    break;
            }

            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, model.avatar);

            holder.setText(R.id.tv_name, model.name);
            holder.setText(R.id.tv_score, "+" + model.score);
            holder.setText(R.id.tv_award, "+" + model.award);

            View viewMain = holder.getView(R.id.cl_main);
            if (model.isSelf) {
                viewMain.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(4),
                        null, GradientDrawable.RECTANGLE, null, Color.parseColor("#26ebd095")));
            } else {
                viewMain.setBackground(null);
            }

            View viewEliminate = holder.getView(R.id.tv_eliminate);
            viewEliminate.setVisibility(model.isEliminate ? View.VISIBLE : View.GONE);
        }
    }

    private static class SettleRankingModel {
        public String userId; // 用户id
        public String name; // 昵称
        public int rank; // 排名 从 1 开始
        public int award; // 奖励
        public int score; // 积分
        public String avatar; // 头像
        public boolean isSelf; // 是否是自己
        public boolean isEliminate; // 是否淘汰了
    }

    private static class MyParams implements Serializable {
        public MGCommonGameSettle gameSettle; // 游戏结算状态
        public LeagueModel leagueModel; // 联赛信息
    }

    @Override
    protected boolean canceledOnTouchOutside() {
        return false;
    }

    @Override
    protected boolean cancelable() {
        return false;
    }

    public void setEventListener(LeagueGameSettleEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public interface LeagueGameSettleEventListener {

        /** 退出 */
        void onFinish();

        /** 点击回到首页 */
        void onBackHomePage();
    }

}
