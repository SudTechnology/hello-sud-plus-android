package tech.sud.mgp.hello.ui.scenes.quiz.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
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

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.room.resp.QuizGamePlayerResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;

/**
 * 竞猜场景 结算页面
 */
public class QuizSettleDialog extends BaseDialogFragment {

    private List<QuizGamePlayerResp.Player> playerList; // 游戏玩家列表
    private SudMGPMGState.MGCommonGameSettle gameSettle; // 游戏结算状态

    private final MyAdapter adapter = new MyAdapter();

    private TextView tvTitle;
    private View viewWin; // 顶部，猜中了的布局
    private TextView tvWinCoin;
    private TextView tvWinHint;
    private View viewLose; // 顶部，未猜中的布局
    private View viewNotJoin; // 顶部，未参与的布局
    private TextView tvNotJoinTitle;

    private RecyclerView recyclerView;
    private TextView tvClose;
    private View viewPlayerOperate;
    private TextView tvPlayerClose;
    private TextView tvPlayerAgain;

    private CustomCountdownTimer countdownTimer;

    private View.OnClickListener againOnClickListener;

    public static QuizSettleDialog newInstance(List<QuizGamePlayerResp.Player> playerList, SudMGPMGState.MGCommonGameSettle gameSettle) {
        Bundle args = new Bundle();
        MyParams params = new MyParams();
        params.playerList = playerList;
        params.gameSettle = gameSettle;
        args.putSerializable("params", params);
        QuizSettleDialog fragment = new QuizSettleDialog();
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
                playerList = params.playerList;
                gameSettle = params.gameSettle;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_game_settle;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(296);
    }

    @Override
    protected boolean canceledOnTouchOutside() {
        return false;
    }

    @Override
    protected boolean beforeSetContentView() {
        if (playerList == null || gameSettle == null ||
                gameSettle.results == null || gameSettle.results.size() == 0) {
            return true;
        }
        return super.beforeSetContentView();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvTitle = findViewById(R.id.tv_title);
        viewWin = findViewById(R.id.ll_top_win);
        tvWinCoin = findViewById(R.id.tv_win_coin);
        tvWinHint = findViewById(R.id.tv_win_hint);
        viewLose = findViewById(R.id.ll_top_lose);
        viewNotJoin = findViewById(R.id.ll_top_not_join);
        tvNotJoinTitle = findViewById(R.id.tv_not_join_title);
        recyclerView = findViewById(R.id.recycler_view);
        tvClose = findViewById(R.id.tv_close);
        viewPlayerOperate = findViewById(R.id.ll_player_operate);
        tvPlayerClose = findViewById(R.id.tv_player_close);
        tvPlayerAgain = findViewById(R.id.tv_player_again);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        super.initData();
        // 整理数据
        boolean isJoin = false; // 是否加入了竞猜
        boolean isWin = false; // 是否赢了
        boolean selfIsPlayer = getPlayer(HSUserInfo.userId + "") != null; // 自己是否是玩家
        List<SettleRankingModel> list = new ArrayList<>();
        for (int i = 0; i < gameSettle.results.size(); i++) {
            SudMGPMGState.MGCommonGameSettle.PlayerResult result = gameSettle.results.get(i);
            if (result.isAI == 1) {
                continue;
            }
            QuizGamePlayerResp.Player player = getPlayer(result.uid);

            SettleRankingModel model = new SettleRankingModel();
            model.userId = result.uid;
            model.rank = result.rank;
            model.award = result.award;
            model.score = result.score;
            if (player != null) {
                model.name = player.nickname;
                model.avatar = player.header;
                model.support = player.support;
                model.isSelf = (HSUserInfo.userId + "").equals(result.uid);
                if (player.support) {
                    isJoin = true;
                    if (result.rank == 1) {
                        isWin = true;
                    }
                }
            }
            list.add(model);
        }
        adapter.setList(list);

        if (selfIsPlayer) { // 自己是玩家
            if (isWin) { // 赢了
                viewWin.setVisibility(View.VISIBLE);
                viewLose.setVisibility(View.GONE);
                viewNotJoin.setVisibility(View.GONE);
                tvTitle.setText(R.string.congratulation_win);
                tvWinCoin.setText("+" + APPConfig.QUIZ_WIN_COUNT);
                tvWinHint.setText(R.string.quiz_player_win_hint);
            } else if (isJoin) { // 输了
                viewWin.setVisibility(View.GONE);
                viewLose.setVisibility(View.VISIBLE);
                viewNotJoin.setVisibility(View.GONE);
                tvTitle.setText(R.string.the_next_refueling);
            } else { // 未参与
                viewWin.setVisibility(View.GONE);
                viewLose.setVisibility(View.GONE);
                viewNotJoin.setVisibility(View.VISIBLE);
                tvTitle.setText(R.string.not_quiz);
                tvNotJoinTitle.setText(R.string.not_quiz_player_hint);
            }
            viewPlayerOperate.setVisibility(View.VISIBLE);
            tvClose.setVisibility(View.GONE);
        } else { // 自己是观众
            if (isWin) { // 赢了
                viewWin.setVisibility(View.VISIBLE);
                viewLose.setVisibility(View.GONE);
                viewNotJoin.setVisibility(View.GONE);
                tvTitle.setText(R.string.guess_correctly);
                tvWinCoin.setText("+" + APPConfig.QUIZ_WIN_COUNT);
                tvWinHint.setText(R.string.quiz_audience_win_hint);
            } else if (isJoin) { // 输了
                viewWin.setVisibility(View.GONE);
                viewLose.setVisibility(View.VISIBLE);
                viewNotJoin.setVisibility(View.GONE);
                tvTitle.setText(R.string.the_next_refueling);
            } else { // 未参与
                viewWin.setVisibility(View.GONE);
                viewLose.setVisibility(View.GONE);
                viewNotJoin.setVisibility(View.VISIBLE);
                tvTitle.setText(R.string.not_quiz);
                tvNotJoinTitle.setText(R.string.not_quiz_audience_hint);
            }
            viewPlayerOperate.setVisibility(View.GONE);
            tvClose.setVisibility(View.VISIBLE);
        }

        startCountdown();
    }

    public void startCountdown() {
        cancelCountdown();
        countdownTimer = new CustomCountdownTimer(10) {
            @Override
            protected void onTick(int count) {
                Context context = getContext();
                if (context == null) {
                    return;
                }
                String info = context.getString(R.string.close) + "(" + count + ")";
                tvClose.setText(info);
                tvPlayerClose.setText(info);
            }

            @Override
            protected void onFinish() {
                LifecycleUtils.safeLifecycle(QuizSettleDialog.this, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        dismiss();
                    }
                });
            }
        };
        countdownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountdown();
    }

    public void cancelCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvPlayerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvPlayerAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (againOnClickListener != null) {
                    againOnClickListener.onClick(v);
                }
            }
        });
    }

    /** 设置再来一局的点击监听器 */
    public void setAgainOnClickListener(View.OnClickListener listener) {
        againOnClickListener = listener;
    }

    private QuizGamePlayerResp.Player getPlayer(String userId) {
        for (QuizGamePlayerResp.Player player : playerList) {
            if (userId != null && userId.equals(player.userId + "")) {
                return player;
            }
        }
        return null;
    }

    private static class MyParams implements Serializable {
        public List<QuizGamePlayerResp.Player> playerList; // 游戏玩家列表
        public SudMGPMGState.MGCommonGameSettle gameSettle; // 游戏结算状态
    }

    private static class MyAdapter extends BaseQuickAdapter<SettleRankingModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_quiz_settle);
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

            View viewHaveSupport = holder.getView(R.id.ll_have_support);
            if (!(HSUserInfo.userId + "").equals(model.userId) && model.support) {
                viewHaveSupport.setVisibility(View.VISIBLE);
            } else {
                viewHaveSupport.setVisibility(View.GONE);
            }

            View viewMain = holder.getView(R.id.cl_main);
            if (model.isSelf) {
                viewMain.setBackgroundResource(R.drawable.shape_r4_ffd16c);
            } else {
                viewMain.setBackground(null);
            }
        }
    }

    private static class SettleRankingModel {
        public String userId; // 用户id
        public String name; // 昵称
        public int rank; // 排名 从 1 开始
        public int award; // 奖励
        public int score; // 积分
        public String avatar; // 头像
        public boolean support; // 是否已支持
        public boolean isSelf; // 是否是自己
    }

}
