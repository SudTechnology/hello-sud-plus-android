package tech.sud.mgp.hello;

import android.app.Activity;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tech.sud.mgp.SudMGPWrapper.decorator.SudFSMMGListener;
import tech.sud.mgp.SudMGPWrapper.decorator.SudFSTAPPDecorator;
import tech.sud.mgp.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.SudMGPWrapper.state.MGStateResponse;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudFSMMG;
import tech.sud.mgp.core.ISudFSMStateHandle;

/**
 * 游戏业务逻辑
 * 1.自定义ViewModel继承此类，实现对应方法。(注意：onAddGameView()与onRemoveGameView()与页面有交互)
 * 2.外部调用switchGame(activity,gameRoomId,gameId)方法启动游戏，参数定义可查看方法注释。
 * 3.页面销毁时调用onDestroy()
 */
public class QuickStartGameViewModel extends BaseGameViewModel {

    /** Sud平台申请的appId */
    public static String SudMGP_APP_ID = "1461564080052506636";
    /** Sud平台申请的appKey */
    public static String SudMGP_APP_KEY = "03pNxK2lEXsKiiwrBQ9GbH541Fk2Sfnc";
    /** true 加载游戏时为测试环境 false 加载游戏时为生产环境 */
    public static final boolean GAME_IS_TEST_ENV = true;

    /** 使用的UserId。这里随机生成作演示，开发者将其修改为业务使用的唯一userId */
    public static String userId = QuickStartUtils.genUserID();

    /** 游戏自定义安全操作区域 */
    public GameViewInfoModel.GameViewRectModel gameViewRectModel;

    /** 游戏的语言代码 */
    public String languageCode = "zh-CN";

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>(); // 游戏View回调

    /** 向接入方服务器获取code */
    @Override
    protected void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener) {
        // TODO: 2022/6/10 注意，这里是演示使用OkHttpClient请求hello-sud服务
        // TODO: 2022/6/10 开发者在与后端联调时需将其改成自己的网络请求方式向自己的服务器获取code
        OkHttpClient client = new OkHttpClient();
        String req;
        try {
            JSONObject reqJsonObj = new JSONObject();
            reqJsonObj.put("user_id", userId);
            reqJsonObj.put("app_id", appId);
            req = reqJsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            req = "";
        }

        RequestBody body = RequestBody.create(req, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://mgp-hello.sudden.ltd/login/v3")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailed();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String dataJson = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonObject = new JSONObject(dataJson);
                    int ret_code = jsonObject.getInt("ret_code");
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    String code = dataObject.getString("code");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (ret_code == MGStateResponse.SUCCESS) {
                                listener.onSuccess(code);
                            } else {
                                listener.onFailed();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailed();
                        }
                    });
                }
            }
        });
    }

    /** 设置当前用户id(接入方定义) */
    @Override
    protected String getUserId() {
        return userId;
    }

    /** 设置Sud平台申请的appId */
    @Override
    protected String getAppId() {
        return SudMGP_APP_ID;
    }

    /** 设置Sud平台申请的appKey */
    @Override
    protected String getAppKey() {
        return SudMGP_APP_KEY;
    }

    /** 设置游戏的语言代码 */
    @Override
    protected String getLanguageCode() {
        return languageCode;
    }

    /**
     * 设置游戏的安全操作区域，{@link ISudFSMMG}.onGetGameViewInfo()的实现。
     * 参考文档：https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG/onGetGameViewInfo.html
     *
     * @param gameViewInfoModel 游戏视图模型
     */
    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        // 相对于view_size（左、上、右、下）边框偏移（单位像素）
        // 开发者可自定义gameViewRectModel来控制安全区域
        if (gameViewRectModel != null) {
            gameViewInfoModel.view_game_rect = gameViewRectModel;
        }
    }

    /**
     * 获取游戏配置对象，{@link ISudFSMMG}.onGetGameCfg()的实现。
     * 参考文档：https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG/onGetGameCfg.html
     * 开发者拿到此对象之后，可修改自己需要的配置
     * 注意：在加载游戏之前配置才有效
     *
     * @return 游戏配置对象
     */
    public GameConfigModel getGameConfigModel() {
        return gameConfigModel;
    }

    /**
     * true 加载游戏时为测试环境
     * false 加载游戏时为生产环境
     */
    @Override
    protected boolean isTestEnv() {
        return GAME_IS_TEST_ENV;
    }

    /** 将游戏View添加到页面中 */
    @Override
    protected void onAddGameView(View gameView) {
        gameViewLiveData.setValue(gameView);
    }

    /** 将页面中的游戏View移除 */
    @Override
    protected void onRemoveGameView() {
        gameViewLiveData.setValue(null);
    }

    // ************ 上面是基础能力以及必要配置，下面讲解状态交互
    // ************ 主要有：1.App向游戏发送状态；2.游戏向App回调状态

    /**
     * 1.App向游戏发送状态
     * 这里演示的是发送：1. 加入状态；
     * 开发者可自由定义方法，能发送的状态都封装在{@link SudFSTAPPDecorator}
     * 参考文档：https://docs.sud.tech/zh-CN/app/Client/APPFST/
     * 注意：
     * 1，App向游戏发送状态，因为需要走网络，所以向游戏发送状态之后，不能马上销毁游戏或者finish Activity，否则状态无法发送成功。
     * 2，要保证状态能到达，可以发送之后，delay 500ms再销毁游戏或者finish Activity。
     */
    public void notifyAPPCommonSelfIn(boolean isIn, int seatIndex, boolean isSeatRandom, int teamId) {
        sudFSTAPPDecorator.notifyAPPCommonSelfIn(isIn, seatIndex, isSeatRandom, teamId);
    }

    /**
     * 2.游戏向App回调状态
     * 这里演示的是接收游戏回调状态：10. 游戏状态 mg_common_game_state
     * 游戏回调的每个状态都对应着一个方法，方法定义在：{@link SudFSMMGListener}
     */
    @Override
    public void onGameMGCommonGameState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameState model) {
        super.onGameMGCommonGameState(handle, model);
    }

}
