package tech.sud.mgp.hello.ui.scenes.base.interaction.baseball.control;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
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
        baseballGameViewModel.roomId = activity.getRoomInfoModel().roomId;

        baseballGameViewModel.isShowLoadingGameBg = false;
        baseballGameViewModel.isShowCustomLoading = true;
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
        baseballGameViewModel.destroyBaseballLiveData.observe(activity, o -> stopBaseball());
        baseballGameViewModel.baseballClickRectLiveData.observe(activity, model -> {
            List<SudMGPMGState.InteractionClickRect> list = null;
            if (model != null) {
                list = model.list;
            }
            gameContainer.setClickRectList(list);
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
        if (getPlayingGameId() > 0) { // 有加载其他游戏时，不加载棒球
            return false;
        }
        long playingGameId = baseballGameViewModel.getPlayingGameId();
        if (playingGameId <= 0) {
            showLoadingDialog();
        }
        baseballGameViewModel.startBaseball();
        return true;
    }

    /** 隐藏棒球 */
    private void stopBaseball() {
        baseballGameViewModel.switchGame(activity, getGameRoomId(), 0);
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
            stopBaseball();
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
