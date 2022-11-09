package tech.sud.mgp.hello.ui.scenes.base.viewmodel;

import android.text.TextUtils;
import android.util.Base64;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.GlobalSP;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.req.RocketFireRecordReq;
import tech.sud.mgp.hello.service.game.req.RocketFireRecordSummeryReq;
import tech.sud.mgp.hello.service.game.req.RocketSetDefaultSeatReq;
import tech.sud.mgp.hello.service.game.resp.RocketFirePriceResp;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.UserInfoListResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.common.utils.FilePath;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;

/**
 * 带火箭动效房间的游戏逻辑
 */
public class AppRocketGameViewModel extends AppGameViewModel {

    public FragmentActivity fragmentActivity;
    public long roomId;
    private boolean rocketIsReady;

    public MutableLiveData<SudMGPMGState.MGCustomRocketFireModel> gameFireRocketLiveData = new MutableLiveData<>(); // 发射火箭
    public MutableLiveData<SudMGPMGState.MGCustomRocketClickLockComponent> clickLockComponentLiveData = new MutableLiveData<>(); // 点击了锁住的组件
    public MutableLiveData<Object> rocketPrepareCompletedLiveData = new MutableLiveData<>(); // 火箭准备完成
    public MutableLiveData<SudMGPMGState.MGCustomRocketSetClickRect> rocketClickRectLiveData = new MutableLiveData<>(); // 火箭点击区域

    // region 火箭返回状态

    /**
     * 1. 礼物配置文件(火箭)
     * mg_custom_rocket_config
     */
    @Override
    public void onGameMGCustomRocketConfig(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketConfig model) {
        super.onGameMGCustomRocketConfig(handle, model);
        GameRepository.rocketMallComponentList(fragmentActivity, new RxCallback<SudMGPAPPState.AppCustomRocketConfig>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppCustomRocketConfig appCustomRocketConfig) {
                super.onSuccess(appCustomRocketConfig);
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_CONFIG, appCustomRocketConfig);
            }
        });
    }

    /**
     * 2. 拥有模型列表(火箭)
     * mg_custom_rocket_model_list
     */
    @Override
    public void onGameMGCustomRocketModelList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketModelList model) {
        super.onGameMGCustomRocketModelList(handle, model);
        GameRepository.rocketModelList(fragmentActivity, new RxCallback<SudMGPAPPState.AppCustomRocketModelList>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppCustomRocketModelList appCustomRocketModelList) {
                super.onSuccess(appCustomRocketModelList);
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_MODEL_LIST, appCustomRocketModelList);
            }
        });
    }

    /**
     * 3. 拥有组件列表(火箭)
     * mg_custom_rocket_component_list
     */
    @Override
    public void onGameMGCustomRocketComponentList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketComponentList model) {
        super.onGameMGCustomRocketComponentList(handle, model);
        GameRepository.rocketComponentList(fragmentActivity, new RxCallback<SudMGPAPPState.AppCustomRocketComponentList>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppCustomRocketComponentList appCustomRocketComponentList) {
                super.onSuccess(appCustomRocketComponentList);
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_COMPONENT_LIST, appCustomRocketComponentList);
            }
        });
    }

    /**
     * 4. 获取用户信息(火箭)
     * mg_custom_rocket_user_info
     */
    @Override
    public void onGameMGCustomRocketUserInfo(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketUserInfo model) {
        super.onGameMGCustomRocketUserInfo(handle, model);
        List<Long> userIdList = new ArrayList<>();
        try {
            if (model != null && model.userIdList != null) {
                for (String s : model.userIdList) {
                    userIdList.add(Long.parseLong(s));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            callbackGetUserInfoError(e);
            return;
        }
        HomeRepository.getUserInfoList(fragmentActivity, userIdList, new RxCallback<UserInfoListResp>() {
            @Override
            public void onNext(BaseResponse<UserInfoListResp> t) {
                super.onNext(t);
                SudMGPAPPState.AppCustomRocketUserInfo appCustomRocketUserInfo = new SudMGPAPPState.AppCustomRocketUserInfo();
                appCustomRocketUserInfo.resultCode = t.getRetCode();
                appCustomRocketUserInfo.error = t.getRetMsg();
                if (t.getData() != null) {
                    appCustomRocketUserInfo.userList = conver(t.getData().userInfoList);
                }
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_USER_INFO, appCustomRocketUserInfo);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                callbackGetUserInfoError(e);
            }
        });
    }

    /**
     * 5. 订单记录列表(火箭)
     * mg_custom_rocket_order_record_list
     */
    @Override
    public void onGameMGCustomRocketOrderRecordList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketOrderRecordList model) {
        super.onGameMGCustomRocketOrderRecordList(handle, model);
        if (model == null) {
            return;
        }
        GameRepository.rocketBuyComponentRecord(fragmentActivity, model.pageIndex, model.pageSize, new RxCallback<SudMGPAPPState.AppCustomRocketOrderRecordList>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppCustomRocketOrderRecordList appCustomRocketOrderRecordList) {
                super.onSuccess(appCustomRocketOrderRecordList);
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_ORDER_RECORD_LIST, appCustomRocketOrderRecordList);
            }
        });
    }

    /**
     * 6. 展馆内列表(火箭)
     * mg_custom_rocket_room_record_list
     */
    @Override
    public void onGameMGCustomRocketRoomRecordList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketRoomRecordList model) {
        super.onGameMGCustomRocketRoomRecordList(handle, model);
        if (model == null) {
            return;
        }
        RocketFireRecordSummeryReq req = new RocketFireRecordSummeryReq();
        req.pageIndex = model.pageIndex;
        req.pageSize = model.pageSize;
        req.roomId = roomId;
        GameRepository.rocketFireRecordSummery(fragmentActivity, req, new RxCallback<SudMGPAPPState.AppCustomRocketRoomRecordList>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppCustomRocketRoomRecordList appCustomRocketOrderRecordList) {
                super.onSuccess(appCustomRocketOrderRecordList);
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_ROOM_RECORD_LIST, appCustomRocketOrderRecordList);
            }
        });
    }

    /**
     * 7. 展馆内玩家送出记录(火箭)
     * mg_custom_rocket_user_record_list
     */
    @Override
    public void onGameMGCustomRocketUserRecordList(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketUserRecordList model) {
        super.onGameMGCustomRocketUserRecordList(handle, model);
        if (model == null) {
            return;
        }
        RocketFireRecordReq req = new RocketFireRecordReq();
        req.pageIndex = model.pageIndex;
        req.pageSize = model.pageSize;
        req.userId = model.userId;
        req.roomId = roomId;
        GameRepository.rocketFireRecord(fragmentActivity, req, new RxCallback<SudMGPAPPState.AppCustomRocketUserRecordList>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppCustomRocketUserRecordList appCustomRocketUserRecordList) {
                super.onSuccess(appCustomRocketUserRecordList);
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_USER_RECORD_LIST, appCustomRocketUserRecordList);
            }
        });
    }

    /**
     * 8. 设置默认模型(火箭)
     * mg_custom_rocket_set_default_model
     */
    @Override
    public void onGameMGCustomRocketSetDefaultModel(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketSetDefaultModel model) {
        super.onGameMGCustomRocketSetDefaultModel(handle, model);
        if (model == null) {
            return;
        }
        RocketSetDefaultSeatReq req = new RocketSetDefaultSeatReq();
        req.modelId = model.modelId;
        SudMGPAPPState.AppCustomRocketSetDefaultModel resp = new SudMGPAPPState.AppCustomRocketSetDefaultModel();
        resp.data = new SudMGPAPPState.AppCustomRocketSetDefaultModel.Data();
        resp.data.modelId = req.modelId;
        GameRepository.rocketSetDefaultModel(fragmentActivity, req, new RxCallback<Object>() {
            @Override
            public void onNext(BaseResponse<Object> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_SET_DEFAULT_MODEL, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_SET_DEFAULT_MODEL, resp);
            }
        });
    }

    /**
     * 9. 动态计算一键发送价格(火箭)
     * mg_custom_rocket_dynamic_fire_price
     */
    @Override
    public void onGameMGCustomRocketDynamicFirePrice(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketDynamicFirePrice model) {
        super.onGameMGCustomRocketDynamicFirePrice(handle, model);
        if (model == null) {
            return;
        }
        SudMGPAPPState.AppCustomRocketDynamicFirePrice resp = new SudMGPAPPState.AppCustomRocketDynamicFirePrice();
        GameRepository.rocketFirePrice(fragmentActivity, model, new RxCallback<RocketFirePriceResp>() {
            @Override
            public void onNext(BaseResponse<RocketFirePriceResp> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = new SudMGPAPPState.AppCustomRocketDynamicFirePrice.Data();
                if (t.getData() != null) {
                    resp.data.price = t.getData().price;
                }
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE, resp);
            }
        });
    }

    /**
     * 10. 一键发送(火箭)
     * mg_custom_rocket_fire_model
     */
    @Override
    public void onGameMGCustomRocketFireModel(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketFireModel model) {
        super.onGameMGCustomRocketFireModel(handle, model);
        gameFireRocketLiveData.setValue(model);
    }

    /**
     * 11. 新组装模型(火箭)
     * mg_custom_rocket_create_model
     */
    @Override
    public void onGameMGCustomRocketCreateModel(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketCreateModel model) {
        super.onGameMGCustomRocketCreateModel(handle, model);
        SudMGPAPPState.AppCustomRocketCreateModel resp = new SudMGPAPPState.AppCustomRocketCreateModel();
        GameRepository.rocketCreateModel(fragmentActivity, model, new RxCallback<SudMGPAPPState.AppCustomRocketCreateModel.Data>() {
            @Override
            public void onNext(BaseResponse<SudMGPAPPState.AppCustomRocketCreateModel.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_CREATE_MODEL, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_CREATE_MODEL, resp);
            }
        });
    }

    /**
     * 12. 模型更换组件(火箭)
     * mg_custom_rocket_replace_component
     */
    @Override
    public void onGameMGCustomRocketReplaceComponent(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketReplaceComponent model) {
        super.onGameMGCustomRocketReplaceComponent(handle, model);
        SudMGPAPPState.AppCustomRocketReplaceComponent resp = new SudMGPAPPState.AppCustomRocketReplaceComponent();
        GameRepository.rocketReplaceComponent(fragmentActivity, model, new RxCallback<SudMGPAPPState.AppCustomRocketReplaceComponent.Data>() {
            @Override
            public void onNext(BaseResponse<SudMGPAPPState.AppCustomRocketReplaceComponent.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_REPLACE_COMPONENT, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_REPLACE_COMPONENT, resp);
            }
        });
    }

    /**
     * 13. 购买组件(火箭)
     * mg_custom_rocket_buy_component
     */
    @Override
    public void onGameMGCustomRocketBuyComponent(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketBuyComponent model) {
        super.onGameMGCustomRocketBuyComponent(handle, model);
        SudMGPAPPState.AppCustomRocketBuyComponent resp = new SudMGPAPPState.AppCustomRocketBuyComponent();
        GameRepository.rocketBuyComponent(fragmentActivity, model, new RxCallback<SudMGPAPPState.AppCustomRocketBuyComponent.Data>() {
            @Override
            public void onNext(BaseResponse<SudMGPAPPState.AppCustomRocketBuyComponent.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_BUY_COMPONENT, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_BUY_COMPONENT, resp);
            }
        });
    }

    /**
     * 14. 播放效果开始(火箭)
     * mg_custom_rocket_play_effect_start
     */
    @Override
    public void onGameMGCustomRocketPlayEffectStart(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketPlayEffectStart model) {
        super.onGameMGCustomRocketPlayEffectStart(handle, model);
    }

    /**
     * 15. 播放效果完成(火箭)
     * mg_custom_rocket_play_effect_finish
     */
    @Override
    public void onGameMGCustomRocketPlayEffectFinish(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketPlayEffectFinish model) {
        super.onGameMGCustomRocketPlayEffectFinish(handle, model);
    }

    /**
     * 16. 验证签名合规(火箭)
     * mg_custom_rocket_verify_sign
     */
    @Override
    public void onGameMGCustomRocketVerifySign(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketVerifySign model) {
        super.onGameMGCustomRocketVerifySign(handle, model);
        SudMGPAPPState.AppCustomRocketVerifySign resp = new SudMGPAPPState.AppCustomRocketVerifySign();
        GameRepository.rocketVerifySign(fragmentActivity, model, new RxCallback<SudMGPAPPState.AppCustomRocketVerifySign.Data>() {
            @Override
            public void onNext(BaseResponse<SudMGPAPPState.AppCustomRocketVerifySign.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_VERIFY_SIGN, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_VERIFY_SIGN, resp);
            }
        });
    }

    /**
     * 17. 上传icon(火箭)
     * mg_custom_rocket_upload_model_icon
     */
    @Override
    public void onGameMGCustomRocketUploadModelIcon(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketUploadModelIcon model) {
        super.onGameMGCustomRocketUploadModelIcon(handle, model);
        if (model == null || TextUtils.isEmpty(model.data)) {
            return;
        }
        ThreadUtils.getIoPool().execute(() -> {
            byte[] buf = Base64.decode(model.data, Base64.DEFAULT);
            if (buf != null && buf.length > 0) {
                File file = FilePath.getRocketThumbFileDir(Utils.getApp());
                FileUtils.delete(file);
                String anewRocketThumbPath = createRocketThumbPath();
                boolean isSuccess = FileIOUtils.writeFileFromBytesByChannel(anewRocketThumbPath, buf, true);
                LogUtils.d("uploadModelIcon:" + isSuccess);
                if (isSuccess) {
                    GlobalSP.getSP().put(GlobalSP.KEY_ROCKET_THUMB_PATH, anewRocketThumbPath);
                }
            }
        });
    }

    /**
     * 18. 前期准备完成(火箭)
     * mg_custom_rocket_prepare_finish
     */
    @Override
    public void onGameMGCustomRocketPrepareFinish(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketPrepareFinish model) {
        super.onGameMGCustomRocketPrepareFinish(handle, model);
        rocketIsReady = true;
        rocketPrepareCompletedLiveData.setValue(null);
    }

    /**
     * 20. 火箭主界面已隐藏(火箭)
     * mg_custom_rocket_hide_game_scene
     */
    @Override
    public void onGameMGCustomRocketHideGameScene(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketHideGameScene model) {
        super.onGameMGCustomRocketHideGameScene(handle, model);
    }

    /**
     * 21. 点击锁住组件(火箭)
     * mg_custom_rocket_click_lock_component
     */
    @Override
    public void onGameMGCustomRocketClickLockComponent(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketClickLockComponent model) {
        super.onGameMGCustomRocketClickLockComponent(handle, model);
        clickLockComponentLiveData.setValue(model);
    }

    /**
     * 22. 火箭效果飞行点击(火箭)
     * mg_custom_rocket_fly_click
     */
    @Override
    public void onGameMGCustomRocketFlyClick(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketFlyClick model) {
        super.onGameMGCustomRocketFlyClick(handle, model);
    }

    /**
     * 23. 火箭效果飞行结束(火箭)
     * mg_custom_rocket_fly_end
     */
    @Override
    public void onGameMGCustomRocketFlyEnd(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketFlyEnd model) {
        super.onGameMGCustomRocketFlyEnd(handle, model);
    }

    /**
     * 24. 设置点击区域(火箭)
     * mg_custom_rocket_set_click_rect
     */
    @Override
    public void onGameMGCustomRocketSetClickRect(ISudFSMStateHandle handle, SudMGPMGState.MGCustomRocketSetClickRect model) {
        super.onGameMGCustomRocketSetClickRect(handle, model);
        rocketClickRectLiveData.setValue(model);
    }

    // endregion 火箭返回状态

    // region 向火箭发送状态

    /**
     * 14. app播放火箭发射动效(火箭)
     */
    public void notifyAppCustomRocketPlayModelList(SudMGPAPPState.AppCustomRocketPlayModelList model) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_PLAY_MODEL_LIST, model);
    }

    /**
     * 17. app主动调起火箭主界面(火箭)
     */
    public void notifyAppCustomRocketShowGameScene() {
        SudMGPAPPState.AppCustomRocketShowGameScene model = new SudMGPAPPState.AppCustomRocketShowGameScene();
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_SHOW_GAME_SCENE, model);
    }

    /**
     * 18. app主动隐藏火箭主界面(火箭)
     */
    public void notifyAppCustomRocketHideGameScene() {
        SudMGPAPPState.AppCustomRocketHideGameScene model = new SudMGPAPPState.AppCustomRocketHideGameScene();
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_HIDE_GAME_SCENE, model);
    }

    /**
     * 19. app推送解锁组件(火箭)
     */
    public void notifyAppCustomRocketUnlockComponent(SudMGPAPPState.AppCustomRocketUnlockComponent model) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_UNLOCK_COMPONENT, model);
    }
    // endregion 向火箭发送状态

    private void callbackGetUserInfoError(Throwable e) {
        SudMGPAPPState.AppCustomRocketUserInfo appCustomRocketUserInfo = new SudMGPAPPState.AppCustomRocketUserInfo();
        appCustomRocketUserInfo.resultCode = RetCode.FAIL;
        appCustomRocketUserInfo.error = e.toString();
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_CUSTOM_ROCKET_USER_INFO, appCustomRocketUserInfo);
    }

    public List<SudMGPAPPState.CustomRocketUserInfoModel> conver(List<UserInfoResp> list) {
        if (list != null) {
            List<SudMGPAPPState.CustomRocketUserInfoModel> userInfoList = new ArrayList<>();
            for (UserInfoResp userInfoResp : list) {
                userInfoList.add(conver(userInfoResp));
            }
        }
        return null;
    }

    public SudMGPAPPState.CustomRocketUserInfoModel conver(UserInfoResp userInfoResp) {
        if (userInfoResp == null) {
            return null;
        }
        SudMGPAPPState.CustomRocketUserInfoModel userInfo = new SudMGPAPPState.CustomRocketUserInfoModel();
        userInfo.userId = userInfoResp.userId + "";
        userInfo.nickname = userInfoResp.nickname;
        userInfo.sex = -1;
        userInfo.url = userInfoResp.getUseAvatar();
        return userInfo;
    }

    /** 启动火箭 */
    public void startRocket() {
        switchGame(fragmentActivity, getGameRoomId(), GameIdCons.CUSTOM_ROCKET);
    }

    @Override
    public void switchGame(FragmentActivity activity, long gameRoomId, long gameId) {
        if (!isRunning) {
            return;
        }
        if (playingGameId == gameId && this.gameRoomId == gameRoomId) {
            return;
        }
        destroyMG();
        this.gameRoomId = gameRoomId;
        playingGameId = gameId;
        login(activity, gameId);
    }

    @Override
    protected void destroyMG() {
        super.destroyMG();
        rocketIsReady = false;
    }

    /** 火箭是否已准备就绪 */
    public boolean rocketIsReady() {
        if (playingGameId == GameIdCons.CUSTOM_ROCKET && rocketIsReady) {
            return true;
        }
        return false;
    }

    /** 获取本地保存的火箭缩略图路径 */
    public static String getExistsRocketThumbPath() {
        return GlobalSP.getSP().getString(GlobalSP.KEY_ROCKET_THUMB_PATH);
    }

    /** 新建一个火箭缩略图路径 */
    private String createRocketThumbPath() {
        File dir = FilePath.getRocketThumbFileDir(Utils.getApp());
        return new File(dir, "rocket_thumb_" + System.currentTimeMillis() + ".png").getAbsolutePath();
    }

}
