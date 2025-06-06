package tech.sud.mgp.hello.ui.scenes.audio3d.viewmodel;

import androidx.lifecycle.MutableLiveData;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.gip.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

public class Audio3DGameViewModel extends AppGameViewModel {

    public MutableLiveData<SudGIPMGState.MGCustomCrRoomInitData> mOnInitDataLiveData = new MutableLiveData<>();
    public MutableLiveData<SudGIPMGState.MGCustomCrClickSeat> mOnClickSeatLiveData = new MutableLiveData<>();

    // region 发送给游戏的状态

    /**
     * 1. 设置房间配置
     * 收到游戏发过来的mg_custom_cr_room_init_data状态后
     * App把房间配置以及主播位数据发送给游戏
     */
    public void notifyAppCustomCrSetRoomConfig(SudGIPAPPState.AppCustomCrSetRoomConfig model) {
        notifyStateChange(SudGIPAPPState.APP_CUSTOM_CR_SET_ROOM_CONFIG, model);
    }

    /**
     * 2. 设置主播位数据
     * 收到游戏发过来的mg_custom_cr_room_init_data状态后
     * App把房间配置以及主播位数据发送给游戏
     * <p>
     * 初始化时，需要发送5个主播位的全量数据
     * 后续如果某个主播位有变化，可只传一个或多个主播位的数据(需要该主播位的全量数据)
     */
    public void notifyAppCustomCrSetSeats(SudGIPAPPState.AppCustomCrSetSeats model) {
        notifyStateChange(SudGIPAPPState.APP_CUSTOM_CR_SET_SEATS, model);
    }

    /**
     * 3. 播放收礼效果
     */
    public void notifyAppCustomCrPlayGiftEffect(SudGIPAPPState.AppCustomCrPlayGiftEffect model) {
        notifyStateChange(SudGIPAPPState.APP_CUSTOM_CR_PLAY_GIFT_EFFECT, model);
    }

    /**
     * 4. 通知播放爆灯特效
     */
    public void notifyAppCustomCrSetLightFlash(SudGIPAPPState.AppCustomCrSetLightFlash model) {
        notifyStateChange(SudGIPAPPState.APP_CUSTOM_CR_SET_LIGHT_FLASH, model);
    }

    /**
     * 5. 通知主播播放指定动作
     */
    public void notifyAppCustomCrPlayAnim(SudGIPAPPState.AppCustomCrPlayAnim model) {
        notifyStateChange(SudGIPAPPState.APP_CUSTOM_CR_PLAY_ANIM, model);
    }

    /**
     * 6. 通知麦浪值变化
     */
    public void notifyAppCustomCrMicphoneValueSeat(SudGIPAPPState.AppCustomCrMicphoneValueSeat model) {
        notifyStateChange(SudGIPAPPState.APP_CUSTOM_CR_MICPHONE_VALUE_SEAT, model);
    }

    /**
     * 7. 通知暂停或恢复立方体自转
     */
    public void notifyAppCustomCrPauseRotate(SudGIPAPPState.AppCustomCrPauseRotate model) {
        notifyStateChange(SudGIPAPPState.APP_CUSTOM_CR_PAUSE_ROTATE, model);
    }

    // endregion 发送给游戏的状态

    // region 游戏发来的状态

    /**
     * 1. 请求房间数据
     * mg_custom_cr_room_init_data
     */
    @Override
    public void onGameMGCustomCrRoomInitData(ISudFSMStateHandle handle, SudGIPMGState.MGCustomCrRoomInitData model) {
        super.onGameMGCustomCrRoomInitData(handle, model);
        mOnInitDataLiveData.setValue(model);
    }

    /**
     * 2. 点击主播位或老板位通知
     * mg_custom_cr_click_seat
     */
    @Override
    public void onGameMGCustomCrClickSeat(ISudFSMStateHandle handle, SudGIPMGState.MGCustomCrClickSeat model) {
        super.onGameMGCustomCrClickSeat(handle, model);
        mOnClickSeatLiveData.setValue(model);
    }
    // endregion 游戏发来的状态

}
