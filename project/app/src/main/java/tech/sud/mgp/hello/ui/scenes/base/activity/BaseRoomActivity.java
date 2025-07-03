package tech.sud.mgp.hello.ui.scenes.base.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState.AIPlayers;
import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState.APPCommonGameCreateOrderResult;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.gip.core.ISudListenerNotifyStateChange;
import tech.sud.gip.core.SudLoadMGMode;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.AnimUtils;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.utils.permission.PermissionFragment;
import tech.sud.mgp.hello.common.utils.permission.SudPermissionUtils;
import tech.sud.mgp.hello.common.widget.dialog.BottomOptionDialog;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.req.CreateOrderReq;
import tech.sud.mgp.hello.service.game.req.GamePlayerPropsReq;
import tech.sud.mgp.hello.service.game.resp.GamePlayerPropsResp;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.service.main.resp.RandomAiCloneResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.service.room.resp.GiftListResp;
import tech.sud.mgp.hello.service.room.resp.MonopolyCardsResp;
import tech.sud.mgp.hello.service.room.resp.RobotListResp;
import tech.sud.mgp.hello.ui.common.constant.RequestKey;
import tech.sud.mgp.hello.ui.main.base.activity.MainActivity;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.constant.OperateMicType;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameLoadingProgressModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameTextModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameViewParams;
import tech.sud.mgp.hello.ui.scenes.base.model.GiftNotifyModel;
import tech.sud.mgp.hello.ui.scenes.base.model.OrderInviteModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoleType;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomTextModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.base.utils.AIPlayersConverter;
import tech.sud.mgp.hello.ui.scenes.base.utils.AudioMsgPlayerConcurrent;
import tech.sud.mgp.hello.ui.scenes.base.utils.UserInfoRespConverter;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.SceneRoomViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.dialog.AddAiDialog;
import tech.sud.mgp.hello.ui.scenes.base.widget.dialog.GameModeDialog;
import tech.sud.mgp.hello.ui.scenes.base.widget.dialog.RoomMoreDialog;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.MonopolyCardContainer;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.SceneRoomBottomView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.SceneRoomTopView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.RoomInputMsgView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.OnMicItemClickListener;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.SceneRoomMicWrapView;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d.Audio3DCmdFaceNotifyModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.danmaku.RoomCmdDanmakuTeamChangeModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.DanceModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.game.RoomCmdPropsCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.monopoly.RoomCmdMonopolyCardGiftModel;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.order.RoomCmdUserOrderModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.GiftSendClickListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.utils.GiftModelConverter;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.GiftEffectView;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.RoomGiftDialog;
import tech.sud.mgp.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.rtc.audio.core.SudAudioPlayListener;

/**
 * 场景房间的基类
 */
public abstract class BaseRoomActivity<T extends AppGameViewModel> extends BaseActivity implements SceneRoomServiceCallback {

    protected RoomInfoModel roomInfoModel; // 房间信息
    protected long playingGameId; // 当前正在玩的游戏id

    protected SceneRoomTopView topView;
    protected SceneRoomMicWrapView micView;
    protected SceneRoomChatView chatView;
    protected SceneRoomBottomView bottomView;
    protected FrameLayout giftContainer;
    protected GiftEffectView effectView;
    protected RoomInputMsgView inputMsgView;
    protected FrameLayout gameContainer;
    protected FrameLayout topGameContainer;
    protected TextView tvGameNumber;
    protected View clOpenMic;
    private TextView tvOpenMic;
    private TextView tvASRHint;
    private TextView tvAddRobot;
    private MonopolyCardContainer mMonopolyCardContainer;

    protected boolean closeing; // 标识是否正在关闭房间
    protected SceneRoomService.MyBinder binder;
    protected final SceneRoomViewModel viewModel = new SceneRoomViewModel();
    protected final T gameViewModel = initGameViewModel();

    // 初始化游戏业务模型
    protected abstract T initGameViewModel();

    private RoomGiftDialog roomGiftDialog;
    private GameModeDialog gameModeDialog;

    /** 场景配置，子类可对其进行修改进行定制化需求 */
    protected final RoomConfig roomConfig = new RoomConfig();

    public long delayExitDuration = 500; // 延时关闭的时长
    private AudioMsgPlayerConcurrent mAudioMsgPlayer;

    private final int customRobotLevelBigModel = -1;
    private List<String> playVoiceAiUidList = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected boolean beforeSetContentView() {
        Serializable modelSerializable = getIntent().getSerializableExtra("RoomInfoModel");
        if (modelSerializable instanceof RoomInfoModel) {
            roomInfoModel = (RoomInfoModel) modelSerializable;
        } else {
            boolean isPendingIntent = getIntent().getBooleanExtra(RequestKey.KEY_IS_PENDING_INTENT, false);
            if (isPendingIntent) {
                roomInfoModel = SceneRoomService.getRoomInfoModel();
            }
        }
        if (roomInfoModel == null || roomInfoModel.roomId == 0) {
            return true;
        }
        playingGameId = roomInfoModel.gameId;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_scene;
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView = findViewById(R.id.room_top_view);
        micView = findViewById(R.id.room_mic_view);
        chatView = findViewById(R.id.room_chat_view);
        bottomView = findViewById(R.id.room_bottom_view);
        giftContainer = findViewById(R.id.gift_container);
        inputMsgView = findViewById(R.id.room_input_msg_view);
        gameContainer = findViewById(R.id.game_container);
        topGameContainer = findViewById(R.id.top_game_container);
        tvGameNumber = findViewById(R.id.tv_game_number);
        clOpenMic = findViewById(R.id.cl_open_mic);
        tvOpenMic = findViewById(R.id.tv_open_mic);
        tvASRHint = findViewById(R.id.tv_asr_hint);
        tvAddRobot = findViewById(R.id.tv_add_robot);

        clOpenMic.setVisibility(View.GONE);

        gameViewModel.gameConfigModel.ui.lobby_players.hide = true; // 配置不展示大厅玩家展示位
        gameViewModel.gameConfigModel.ui.nft_avatar.hide = false; // 显示NFT图像
        gameViewModel.gameConfigModel.ui.game_opening.hide = false; // 显示开场动画
        gameViewModel.gameConfigModel.ui.game_mvp.hide = false; // 显示MVP动画

        // 设置沉浸式状态栏时，顶部view的间距
        ViewGroup.LayoutParams topViewParams = topView.getLayoutParams();
        if (topViewParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) topViewParams;
            marginLayoutParams.topMargin = marginLayoutParams.topMargin + ImmersionBar.getStatusBarHeight(this);
            topView.setLayoutParams(marginLayoutParams);
        }

        topView.setFinishGameVisible(false);
        topView.setSelectGameVisible(isOwner());

        if (roomConfig.isSupportAddRobot) {
            tvAddRobot.setVisibility(View.VISIBLE);
        }
    }

    protected void bringToFrontViews() {
        giftContainer.bringToFront();
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
        topGameContainer.bringToFront();
    }

    @Override
    protected void initData() {
        super.initData();
        topView.setName(roomInfoModel.roomName);
        if (BuildConfig.gameIsTestEnv) {
            topView.setId(roomInfoModel.roomId + "--" + roomInfoModel.roomNumber);
        } else {
            topView.setId(getString(R.string.audio_room_number) + " " + roomInfoModel.roomNumber);
        }
        viewModel.initData();
        bindService();
    }

    private void enterRoom() {
        if (binder != null) {
            binder.setCallback(this);
            binder.enterRoom(roomConfig, getClass(), roomInfoModel);
        }
        updatePageStyle();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        setViewListeners();
        setGameListeners();
    }

    private void setViewListeners() {
        KeyboardUtils.registerSoftInputChangedListener(this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                inputMsgView.onSoftInputChanged(height);
                if (height == 0) {
                    updateStatusBar();
                }
            }
        });
        micView.setOnMicItemClickListener(new OnMicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AudioRoomMicModel model = micView.getItem(position);
                if (model != null) {
                    if (model.userId == 0) {
                        if (binder != null) {
                            binder.micLocationSwitch(position, true, OperateMicType.USER);
                        }
                    } else if (model.userId == HSUserInfo.userId) {
                        clickSelfMicLocation(position);
                    } else if (model.userId > 0) {
                        clickOtherMicLocation(position, model);
                    }
                }
            }
        });
        bottomView.setGiftClickListener(v -> {
            showSendGiftDialog(null, 0);
        });
        bottomView.setGotMicClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binder != null) {
                    binder.autoUpMic(OperateMicType.USER);
                }
            }
        });
        bottomView.setInputClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.logSoftInputMode(getWindow().getAttributes().softInputMode);
                inputMsgView.show();
            }
        });
        bottomView.setMicStateClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean micOpen = !bottomView.isMicOpened();
                if (gameViewModel.isInterceptSwitchMic(micOpen)) {
                    ToastUtils.showLong(R.string.unable_to_speak);
                    return;
                }
                setMicStatus(micOpen);
            }
        });
        inputMsgView.setSendMsgListener(new RoomInputMsgView.SendMsgListener() {
            @Override
            public void onSendMsg(CharSequence msg) {
                if (binder != null) {
                    binder.sendPublicMsg(msg);
                }
                inputMsgView.hide();
                inputMsgView.clearInput();
            }
        });
        topView.setSelectGameClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSelectGame();
            }
        });
        topView.setMoreOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMore();
            }
        });
        topView.setFinishGameOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFinishGame();
            }
        });
        tvAddRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewAddAiDialog();
//                showRobotLevelPopup();
            }
        });
    }

    private void showNewAddAiDialog() {
        AddAiDialog dialog = new AddAiDialog(this);
        dialog.show();
        dialog.setAddAiListener(new AddAiDialog.AddAiListener() {
            @Override
            public void onClickAddRobot() {
                onClickAddRobotNew();
                dialog.dismiss();
            }

            @Override
            public void onClickDefaultTone() {
                onClickDefaultToneNew();
                dialog.dismiss();
            }

            @Override
            public void onClickCustomTone() {
                onClickAddRoleClone();
                dialog.dismiss();
            }
        });
    }

    // 从默认的大模型AI当中，随机选一个添加进去
    private void onClickDefaultToneNew() {
        int idCount = 275;
        int randomAiId = new Random().nextInt(idCount) + 1;
        onClickAddRobot(3, 1, randomAiId + "");
    }

    // 随机选一个等级的添加进去
    private void onClickAddRobotNew() {
        int randomLevel = new Random().nextInt(3) + 1;
        onClickAddRobot(randomLevel, 0, null);
    }

    private void showRobotLevelPopup() {
        View contnetView = View.inflate(this, R.layout.popup_bot_level, null);
        LinearLayout viewRoot = contnetView.findViewById(R.id.view_root);

        int popupWindowWidth = DensityUtils.dp2px(this, 80);
        int popupWindowHeight = DensityUtils.dp2px(this, 168);
        PopupWindow popupWindow = new PopupWindow(contnetView, popupWindowWidth, popupWindowHeight);

        popupWindow.setOutsideTouchable(true); //设置点击外部区域可以取消popupWindow
        popupWindow.setFocusable(true); // 返回键取消popupwindow

        addRobotLevelItem(popupWindow, viewRoot, customRobotLevelBigModel); // 大模型
        addRobotLevelItem(popupWindow, viewRoot, 3);
        addRobotLevelItem(popupWindow, viewRoot, 2);
        addRobotLevelItem(popupWindow, viewRoot, 1);

        int xoff = -(popupWindowWidth / 2 - tvAddRobot.getMeasuredWidth() / 2);
        int yoff = -(popupWindowHeight + DensityUtils.dp2px(this, 32));
        popupWindow.showAsDropDown(tvAddRobot, xoff, yoff);
    }

    private void addRobotLevelItem(PopupWindow popupWindow, LinearLayout viewRoot, int level) {
        ConstraintLayout parent = new ConstraintLayout(this);

        TextView tv = new TextView(this);
        tv.setTextSize(13);
        tv.setTextColor(Color.BLACK);
        switch (level) {
            case 1:
                tv.setText(R.string.simple);
                break;
            case 2:
                tv.setText(R.string.moderate);
                break;
            case 3:
                tv.setText(R.string.difficulty);
                break;
            case customRobotLevelBigModel:
                tv.setText(R.string.large_model);
                break;
        }
        tv.setBackgroundResource(R.drawable.selector_bot_level_item);
        tv.setGravity(Gravity.CENTER);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(20));
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        parent.addView(tv, params);

        if (viewRoot.getChildCount() > 0) {
            View view = new View(this);
            view.setBackgroundColor(Color.parseColor("#dddddd"));
            int marginHorizontal = DensityUtils.dp2px(7);
            ConstraintLayout.LayoutParams viewParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(1));
            viewParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            viewParams.setMarginStart(marginHorizontal);
            viewParams.setMarginEnd(marginHorizontal);
            parent.addView(view, viewParams);
        }

        viewRoot.addView(parent, LinearLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(40));

        tv.setOnClickListener(v -> {
            if (level == customRobotLevelBigModel) {
                showBigModelPopupWindow();
            } else {
                onClickAddRobot(level, 0, null);
            }
            popupWindow.dismiss();
        });

    }

    // 大模型AI选项
    private void showBigModelPopupWindow() {
        View contnetView = View.inflate(this, R.layout.popup_bot_level, null);
        LinearLayout viewRoot = contnetView.findViewById(R.id.view_root);
        viewRoot.setBackgroundColor(Color.WHITE);

        int popupWindowWidth = DensityUtils.dp2px(this, 80);
        int popupWindowHeight = DensityUtils.dp2px(this, 40 * 10);

        ScrollView scrollView = new ScrollView(this);
        viewRoot.addView(scrollView, popupWindowWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        PopupWindow popupWindow = new PopupWindow(contnetView, popupWindowWidth, popupWindowHeight);

        popupWindow.setOutsideTouchable(true); //设置点击外部区域可以取消popupWindow
        popupWindow.setFocusable(true); // 返回键取消popupwindow

        addRobotBigModelItem(popupWindow, container, getString(R.string.role_clone), null, true); // 角色分身

        int idCount = 275;
        for (int i = 1; i <= idCount; i++) {
            String aiId = i + "";
            addRobotBigModelItem(popupWindow, container, aiId, aiId, false); // 大模型
        }

        int xoff = -(popupWindowWidth / 2 - tvAddRobot.getMeasuredWidth() / 2);
        int yoff = -(popupWindowHeight + DensityUtils.dp2px(this, 32));
        popupWindow.showAsDropDown(tvAddRobot, xoff, yoff);
    }

    private void addRobotBigModelItem(PopupWindow popupWindow, LinearLayout viewRoot, String name, String aiId, boolean isAiClone) {
        ConstraintLayout parent = new ConstraintLayout(this);

        TextView tv = new TextView(this);
        tv.setTextSize(13);
        tv.setTextColor(Color.BLACK);
        tv.setText(name);
        tv.setBackgroundResource(R.drawable.selector_bot_level_item);
        tv.setGravity(Gravity.CENTER);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(20));
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        parent.addView(tv, params);

        if (viewRoot.getChildCount() > 0) {
            View view = new View(this);
            view.setBackgroundColor(Color.parseColor("#dddddd"));
            int marginHorizontal = DensityUtils.dp2px(7);
            ConstraintLayout.LayoutParams viewParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(1));
            viewParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            viewParams.setMarginStart(marginHorizontal);
            viewParams.setMarginEnd(marginHorizontal);
            parent.addView(view, viewParams);
        }

        viewRoot.addView(parent, LinearLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(40));

        tv.setOnClickListener(v -> {
            if (isAiClone) {
                onClickAddRoleClone();
            } else {
                onClickAddRobot(3, 1, aiId);
            }
            popupWindow.dismiss();
        });

    }

    /** 添加一个角色分身 */
    private void onClickAddRoleClone() {
        HomeRepository.randomAiClone(this, new RxCallback<RandomAiCloneResp>() {
            @Override
            public void onSuccess(RandomAiCloneResp resp) {
                super.onSuccess(resp);
                if (resp == null) {
                    return;
                }
                // 添加大模型分身到游戏
                List<SudGIPAPPState.ModelAIPlayers> aiPlayersList = new ArrayList<>();
                SudGIPAPPState.ModelAIPlayers modelAIPlayers = new SudGIPAPPState.ModelAIPlayers();
                modelAIPlayers.userId = resp.aiUid;
                modelAIPlayers.avatar = resp.avatarUrl;
                modelAIPlayers.name = resp.nickname;
                modelAIPlayers.gender = resp.gender;
                modelAIPlayers.aiIdStr = resp.aiId;
                aiPlayersList.add(modelAIPlayers);
                SudGIPAPPState.APPCommonGameAddBigScaleModelAIPlayers model = new SudGIPAPPState.APPCommonGameAddBigScaleModelAIPlayers();
                model.aiPlayers = aiPlayersList;
                model.isReady = 1;
                gameViewModel.notifyStateChange(SudGIPAPPState.APP_COMMON_GAME_ADD_BIG_SCALE_MODEL_AI_PLAYERS, model);

                // 分身上麦
                if (binder == null) {
                    return;
                }
                List<AudioRoomMicModel> micList = binder.getMicList();
                // 找到一个空位置
                AudioRoomMicModel newRobotMic = findNewRobotMic(micList);
                if (newRobotMic != null) {
//                    ToastUtils.showShort(R.string.no_empty_seat);
                    binder.robotUpMicLocation(UserInfoRespConverter.conver(modelAIPlayers), newRobotMic.micIndex);
                }
            }
        });
    }

    /** 点击了添加机器人 */
    protected void onClickAddRobot(int level, int aiType, String aiId) {
        RoomRepository.robotList(this, 300, new RxCallback<RobotListResp>() {
            @Override
            public void onSuccess(RobotListResp robotListResp) {
                super.onSuccess(robotListResp);
                if (robotListResp == null || robotListResp.robotList == null || robotListResp.robotList.size() == 0) {
                    return;
                }
                if (binder == null) {
                    return;
                }
                List<AudioRoomMicModel> micList = binder.getMicList();
                // 找到一个可以用的机器人
                AIPlayers aiPlayers = findAvailableAiPlayers(robotListResp.robotList, micList, level);

                if (aiPlayers == null) {
                    return;
                }

                // 找到一个空位置
                AudioRoomMicModel newRobotMic = findNewRobotMic(micList);
                if (newRobotMic != null) {
//                    ToastUtils.showShort(R.string.no_empty_seat);
                    binder.robotUpMicLocation(UserInfoRespConverter.conver(aiPlayers), newRobotMic.micIndex);
                }

                // 添加到游戏中
                if (aiType == 0) { // 普通AI
                    List<AIPlayers> aiPlayersList = new ArrayList<>();
                    aiPlayersList.add(aiPlayers);
                    gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameAddAIPlayers(aiPlayersList, 1);
                } else if (aiType == 1) { // 大模型
                    List<SudGIPAPPState.ModelAIPlayers> aiPlayersList = new ArrayList<>();
                    SudGIPAPPState.ModelAIPlayers modelAIPlayers = new SudGIPAPPState.ModelAIPlayers();
                    modelAIPlayers.userId = aiPlayers.userId;
                    modelAIPlayers.avatar = aiPlayers.avatar;
                    modelAIPlayers.name = aiPlayers.name;
                    modelAIPlayers.gender = aiPlayers.gender;
                    modelAIPlayers.aiIdStr = aiId;
                    aiPlayersList.add(modelAIPlayers);
                    SudGIPAPPState.APPCommonGameAddBigScaleModelAIPlayers model = new SudGIPAPPState.APPCommonGameAddBigScaleModelAIPlayers();
                    model.aiPlayers = aiPlayersList;
                    model.isReady = 1;
                    gameViewModel.notifyStateChange(SudGIPAPPState.APP_COMMON_GAME_ADD_BIG_SCALE_MODEL_AI_PLAYERS, model);
                }
            }
        });
    }

    protected AudioRoomMicModel findNewRobotMic(List<AudioRoomMicModel> micList) {
        int totalRobotCount = 0;
        AudioRoomMicModel emptyMicModel = null;
        for (AudioRoomMicModel model : micList) {
            if (model.isAi) {
                totalRobotCount++;
            }
            if (emptyMicModel == null && !model.hasUser()) {
                emptyMicModel = model;
            }
        }
        return emptyMicModel;
    }

    /**
     * 不在麦位上就可以使用
     * 修改为随机获取机器人
     *
     * @param robotList 机器人列表
     * @param micList   麦位列表
     * @param level
     */
    protected AIPlayers findAvailableAiPlayers(List<AIPlayers> robotList, List<AudioRoomMicModel> micList, int level) {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int position = random.nextInt(robotList.size());
            AIPlayers aiPlayers = robotList.get(position);
            boolean exists = false;
            // 判断该机器人是否已经在麦位上了
            for (AudioRoomMicModel audioRoomMicModel : micList) {
                if ((audioRoomMicModel.userId + "").equals(aiPlayers.userId)) {
                    exists = true;
                    break;
                }
            }
            if (!exists && aiPlayers.level == level) {
                return aiPlayers;
            }
        }
        return null;
    }

    /** 点击了选择游戏 */
    protected void clickSelectGame() {
        GameModeDialog dialog = GameModeDialog.getInstance(roomInfoModel.sceneType);
        dialog.setFinishGame(gameViewModel.isOperateFinishGame());
        dialog.setPlayingGameId(playingGameId);
        dialog.show(getSupportFragmentManager(), null);
        dialog.setSelectGameListener(new GameModeDialog.SelectGameListener() {
            @Override
            public void onSelectGame(long gameId, boolean isFinishGame) {
                if (gameId == 0 && isFinishGame) { // 结束游戏
                    clickFinishGame();
                } else {
                    // 自己主动切游戏，需判断一下是否要拦截
                    int gameState = gameViewModel.getGameState();
                    if (gameState == SudGIPMGState.MGCommonGameState.LOADING || gameState == SudGIPMGState.MGCommonGameState.PLAYING) {
                        if (gameId > 0) {
                            ToastUtils.showLong(R.string.switch_game_warn);
                        } else {
                            ToastUtils.showLong(R.string.close_game_warn);
                        }
                        return;
                    }
                    intentSwitchGame(gameId);
                }
            }
        });
        dialog.setOnDestroyListener(() -> gameModeDialog = null);
        gameModeDialog = dialog;
    }

    /** 点击了更多按钮 */
    protected void clickMore() {
        RoomMoreDialog dialog = RoomMoreDialog.getInstance(isGamePlaying());
        dialog.setHangOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intentHang();
            }
        });
        dialog.setExitOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intentClose();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /** 意图挂起房间 */
    private void intentHang() {
        if (SudPermissionUtils.checkFloatPermission(this)) {
            hangRoom();
        } else {
            SimpleChooseDialog dialog = new SimpleChooseDialog(this,
                    getString(R.string.floating_permission_info),
                    getString(R.string.exit_room),
                    getString(R.string.go_setting));
            dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
                @Override
                public void onChoose(int index) {
                    if (index == 0) {
                        exitRoom();
                    } else if (index == 1) {
                        SudPermissionUtils.setFloatPermission(context);
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /** 是否是全屏显示的 */
    private boolean isGamePlaying() {
        return playingGameId > 0;
    }

    /** 挂起房间 */
    private void hangRoom() {
        if (binder != null) {
            binder.showFloating(roomInfoModel, getClass());
        }
        releaseService();
        gameViewModel.onDestroy();
        finish();
    }

    /**
     * 设置麦克风的状态
     *
     * @param micOpen true为开启麦克风 false为关闭麦克风
     */
    private void setMicStatus(boolean micOpen) {
        if (micOpen) {
            openMic();
        } else {
            closeMic();
        }
    }

    private void clickFinishGame() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.finish_game_confirm));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    gameViewModel.finishGame();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setGameListeners() {
        gameViewModel.gameViewLiveData.observe(this, new Observer<GameViewParams>() {
            @Override
            public void onChanged(GameViewParams params) {
                View view = params.gameView;
                if (params.loadType == GameModel.LOAD_TYPE_REYOU_SDK) {
                    if (view == null) {
                        topGameContainer.removeAllViews();
                        topGameContainer.setBackgroundResource(R.color.transparent);
                    } else {
                        if (params.scale == null) {
                            ViewUtils.setHeight(topGameContainer, FrameLayout.LayoutParams.MATCH_PARENT);
                        } else {
                            ViewUtils.setHeight(topGameContainer, (int) (DensityUtils.getAppScreenWidth() * params.scale));
                        }
                        topGameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                        topGameContainer.setBackgroundResource(R.color.black);
                    }
                } else {
                    if (view == null) {
                        gameContainer.removeAllViews();
                    } else {
                        gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    }
                }
            }
        });
        gameViewModel.gameMessageLiveData.observe(this, new Observer<GameTextModel>() {
            @Override
            public void onChanged(GameTextModel msg) {
                if (msg != null) {
                    if (binder != null) {
                        binder.addChatMsg(msg);
                    }
                }
            }
        });
        gameViewModel.updateMicLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object obj) {
                if (binder != null) {
                    binder.updateMicList();
                }
            }
        });
        gameViewModel.autoUpMicLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                businessAutoUpMic();
            }
        });
        gameViewModel.gameASRLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                onASRChanged(aBoolean);
            }
        });
        gameViewModel.gameRTCPublishLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }
                setMicStatus(aBoolean);
            }
        });
        gameViewModel.gameRTCPlayLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (binder != null) {
                    binder.setRTCPlay(aBoolean);
                }
            }
        });
        gameViewModel.showFinishGameBtnLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isShow) {
                onShowFinishGameChange(isShow);
            }
        });
        gameViewModel.playerInLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                updateGameNumber();
            }
        });
        gameViewModel.micSpaceMaxLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean spaceMax) {
                if (spaceMax) {
                    micView.shirnkMicView();
                }
            }
        });
        gameViewModel.autoJoinGameLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                autoJoinGame();
            }
        });
        gameViewModel.captainChangeLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                checkGameAddAiPlayers();
            }
        });
        gameViewModel.gameLoadingProgressLiveData.observe(this, new Observer<GameLoadingProgressModel>() {
            @Override
            public void onChanged(GameLoadingProgressModel model) {
                if (model != null && model.progress == 99) {
                    gameContainer.postDelayed(() -> {
                        // 兼容小米手机全屏处理
                        updateStatusBar();
                    }, 100);
                }
            }
        });
        gameViewModel.onGameGetScoreLiveData.observe(this, (o) -> {
            onGameGetScore();
        });
        gameViewModel.onGameSetScoreLiveData.observe(this, this::onGameSetScore);
        gameViewModel.gameCreateOrderLiveData.observe(this, this::onGameCreateOrder);
        gameViewModel.monopolyCardsLiveData.observe(this, model -> {
            RoomRepository.getMonopolyCards(this, new RxCallback<MonopolyCardsResp>() {
                @Override
                public void onSuccess(MonopolyCardsResp resp) {
                    super.onSuccess(resp);
                    SudGIPAPPState.APPCommonGamePlayerMonopolyCards backModel = new SudGIPAPPState.APPCommonGamePlayerMonopolyCards();
                    backModel.reroll_card_count = resp.rerollCardCount;
                    backModel.free_rent_card_count = resp.freeRentCardCount;
                    backModel.ctrl_dice_card_count = resp.ctrlDiceCardCount;
                    gameViewModel.notifyStateChange(SudGIPAPPState.APP_COMMON_GAME_PLAYER_MONOPOLY_CARDS, backModel);
                }
            });
        });
        gameViewModel.onGameDestroyLiveData.observe(this, mgCommonDestroyGameScene -> {
            intentSwitchGame(GameIdCons.NONE);
        });
        gameViewModel.onGameMoneyNotEnoughLiveData.observe(this, model -> {
            webGameOnPay(new WebGameOnPayListener() {
                @Override
                public void onSuccess() {
                    gameViewModel.notifyStateChange(SudGIPAPPState.APP_COMMON_UPDATE_GAME_MONEY, new SudGIPAPPState.AppCommonUpdateGameMoney());
                }
            });
        });
        gameViewModel.onGamePlayerPropsCardsLiveData.observe(this, model -> {
            GamePlayerPropsReq req = new GamePlayerPropsReq();
            req.gameId = getPlayingGameId();
            GameRepository.gamePlayerProps(context, req, new RxCallback<GamePlayerPropsResp>() {
                @Override
                public void onSuccess(GamePlayerPropsResp gamePlayerPropsResp) {
                    super.onSuccess(gamePlayerPropsResp);
                    SudGIPAPPState.AppCommonGamePlayerPropsCards appCommonGamePlayerPropsCards = new SudGIPAPPState.AppCommonGamePlayerPropsCards();
                    appCommonGamePlayerPropsCards.props = gamePlayerPropsResp.props;
                    gameViewModel.notifyStateChange(SudGIPAPPState.APP_COMMON_GAME_PLAYER_PROPS_CARDS, appCommonGamePlayerPropsCards);
                }
            });
        });
        gameViewModel.aiMessageLiveData.observe(this, aiMessageModel -> {
            long userId = 0;
            try {
                userId = Long.parseLong(aiMessageModel.uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            UserInfoRepository.getUserInfo(this, userId, userInfoResp -> {
                RoomTextModel model = new RoomTextModel();
                model.userId = aiMessageModel.uid;
                if (userInfoResp != null) {
                    model.avatar = userInfoResp.getUseAvatar();
                    model.nickName = userInfoResp.nickname;
                }
                model.text = aiMessageModel.content;
                if (binder != null) {
                    binder.addChatMsg(model);
                }
            });
        });
        gameViewModel.scaleModelMsgLiveData.observe(this, this::onScaleModelMsg);
        gameViewModel.onAiRoomMessageLiveData.observe(this, this::onAiRoomMessage);
    }

    private void onAiRoomMessage(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String audioData = obj.getString("audioData");
            String uid = obj.optString("uid");
            String content = obj.optString("content"); // 文本内容
            addAiMsg(uid, content);
            playAudioData(uid, audioData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 添加一条AI的公屏消息
    private void addAiMsg(String uidStr, String content) {
        long uid;
        try {
            uid = Long.parseLong(uidStr);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        List<Long> uidList = new ArrayList<>();
        uidList.add(uid);
        UserInfoRepository.getUserInfoList(this, uidList, new UserInfoRepository.UserInfoListResultListener() {
            @Override
            public void userInfoList(List<UserInfoResp> userInfos) {
                if (userInfos == null || userInfos.size() == 0) {
                    return;
                }
                if (binder == null) {
                    return;
                }
                UserInfoResp userInfoResp = userInfos.get(0);
                // 往公屏列表当中插入一条数据
                RoomTextModel model = new RoomTextModel();
                model.userId = userInfoResp.userId + "";
                model.avatar = userInfoResp.getUseAvatar();
                model.nickName = userInfoResp.nickname;
                model.text = content;
                binder.addChatMsg(model);
            }
        });
    }

    private void onScaleModelMsg(SudGIPMGState.MGCommonAiLargeScaleModelMsg model) {
        if (model == null) {
            return;
        }
        // TODO: 2025/4/27 先不播放游戏通道过来的数据
//        playAudioData(model.audioData);
    }

    private void playAudioData(String uid, String audioData) {
//        if (mAudioMsgPlayer == null) {
//            mAudioMsgPlayer = new AudioMsgPlayerConcurrent();
//        }
//        mAudioMsgPlayer.play(audioData);

        // 改为走RTC通道去播放
        if (binder != null) {
            binder.playAudio(base64Decode(audioData), new SudAudioPlayListener() {
                @Override
                public void onPlaying() {
                    LogUtils.d("playAudioData onPlaying:" + uid);
                    gameViewModel.notifyGameMicState(uid, true);
                    playVoiceAiUidList.add(uid);
                    mHandler.removeCallbacks(mDelayStartAiSoundLevelTask);
                    mHandler.post(mDelayStartAiSoundLevelTask);
                }

                @Override
                public void onCompleted() {
                    LogUtils.d("playAudioData onCompleted:" + uid);
                    gameViewModel.notifyGameMicState(uid, false);
                    playVoiceAiUidList.remove(uid);
                }
            });
        }
    }

    private void startDelayStartAiSoundLevel() {
        mHandler.removeCallbacks(mDelayStartAiSoundLevelTask);
        mHandler.postDelayed(mDelayStartAiSoundLevelTask, 1000);
    }

    private Runnable mDelayStartAiSoundLevelTask = new Runnable() {
        @Override
        public void run() {
            for (String uid : playVoiceAiUidList) {
                if (binder == null) {
                    return;
                }
                List<AudioRoomMicModel> micList = binder.getMicList();
                if (micList != null && micList.size() > 0) {
                    for (int i = 0; i < micList.size(); i++) {
                        AudioRoomMicModel model = micList.get(i);
                        if (uid.equals(model.userId + "")) {
                            micView.startSoundLevel(i);
                        }
                    }
                }
            }
            startDelayStartAiSoundLevel();
        }
    };

    public byte[] base64Decode(String base64Str) {
        if (base64Str == null || base64Str.length() == 0)
            return null;
        return Base64.decode(base64Str, Base64.NO_WRAP);
    }

    /** 游戏回调，创建订单 */
    private void onGameCreateOrder(SudGIPMGState.MGCommonGameCreateOrder model) {
        if (model == null) {
            return;
        }
        try {
            CreateOrderReq req = new CreateOrderReq();
            req.setCreateOrderValues(model);
            req.gameId = gameViewModel.getPlayingGameId();
            req.roomId = gameViewModel.getGameRoomId();
            GameRepository.createOrder(this, req, new RxCallback<Object>() {
                @Override
                public void onNext(BaseResponse<Object> t) {
                    super.onNext(t);
                    sendCallbackToGame(t.getRetCode() == RetCode.SUCCESS);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    sendCallbackToGame(false);
                }

                private void sendCallbackToGame(boolean isSuccess) {
                    APPCommonGameCreateOrderResult model = new APPCommonGameCreateOrderResult();
                    model.result = isSuccess ? 1 : 0;
                    gameViewModel.notifyStateChange(SudGIPAPPState.APP_COMMON_GAME_CREATE_ORDER_RESULT, model);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onGameGetScore() {
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onSuccess(GetAccountResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameScore(resp.coin);
                }
            }
        });
    }

    private void onGameSetScore(SudGIPMGState.MGCommonGameSetScore model) {
        if (model == null) {
            return;
        }
        GameRepository.bringChip(this, gameViewModel.getPlayingGameId(), gameViewModel.getGameRoomId(),
                model.roundId, model.lastRoundScore, model.incrementalScore, model.totalScore, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        LogUtils.d("bringChip onSuccess");
                    }
                });
    }

    /**
     * 是否可以结束游戏变化
     *
     * @param isShow
     */
    protected void onShowFinishGameChange(Boolean isShow) {
        boolean isOperateFinishGame = isShow != null && isShow;
        // 是房主，则不显示结束游戏按钮，放到选择游戏弹窗当中
        if (isOwner()) {
            topView.setFinishGameVisible(false);
        } else {
            topView.setFinishGameVisible(isOperateFinishGame);
        }
        if (gameModeDialog != null) {
            gameModeDialog.setFinishGame(isOperateFinishGame);
        }
    }

    protected boolean isOwner() {
        return roomInfoModel.roleType == RoleType.OWNER;
    }

    /** 把麦位上的机器人都添加到游戏上去 */
    private void checkGameAddAiPlayers() {
        if (gameViewModel.isCaptain(HSUserInfo.userId) && roomConfig.isSupportAddRobot) {
            if (binder != null) {
                List<AIPlayers> aiPlayers = new ArrayList<>();
                for (AudioRoomMicModel audioRoomMicModel : binder.getMicList()) {
                    if (audioRoomMicModel.hasUser() && audioRoomMicModel.isAi) {
                        aiPlayers.add(AIPlayersConverter.conver(audioRoomMicModel));
                    }
                }
                if (aiPlayers.size() > 0) {
                    gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameAddAIPlayers(aiPlayers, 1);
                }
            }
        }
    }

    /** 触发自动加入游戏 */
    protected void autoJoinGame() {
        gameViewModel.joinGame();
    }

    // asr的开启与关闭
    protected void onASRChanged(boolean open) {
        if (binder != null) {
            binder.setASROpen(open);
            if (open && !binder.isOpenedMic()) {
                clOpenMic.setVisibility(View.VISIBLE);
                clOpenMic.removeCallbacks(delayDismissTask);
                clOpenMic.postDelayed(delayDismissTask, 3000);
                AnimUtils.shakeVertical(clOpenMic);
                setOpenMicText();
            }
        }
    }

    /** 设置开麦提示的文字 */
    private void setOpenMicText() {
        if (playingGameId == GameIdCons.I_GUESS_YOU_SAID) { //  你说我猜
            tvOpenMic.setText(R.string.asr_guide_draw);
        } else if (playingGameId == GameIdCons.DIGITAL_BOMB) { // 数字炸弹
            tvOpenMic.setText(R.string.asr_guide_number_boom);
        } else if (playingGameId == GameIdCons.YOU_DRAW_AND_I_GUESS) { // 你画我猜
            tvOpenMic.setText(R.string.asr_guide_draw);
        }
    }

    // 是否支持ASR
    private boolean isSupportASR(long gameId) {
        if (gameId == GameIdCons.I_GUESS_YOU_SAID) return true;
        if (gameId == GameIdCons.DIGITAL_BOMB) return true;
        if (gameId == GameIdCons.YOU_DRAW_AND_I_GUESS) return true;
        return false;
    }

    // 点击了自己的麦位
    private void clickSelfMicLocation(int position) {
        BottomOptionDialog dialog = new BottomOptionDialog(this);
        int downMicKey = 1;
        dialog.addOption(downMicKey, getString(R.string.audio_down_mic)); // 增加下麦按钮
        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == downMicKey) {
                    //如果正在游戏中，则需要一个逃跑弹窗显示
                    if (gameViewModel.playerIsPlaying(HSUserInfo.userId)) {
                        playingDownMic(position);
                    } else {
                        if (binder != null) {
                            binder.micLocationSwitch(position, false, OperateMicType.USER); // 执行下麦
                        }
                        gameViewModel.exitGame();
                    }
                }
            }
        });
        dialog.show();
    }

    // 点击了其他人的麦位
    private void clickOtherMicLocation(int position, AudioRoomMicModel audioRoomMicModel) {
        long userId = audioRoomMicModel.userId;
        long selfUserId = HSUserInfo.userId;

        BottomOptionDialog dialog = new BottomOptionDialog(this);
        HashMap<Integer, String> options = new HashMap<>();

        // 1,加载了游戏
        // 2,自己是队长
        // 3,并且该用户也在游戏当中，可以转让队长
        int transferCaptainKey = 1;
        if (playingGameId > 0 && gameViewModel.isCaptain(selfUserId) && gameViewModel.playerIsIn(userId)) {
            options.put(transferCaptainKey, getString(R.string.transfer_captain));
        }

        // 1,加载了游戏
        // 2,游戏未开始
        // 3,自己是队长
        // 4,并且该用户加入了游戏，可以将他踢出游戏
        int kickGameKey = 2;
        if (playingGameId > 0
                && gameViewModel.getGameState() != SudGIPMGState.MGCommonGameState.PLAYING
                && gameViewModel.getGameState() != SudGIPMGState.MGCommonGameState.LOADING
                && gameViewModel.isCaptain(selfUserId)
                && gameViewModel.playerIsIn(userId)) {
            options.put(kickGameKey, getString(R.string.kick_game));
        }

        // 自己是房主，可以把其他人给踢出
        int kickOutRoomKey = 3;
        if (isOwner()) {
            options.put(kickOutRoomKey, getString(R.string.kick_out_room));
        }

        if (options.size() > 0) {
            for (Map.Entry<Integer, String> next : options.entrySet()) {
                dialog.addOption(next.getKey(), next.getValue()); // 增加下麦按钮
            }
        } else {
            return;
        }

        dialog.setOnItemClickListener(new BottomOptionDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BottomOptionDialog.BottomOptionModel model) {
                dialog.dismiss();
                if (model.key == transferCaptainKey) {
                    gameViewModel.notifyAPPCommonSelfCaptain(userId + "");
                } else if (model.key == kickGameKey) {
                    gameViewModel.notifyAPPCommonSelfKick(userId + "");
                } else if (model.key == kickOutRoomKey) {
                    if (binder != null) {
                        binder.kickOutRoom(audioRoomMicModel);
                    }
                    if (audioRoomMicModel.hasUser()) {
                        kickUserFromGame(audioRoomMicModel.userId + "");
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * 游戏中下麦提示弹窗
     */
    private void playingDownMic(int position) {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this,
                getString(R.string.playing_down_mic),
                getString(R.string.audio_cancle),
                getString(R.string.confirm_));
        dialog.show();
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    if (binder != null) {
                        binder.micLocationSwitch(position, false, OperateMicType.USER); // 执行下麦
                    }
                    gameViewModel.exitGame();
                }
                dialog.dismiss();
            }
        });
    }

    /** 意图退出房间 */
    private void intentClose() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this,
                getString(R.string.audio_close_room_title),
                getString(R.string.audio_cancle),
                getString(R.string.confirm));
        dialog.show();
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    delayExitRoom();
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    // 延迟退出房间
    public long delayExitRoom() {
        return delayExitRoom(false);
    }

    // 延迟退出房间
    public long delayExitRoom(boolean isStartMainPage) {
        if (closeing) return 0;
        closeing = true;
        if (playingGameId > 0) {
            // 在游戏时才需要
            gameViewModel.exitGame();
            topView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitRoom(isStartMainPage);
                }
            }, delayExitDuration);
            return delayExitDuration;
        } else {
            exitRoom(isStartMainPage);
            return 0;
        }
    }

    // 退出房间
    private void exitRoom() {
        exitRoom(false);
    }

    // 退出房间
    private void exitRoom(boolean isStartMainPage) {
        if (binder != null) {
            binder.exitRoom();
        }
        releaseService();
        if (isStartMainPage) {
            startMainPage();
        }
        gameViewModel.onDestroy();
        finish();
    }

    private void startMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        intentHang();
    }

    /**
     * 自己主动切换游戏
     *
     * @param gameId 游戏id
     */
    protected void intentSwitchGame(long gameId) {
        // 发送http通知后台
        GameRepository.switchGame(this, roomInfoModel.roomId, gameId, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (switchGame(gameId)) {
                    onSelfSwitchGame(gameId);
                }
            }
        });
    }

    /** 自己主动切换了游戏 */
    protected void onSelfSwitchGame(long gameId) {
        if (binder != null) {
            binder.switchGame(gameId);
        }
    }

    protected void updateGameNumber() {
        long gameId = playingGameId;
        if (!roomConfig.isSudGame || gameId <= 0 || !roomConfig.isShowGameNumber || GameIdCons.isInteractionGame(gameId)) {
            tvGameNumber.setText("");
            return;
        }
        int gameMaxNumber = viewModel.getGameMaxNumber(gameId);
        int playerInNumber = gameViewModel.getPlayerInNumber();
        String numberStr = getString(R.string.game_number) + "：" + playerInNumber + "/" + gameMaxNumber;
        tvGameNumber.setText(numberStr);
    }

    private void openMic() {
        SudPermissionUtils.requirePermission(this, getSupportFragmentManager(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                new PermissionFragment.OnPermissionListener() {
                    @Override
                    public void onPermission(boolean success) {
                        if (success) {
                            if (binder != null) {
                                binder.setMicState(true);
                                boolean asrIsOpen = gameViewModel.gameASRLiveData.getValue() != null && gameViewModel.gameASRLiveData.getValue();
                                binder.setASROpen(asrIsOpen);
                            }
                        }
                    }
                });
    }

    private void closeMic() {
        if (binder != null) {
            binder.setMicState(false);
        }
    }

    /** 获取初始化礼物弹窗时的麦位数据 */
    protected List<AudioRoomMicModel> getGiftDialogMicList() {
        if (binder != null) {
            return binder.getMicList();
        }
        return null;
    }

    /**
     * 1.如果送给单个对象
     * underUser:代表送礼人信息
     * index:传0
     * 2.展示麦上列表
     * underUser:null
     * index:默认选中的麦上用户麦序号
     */
    protected void showSendGiftDialog(UserInfo underUser, int index) {
        roomGiftDialog = RoomGiftDialog.newInstance(roomInfoModel.sceneType, roomInfoModel.gameId);
        if (underUser == null) {
            roomGiftDialog.setMicUsers(getGiftDialogMicList(), index);
        } else {
            roomGiftDialog.setToUser(underUser);
        }
        roomGiftDialog.giftSendClickListener = new GiftSendClickListener() {
            @Override
            public void onSendClick(GiftModel giftModel, int giftCount, List<UserInfo> toUsers) {
                onSendGift(giftModel, giftCount, toUsers);
            }

            @Override
            public void onNotify(Map<Long, Boolean> userState) {
                if (binder != null) {
                    binder.updateMicList();
                }
            }
        };
        roomGiftDialog.show(getSupportFragmentManager(), "gift");
        roomGiftDialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                roomGiftDialog = null;
                if (binder != null) {
                    binder.updateMicList();
                }
            }
        });
        roomGiftDialog.setOnShowCustomRocketClickListener((v) -> {
            onGiftDialogShowCustomRocket();
        });
    }

    /** 礼物弹窗，点击显示定制火箭 */
    protected void onGiftDialogShowCustomRocket() {
    }

    protected void onSendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUserList) {
        if (toUserList == null || toUserList.size() == 0) {
            return;
        }
        // 发送http到后端
        int giftConfigType; // 礼物配置方式（1：客户端，2：服务端）
        if (giftModel.type == 0) { // 1.4.0新增:礼物类型 0：内置礼物 1：后端配置礼物
            giftConfigType = 1;
        } else {
            giftConfigType = 2;
        }
        List<String> receiverList = new ArrayList<>();
        for (UserInfo user : toUserList) {
            receiverList.add(user.userID);
        }
        UserInfo sendUser = RoomCmdModelUtils.getSendUser();
        RoomRepository.sendGift(context, roomInfoModel.roomId, giftModel.giftId, giftCount,
                giftConfigType, giftModel.giftPrice, receiverList, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        boolean isAllSeat = isAllSeat(toUserList);
                        if (binder != null) {
                            binder.sendGift(giftModel, giftCount, toUserList, isAllSeat);
                        }
                        showGift(giftModel, giftCount, sendUser, toUserList, isAllSeat);
                    }
                });
    }

    /** 判断送的礼是否是全麦 */
    public boolean isAllSeat(List<UserInfo> toUserList) {
        if (binder == null) {
            return false;
        }
        List<AudioRoomMicModel> micList = binder.getMicList();
        if (toUserList == null || toUserList.size() == 0) {
            return false;
        }
        if (micList == null || micList.size() == 0) {
            return false;
        }
        // 语聊房自身麦位上的人，都在toUserList列表内，就是全麦
        for (AudioRoomMicModel model : micList) {
            if (model.userId <= 0) {
                continue;
            }
            String userId = model.userId + "";
            boolean exists = false;
            for (UserInfo userInfo : toUserList) {
                if (userId.equals(userInfo.userID)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) { // 有一个不存在，那就是非全麦
                return false;
            }
        }
        return true;
    }

    /** 显示礼物 */
    protected void showGift(GiftModel giftModel, int giftCount, UserInfo fromUser, List<UserInfo> toUserList, boolean isAllSeat) {
        if (!canShowGift()) {
            return;
        }
        if (effectView == null) {
            effectView = new GiftEffectView(this);
            effectView.addLifecycleObserver(this);
            giftContainer.addView(effectView);
        }
        effectView.showEffect(giftModel);
    }

    /** 是否可以展示礼物动画 */
    protected boolean canShowGift() {
        return true;
    }

    protected void initGame() {
        if (roomConfig.isSudGame) {
            callSudSwitchGame(getGameRoomId(), roomInfoModel.gameId, getLoadMGMode(), getAuthorizationSecret());
        }
        updateGameNumber();
    }

    /** 获取游戏房间的id */
    public String getGameRoomId() {
        return roomInfoModel.roomId + "";
    }

    // 切换游戏之后，更新页面样式
    protected void updatePageStyle() {
        if (isSupportASR(playingGameId) && roomConfig.isShowASRTopHint) {
            tvASRHint.setVisibility(View.VISIBLE);
        } else {
            tvASRHint.setVisibility(View.GONE);
        }
//        if (roomConfig.isSupportAddRobot && playingGameId > 0 && isOwner()) {
//            tvAddRobot.setVisibility(View.VISIBLE);
//        } else {
//            tvAddRobot.setVisibility(View.GONE);
//        }
        if (playingGameId == GameIdCons.MONOPOLY) {
            showMonopolyCardView();
        } else {
            hideMonopolyCardView();
        }
    }

    private void showMonopolyCardView() {
        if (mMonopolyCardContainer == null) {
            initMonopolyCardView();
        } else {
            mMonopolyCardContainer.setVisibility(View.VISIBLE);
        }
    }

    private void initMonopolyCardView() {
        ViewStub viewStub = findViewById(R.id.view_stub_monopoly_card_view);
        viewStub.inflate();
        mMonopolyCardContainer = findViewById(R.id.monopoly_card_container);
        if (mMonopolyCardContainer != null) {
            mMonopolyCardContainer.setVisibility(View.VISIBLE);
        }
        refreshMonopolyCardDatas();
        mMonopolyCardContainer.setCustomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendGiftDialog(null, 0);
            }
        });
    }

    private void refreshMonopolyCardDatas() {
        RoomRepository.giftList(this, roomInfoModel.sceneType, playingGameId, new RxCallback<GiftListResp>() {
            private boolean isSuccess;

            @Override
            public void onSuccess(GiftListResp giftListResp) {
                super.onSuccess(giftListResp);
                isSuccess = true;
                mMonopolyCardContainer.setDatas(giftListResp);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                if (!isSuccess) {
                    ThreadUtils.runOnUiThreadDelayed(() -> {
                        refreshMonopolyCardDatas();
                    }, 10000);
                }
            }
        });
    }

    private void hideMonopolyCardView() {
        if (mMonopolyCardContainer != null) {
            mMonopolyCardContainer.setVisibility(View.GONE);
        }
    }

    protected void updateStatusBar() {
        if (roomConfig.isSudGame && roomInfoModel != null && roomInfoModel.gameId > 0) { // 玩着游戏
            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(true).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).fullScreen(false).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    /**
     * 绑定后台服务
     */
    private void bindService() {
        Intent intent = new Intent(this, SceneRoomService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Android8后需要开启前台服务才可以
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (SceneRoomService.MyBinder) service;
            binder.dismissFloating();
            enterRoom();
            initGame();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 系统会在与服务的连接意外中断（或者随着activity 的生命周期stop）时调用该方法，当客户端取消绑定的时候，不会回调该方法
            gameViewModel.onDestroy();
            finish();
        }
    };


    // region service回调
    @Override
    public void onEnterRoomSuccess() {
        checkAddDefaultRobot();
        businessAutoUpMic();
    }

    /** 检查是否要添加默认的机器人 */
    protected void checkAddDefaultRobot() {
        if (!roomConfig.isSupportAddRobot) {
            return;
        }
        boolean existsMicUser = false;
        if (binder != null) {
            for (AudioRoomMicModel audioRoomMicModel : binder.getMicList()) {
                if (audioRoomMicModel.hasUser()) {
                    existsMicUser = true;
                    break;
                }
            }
        }
        // 麦位上有人了，不再添加默认机器人
        if (existsMicUser) {
            return;
        }
        // 不存在，默认添加三个机器人
        RoomRepository.robotList(this, 300, new RxCallback<RobotListResp>() {
            @Override
            public void onSuccess(RobotListResp resp) {
                super.onSuccess(resp);
                if (resp == null || resp.robotList == null || resp.robotList.size() == 0) {
                    return;
                }
                if (binder == null) {
                    return;
                }
                // 修改逻辑为，随机选三个机器人上麦位
                int addRobotCount = 3;
                if (resp.robotList.size() <= addRobotCount) { // 机器人列表不足，不用随机
                    for (int i = 0; i < resp.robotList.size(); i++) {
                        if (i < addRobotCount) {
                            binder.robotUpMicLocation(UserInfoRespConverter.conver(resp.robotList.get(i)), i + 1);
                        }
                    }
                } else { // 随机选机器人
                    List<AIPlayers> list = new ArrayList<>();
                    // 默认先加一个难度最高的
                    findLevelRobot(resp, addRobotCount, list, AIPlayers.LEVEL_DIFFICULTY);

                    // 再找中等难度的
                    if (list.size() < addRobotCount) {
                        findLevelRobot(resp, addRobotCount, list, AIPlayers.LEVEL_MODERATE);
                    }

                    // 再找简单的
                    if (list.size() < addRobotCount) {
                        findLevelRobot(resp, addRobotCount, list, AIPlayers.LEVEL_SIMPLE);
                    }

                    // 如果还需要，那就不限等级的找了
                    findLevelRobot(resp, addRobotCount, list, null);

                    for (int i = 0; i < list.size(); i++) {
                        binder.robotUpMicLocation(UserInfoRespConverter.conver(list.get(i)), i + 1);
                    }
                }
            }
        });
    }

    /**
     * 寻找指定等级机器人
     * needLevel不为空时，只找一个对应等级的即可
     * needLevel为空时，找到满人为止(addRobotCount)
     */
    private static void findLevelRobot(RobotListResp resp, int addRobotCount, List<AIPlayers> list, Integer needLevel) {
        Random random = new Random();
        int whileCount = 0;
        while (list.size() < addRobotCount) {
            whileCount++;
            if (whileCount > 1000) { // 防止一直找不到对应等级的机器人
                break;
            }
            int position = random.nextInt(resp.robotList.size());
            AIPlayers aiPlayers = resp.robotList.get(position);
            if (!list.contains(aiPlayers)) {
                if (needLevel == null) {
                    list.add(aiPlayers);
                } else if (aiPlayers.level == needLevel) {
                    list.add(aiPlayers);
                    return;
                }
            }
        }
    }

    /** 业务自动上麦 */
    protected void businessAutoUpMic() {
        if (binder != null) {
            binder.autoUpMic(OperateMicType.USER);
        }
    }

    @Override
    public void onMicList(List<AudioRoomMicModel> list) {
        micView.setList(list);
        if (roomGiftDialog != null) {
            roomGiftDialog.updateMicUsers(getGiftDialogMicList());
        }
    }

    @Override
    public void onMicChange(int micIndex, UserInfo userInfo, boolean isUp) {
        if (isUp) {
            if (userInfo.isAi) {
                List<AIPlayers> aiPlayers = new ArrayList<>();
                aiPlayers.add(AIPlayersConverter.conver(userInfo));
                gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameAddAIPlayers(aiPlayers, 1);
            }
        } else {
            if (userInfo.isAi) {
                kickUserFromGame(userInfo.userID);
            }
        }
    }

    @Override
    public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
        micView.notifyItemChange(micIndex, model);
        updateGiftDialogMicUsers(model);
    }

    /** 更新礼物弹窗上面的麦位数据 */
    protected void updateGiftDialogMicUsers(AudioRoomMicModel model) {
        if (roomGiftDialog != null) {
            roomGiftDialog.updateOneMicUsers(model);
        }
    }

    @Override
    public void selfMicIndex(int micIndex) {
        if (micIndex >= 0) {
            bottomView.hideGotMic();
            bottomView.showMicState();
        } else {
            bottomView.showGotMic();
            bottomView.hideMicState();
        }
        gameViewModel.selfMicIndex(micIndex);
    }

    @Override
    public void addPublicMsg(Object msg) {
        chatView.addMsg(msg);
    }

    @Override
    public void onChatList(List<Object> list) {
        chatView.setList(list);
    }

    @Override
    public void sendGiftsNotify(GiftNotifyModel notify) {
        showGift(notify.gift, notify.giftCount, notify.sendUser, notify.toUserList, notify.isAllSeat);
    }

    @Override
    public void onMicStateChanged(boolean isOpened) {
        bottomView.setMicOpened(isOpened);
        gameViewModel.setMicOpened(isOpened);
    }

    @Override
    public void onSoundLevel(String userId, int micIndex, float soundLevel) {
        if (soundLevel > 1) { // 大于1，才触发声浪
            micView.startSoundLevel(micIndex);
        }
        gameViewModel.onSoundLevel(userId, soundLevel);
    }

    @Override
    public void onRoomOnlineUserCountUpdate(int count) {
        topView.setNumber(count + "");
    }

    @Override
    public void onGameChange(long gameId) {
        switchGame(gameId);
    }

    protected boolean switchGame(long gameId) {
        if (!roomConfig.isSudGame) {
            return false;
        }
        String gameRoomId = getGameRoomId();
        if (playingGameId == gameId && gameRoomId.equals(gameViewModel.getGameRoomId())) {
            return false;
        }
        playingGameId = gameId;
        roomInfoModel.gameId = gameId;
        callSudSwitchGame(getGameRoomId(), gameId, getLoadMGMode(), getAuthorizationSecret());
        updatePageStyle();
        updateStatusBar();
        updateGameNumber();
        if (binder != null) {
            binder.updateMicList();
        }
        return true;
    }

    protected void callSudSwitchGame(String gameRoomId, long gameId, int loadMGMode, String authorizationSecret) {
        gameViewModel.switchGame(this, getGameRoomId(), gameId, getLoadMGMode(), getAuthorizationSecret());
    }

    /**
     * 获取跨App域模式时的授权码
     * 当加载游戏模式为{@link SudLoadMGMode#kSudLoadMGModeAppCrossAuth 为跨APP域模式}时
     * 本字段才有效用
     *
     * @return
     */
    protected String getAuthorizationSecret() {
        return null;
    }

    /**
     * 获取加载游戏模式
     * {@link SudLoadMGMode#kSudLoadMGModeNormal 为默认模式}； {@link SudLoadMGMode#kSudLoadMGModeAppCrossAuth 为跨APP域模式}
     *
     * @return 加载游戏模式
     */
    protected int getLoadMGMode() {
        return SudLoadMGMode.kSudLoadMGModeNormal;
    }

    @Override
    public void onWrapMicModel(AudioRoomMicModel model) {
        gameViewModel.wrapMicModel(model);
        if (roomGiftDialog != null) {
            roomGiftDialog.setGiftEnable(model);
        } else {
            model.giftEnable = false;
        }
    }

    @Override
    public void onCapturedAudioData(AudioPCMData audioPCMData) {
        gameViewModel.onCapturedAudioData(audioPCMData);
    }

    @Override
    public void onSelfSendMsg(String msg) {
        gameViewModel.sendMsgCompleted(msg);

        // 大富翁弹幕区域
        if (mMonopolyCardContainer != null && playingGameId == GameIdCons.MONOPOLY) {
            List<GiftListResp.BackGiftModel> danmakuList = mMonopolyCardContainer.getDanmakuList();
            if (danmakuList != null && danmakuList.size() > 0) {
                for (GiftListResp.BackGiftModel backGiftModel : danmakuList) {
                    if (backGiftModel.details != null && !TextUtils.isEmpty(backGiftModel.details.content)) {
                        String content = backGiftModel.details.content;
                        if (content.equals(msg)) { // 触发送礼，送给自己就可以了
                            List<UserInfo> userInfoList = new ArrayList<>();
                            userInfoList.add(RoomCmdModelUtils.getSendUser());
                            onSendGift(GiftModelConverter.conver(backGiftModel), 1, userInfoList);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMicLocationSwitchCompleted(int micIndex, boolean operate, OperateMicType type) {
        if (operate) {
            // 下面这个逻辑处理的原因是在于
            // 例如狼人杀游戏，用户点击加入游戏后，游戏会短时间内发送mg_common_player_in以及mg_common_self_microphone
            // app会处理上麦以及开麦的逻辑，但上麦是有延迟的，并且开麦依赖于上麦成功之后的streamId
            // 所以这里在上麦成功之后，再判断一下是否要执行开麦的逻辑
            if (type == OperateMicType.GAME_AUTO) {
                Boolean isPublish = gameViewModel.gameRTCPublishLiveData.getValue();
                if (isPublish != null && isPublish) {
                    openMic();
                }
            }
        }
    }

    @Override
    public void onOrderInvite(RoomCmdUserOrderModel model) {
    }

    @Override
    public void onOrderInviteAnswered(OrderInviteModel model) {
    }

    @Override
    public void onOrderOperate(long orderId, long gameId, String gameName, String userId, String userName, boolean operate) {
    }

    @Override
    public void onReceiveInvite(boolean agreeState) {
    }

    @Override
    public void onRoomPkUpdate() {
    }

    @Override
    public void onRoomPkChangeGame(long gameId) {
    }

    @Override
    public void onRoomPkRemoveRival() {
    }

    @Override
    public void onRoomPkCoutndown() {
    }

    @Override
    public void onRecoverCompleted() {
    }

    @Override
    public void notifyStateChange(String state, String dataJson, ISudListenerNotifyStateChange listener) {
        gameViewModel.notifyStateChange(state, dataJson, listener);
    }

    @Override
    public void onDanceList(List<DanceModel> list) {
    }

    @Override
    public void onUpdateDance(int index) {
    }

    @Override
    public void onDanceWait() {
    }

    @Override
    public void onDiscoContribution(List<ContributionModel> list) {
    }

    @Override
    public void onDJCountdown(int countdown) {
    }

    @Override
    public void onKickOutRoom(String userId) {
        processOnKickOutRoom(userId);
    }

    @Override
    public void onUpdateCrossApp(CrossAppModel model) {
    }

    @Override
    public void onDanmakuMatch(RoomCmdDanmakuTeamChangeModel model) {
    }

    @Override
    public void onAudio3DConfig(SudGIPAPPState.AppCustomCrSetRoomConfig model) {
    }

    @Override
    public void onAudio3DSeats(SudGIPAPPState.AppCustomCrSetSeats model) {
    }

    @Override
    public void onAudio3DFaceNotify(Audio3DCmdFaceNotifyModel model) {
    }

    @Override
    public void onPlayerVideoSizeChanged(String streamID, int width, int height) {
    }

    @Override
    public void onMonopolyCardGiftNotify(RoomCmdMonopolyCardGiftModel model) {
        if (model == null || model.content == null || model.content.receiverUidList == null) {
            return;
        }
        for (String uid : model.content.receiverUidList) {
            SudGIPAPPState.APPCommonGameShowMonopolyCardEffect state = new SudGIPAPPState.APPCommonGameShowMonopolyCardEffect();
            state.type = model.content.type;
            state.fromUid = model.content.senderUid;
            state.toUid = uid;
            state.count = model.content.amount;
            gameViewModel.notifyStateChange(SudGIPAPPState.APP_COMMON_GAME_SHOW_MONOPOLY_CARD_EFFECT, state);
        }
    }

    @Override
    public void onGamePropsCardGift(RoomCmdPropsCardGiftModel model) {
        if (model == null || model.content == null || model.content.receiverUidList == null) {
            return;
        }
        for (String uid : model.content.receiverUidList) {
            SudGIPAPPState.AppCommonGamePlayerPropsCardsEffect state = new SudGIPAPPState.AppCommonGamePlayerPropsCardsEffect();
            state.paid_events_type = model.content.paidEventType;
            state.fromUid = model.content.senderUid;
            state.toUid = uid;
            state.count = model.content.amount;
            gameViewModel.notifyStateChange(SudGIPAPPState.APP_COMMON_GAME_PLAYER_PROPS_CARDS_EFFECT, state);
        }
    }

    // endregion service回调

    /** 处理踢出房间的逻辑 */
    private void processOnKickOutRoom(String userId) {
        // 自己的话，执行退出房间
        if ((HSUserInfo.userId + "").equals(userId)) {
            delayExitRoom();
            return;
        }
        // 如果是别人，判断自己是不是队长，是队长就把他踢出游戏
        kickUserFromGame(userId);
    }

    /** 把该用户从游戏当中踢出 */
    protected void kickUserFromGame(String userId) {
        if (playingGameId > 0
                && gameViewModel.isCaptain(HSUserInfo.userId)
                && gameViewModel.getGameState() != SudGIPMGState.MGCommonGameState.PLAYING
                && gameViewModel.getGameState() != SudGIPMGState.MGCommonGameState.LOADING) {
            gameViewModel.notifyAPPCommonSelfKick(userId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameViewModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameViewModel.onResume();
        updateStatusBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameViewModel.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameViewModel.onStop();
    }

    // 释放绑定的服务
    protected void releaseService() {
        if (binder != null) {
            binder.removeCallback(this);
            unbindService(serviceConnection);
            binder = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseService();
        if (effectView != null) {
            effectView.onDestory();
        }
        gameViewModel.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }

    private final Runnable delayDismissTask = new Runnable() {
        @Override
        public void run() {
            Animation animation = clOpenMic.getAnimation();
            if (animation != null) {
                animation.cancel();
            }
            clOpenMic.setVisibility(View.GONE);
        }
    };

    public SceneRoomService.MyBinder getBinder() {
        return binder;
    }

    public RoomInfoModel getRoomInfoModel() {
        return roomInfoModel;
    }

    public long getPlayingGameId() {
        return playingGameId;
    }

    public long getRoomId() {
        return roomInfoModel.roomId;
    }

    public void webGameOnCloseGame() {
        switchGame(0);
    }

    public void webGameOnPay(WebGameOnPayListener listener) {
        HomeRepository.addCoin(this, HSUserInfo.userId, 10_0000, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        });
    }

    public interface WebGameOnPayListener {
        void onSuccess();
    }

}
