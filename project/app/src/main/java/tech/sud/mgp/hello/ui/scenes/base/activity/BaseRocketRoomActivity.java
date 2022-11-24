package tech.sud.mgp.hello.ui.scenes.base.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState.AppCustomRocketPlayModelList;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
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
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppRocketGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.dialog.RocketFireSelectDialog;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.RocketContainer;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftId;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 带火箭动效的房间
 */
public abstract class BaseRocketRoomActivity<T extends AppGameViewModel> extends BaseRoomActivity<T> {

    protected View viewRocketEntrance;
    protected RocketContainer rocketContainer;
    protected AppRocketGameViewModel rocketGameViewModel = new AppRocketGameViewModel();
    private View rocketOperateContainer;
    private TextView tvCloseRocketEffect;
    private View rocketEffectOperateContainer;
    private TextView tvRocketQuicken;

    private boolean isShowRocketScene; // 是否要展示火箭主页面
    private LoadingDialog loadingDialog; // 加载火箭loading弹窗
    private final List<AppCustomRocketPlayModelList> fireRocketList = new ArrayList<>(); // 火箭待发射列表
    private RocketFireSelectDialog rocketFireSelectDialog;

    @Override
    protected void initWidget() {
        super.initWidget();
        viewRocketEntrance = findViewById(R.id.view_custom_rocket);
        rocketContainer = findViewById(R.id.rocket_container);
        rocketOperateContainer = findViewById(R.id.rocket_operate_container);
        tvCloseRocketEffect = findViewById(R.id.tv_close_rocket_effect);
        rocketEffectOperateContainer = findViewById(R.id.rocket_effect_operate_container);
        tvRocketQuicken = findViewById(R.id.tv_rocket_quicken);

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

        rocketGameViewModel.fragmentActivity = this;
        rocketGameViewModel.roomId = roomInfoModel.roomId;

        rocketGameViewModel.isShowLoadingGameBg = false;
        rocketGameViewModel.isShowCustomLoading = true;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewRocketEntrance.setOnClickListener((v) -> {
            showRocketGameScene();
        });
        rocketGameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) {
                    rocketContainer.removeAllViews();
                } else {
                    rocketContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });
        rocketGameViewModel.gameFireRocketLiveData.observe(this, new Observer<SudMGPMGState.MGCustomRocketFireModel>() {
            @Override
            public void onChanged(SudMGPMGState.MGCustomRocketFireModel model) {
                onGameFireRocket(model);
            }
        });
        rocketGameViewModel.clickLockComponentLiveData.observe(this, new Observer<SudMGPMGState.MGCustomRocketClickLockComponent>() {
            @Override
            public void onChanged(SudMGPMGState.MGCustomRocketClickLockComponent model) {
                onClickLockComponent(model);
            }
        });
        rocketGameViewModel.rocketPrepareCompletedLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                onRocketPrepareCompleted();
            }
        });
        rocketGameViewModel.rocketClickRectLiveData.observe(this, new Observer<SudMGPMGState.MGCustomRocketSetClickRect>() {
            @Override
            public void onChanged(SudMGPMGState.MGCustomRocketSetClickRect model) {
                List<SudMGPMGState.MGCustomRocketSetClickRect.RocketClickRect> list = null;
                if (model != null) {
                    list = model.list;
                }
                rocketContainer.setClickRectList(list);
            }
        });
        rocketGameViewModel.rocketPlayEffectStartLiveData.observe(this, o -> {
            onRocketPlayEffectStart();
        });
        rocketGameViewModel.rocketPlayEffectFinishLiveData.observe(this, o -> {
            onRocketPlayEffectFinish();
        });
        rocketGameViewModel.destroyRocketLiveData.observe(this, o -> {
            stopRocket();
        });
        LiveEventBus.<JumpRocketEvent>get(LiveEventBusKey.KEY_JUMP_ROCKET).observeSticky(this, jumpRocketObserver);
        tvCloseRocketEffect.setOnClickListener((v) -> {
            rocketGameViewModel.notifyAppCustomRocketClosePlayEffect();
        });
        tvRocketQuicken.setOnClickListener((v) -> {
            rocketGameViewModel.notifyAppCustomRocketFlyClick();
        });
    }

    private void onRocketPlayEffectFinish() {
        rocketEffectOperateContainer.setVisibility(View.GONE);
    }

    private void onRocketPlayEffectStart() {
        rocketEffectOperateContainer.setVisibility(View.VISIBLE);
    }

    private final Observer<JumpRocketEvent> jumpRocketObserver = new Observer<JumpRocketEvent>() {
        @Override
        public void onChanged(JumpRocketEvent jumpRocketEvent) {
            if (jumpRocketEvent.isConsume) {
                return;
            }
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
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.component_lock_title));
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
        GameRepository.rocketUnlockComponent(this, model.componentId, 0, new RxCallback<Object>() {
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
        if (binder == null) {
            return;
        }
        RocketFireSelectDialog dialog = new RocketFireSelectDialog();
        dialog.setMicList(binder.getMicList());
        dialog.setOnConfirmListener(new RocketFireSelectDialog.OnConfirmListener() {
            @Override
            public void onConfirm(List<UserInfo> list) {
                onSendRocket(model, 1, list);
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

    @Override
    protected void onSendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUsers) {
        if (giftModel.giftId == GiftId.ROCKET) {
            onSendRocket(null, giftCount, toUsers);
        } else {
            super.onSendGift(giftModel, giftCount, toUsers);
        }
    }

    /** 发射火箭 */
    private void onSendRocket(SudMGPMGState.MGCustomRocketFireModel model, int number, List<UserInfo> userInfoList) {
        if (userInfoList == null || userInfoList.size() == 0) {
            return;
        }
        RocketFireReq req = new RocketFireReq();
        req.roomId = roomInfoModel.roomId;
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
        GameRepository.rocketFire(this, req, new RxCallback<RocketFireResp>() {
            @Override
            public void onSuccess(RocketFireResp rocketFireResp) {
                super.onSuccess(rocketFireResp);
                String extData = SudJsonUtils.toJson(rocketFireResp);
                GiftModel giftModel = GiftHelper.getInstance().getGift(GiftId.ROCKET);
                giftModel.extData = extData;
                for (UserInfo userInfo : userInfoList) {
                    if (binder != null) {
                        binder.sendGift(giftModel, number, userInfo);
                    }
                }
                onShowRocketFire(rocketFireResp);
            }
        });
    }

    /** 显示礼物 */
    @Override
    protected void showGift(GiftModel giftModel) {
        super.showGift(giftModel);
        if (!canShowGift()) {
            return;
        }
        if (giftModel.giftId == GiftId.ROCKET) {
            RocketFireResp rocketFireResp = SudJsonUtils.fromJson(giftModel.extData, RocketFireResp.class);
            onShowRocketFire(rocketFireResp);
        }
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
                    model.componentList = rocketFireResp.componentList;
                    fireRocketList.add(model);
                    startRocket();
                }
            }
        }
        rocketFirePlay();
    }

    /** 打开火箭主页面 */
    protected void showRocketGameScene() {
        boolean success = startRocket();
        if (success) {
            isShowRocketScene = true;
            rocketGameViewModel.notifyAppCustomRocketShowGameScene();
        }
    }

    /** 打开火箭 */
    private boolean startRocket() {
        if (playingGameId > 0) { // 有加载游戏时，不加载火箭
            return false;
        }
        long playingGameId = rocketGameViewModel.getPlayingGameId();
        if (playingGameId <= 0) {
            showLoadingDialog();
        }
        rocketGameViewModel.startRocket();
        return true;
    }

    @Override
    protected boolean switchGame(long gameId) {
        if (gameId > 0) {
            stopRocket();
        }
        return super.switchGame(gameId);
    }

    /** 隐藏火箭 */
    private void stopRocket() {
        rocketEffectOperateContainer.setVisibility(View.GONE);
        rocketGameViewModel.switchGame(this, getGameRoomId(), 0);
        hideLoadingDialog();
    }

    protected void showLoadingDialog() {
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

    @Override
    protected void onGiftDialogShowCustomRocket() {
        super.onGiftDialogShowCustomRocket();
        showRocketGameScene();
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (playingGameId > 0) {
            viewRocketEntrance.setVisibility(View.GONE);
        } else {
            viewRocketEntrance.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void bringToFrontViews() {
        giftContainer.bringToFront();
        rocketContainer.bringToFront();
        rocketOperateContainer.bringToFront();
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
    }

    @Override
    public void onMicList(List<AudioRoomMicModel> list) {
        super.onMicList(list);
        if (rocketFireSelectDialog != null) {
            rocketFireSelectDialog.setMicList(list);
        }
    }

    @Override
    public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
        super.notifyMicItemChange(micIndex, model);
        if (rocketFireSelectDialog != null && binder != null) {
            rocketFireSelectDialog.setMicList(binder.getMicList());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rocketGameViewModel.onDestroy();
        LiveEventBus.<JumpRocketEvent>get(LiveEventBusKey.KEY_JUMP_ROCKET).removeObserver(jumpRocketObserver);
    }
}
