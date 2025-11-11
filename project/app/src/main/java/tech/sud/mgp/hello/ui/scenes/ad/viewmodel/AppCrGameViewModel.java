package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;
import tech.sud.mgp.hello.service.main.config.SudConfig;

public class AppCrGameViewModel extends BaseCrGameViewModel {

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>();

    @Override
    protected void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener) {
        LifecycleOwner owner;
        if (activity instanceof LifecycleOwner) {
            owner = (LifecycleOwner) activity;
        } else {
            owner = null;
        }
        // 请求登录code
        GameRepository.login(owner, getAppId(), new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null && !TextUtils.isEmpty(t.getData().runtimeCode)) {
                    listener.onSuccess(t.getData().runtimeCode);
                } else {
                    listener.onFailed(-1, "error:" + t.getRetCode());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                listener.onFailed(-1, "error:" + e);
            }
        });
    }

    @Override
    protected String getUserId() {
        return HSUserInfo.userId + "";
    }

    @Override
    protected String getAppId() {
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
        if (sudConfig != null) {
            return sudConfig.appId;
        }
        return "";
    }

    @Override
    protected String getAppKey() {
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
        if (sudConfig != null) {
            return sudConfig.appKey;
        }
        return "";
    }

    @Override
    protected void onAddGameView(View gameView) {
        gameViewLiveData.setValue(gameView);
    }

    @Override
    protected void onRemoveGameView() {
        gameViewLiveData.setValue(null);
    }

}
