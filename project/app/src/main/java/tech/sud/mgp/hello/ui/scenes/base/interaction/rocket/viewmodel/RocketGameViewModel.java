package tech.sud.mgp.hello.ui.scenes.base.interaction.rocket.viewmodel;

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

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.gip.core.ISudFSMStateHandle;
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
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.InteractionGameViewModel;

/**
 * 带火箭动效房间的游戏逻辑
 */
public class RocketGameViewModel extends InteractionGameViewModel {

    public FragmentActivity fragmentActivity;
    public long roomId;
    private boolean rocketIsReady; // 火箭游戏是否已加载
    private boolean isPlayingRocketEffect; // 火箭动效是否播放中
    private boolean isShowingRocketScene; // 火箭的主界面是否已显示

    public MutableLiveData<SudGIPMGState.MGCustomRocketFireModel> gameFireRocketLiveData = new MutableLiveData<>(); // 发射火箭
    public MutableLiveData<SudGIPMGState.MGCustomRocketClickLockComponent> clickLockComponentLiveData = new MutableLiveData<>(); // 点击了锁住的组件
    public MutableLiveData<Object> rocketPrepareCompletedLiveData = new MutableLiveData<>(); // 火箭准备完成
    public MutableLiveData<SudGIPMGState.MGCustomRocketSetClickRect> rocketClickRectLiveData = new MutableLiveData<>(); // 火箭点击区域
    public MutableLiveData<Object> rocketPlayEffectStartLiveData = new MutableLiveData<>(); // 火箭飞行开始
    public MutableLiveData<Object> rocketPlayEffectFinishLiveData = new MutableLiveData<>(); // 火箭飞行结束
    public MutableLiveData<Object> destroyRocketLiveData = new MutableLiveData<>(); // 销毁火箭通知

    // region 火箭返回状态

    /**
     * 1. 礼物配置文件(火箭)
     * mg_custom_rocket_config
     */
    @Override
    public void onGameMGCustomRocketConfig(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketConfig model) {
        super.onGameMGCustomRocketConfig(handle, model);
        GameRepository.rocketMallComponentList(fragmentActivity, new RxCallback<SudGIPAPPState.AppCustomRocketConfig>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppCustomRocketConfig appCustomRocketConfig) {
                super.onSuccess(appCustomRocketConfig);
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_CONFIG, appCustomRocketConfig);
            }
        });
    }

    /**
     * 2. 拥有模型列表(火箭)
     * mg_custom_rocket_model_list
     */
    @Override
    public void onGameMGCustomRocketModelList(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketModelList model) {
        super.onGameMGCustomRocketModelList(handle, model);
        GameRepository.rocketModelList(fragmentActivity, new RxCallback<SudGIPAPPState.AppCustomRocketModelList>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppCustomRocketModelList appCustomRocketModelList) {
                super.onSuccess(appCustomRocketModelList);
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_MODEL_LIST, appCustomRocketModelList);
            }
        });
    }

    /**
     * 3. 拥有组件列表(火箭)
     * mg_custom_rocket_component_list
     */
    @Override
    public void onGameMGCustomRocketComponentList(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketComponentList model) {
        super.onGameMGCustomRocketComponentList(handle, model);
        GameRepository.rocketComponentList(fragmentActivity, new RxCallback<SudGIPAPPState.AppCustomRocketComponentList>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppCustomRocketComponentList appCustomRocketComponentList) {
                super.onSuccess(appCustomRocketComponentList);
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_COMPONENT_LIST, appCustomRocketComponentList);
            }
        });
    }

    /**
     * 4. 获取用户信息(火箭)
     * mg_custom_rocket_user_info
     */
    @Override
    public void onGameMGCustomRocketUserInfo(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketUserInfo model) {
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
                SudGIPAPPState.AppCustomRocketUserInfo appCustomRocketUserInfo = new SudGIPAPPState.AppCustomRocketUserInfo();
                appCustomRocketUserInfo.resultCode = t.getRetCode();
                appCustomRocketUserInfo.error = t.getRetMsg();
                if (t.getData() != null) {
                    appCustomRocketUserInfo.userList = conver(t.getData().userInfoList);
                }
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_USER_INFO, appCustomRocketUserInfo);
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
    public void onGameMGCustomRocketOrderRecordList(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketOrderRecordList model) {
        super.onGameMGCustomRocketOrderRecordList(handle, model);
        if (model == null) {
            return;
        }
        GameRepository.rocketBuyComponentRecord(fragmentActivity, model.pageIndex, model.pageSize, new RxCallback<SudGIPAPPState.AppCustomRocketOrderRecordList>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppCustomRocketOrderRecordList appCustomRocketOrderRecordList) {
                super.onSuccess(appCustomRocketOrderRecordList);
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_ORDER_RECORD_LIST, appCustomRocketOrderRecordList);
            }
        });
    }

    /**
     * 6. 展馆内列表(火箭)
     * mg_custom_rocket_room_record_list
     */
    @Override
    public void onGameMGCustomRocketRoomRecordList(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketRoomRecordList model) {
        super.onGameMGCustomRocketRoomRecordList(handle, model);
        if (model == null) {
            return;
        }
        RocketFireRecordSummeryReq req = new RocketFireRecordSummeryReq();
        req.pageIndex = model.pageIndex;
        req.pageSize = model.pageSize;
        req.roomId = roomId;
        GameRepository.rocketFireRecordSummery(fragmentActivity, req, new RxCallback<SudGIPAPPState.AppCustomRocketRoomRecordList>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppCustomRocketRoomRecordList appCustomRocketOrderRecordList) {
                super.onSuccess(appCustomRocketOrderRecordList);
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_ROOM_RECORD_LIST, appCustomRocketOrderRecordList);
            }
        });
    }

    /**
     * 7. 展馆内玩家送出记录(火箭)
     * mg_custom_rocket_user_record_list
     */
    @Override
    public void onGameMGCustomRocketUserRecordList(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketUserRecordList model) {
        super.onGameMGCustomRocketUserRecordList(handle, model);
        if (model == null) {
            return;
        }
        RocketFireRecordReq req = new RocketFireRecordReq();
        req.pageIndex = model.pageIndex;
        req.pageSize = model.pageSize;
        req.userId = model.userId;
        req.roomId = roomId;
        GameRepository.rocketFireRecord(fragmentActivity, req, new RxCallback<SudGIPAPPState.AppCustomRocketUserRecordList>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppCustomRocketUserRecordList appCustomRocketUserRecordList) {
                super.onSuccess(appCustomRocketUserRecordList);
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_USER_RECORD_LIST, appCustomRocketUserRecordList);
            }
        });
    }

    /**
     * 8. 设置默认模型(火箭)
     * mg_custom_rocket_set_default_model
     */
    @Override
    public void onGameMGCustomRocketSetDefaultModel(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketSetDefaultModel model) {
        super.onGameMGCustomRocketSetDefaultModel(handle, model);
        if (model == null) {
            return;
        }
        RocketSetDefaultSeatReq req = new RocketSetDefaultSeatReq();
        req.modelId = model.modelId;
        SudGIPAPPState.AppCustomRocketSetDefaultModel resp = new SudGIPAPPState.AppCustomRocketSetDefaultModel();
        resp.data = new SudGIPAPPState.AppCustomRocketSetDefaultModel.Data();
        resp.data.modelId = req.modelId;
        GameRepository.rocketSetDefaultModel(fragmentActivity, req, new RxCallback<Object>() {
            @Override
            public void onNext(BaseResponse<Object> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_SET_DEFAULT_MODEL, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_SET_DEFAULT_MODEL, resp);
            }
        });
    }

    /**
     * 9. 动态计算一键发送价格(火箭)
     * mg_custom_rocket_dynamic_fire_price
     */
    @Override
    public void onGameMGCustomRocketDynamicFirePrice(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketDynamicFirePrice model) {
        super.onGameMGCustomRocketDynamicFirePrice(handle, model);
        if (model == null) {
            return;
        }
        SudGIPAPPState.AppCustomRocketDynamicFirePrice resp = new SudGIPAPPState.AppCustomRocketDynamicFirePrice();
        GameRepository.rocketFirePrice(fragmentActivity, model, new RxCallback<RocketFirePriceResp>() {
            @Override
            public void onNext(BaseResponse<RocketFirePriceResp> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = new SudGIPAPPState.AppCustomRocketDynamicFirePrice.Data();
                if (t.getData() != null) {
                    resp.data.price = t.getData().price;
                }
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE, resp);
            }
        });
    }

    /**
     * 10. 一键发送(火箭)
     * mg_custom_rocket_fire_model
     */
    @Override
    public void onGameMGCustomRocketFireModel(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketFireModel model) {
        super.onGameMGCustomRocketFireModel(handle, model);
        gameFireRocketLiveData.setValue(model);
    }

    /**
     * 11. 新组装模型(火箭)
     * mg_custom_rocket_create_model
     */
    @Override
    public void onGameMGCustomRocketCreateModel(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketCreateModel model) {
        super.onGameMGCustomRocketCreateModel(handle, model);
        SudGIPAPPState.AppCustomRocketCreateModel resp = new SudGIPAPPState.AppCustomRocketCreateModel();
        GameRepository.rocketCreateModel(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppCustomRocketCreateModel.Data>() {
            @Override
            public void onNext(BaseResponse<SudGIPAPPState.AppCustomRocketCreateModel.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_CREATE_MODEL, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_CREATE_MODEL, resp);
            }
        });
    }

    /**
     * 12. 模型更换组件(火箭)
     * mg_custom_rocket_replace_component
     */
    @Override
    public void onGameMGCustomRocketReplaceComponent(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketReplaceComponent model) {
        super.onGameMGCustomRocketReplaceComponent(handle, model);
        SudGIPAPPState.AppCustomRocketReplaceComponent resp = new SudGIPAPPState.AppCustomRocketReplaceComponent();
        GameRepository.rocketReplaceComponent(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppCustomRocketReplaceComponent.Data>() {
            @Override
            public void onNext(BaseResponse<SudGIPAPPState.AppCustomRocketReplaceComponent.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_REPLACE_COMPONENT, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_REPLACE_COMPONENT, resp);
            }
        });
    }

    /**
     * 13. 购买组件(火箭)
     * mg_custom_rocket_buy_component
     */
    @Override
    public void onGameMGCustomRocketBuyComponent(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketBuyComponent model) {
        super.onGameMGCustomRocketBuyComponent(handle, model);
        SudGIPAPPState.AppCustomRocketBuyComponent resp = new SudGIPAPPState.AppCustomRocketBuyComponent();
        GameRepository.rocketBuyComponent(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppCustomRocketBuyComponent.Data>() {
            @Override
            public void onNext(BaseResponse<SudGIPAPPState.AppCustomRocketBuyComponent.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_BUY_COMPONENT, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_BUY_COMPONENT, resp);
            }
        });
    }

    /**
     * 14. 播放效果开始(火箭)
     * mg_custom_rocket_play_effect_start
     */
    @Override
    public void onGameMGCustomRocketPlayEffectStart(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketPlayEffectStart model) {
        super.onGameMGCustomRocketPlayEffectStart(handle, model);
        rocketPlayEffectStartLiveData.setValue(null);
        isPlayingRocketEffect = true;
    }

    /**
     * 15. 播放效果完成(火箭)
     * mg_custom_rocket_play_effect_finish
     */
    @Override
    public void onGameMGCustomRocketPlayEffectFinish(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketPlayEffectFinish model) {
        super.onGameMGCustomRocketPlayEffectFinish(handle, model);
        rocketPlayEffectFinishLiveData.setValue(null);
        isPlayingRocketEffect = false;
        checkDestroyRocket();
    }

    /**
     * 16. 验证签名合规(火箭)
     * mg_custom_rocket_verify_sign
     */
    @Override
    public void onGameMGCustomRocketVerifySign(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketVerifySign model) {
        super.onGameMGCustomRocketVerifySign(handle, model);
        SudGIPAPPState.AppCustomRocketVerifySign resp = new SudGIPAPPState.AppCustomRocketVerifySign();
        GameRepository.rocketVerifySign(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppCustomRocketVerifySign.Data>() {
            @Override
            public void onNext(BaseResponse<SudGIPAPPState.AppCustomRocketVerifySign.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_VERIFY_SIGN, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_VERIFY_SIGN, resp);
            }
        });
    }

    /**
     * 17. 上传icon(火箭)
     * mg_custom_rocket_upload_model_icon
     */
    @Override
    public void onGameMGCustomRocketUploadModelIcon(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketUploadModelIcon model) {
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
    public void onGameMGCustomRocketPrepareFinish(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketPrepareFinish model) {
        super.onGameMGCustomRocketPrepareFinish(handle, model);
        rocketIsReady = true;
        rocketPrepareCompletedLiveData.setValue(null);
    }

    /**
     * 19. 火箭主界面已显示(火箭)
     * mg_custom_rocket_show_game_scene
     */
    @Override
    public void onGameMGCustomRocketShowGameScene(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketShowGameScene model) {
        super.onGameMGCustomRocketShowGameScene(handle, model);
        isShowingRocketScene = true;
    }

    /**
     * 20. 火箭主界面已隐藏(火箭)
     * mg_custom_rocket_hide_game_scene
     */
    @Override
    public void onGameMGCustomRocketHideGameScene(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketHideGameScene model) {
        super.onGameMGCustomRocketHideGameScene(handle, model);
        isShowingRocketScene = false;
        checkDestroyRocket();
    }

    /**
     * 21. 点击锁住组件(火箭)
     * mg_custom_rocket_click_lock_component
     */
    @Override
    public void onGameMGCustomRocketClickLockComponent(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketClickLockComponent model) {
        super.onGameMGCustomRocketClickLockComponent(handle, model);
        clickLockComponentLiveData.setValue(model);
    }

    /**
     * 25. 颜色和签名自定义改到装配间的模式，保存颜色或签名
     * mg_custom_rocket_save_sign_color
     */
    @Override
    public void onGameMGCustomRocketSaveSignColor(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketSaveSignColor model) {
        super.onGameMGCustomRocketSaveSignColor(handle, model);
        SudGIPAPPState.AppCustomRocketSaveSignColor resp = new SudGIPAPPState.AppCustomRocketSaveSignColor();
        GameRepository.rocketSaveSignColor(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppCustomRocketSaveSignColor.Data>() {
            @Override
            public void onNext(BaseResponse<SudGIPAPPState.AppCustomRocketSaveSignColor.Data> t) {
                super.onNext(t);
                resp.resultCode = t.getRetCode();
                resp.error = t.getRetMsg();
                resp.data = t.getData();
                notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_SAVE_SIGN_COLOR, resp);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                resp.resultCode = RetCode.FAIL;
                resp.error = e.toString();
                notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_SAVE_SIGN_COLOR, resp);
            }
        });
    }

    /**
     * 22. 火箭效果飞行点击(火箭)
     * mg_custom_rocket_fly_click
     */
    @Override
    public void onGameMGCustomRocketFlyClick(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketFlyClick model) {
        super.onGameMGCustomRocketFlyClick(handle, model);
    }

    /**
     * 23. 火箭效果飞行结束(火箭)
     * mg_custom_rocket_fly_end
     */
    @Override
    public void onGameMGCustomRocketFlyEnd(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketFlyEnd model) {
        super.onGameMGCustomRocketFlyEnd(handle, model);
    }

    /**
     * 24. 设置点击区域(火箭)
     * mg_custom_rocket_set_click_rect
     */
    @Override
    public void onGameMGCustomRocketSetClickRect(ISudFSMStateHandle handle, SudGIPMGState.MGCustomRocketSetClickRect model) {
        super.onGameMGCustomRocketSetClickRect(handle, model);
        rocketClickRectLiveData.setValue(model);
    }

    // endregion 火箭返回状态

    // region 向火箭发送状态

    /**
     * 10. 一键发送回调
     */
    public void notifyAppCustomRocketFireModel(SudGIPAPPState.AppCustomRocketFireModel model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_FIRE_MODEL, model);
    }

    /**
     * 14. app播放火箭发射动效(火箭)
     */
    public void notifyAppCustomRocketPlayModelList(SudGIPAPPState.AppCustomRocketPlayModelList model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_PLAY_MODEL_LIST, model);
    }

    /**
     * 17. app主动调起火箭主界面(火箭)
     */
    public void notifyAppCustomRocketShowGameScene() {
        SudGIPAPPState.AppCustomRocketShowGameScene model = new SudGIPAPPState.AppCustomRocketShowGameScene();
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_SHOW_GAME_SCENE, model);
    }

    /**
     * 18. app主动隐藏火箭主界面(火箭)
     */
    public void notifyAppCustomRocketHideGameScene() {
        SudGIPAPPState.AppCustomRocketHideGameScene model = new SudGIPAPPState.AppCustomRocketHideGameScene();
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_HIDE_GAME_SCENE, model);
    }

    /**
     * 19. app推送解锁组件(火箭)
     */
    public void notifyAppCustomRocketUnlockComponent(SudGIPAPPState.AppCustomRocketUnlockComponent model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_UNLOCK_COMPONENT, model);
    }

    /**
     * 20. app推送火箭效果飞行点击(火箭)
     */
    public void notifyAppCustomRocketFlyClick() {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_FLY_CLICK, new SudGIPAPPState.AppCustomRocketFlyClick());
    }

    /**
     * 21. app推送关闭火箭播放效果(火箭)
     */
    public void notifyAppCustomRocketClosePlayEffect() {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_CLOSE_PLAY_EFFECT, new SudGIPAPPState.AppCustomRocketClosePlayEffect());
    }
    // endregion 向火箭发送状态

    private void callbackGetUserInfoError(Throwable e) {
        SudGIPAPPState.AppCustomRocketUserInfo appCustomRocketUserInfo = new SudGIPAPPState.AppCustomRocketUserInfo();
        appCustomRocketUserInfo.resultCode = RetCode.FAIL;
        appCustomRocketUserInfo.error = e.toString();
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_CUSTOM_ROCKET_USER_INFO, appCustomRocketUserInfo);
    }

    public List<SudGIPAPPState.CustomRocketUserInfoModel> conver(List<UserInfoResp> list) {
        if (list != null) {
            List<SudGIPAPPState.CustomRocketUserInfoModel> userInfoList = new ArrayList<>();
            for (UserInfoResp userInfoResp : list) {
                userInfoList.add(conver(userInfoResp));
            }
        }
        return null;
    }

    public SudGIPAPPState.CustomRocketUserInfoModel conver(UserInfoResp userInfoResp) {
        if (userInfoResp == null) {
            return null;
        }
        SudGIPAPPState.CustomRocketUserInfoModel userInfo = new SudGIPAPPState.CustomRocketUserInfoModel();
        userInfo.userId = userInfoResp.userId + "";
        userInfo.nickname = userInfoResp.nickname;
        userInfo.sex = -1;
        userInfo.url = userInfoResp.getUseAvatar();
        return userInfo;
    }

    @Override
    protected void destroyMG() {
        super.destroyMG();
        rocketIsReady = false;
        isPlayingRocketEffect = false;
        isShowingRocketScene = false;
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

    /** 判断是否要销毁火箭 */
    private void checkDestroyRocket() {
        if (isPlayingRocketEffect || isShowingRocketScene) {
            return;
        }
        destroyRocketLiveData.setValue(null);
    }

}
