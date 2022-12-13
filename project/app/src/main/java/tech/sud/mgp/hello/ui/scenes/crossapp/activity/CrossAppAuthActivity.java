package tech.sud.mgp.hello.ui.scenes.crossapp.activity;

import android.text.TextUtils;

import tech.sud.mgp.core.SudLoadMGMode;
import tech.sud.mgp.hello.service.room.resp.AuthRoomInfo;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 跨域 授权 房间页面
 */
public class CrossAppAuthActivity extends BaseRoomActivity<AppGameViewModel> {

    @Override
    protected AppGameViewModel initGameViewModel() {
        return new AppGameViewModel();
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

}
