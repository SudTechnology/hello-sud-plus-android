package tech.sud.mgp.hello.ui.scenes.crossapp.activity;

import android.text.TextUtils;

import tech.sud.gip.core.SudLoadMGMode;
import tech.sud.mgp.hello.service.room.resp.AuthRoomInfo;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 跨域 授权 房间页面
 */
public class CrossAppAuthActivity extends AbsAudioRoomActivity<AppGameViewModel> {

    @Override
    protected AppGameViewModel initGameViewModel() {
        return new AppGameViewModel();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView.setSelectGameVisible(false);
    }

    @Override
    protected int getLoadMGMode() {
        AuthRoomInfo authRoomInfo = roomInfoModel.authRoomInfo;
        if (authRoomInfo != null && !TextUtils.isEmpty(authRoomInfo.roomId) && !TextUtils.isEmpty(authRoomInfo.authSecret)) {
            return SudLoadMGMode.kSudLoadMGModeAppCrossAuth;
        }
        return super.getLoadMGMode();
    }

    @Override
    protected String getAuthorizationSecret() {
        AuthRoomInfo authRoomInfo = roomInfoModel.authRoomInfo;
        if (authRoomInfo != null) {
            return authRoomInfo.authSecret;
        }
        return super.getAuthorizationSecret();
    }

    @Override
    public String getGameRoomId() {
        AuthRoomInfo authRoomInfo = roomInfoModel.authRoomInfo;
        if (authRoomInfo != null) {
            return authRoomInfo.roomId;
        }
        return super.getGameRoomId();
    }

}
