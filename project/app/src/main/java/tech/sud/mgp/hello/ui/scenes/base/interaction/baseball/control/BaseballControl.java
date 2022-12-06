package tech.sud.mgp.hello.ui.scenes.base.interaction.baseball.control;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

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
    private final BaseballGameViewModel gameViewModel = new BaseballGameViewModel();

    private LoadingDialog loadingDialog; // 加载loading弹窗

    public BaseballControl(BaseInteractionRoomActivity<?> activity) {
        super(activity);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        View includeRocket = View.inflate(getContext(), R.layout.include_baseball, null);
        FrameLayout interactionContainer = getInteractionContainer();
        interactionContainer.addView(includeRocket, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        gameContainer = includeRocket.findViewById(R.id.baseball_container);

        gameViewModel.fragmentActivity = activity;
        gameViewModel.roomId = activity.getRoomInfoModel().roomId;

        gameViewModel.isShowLoadingGameBg = false;
        gameViewModel.isShowCustomLoading = true;
    }

    @Override
    public void setListeners() {
        super.setListeners();
        gameViewModel.gameViewLiveData.observe(activity, new Observer<View>() {
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
        gameViewModel.gameLoadingProgressLiveData.observe(activity, model -> {
            if (model != null && model.progress == 100) {
                gameContainer.postDelayed(() -> {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }, 100);
            }
        });
    }

    /** 打开棒球主页面 */
    private void showBaseballScene() {
        boolean success = startBaseball();
        if (success) {
//            isShowGameScene = true;
//            gameViewModel.notifyAppCustomRocketShowGameScene();
        }
    }

    /** 打开棒球 */
    private boolean startBaseball() {
        if (getPlayingGameId() > 0) { // 有加载其他游戏时，不加载棒球
            return false;
        }
        long playingGameId = gameViewModel.getPlayingGameId();
        if (playingGameId <= 0) {
            showLoadingDialog();
        }
        gameViewModel.startBaseball();
        return true;
    }

    /** 隐藏棒球 */
    private void stopRocket() {
        gameViewModel.switchGame(activity, getGameRoomId(), 0);
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

    @Override
    public void switchGame(long gameId) {
        super.switchGame(gameId);
        if (gameId > 0) {
            stopRocket();
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
        showBaseballScene();
    }

    @Override
    public long getControlGameId() {
        return GameIdCons.BASEBALL;
    }

}
