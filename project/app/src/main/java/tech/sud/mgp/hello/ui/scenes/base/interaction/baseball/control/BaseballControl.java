package tech.sud.mgp.hello.ui.scenes.base.interaction.baseball.control;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState.APPCommonGameCreateOrderResult;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.req.BaseballPlayReq;
import tech.sud.mgp.hello.ui.common.dialog.LoadingDialog;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity.BaseInteractionRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.control.BaseInteractionControl;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.widget.view.InteractionGameContainer;
import tech.sud.mgp.hello.ui.scenes.base.interaction.baseball.viewmodel.BaseballGameViewModel;

/**
 * 棒球控制器，处理View以及ViewModel的交互
 */
public class BaseballControl extends BaseInteractionControl {

    private InteractionGameContainer gameContainer;
    private final BaseballGameViewModel baseballGameViewModel = new BaseballGameViewModel();

    private boolean isShowBaseballScene; // 是否要展示棒球主页面
    private LoadingDialog loadingDialog; // 加载loading弹窗

    public BaseballControl(BaseInteractionRoomActivity<?> activity) {
        super(activity);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        View includeBaseball = View.inflate(getContext(), R.layout.include_baseball, null);
        FrameLayout interactionContainer = getInteractionContainer();
        interactionContainer.addView(includeBaseball, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        gameContainer = includeBaseball.findViewById(R.id.baseball_container);

        baseballGameViewModel.fragmentActivity = activity;
        baseballGameViewModel.roomId = getRoomId();

        baseballGameViewModel.isShowLoadingGameBg = false;
        baseballGameViewModel.isShowCustomLoading = true;

        // TODO: 2022/11/7 下面的代码需要去掉的
//        baseballGameViewModel.isShowLoadingGameBg = true;
//        baseballGameViewModel.isShowCustomLoading = false;
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
//        File file = new File(dir.getAbsolutePath() + File.separator + "Pictures", "baseball.rpk");
//        SudMGP.getCfg().addEmbeddedMGPkg(GameIdCons.BASEBALL, file.getAbsolutePath());

//        SudMGP.getCfg().addEmbeddedMGPkg(GameIdCons.BASEBALL, "baseball.rpk");
    }

    @Override
    public void setListeners() {
        super.setListeners();
        baseballGameViewModel.gameViewLiveData.observe(activity, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) {
                    gameContainer.removeAllViews();
                } else {
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    gameContainer.bringToFrondClickRectView();
                }
            }
        });
        baseballGameViewModel.gameLoadingProgressLiveData.observe(activity, model -> {
            if (model != null && model.progress == 100) {
                gameContainer.postDelayed(() -> {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }, 100);
            }
        });
        baseballGameViewModel.baseballPrepareCompletedLiveData.observe(activity, o -> onBaseballPrepareCompleted());
        baseballGameViewModel.destroyBaseballLiveData.observe(activity, o -> stopInteractionGame());
        baseballGameViewModel.baseballClickRectLiveData.observe(activity, model -> {
            List<SudMGPMGState.InteractionClickRect> list = null;
            if (model != null) {
                list = model.list;
            }
            gameContainer.setClickRectList(list);
        });
        baseballGameViewModel.gameCreateOrderLiveData.observe(activity, this::onGameCreateOrder);
    }

    private void onGameCreateOrder(SudMGPMGState.MGCommonGameCreateOrder model) {
        if (model == null) {
            return;
        }
        long coin = 5 * model.value;
        String title = activity.getString(R.string.baseball_consume_title, coin + "", model.value + "");
        SimpleChooseDialog dialog = new SimpleChooseDialog(activity, title, activity.getString(R.string.cancel), activity.getString(R.string.affirm));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    sendBaseBallPlay(model);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void sendBaseBallPlay(SudMGPMGState.MGCommonGameCreateOrder model) {
        BaseballPlayReq req = new BaseballPlayReq();
        req.cmd = model.cmd;
        req.number = model.value;
        req.roomId = getRoomId();
        GameRepository.baseballPlay(activity, req, new RxCallback<Object>() {
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
                baseballGameViewModel.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_CREATE_ORDER_RESULT, model);
            }

        });
    }

    private void onBaseballPrepareCompleted() {
        // 打开棒球主页面
        if (isShowBaseballScene) {
            isShowBaseballScene = false;
            baseballGameViewModel.notifyAppBaseballShowGameScene(new SudMGPAPPState.AppBaseballShowGameScene());
        }
        hideLoadingDialog();
    }

    /** 打开棒球主页面 */
    private void showBaseballScene() {
        boolean success = startBaseball();
        if (success) {
            isShowBaseballScene = true;
            baseballGameViewModel.notifyAppBaseballShowGameScene(new SudMGPAPPState.AppBaseballShowGameScene());
        }
    }

    /** 打开棒球 */
    private boolean startBaseball() {
        if (getActivityPlayingGameId() > 0 && getActivityPlayingGameId() != getControlGameId()) { // 有加载其他游戏时，不加载棒球
            return false;
        }
        long playingGameId = baseballGameViewModel.getPlayingGameId();
        if (playingGameId <= 0) {
            showLoadingDialog();
        }
        baseballGameViewModel.switchGame(activity, getRoomId() + "", GameIdCons.BASEBALL);
        return true;
    }

    /** 隐藏棒球 */
    public void stopInteractionGame() {
        baseballGameViewModel.switchGame(activity, getRoomId() + "", 0);
        hideLoadingDialog();
    }

    private void showLoadingDialog() {
        if (loadingDialog != null) {
            return;
        }
        loadingDialog = new LoadingDialog(getString(R.string.go_baseball_stage));
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
        baseballGameViewModel.onDestroy();
    }

    @Override
    public void onClickEntrance() {
        super.onClickEntrance();
        showBaseballScene();
    }

    @Override
    public long getControlGameId() {
        return GameIdCons.BASEBALL;
    }

}
