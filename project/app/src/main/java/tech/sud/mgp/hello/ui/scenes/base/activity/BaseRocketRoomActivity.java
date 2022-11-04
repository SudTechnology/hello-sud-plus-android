package tech.sud.mgp.hello.ui.scenes.base.activity;

import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
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

    @Override
    protected void initWidget() {
        super.initWidget();
        viewRocketEntrance = findViewById(R.id.view_custom_rocket);
        rocketContainer = findViewById(R.id.rocket_container);

        SudMGP.getCfg().setShowLoadingGameBg(false);
        SudMGP.getCfg().setShowCustomLoading(true);

        if (BuildConfig.DEBUG) {
            SudMGP.getCfg().addEmbeddedMGPkg(GameIdCons.CUSTOM_ROCKET, "assets.rpk");
        }

        rocketGameViewModel.fragmentActivity = this;
        rocketGameViewModel.roomId = roomInfoModel.roomId;
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
                GiftModel giftModel = GiftHelper.getInstance().getGift(GiftId.ROCKET);
                onSendGift(giftModel, 1, userInfoList);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    /** 打开火箭主页面 */
    protected void showRocketGameScene() {
        rocketGameViewModel.showRocketGameScene();
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
