package tech.sud.mgp.hello.ui.scenes.base.activity;

import android.Manifest;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState.AppCustomRocketPlayModelList;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.permission.PermissionFragment;
import tech.sud.mgp.hello.common.utils.permission.SudPermissionUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.req.RocketFireReq;
import tech.sud.mgp.hello.service.game.resp.RocketFireResp;
import tech.sud.mgp.hello.ui.common.dialog.LoadingDialog;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.UserInfoConverter;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppRocketGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.dialog.RocketFireSelectDialog;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftId;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 带火箭动效的房间
 */
public abstract class BaseRocketRoomActivity<T extends AppGameViewModel> extends BaseRoomActivity<T> {

    private View viewRocketEntrance;
    protected FrameLayout rocketContainer;
    protected AppRocketGameViewModel rocketGameViewModel = new AppRocketGameViewModel();

    private boolean isShowRocketScene; // 是否要展示火箭主页面
    private LoadingDialog loadingDialog; // 加载火箭loading弹窗
    private List<AppCustomRocketPlayModelList> fireRocketList = new ArrayList<>(); // 火箭待发射列表

    @Override
    protected void initWidget() {
        super.initWidget();
        viewRocketEntrance = findViewById(R.id.view_custom_rocket);
        rocketContainer = findViewById(R.id.rocket_container);

        if (BuildConfig.DEBUG) {
            File dir = Environment.getExternalStorageDirectory();
            File file = new File(dir, "rocket.rpk");
            SudMGP.getCfg().addEmbeddedMGPkg(GameIdCons.CUSTOM_ROCKET, file.getAbsolutePath());
        }

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
    }

    /** 火箭准备完成了 */
    private void onRocketPrepareCompleted() {
        // 打开火箭主页面
        if (isShowRocketScene) {
            isShowRocketScene = false;
            rocketGameViewModel.notifyAppCustomRocketShowGameScene();
        }
        hideLoadingDialog();
        for (AppCustomRocketPlayModelList model : fireRocketList) {
            rocketGameViewModel.notifyAppCustomRocketPlayModelList(model);
        }
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

    private void showRocketFireSelectDialog(SudMGPMGState.MGCustomRocketFireModel model) {
        if (binder == null) {
            return;
        }
        RocketFireSelectDialog dialog = new RocketFireSelectDialog();
        dialog.setMicList(binder.getMicList());
        dialog.setOnConfirmListener(new RocketFireSelectDialog.OnConfirmListener() {
            @Override
            public void onConfirm(List<AudioRoomMicModel> list) {
                List<UserInfo> userInfoList = UserInfoConverter.conver(list);
                onSendRocket(model, 1, userInfoList);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
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
        List<String> receiverList = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            receiverList.add(userInfo.userID);
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
                onSendGift(giftModel, 1, userInfoList);
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
    }

    /** 打开火箭主页面 */
    protected void showRocketGameScene() {
        isShowRocketScene = true;
        startRocket();
    }

    /** 打开火箭 */
    protected void startRocket() {
        if (playingGameId > 0) { // 有加载游戏时，不加载火箭
            return;
        }
        if (BuildConfig.DEBUG) {
            SudPermissionUtils.requirePermission(this, getSupportFragmentManager(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    new PermissionFragment.OnPermissionListener() {
                        @Override
                        public void onPermission(boolean success) {
                            if (success) {
                                long playingGameId = rocketGameViewModel.getPlayingGameId();
                                if (playingGameId <= 0) {
                                    showLoadingDialog();
                                }
                                rocketGameViewModel.startRocket();
                            }
                        }
                    });
            return;
        }
        long playingGameId = rocketGameViewModel.getPlayingGameId();
        if (playingGameId <= 0) {
            showLoadingDialog();
        }
        rocketGameViewModel.startRocket();
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
        rocketGameViewModel.switchGame(this, getGameRoomId(), 0);
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
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rocketGameViewModel.onDestroy();
    }
}
