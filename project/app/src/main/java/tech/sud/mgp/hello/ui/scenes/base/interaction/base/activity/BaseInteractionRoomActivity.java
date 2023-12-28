package tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity;

import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.FlavorChannel;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.control.BaseInteractionControl;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.model.InteractionGameModel;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.widget.view.InteractionBannerView;
import tech.sud.mgp.hello.ui.scenes.base.interaction.baseball.control.BaseballControl;
import tech.sud.mgp.hello.ui.scenes.base.interaction.kingeaters.control.KingEatersControl;
import tech.sud.mgp.hello.ui.scenes.base.interaction.racecar.control.RacecarControl;
import tech.sud.mgp.hello.ui.scenes.base.interaction.rocket.control.RocketControl;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 互动游戏 房间
 */
public abstract class BaseInteractionRoomActivity<T extends AppGameViewModel> extends BaseRoomActivity<T> {

    protected InteractionBannerView interactionBannerView;
    private final List<BaseInteractionControl> controlList = new ArrayList<>();
    private FrameLayout interactionContainer;
    protected FrameLayout webGameContainer;

    @Override
    protected void initWidget() {
        super.initWidget();
        interactionBannerView = findViewById(R.id.interaction_banner_view);
        interactionContainer = findViewById(R.id.interaction_container);
        webGameContainer = findViewById(R.id.web_game_container);

        initInteractionGame();
        for (BaseInteractionControl control : controlList) {
            control.initWidget();
        }
    }

    private void initInteractionGame() {
        if (FlavorChannel.OVERSEA.equals(BuildConfig.CHANNEL)) {
            // 控制器
            controlList.add(new RocketControl(this));
            controlList.add(new BaseballControl(this));
            controlList.add(new RacecarControl(this));
            controlList.add(new KingEatersControl(this));

            // banner
            List<InteractionGameModel> bannerList = new ArrayList<>();
            bannerList.add(new InteractionGameModel(GameIdCons.CUSTOM_ROCKET, R.drawable.ic_rocket_entrance));
            bannerList.add(new InteractionGameModel(GameIdCons.BASEBALL, R.drawable.ic_baseball_entrance));
            bannerList.add(new InteractionGameModel(GameIdCons.CRAZY_RACECAR, R.drawable.ic_crazy_racecar));
            bannerList.add(new InteractionGameModel(GameIdCons.KING_EATERS, R.drawable.ic_king_eaters));

            interactionBannerView.setDatas(bannerList);
        } else {
            // 控制器
            controlList.add(new RocketControl(this));
            controlList.add(new BaseballControl(this));
//            controlList.add(new RacecarControl(this));
//            controlList.add(new KingEatersControl(this));

            // banner
            List<InteractionGameModel> bannerList = new ArrayList<>();
            bannerList.add(new InteractionGameModel(GameIdCons.CUSTOM_ROCKET, R.drawable.ic_rocket_entrance));
            bannerList.add(new InteractionGameModel(GameIdCons.BASEBALL, R.drawable.ic_baseball_entrance));
//            bannerList.add(new InteractionGameModel(GameIdCons.CRAZY_RACECAR, R.drawable.ic_crazy_racecar));
//            bannerList.add(new InteractionGameModel(GameIdCons.KING_EATERS, R.drawable.ic_king_eaters));

            interactionBannerView.setDatas(bannerList);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        for (BaseInteractionControl control : controlList) {
            control.initData();
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        interactionBannerView.setOnPagerClickListener(model -> {
            switchInteractionGame(model.gameId);
        });

        for (BaseInteractionControl control : controlList) {
            control.setListeners();
        }
    }

    protected void switchInteractionGame(long gameId) {
        for (BaseInteractionControl control : controlList) {
            if (control.getControlGameId() == gameId) {
                control.onClickEntrance();
            } else {
                control.stopInteractionGame();
            }
        }
    }

    @Override
    protected void onSendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUsers) {
        boolean intercept = false;
        for (BaseInteractionControl control : controlList) {
            if (control.onInterceptSendGift(giftModel, giftCount, toUsers)) {
                intercept = true;
            }
        }
        if (intercept) {
            return;
        }
        super.onSendGift(giftModel, giftCount, toUsers);
    }

    /** 显示礼物 */
    @Override
    protected void showGift(GiftModel giftModel, int giftCount, UserInfo fromUser, List<UserInfo> toUserList, boolean isAllSeat) {
        super.showGift(giftModel, giftCount, fromUser, toUserList, isAllSeat);
        if (!canShowGift()) {
            return;
        }
        for (BaseInteractionControl control : controlList) {
            control.showGift(giftModel);
        }
    }

    @Override
    protected boolean switchGame(long gameId) {
        for (BaseInteractionControl control : controlList) {
            control.switchGame(gameId);
        }
        return super.switchGame(gameId);
    }

    @Override
    protected void onGiftDialogShowCustomRocket() {
        super.onGiftDialogShowCustomRocket();
        for (BaseInteractionControl control : controlList) {
            control.onGiftDialogShowCustomRocket();
        }
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (playingGameId > 0 || !roomConfig.isShowInteractionGame) {
            interactionBannerView.setVisibility(View.GONE);
        } else {
            interactionBannerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void bringToFrontViews() {
        giftContainer.bringToFront();
        interactionContainer.bringToFront();
        webGameContainer.bringToFront();
        inputMsgView.bringToFront();
        clOpenMic.bringToFront();
    }

    @Override
    public void onMicList(List<AudioRoomMicModel> list) {
        super.onMicList(list);
        for (BaseInteractionControl control : controlList) {
            control.onMicList(list);
        }
    }

    @Override
    public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
        super.notifyMicItemChange(micIndex, model);
        for (BaseInteractionControl control : controlList) {
            control.notifyMicItemChange(micIndex, model);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (BaseInteractionControl control : controlList) {
            control.onDestroy();
        }
    }

    public FrameLayout getInteractionContainer() {
        return interactionContainer;
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (BaseInteractionControl control : controlList) {
            control.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (BaseInteractionControl control : controlList) {
            control.onPause();
        }
    }

}
