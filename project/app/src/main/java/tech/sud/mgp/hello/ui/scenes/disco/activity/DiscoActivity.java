package tech.sud.mgp.hello.ui.scenes.disco.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.Gender;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.AnimUtils;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.RobotListResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.manager.SceneDiscoManager;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.disco.model.DiscoInteractionModel;
import tech.sud.mgp.hello.ui.scenes.disco.viewmodel.DiscoGameViewModel;
import tech.sud.mgp.hello.ui.scenes.disco.widget.ContributionRankingDialog;
import tech.sud.mgp.hello.ui.scenes.disco.widget.DancingMenuDialog;
import tech.sud.mgp.hello.ui.scenes.disco.widget.DiscoExplainView;
import tech.sud.mgp.hello.ui.scenes.disco.widget.DiscoInteractionDialog;
import tech.sud.mgp.hello.ui.scenes.disco.widget.DiscoRankingView;
import tech.sud.mgp.hello.ui.scenes.disco.widget.InviteDanceDialog;

/**
 * 蹦迪 场景
 */
public class DiscoActivity extends AbsAudioRoomActivity<DiscoGameViewModel> {

    private TextView tvStartDisco;
    private TextView tvCloseDisco;
    private ImageView ivDancingMenu;
    private TextView tvDanceWait;
    private View viewInteraction;
    private View viewInviteDance;

    private DiscoRankingView discoRankingView;
    private DiscoExplainView discoExplainView;

    private DancingMenuDialog dancingMenuDialog;
    private ContributionRankingDialog contributionRankingDialog;
    private DiscoInteractionDialog discoInteractionDialog;

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

        tvDanceWait = findViewById(R.id.tv_dance_send_hint);
        discoExplainView = findViewById(R.id.disco_explain_view);
        viewInteraction = findViewById(R.id.view_interaction);
        viewInviteDance = findViewById(R.id.view_invite_dance);

        int paddingHorizontal = DensityUtils.dp2px(this, 8);
        int textColor = Color.parseColor("#ffffff");
        int marginEnd = DensityUtils.dp2px(this, 11);

        // 初始化排行榜的View
        discoRankingView = new DiscoRankingView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(marginEnd);
        topView.addCustomView(discoRankingView, params);

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

        // 添加舞台节目单按钮
        initDanceMenuView();

        // 不展示选择游戏
        topView.setSelectGameVisible(false);

        bringToFrontViews();

        AnimUtils.breathe(viewInviteDance);
    }

    private void initDanceMenuView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtils.dp2px(36), DensityUtils.dp2px(32));
        params.setMarginEnd(DensityUtils.dp2px(9));
        ivDancingMenu = new ImageView(this);
        ivDancingMenu.setBackgroundResource(R.drawable.ic_dancing_menu_btn);
        bottomView.addViewToRight(ivDancingMenu, 0, params);
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
        // 蹦迪相关的按钮
        if (playingGameId > 0) {
            discoExplainView.setVisibility(View.VISIBLE);
            ivDancingMenu.setVisibility(View.VISIBLE);
            viewInteraction.setVisibility(View.VISIBLE);
            viewInviteDance.setVisibility(View.VISIBLE);
        } else {
            discoExplainView.setVisibility(View.GONE);
            ivDancingMenu.setVisibility(View.GONE);
            viewInteraction.setVisibility(View.GONE);
            viewInviteDance.setVisibility(View.GONE);
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
        ivDancingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDancingMenu();
            }
        });
        viewInviteDance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDanceDialog();
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
                        checkSetAiPlayers();
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
        viewInteraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInteractionDialog();
            }
        });
    }

    // 展示交互弹窗
    private void showInteractionDialog() {
        DiscoInteractionDialog dialog = DiscoInteractionDialog.newInstance(roomInfoModel.roomId);
        dialog.setOnActionListener(new DiscoInteractionDialog.OnActionListener() {
            @Override
            public void onAction(DiscoInteractionModel model) {
                if (binder != null) {
                    binder.exeDiscoAction(roomInfoModel.roomId, model, new SceneDiscoManager.ActionListener() {
                        @Override
                        public void onAnchorChange(boolean isAnchor) {
                            LifecycleUtils.safeLifecycle(context, new CompletedListener() {
                                @Override
                                public void onCompleted() {
                                    if (discoInteractionDialog != null) {
                                        discoInteractionDialog.setAnchor(isAnchor);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
        discoInteractionDialog = dialog;
        dialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                discoInteractionDialog = null;
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    // 展示邀请主播跳舞弹窗
    private void showAboutDanceDialog() {
        InviteDanceDialog dialog = InviteDanceDialog.newInstance(roomInfoModel.roomId);
        dialog.setOnDanceListener(new InviteDanceDialog.OnDanceListener() {
            @Override
            public void onDance(GiftModel giftModel, List<UserInfoResp> list, int minute) {
                onInviteDance(giftModel, list, minute);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    // 选择了主播跳舞
    private void onInviteDance(GiftModel giftModel, List<UserInfoResp> list, int minute) {
        if (list == null || list.size() == 0 || giftModel == null) {
            return;
        }
        List<UserInfo> userInfos = new ArrayList<>();
        for (UserInfoResp userInfoResp : list) {
            UserInfo info = new UserInfo();
            info.userID = userInfoResp.userId + "";
            info.icon = userInfoResp.avatar;
            info.name = userInfoResp.nickname;
            if (Gender.MALE.equals(userInfoResp.gender)) {
                info.sex = 1;
            } else {
                info.sex = 2;
            }
            userInfos.add(info);
        }
        onSendGift(giftModel, minute, userInfos);
    }

    // 添加机器人
    private void checkSetAiPlayers() {
        if (roomInfoModel.roleType != RoleType.OWNER) {
            return;
        }
        RoomRepository.robotList(this, 30, new RxCallback<RobotListResp>() {
            @Override
            public void onSuccess(RobotListResp robotListResp) {
                super.onSuccess(robotListResp);
                if (robotListResp == null || robotListResp.robotList == null || robotListResp.robotList.size() == 0) {
                    return;
                }
                // 设置机器人
                gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameAddAIPlayers(robotListResp.robotList, 1);

                tvDanceWait.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        robotJoinAnchor(robotListResp);
                    }
                }, 1000);
            }
        });
    }

    private void robotJoinAnchor(RobotListResp robotListResp) {
        // 前面六个机器人上舞台
        for (int i = 0; i < robotListResp.robotList.size(); i++) {
            if (i < SceneDiscoManager.ROBOT_UP_MIC_COUNT) {
                String userId = robotListResp.robotList.get(i).userId;
                gameViewModel.joinAnchor(null, userId);
                try {
                    RoomRepository.discoSwitchAnchor(this, roomInfoModel.roomId, 1, Long.parseLong(userId), new RxCallback<>());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
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
//                    gameViewModel.clearSite();
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

    /** 麦位切换完成 */
    @Override
    public void onMicLocationSwitchCompleted(int micIndex, boolean operate, OperateMicType type) {
        super.onMicLocationSwitchCompleted(micIndex, operate, type);
        if (operate) {
            gameViewModel.joinAnchor(null, null);
            RoomRepository.discoSwitchAnchor(this, roomInfoModel.roomId, 1, HSUserInfo.userId, new RxCallback<>());
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

    @Override
    public void onDJCountdown(int countdown) {
        super.onDJCountdown(countdown);
        discoExplainView.setCountdown(countdown);
    }

    @Override
    public void onEnterRoomSuccess() {
        super.onEnterRoomSuccess();
        robotUpMicLocation();
    }

    // 机器人上麦位
    private void robotUpMicLocation() {
        if (roomInfoModel.roleType != RoleType.OWNER) {
            return;
        }
        RoomRepository.robotList(this, 30, new RxCallback<RobotListResp>() {
            @Override
            public void onSuccess(RobotListResp robotListResp) {
                super.onSuccess(robotListResp);
                if (robotListResp == null || robotListResp.robotList == null || robotListResp.robotList.size() == 0) {
                    return;
                }
                if (binder != null) {
                    for (int i = 0; i < robotListResp.robotList.size(); i++) {
                        if (i < SceneDiscoManager.ROBOT_UP_MIC_COUNT) {
                            SudMGPAPPState.AIPlayers aiPlayers = robotListResp.robotList.get(i);
                            binder.robotUpMicLocation(aiPlayers, i);
                        } else {
                            break;
                        }
                    }
                }
            }
        });
    }

}