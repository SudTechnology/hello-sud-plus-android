package tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity;

import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.control.BaseInteractionControl;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.model.InteractionGameModel;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.widget.view.InteractionBannerView;
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

    @Override
    protected void initWidget() {
        super.initWidget();
        interactionBannerView = findViewById(R.id.interaction_banner_view);
        interactionContainer = findViewById(R.id.interaction_container);

        initControlList();
        for (BaseInteractionControl control : controlList) {
            control.initWidget();
        }
    }

    private void initControlList() {
        controlList.add(new RocketControl(this));
    }

    @Override
    protected void initData() {
        super.initData();
        List<InteractionGameModel> list = new ArrayList<>();
        list.add(new InteractionGameModel(GameIdCons.CUSTOM_ROCKET, R.drawable.ic_rocket_entrance));
        interactionBannerView.setDatas(list);

        for (BaseInteractionControl control : controlList) {
            control.initData();
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        interactionBannerView.setOnPagerClickListener(model -> {
            for (BaseInteractionControl control : controlList) {
                if (control.getControlGameId() == model.gameId) {
                    control.onClickEntrance();
                }
            }
        });

        for (BaseInteractionControl control : controlList) {
            control.setListeners();
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
    protected void showGift(GiftModel giftModel) {
        super.showGift(giftModel);
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
        if (playingGameId > 0) {
            interactionBannerView.setVisibility(View.GONE);
        } else {
            interactionBannerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void bringToFrontViews() {
        giftContainer.bringToFront();
        interactionContainer.bringToFront();
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
}