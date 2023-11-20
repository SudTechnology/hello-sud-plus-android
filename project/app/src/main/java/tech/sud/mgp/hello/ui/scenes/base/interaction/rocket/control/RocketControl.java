package tech.sud.mgp.hello.ui.scenes.base.interaction.rocket.control;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState.AppCustomRocketPlayModelList;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.common.event.model.JumpRocketEvent;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.req.RocketFireReq;
import tech.sud.mgp.hello.service.game.resp.RocketFireResp;
import tech.sud.mgp.hello.ui.common.dialog.LoadingDialog;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity.BaseInteractionRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.control.BaseInteractionControl;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.widget.view.InteractionGameContainer;
import tech.sud.mgp.hello.ui.scenes.base.interaction.rocket.viewmodel.RocketGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.interaction.rocket.widget.dialog.RocketFireSelectDialog;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftId;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 火箭控制器，处理火箭View以及ViewModel的交互
 */
public class RocketControl extends BaseInteractionControl {

    protected InteractionGameContainer rocketContainer;
    protected RocketGameViewModel rocketGameViewModel = new RocketGameViewModel();
    private TextView tvCloseRocketEffect;
    private View rocketEffectOperateContainer;
    private TextView tvRocketQuicken;

    private boolean isShowRocketScene; // 是否要展示火箭主页面
    private LoadingDialog loadingDialog; // 加载火箭loading弹窗
    private final List<AppCustomRocketPlayModelList> fireRocketList = new ArrayList<>(); // 火箭待发射列表
    private RocketFireSelectDialog rocketFireSelectDialog;

    public RocketControl(BaseInteractionRoomActivity<?> activity) {
        super(activity);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        View includeRocket = View.inflate(getContext(), R.layout.include_rocket, null);
        FrameLayout interactionContainer = getInteractionContainer();
        interactionContainer.addView(includeRocket, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        rocketContainer = includeRocket.findViewById(R.id.rocket_container);
        tvCloseRocketEffect = includeRocket.findViewById(R.id.tv_close_rocket_effect);
        rocketEffectOperateContainer = includeRocket.findViewById(R.id.rocket_effect_operate_container);
        tvRocketQuicken = includeRocket.findViewById(R.id.tv_rocket_quicken);

        // TODO: 2022/11/7 下面的代码需要去掉的
//        SudPermissionUtils.requirePermission(this, getSupportFragmentManager(),
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                new PermissionFragment.OnPermissionListener() {
//                    @Override
//                    public void onPermission(boolean success) {
//                        if (success) {
//                        }
//                    }
//                });
//        File dir = Environment.getExternalStorageDirectory();
//        File file = new File(dir.getAbsolutePath() + File.separator + "Pictures", "rocket.rpk");
//        SudMGP.getCfg().addEmbeddedMGPkg(GameIdCons.CUSTOM_ROCKET, file.getAbsolutePath());

//        SudMGP.getCfg().addEmbeddedMGPkg(GameIdCons.CUSTOM_ROCKET, "rocket.rpk");

        rocketGameViewModel.fragmentActivity = activity;
        rocketGameViewModel.roomId = getRoomId();

        rocketGameViewModel.isShowLoadingGameBg = false;
        rocketGameViewModel.isShowCustomLoading = true;
    }

    @Override
    public void setListeners() {
        super.setListeners();
        rocketGameViewModel.gameViewLiveData.observe(activity, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) {
                    rocketContainer.removeAllViews();
                } else {
                    rocketContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    rocketContainer.bringToFrondClickRectView();
                }
            }
        });
        rocketGameViewModel.gameFireRocketLiveData.observe(activity, this::onGameFireRocket);
        rocketGameViewModel.clickLockComponentLiveData.observe(activity, this::onClickLockComponent);
        rocketGameViewModel.rocketPrepareCompletedLiveData.observe(activity, o -> onRocketPrepareCompleted());
        rocketGameViewModel.rocketClickRectLiveData.observe(activity, model -> {
            List<SudMGPMGState.InteractionClickRect> list = null;
            if (model != null) {
                list = model.list;
            }
            rocketContainer.setClickRectList(list);
        });
        rocketGameViewModel.rocketPlayEffectStartLiveData.observe(activity, o -> onRocketPlayEffectStart());
        rocketGameViewModel.rocketPlayEffectFinishLiveData.observe(activity, o -> onRocketPlayEffectFinish());
        rocketGameViewModel.destroyRocketLiveData.observe(activity, o -> stopInteractionGame());
        rocketGameViewModel.gameLoadingProgressLiveData.observe(activity, model -> {
            if (model != null && model.progress == 100) {
                rocketContainer.postDelayed(() -> {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }, 100);
            }
        });

        LiveEventBus.<JumpRocketEvent>get(LiveEventBusKey.KEY_JUMP_ROCKET).observeSticky(activity, jumpRocketObserver);
        tvCloseRocketEffect.setOnClickListener((v) -> {
            rocketGameViewModel.notifyAppCustomRocketClosePlayEffect();
        });
        tvRocketQuicken.setOnClickListener((v) -> rocketGameViewModel.notifyAppCustomRocketFlyClick());
    }

    private void onRocketPlayEffectFinish() {
        rocketEffectOperateContainer.setVisibility(View.GONE);
    }

    private void onRocketPlayEffectStart() {
        if (BuildConfig.gameIsTestEnv) {
            rocketEffectOperateContainer.setVisibility(View.VISIBLE);
        }
    }

    private final Observer<JumpRocketEvent> jumpRocketObserver = new Observer<JumpRocketEvent>() {
        @Override
        public void onChanged(JumpRocketEvent jumpRocketEvent) {
            if (jumpRocketEvent.isConsume) {
                return;
            }
            jumpRocketEvent.isConsume = true;
            showRocketGameScene();
        }
    };

    /** 火箭准备完成了 */
    private void onRocketPrepareCompleted() {
        // 打开火箭主页面
        if (isShowRocketScene) {
            isShowRocketScene = false;
            rocketGameViewModel.notifyAppCustomRocketShowGameScene();
        }
        hideLoadingDialog();
        rocketFirePlay();
    }

    private void rocketFirePlay() {
        boolean rocketIsReady = rocketGameViewModel.rocketIsReady();
        if (!rocketIsReady) {
            return;
        }
        for (AppCustomRocketPlayModelList model : fireRocketList) {
            rocketGameViewModel.notifyAppCustomRocketPlayModelList(model);
        }
        fireRocketList.clear();
    }

    /** 点击了锁住的组件 */
    private void onClickLockComponent(SudMGPMGState.MGCustomRocketClickLockComponent model) {
        if (model == null) {
            return;
        }
        SimpleChooseDialog dialog = new SimpleChooseDialog(activity, getString(R.string.component_lock_title));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    unlockComponent(model);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /** 解锁该组件 */
    private void unlockComponent(SudMGPMGState.MGCustomRocketClickLockComponent model) {
        GameRepository.rocketUnlockComponent(activity, model.componentId, 0, new RxCallback<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                SudMGPAPPState.AppCustomRocketUnlockComponent appCustomRocketUnlockComponent = new SudMGPAPPState.AppCustomRocketUnlockComponent();
                appCustomRocketUnlockComponent.type = model.type;
                appCustomRocketUnlockComponent.componentId = model.componentId;
                rocketGameViewModel.notifyAppCustomRocketUnlockComponent(appCustomRocketUnlockComponent);
            }
        });
    }

    /** 游戏要发射火箭 */
    private void onGameFireRocket(SudMGPMGState.MGCustomRocketFireModel model) {
        showRocketFireSelectDialog(model);
    }

    /** 显示火箭发射选择用户的弹窗 */
    private void showRocketFireSelectDialog(SudMGPMGState.MGCustomRocketFireModel model) {
        if (getBinder() == null) {
            return;
        }
        RocketFireSelectDialog dialog = new RocketFireSelectDialog();
        dialog.setMicList(getBinder().getMicList());
        dialog.setOnConfirmListener(new RocketFireSelectDialog.OnConfirmListener() {
            @Override
            public void onConfirm(List<UserInfo> list) {
                onSendRocket(true, model, 1, list);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
        dialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                rocketFireSelectDialog = null;
            }
        });
        rocketFireSelectDialog = dialog;
    }

    /** 打开火箭主页面 */
    private void showRocketGameScene() {
        boolean success = startRocket();
        if (success) {
            isShowRocketScene = true;
            rocketGameViewModel.notifyAppCustomRocketShowGameScene();
        }
    }

    /** 打开火箭 */
    private boolean startRocket() {
        if (getPlayingGameId() > 0) { // 有加载游戏时，不加载火箭
            return false;
        }
        long playingGameId = rocketGameViewModel.getPlayingGameId();
        if (playingGameId <= 0) {
            showLoadingDialog();
        }
        rocketGameViewModel.switchGame(activity, getRoomId() + "", GameIdCons.CUSTOM_ROCKET);
        return true;
    }

    /** 隐藏火箭 */
    public void stopInteractionGame() {
        rocketEffectOperateContainer.setVisibility(View.GONE);
        rocketGameViewModel.switchGame(activity, getRoomId() + "", 0);
        hideLoadingDialog();
    }

    private void showLoadingDialog() {
        if (loadingDialog != null) {
            return;
        }
        loadingDialog = new LoadingDialog(getString(R.string.go_rocket_stage));
        loadingDialog.show(getSupportFragmentManager(), null);
        loadingDialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                loadingDialog = null;
            }
        });
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 发射火箭
     *
     * @param isFromGame 是否是游戏触发
     */
    private void onSendRocket(boolean isFromGame, SudMGPMGState.MGCustomRocketFireModel model, int number, List<UserInfo> userInfoList) {
        if (userInfoList == null || userInfoList.size() == 0) {
            return;
        }
        RocketFireReq req = new RocketFireReq();
        req.roomId = getRoomId();
        req.number = number;
        List<Long> receiverList = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            try {
                receiverList.add(Long.parseLong(userInfo.userID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        req.receiverList = receiverList;
        if (model != null) {
            req.componentList = model.componentList;
        }
        GameRepository.rocketFire(activity, req, new RxCallback<RocketFireResp>() {
            @Override
            public void onSuccess(RocketFireResp rocketFireResp) {
                super.onSuccess(rocketFireResp);
                if (rocketFireResp == null || rocketFireResp.userOrderIdsMap == null) {
                    return;
                }
                if (getBinder() == null) {
                    return;
                }
                rocketGameViewModel.notifyAppCustomRocketFireModel(new SudMGPAPPState.AppCustomRocketFireModel());
                for (UserInfo userInfo : userInfoList) {
                    List<String> orderList = rocketFireResp.userOrderIdsMap.get(userInfo.userID);
                    if (orderList == null || orderList.size() == 0) {
                        continue;
                    }
                    // 因为后台返回的是多个用户的订单列表，所以这里将其拆成每个用户的订单通过信令发送出去
                    RocketFireResp useRocketFireResp = new RocketFireResp();
                    useRocketFireResp.userOrderIdsMap = new HashMap<>();
                    useRocketFireResp.userOrderIdsMap.put(userInfo.userID, orderList);
                    useRocketFireResp.interactConfig = rocketFireResp.interactConfig;
                    useRocketFireResp.componentList = rocketFireResp.componentList;

                    GiftModel giftModel = GiftHelper.getInstance().createGiftModel(GiftId.ROCKET);
                    giftModel.extData = SudJsonUtils.toJson(useRocketFireResp);
                    getBinder().sendGift(giftModel, number, userInfo, isAllSeat(userInfo));
                }
                onShowRocketFire(rocketFireResp);
            }
        });
    }

    private boolean isAllSeat(UserInfo userInfo) {
        List<UserInfo> list = new ArrayList<>();
        list.add(userInfo);
        return activity.isAllSeat(list);
    }

    /** 展示火箭发射 */
    private void onShowRocketFire(RocketFireResp rocketFireResp) {
        if (rocketFireResp == null || rocketFireResp.userOrderIdsMap == null) {
            return;
        }
        Set<String> keySet = rocketFireResp.userOrderIdsMap.keySet();
        for (String userId : keySet) {
            List<String> orderList = rocketFireResp.userOrderIdsMap.get(userId);
            if (orderList != null) {
                for (String orderId : orderList) {
                    AppCustomRocketPlayModelList model = new AppCustomRocketPlayModelList();
                    model.orderId = orderId;
                    model.interactConfig = rocketFireResp.interactConfig;
                    model.componentList = rocketFireResp.componentList;
                    fireRocketList.add(model);
                    startRocket();
                }
            }
        }
        rocketFirePlay();
    }

    @Override
    public boolean onInterceptSendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUsers) {
        if (giftModel.giftId == GiftId.ROCKET) {
            onSendRocket(false, null, giftCount, toUsers);
            return true;
        }
        return super.onInterceptSendGift(giftModel, giftCount, toUsers);
    }

    @Override
    public void showGift(GiftModel giftModel) {
        super.showGift(giftModel);
        if (giftModel.giftId == GiftId.ROCKET) {
            RocketFireResp rocketFireResp = SudJsonUtils.fromJson(giftModel.extData, RocketFireResp.class);
            onShowRocketFire(rocketFireResp);
        }
    }

    @Override
    public void switchGame(long gameId) {
        super.switchGame(gameId);
        if (gameId > 0) {
            stopInteractionGame();
        }
    }

    @Override
    public void onGiftDialogShowCustomRocket() {
        super.onGiftDialogShowCustomRocket();
        showRocketGameScene();
    }

    @Override
    public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
        super.notifyMicItemChange(micIndex, model);
        if (rocketFireSelectDialog != null && getBinder() != null) {
            rocketFireSelectDialog.setMicList(getBinder().getMicList());
        }
    }

    @Override
    public void onMicList(List<AudioRoomMicModel> list) {
        super.onMicList(list);
        if (rocketFireSelectDialog != null) {
            rocketFireSelectDialog.setMicList(list);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rocketGameViewModel.onDestroy();
        LiveEventBus.<JumpRocketEvent>get(LiveEventBusKey.KEY_JUMP_ROCKET).removeObserver(jumpRocketObserver);
    }

    @Override
    public void onClickEntrance() {
        super.onClickEntrance();
        showRocketGameScene();
    }

    @Override
    public long getControlGameId() {
        return GameIdCons.CUSTOM_ROCKET;
    }

}
