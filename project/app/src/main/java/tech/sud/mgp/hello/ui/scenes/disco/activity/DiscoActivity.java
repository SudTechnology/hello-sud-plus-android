package tech.sud.mgp.hello.ui.scenes.disco.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.disco.viewmodel.DiscoGameViewModel;
import tech.sud.mgp.hello.ui.scenes.disco.widget.ContributionRankingDialog;
import tech.sud.mgp.hello.ui.scenes.disco.widget.DancingMenuDialog;
import tech.sud.mgp.hello.ui.scenes.disco.widget.DiscoRankingView;

/**
 * 蹦迪 场景
 */
public class DiscoActivity extends AbsAudioRoomActivity<DiscoGameViewModel> {

    private TextView tvStartDisco;
    private TextView tvCloseDisco;
    private TextView tvDencingMenu;
    private TextView tvDanceWait;

    private DiscoRankingView discoRankingView;

    private DancingMenuDialog dancingMenuDialog;
    private ContributionRankingDialog contributionRankingDialog;

    @Override
    protected DiscoGameViewModel initGameViewModel() {
        return new DiscoGameViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_disco;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        roomConfig.isShowGameNumber = false; // 不显示游戏人数

        tvDencingMenu = findViewById(R.id.tv_dancing_menu);
        tvDanceWait = findViewById(R.id.tv_dance_send_hint);

        int paddingHorizontal = DensityUtils.dp2px(this, 8);
        int textColor = Color.parseColor("#ffffff");
        int marginEnd = DensityUtils.dp2px(this, 11);

        // 初始化排行榜的View
        discoRankingView = new DiscoRankingView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(marginEnd);
        topView.addCustomView(discoRankingView, params);
        discoRankingView.setVisibility(View.GONE);

        // 开启蹦迪按钮
        tvStartDisco = createTopTextView(paddingHorizontal, textColor, marginEnd);
        tvStartDisco.setText(R.string.start_disco);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                getResources().getIntArray(R.array.gradient_color_disco));
        tvStartDisco.setBackground(gradientDrawable);
        topView.addCustomView(tvStartDisco, (LinearLayout.LayoutParams) tvStartDisco.getLayoutParams());
        tvStartDisco.setVisibility(View.GONE);

        // 关闭蹦迪按钮
        tvCloseDisco = createTopTextView(paddingHorizontal, textColor, marginEnd);
        tvCloseDisco.setText(R.string.close_disco);
        tvCloseDisco.setBackgroundColor(Color.parseColor("#33ffffff"));
        topView.addCustomView(tvCloseDisco, (LinearLayout.LayoutParams) tvCloseDisco.getLayoutParams());
        tvCloseDisco.setVisibility(View.GONE);

        // 不展示选择游戏
        topView.setSelectGameVisible(false);

        bringToFrontViews();
    }

    private TextView createTopTextView(int paddingHorizontal, int textColor, int marginEnd) {
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        tv.setTextSize(12);
        tv.setTextColor(textColor);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                DensityUtils.dp2px(this, 20));
        params.setMarginEnd(marginEnd);
        tv.setLayoutParams(params);
        return tv;
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (roomInfoModel.roleType == RoleType.OWNER) {
            if (playingGameId > 0) {
                tvStartDisco.setVisibility(View.GONE);
                tvCloseDisco.setVisibility(View.VISIBLE);
            } else {
                tvStartDisco.setVisibility(View.VISIBLE);
                tvCloseDisco.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        roomInfoModel.initGameId = roomInfoModel.gameId;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvStartDisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDisco();
            }
        });
        tvCloseDisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDisco();
            }
        });
        tvDencingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDancingMenu();
            }
        });

        gameViewModel.gameStartedLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                // 游戏开始后，自动加入舞池，这里delay是为了让游戏状态能同步
                tvCloseDisco.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gameViewModel.joinDancingFloor(null);
                    }
                }, 1000);
            }
        });

        discoRankingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRanking();
            }
        });
    }

    private void showRanking() {
        ContributionRankingDialog dialog = new ContributionRankingDialog();
        if (binder != null) {
            dialog.setDatas(binder.getDiscoContribution());
        }
        contributionRankingDialog = dialog;
        dialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                contributionRankingDialog = null;
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void showDancingMenu() {
        if (dancingMenuDialog != null) {
            return;
        }
        DancingMenuDialog dialog = new DancingMenuDialog();
        dialog.show(getSupportFragmentManager(), null);
        if (binder != null) {
            dialog.notifyDataSetChange(binder.getDanceList());
        }
        dancingMenuDialog = dialog;
        dialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                dancingMenuDialog = null;
            }
        });
    }

    private void closeDisco() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.close_disco_title),
                getString(R.string.order_finish_left_text), getString(R.string.close));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    gameViewModel.clearSite();
                    tvCloseDisco.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            intentSwitchGame(GameIdCons.NONE);
                        }
                    }, 500);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void startDisco() {
        if (roomInfoModel.initGameId > 0) {
            intentSwitchGame(roomInfoModel.initGameId);
            return;
        }
        HomeRepository.gameList(this, new RxCallback<GameListResp>() {
            @Override
            public void onSuccess(GameListResp gameListResp) {
                super.onSuccess(gameListResp);
                if (gameListResp != null) {
                    List<GameModel> gameList = gameListResp.getGameList(roomInfoModel.sceneType);
                    if (gameList != null && gameList.size() > 0) {
                        GameModel gameModel = gameList.get(0);
                        if (gameModel.gameId > 0) {
                            roomInfoModel.initGameId = gameModel.gameId;
                            startDisco();
                        }
                    }
                }
            }
        });
    }

    /** 空实现，业务不自动上麦 */
    @Override
    protected void businessAutoUpMic() {
    }

    @Override
    public void onMicLocationSwitchCompleted(int micIndex, boolean operate, OperateMicType type) {
        super.onMicLocationSwitchCompleted(micIndex, operate, type);
        if (operate) {
            gameViewModel.joinAnchor(null);
        }
    }

    @Override
    public void onDanceList(List<DanceModel> list) {
        super.onDanceList(list);
        if (dancingMenuDialog != null) {
            dancingMenuDialog.notifyDataSetChange(list);
        }
    }

    @Override
    public void onUpdateDance(int index) {
        super.onUpdateDance(index);
        if (dancingMenuDialog != null) {
            dancingMenuDialog.notifyItemChanged(index);
        }
    }

    @Override
    public void onDanceWait() {
        super.onDanceWait();
        tvDanceWait.removeCallbacks(danceWaitHideTask);
        tvDanceWait.setVisibility(View.VISIBLE);
        tvDanceWait.postDelayed(danceWaitHideTask, 4000);
    }

    @Override
    public void onDiscoContribution(List<ContributionModel> list) {
        super.onDiscoContribution(list);
        discoRankingView.setDatas(list);
        if (list == null || list.size() == 0) {
            discoRankingView.setVisibility(View.GONE);
        } else {
            discoRankingView.setVisibility(View.VISIBLE);
        }
        if (contributionRankingDialog != null) {
            contributionRankingDialog.setDatas(list);
        }
    }

    private final Runnable danceWaitHideTask = new Runnable() {
        @Override
        public void run() {
            tvDanceWait.setVisibility(View.GONE);
        }
    };

}