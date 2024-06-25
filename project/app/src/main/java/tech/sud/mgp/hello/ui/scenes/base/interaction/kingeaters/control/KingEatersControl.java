package tech.sud.mgp.hello.ui.scenes.base.interaction.kingeaters.control;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.req.CreateOrderReq;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.common.dialog.LoadingDialog;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity.BaseInteractionRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.control.BaseInteractionControl;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.widget.view.InteractionGameContainer;
import tech.sud.mgp.hello.ui.scenes.base.interaction.kingeaters.viewmodel.KingEatersGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameViewParams;

/**
 * 大胃王，处理View以及ViewModel的交互
 */
public class KingEatersControl extends BaseInteractionControl {

    private InteractionGameContainer gameContainer;
    private final KingEatersGameViewModel gameViewModel = new KingEatersGameViewModel();

    private boolean isShowMainScene; // 是否要展示主页面
    private LoadingDialog loadingDialog; // 加载loading弹窗

    public KingEatersControl(BaseInteractionRoomActivity<?> activity) {
        super(activity);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        View includeGameLayout = View.inflate(getContext(), R.layout.include_king_eaters, null);
        FrameLayout interactionContainer = getInteractionContainer();
        interactionContainer.addView(includeGameLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        gameContainer = includeGameLayout.findViewById(R.id.game_container);

        gameViewModel.fragmentActivity = activity;
        gameViewModel.roomId = getRoomId();

        gameViewModel.isShowLoadingGameBg = false;
        gameViewModel.isShowCustomLoading = true;

        // TODO: 2022/11/7 下面的代码需要去掉的
//        gameViewModel.isShowLoadingGameBg = true;
//        gameViewModel.isShowCustomLoading = false;
//        SudPermissionUtils.requirePermission(activity, getSupportFragmentManager(),
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                new PermissionFragment.OnPermissionListener() {
//                    @Override
//                    public void onPermission(boolean success) {
//                        if (success) {
//                        }
//                    }
//                });
//        File dir = Environment.getExternalStorageDirectory();
//        File file = new File(dir.getAbsolutePath() + File.separator + "Pictures", "crazyracing.sp");
//        SudMGP.getCfg().addEmbeddedMGPkg(getControlGameId(), file.getAbsolutePath());

//        SudMGP.getCfg().addEmbeddedMGPkg(getControlGameId(), "crazyracing.sp");
    }

    @Override
    public void setListeners() {
        super.setListeners();
        gameViewModel.gameViewLiveData.observe(activity, new Observer<GameViewParams>() {
            @Override
            public void onChanged(GameViewParams params) {
                View view = params.gameView;
                if (view == null) {
                    gameContainer.removeAllViews();
                } else {
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    gameContainer.bringToFrondClickRectView();
                }
            }
        });
        gameViewModel.gameLoadingProgressLiveData.observe(activity, model -> {
            if (model != null && model.progress == 100) {
                gameContainer.postDelayed(() -> {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }, 100);
            }
        });
        gameViewModel.gamePrepareCompletedLiveData.observe(activity, o -> onGamePrepareCompleted());
        gameViewModel.destroyInteractionGameLiveData.observe(activity, o -> stopInteractionGame());
        gameViewModel.gameClickRectLiveData.observe(activity, model -> {
            List<SudMGPMGState.InteractionClickRect> list = null;
            if (model != null) {
                list = model.list;
            }
            gameContainer.setClickRectList(list);
        });
        gameViewModel.gameGetUserInfoListLiveData.observe(activity, this::onGameGetUserInfoList);
        gameViewModel.onGameGetScoreLiveData.observe(activity, (o) -> {
            onGameGetScore();
        });
        gameViewModel.gameCreateOrderLiveData.observe(activity, this::onGameCreateOrder);
    }

    /** 游戏回调，创建订单 */
    private void onGameCreateOrder(SudMGPMGState.MGCommonGameCreateOrder model) {
        if (model == null) {
            return;
        }
        try {
            CreateOrderReq req = new CreateOrderReq();
            req.setCreateOrderValues(model);
            req.gameId = gameViewModel.getPlayingGameId();
            req.roomId = gameViewModel.getGameRoomId();
            GameRepository.createOrder(activity, req, new RxCallback<Object>() {
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
                    SudMGPAPPState.APPCommonGameCreateOrderResult model = new SudMGPAPPState.APPCommonGameCreateOrderResult();
                    model.result = isSuccess ? 1 : 0;
                    gameViewModel.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_CREATE_ORDER_RESULT, model);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onGameGetScore() {
        HomeRepository.getAccount(activity, new RxCallback<GetAccountResp>() {
            @Override
            public void onSuccess(GetAccountResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    gameViewModel.sudFSTAPPDecorator.notifyAPPCommonGameScore(resp.coin);
                }
            }
        });
    }

    private void onGameGetUserInfoList(SudMGPMGState.MGCommonUsersInfo model) {
        if (model == null || model.uids == null) {
            return;
        }
        List<Long> userIdList = new ArrayList<>();
        for (String uid : model.uids) {
            try {
                userIdList.add(Long.parseLong(uid));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (userIdList.size() == 0) {
            return;
        }
        UserInfoRepository.getUserInfoList(activity, userIdList, new UserInfoRepository.UserInfoResultListener() {
            @Override
            public void userInfoList(List<UserInfoResp> userInfos) {
                if (userInfos == null || userInfos.size() == 0) {
                    return;
                }
                SudMGPAPPState.APPCommonUsersInfo backModel = new SudMGPAPPState.APPCommonUsersInfo();
                backModel.infos = new ArrayList<>();
                for (UserInfoResp userInfo : userInfos) {
                    if (userInfo != null) {
                        SudMGPAPPState.APPCommonUsersInfo.UserInfoModel backUserInfoModel = new SudMGPAPPState.APPCommonUsersInfo.UserInfoModel();
                        backUserInfoModel.uid = userInfo.userId + "";
                        backUserInfoModel.avatar = userInfo.getUseAvatar();
                        backUserInfoModel.name = userInfo.nickname;
                        backModel.infos.add(backUserInfoModel);
                    }
                }

                gameViewModel.notifyAppCommonUsersInfo(backModel);
            }
        });
    }

    private void onGamePrepareCompleted() {
        // 打开游戏主页面
        if (isShowMainScene) {
            isShowMainScene = false;
            gameViewModel.notifyAppCommonShowGameScene(new SudMGPAPPState.APPCommonShowGameScene());
        }
        hideLoadingDialog();
    }

    /** 打开主页面 */
    private void showGameScene() {
        boolean success = startInteractionGame();
        if (success) {
            isShowMainScene = true;
            gameViewModel.notifyAppCommonShowGameScene(new SudMGPAPPState.APPCommonShowGameScene());
        }
    }

    /** 打开交互游戏 */
    private boolean startInteractionGame() {
        if (getActivityPlayingGameId() > 0 && getActivityPlayingGameId() != getControlGameId()) { // 有加载其他游戏时，不加载此交互游戏
            return false;
        }
        long playingGameId = gameViewModel.getPlayingGameId();
        if (playingGameId <= 0) {
            showLoadingDialog();
        }
        gameViewModel.switchGame(activity, getRoomId() + "", getControlGameId());
        return true;
    }

    /** 隐藏交互游戏 */
    public void stopInteractionGame() {
        gameViewModel.switchGame(activity, getRoomId() + "", 0);
        hideLoadingDialog();
    }

    private void showLoadingDialog() {
        if (loadingDialog != null) {
            return;
        }
        loadingDialog = new LoadingDialog(getString(R.string.go_king_eaters));
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
    public void switchGame(long gameId) {
        super.switchGame(gameId);
        if (gameId > 0) {
            stopInteractionGame();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gameViewModel.onDestroy();
    }

    @Override
    public void onClickEntrance() {
        super.onClickEntrance();
        showGameScene();
    }

    @Override
    public long getControlGameId() {
        return GameIdCons.KING_EATERS;
    }

    @Override
    public long getPlayingGameId() {
        return gameViewModel.getPlayingGameId();
    }

    @Override
    public void onResume() {
        super.onResume();
        gameViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gameViewModel.onPause();
    }

}
