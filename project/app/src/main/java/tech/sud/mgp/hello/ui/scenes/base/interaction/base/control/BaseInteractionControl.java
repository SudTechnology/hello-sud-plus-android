package tech.sud.mgp.hello.ui.scenes.base.interaction.base.control;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity.BaseInteractionRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomService;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

/**
 * 互动游戏 控制器基类
 */
public abstract class BaseInteractionControl implements IBaseInteractionControl {

    protected BaseInteractionRoomActivity<?> activity;

    public BaseInteractionControl(BaseInteractionRoomActivity<?> activity) {
        this.activity = activity;
    }

    public void initWidget() {
    }

    public void initData() {
    }

    public void setListeners() {
    }

    public final String getString(@StringRes int resId) {
        return activity.getResources().getString(resId);
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return activity.findViewById(id);
    }

    public SceneRoomService.MyBinder getBinder() {
        return activity.getBinder();
    }

    public FragmentManager getSupportFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    public long getActivityPlayingGameId() {
        return activity.getPlayingGameId();
    }

    protected long getRoomId() {
        return activity.getRoomId();
    }

    /** 是否要拦截发送礼物 */
    public boolean onInterceptSendGift(GiftModel giftModel, int giftCount, List<UserInfo> toUsers) {
        return false;
    }

    /** 显示礼物 */
    public void showGift(GiftModel giftModel) {
    }

    public void switchGame(long gameId) {
    }

    public void onGiftDialogShowCustomRocket() {
    }

    public void onDestroy() {
    }

    public void notifyMicItemChange(int micIndex, AudioRoomMicModel model) {
    }

    public void onMicList(List<AudioRoomMicModel> list) {
    }

    /** 点击了入口 */
    public void onClickEntrance() {
    }

    /** 关闭互动游戏 */
    public void stopInteractionGame() {
    }

    /** 获取当前控制的游戏Id */
    public abstract long getControlGameId();

    public FrameLayout getInteractionContainer() {
        return activity.getInteractionContainer();
    }

    public Context getContext() {
        return activity;
    }

    public void onResume() {
    }

    public void onPause() {
    }

}
