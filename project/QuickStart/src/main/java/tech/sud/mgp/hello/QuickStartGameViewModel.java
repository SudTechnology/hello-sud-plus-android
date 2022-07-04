package tech.sud.mgp.hello;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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
import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.SudMGPWrapper.state.MGStateResponse;

/**
 * 游戏业务逻辑
 * 1.定自义ViewModel继承此类，实现对应方法。(注意：onAddGameView()与onRemoveGameView()与页面有交互)
 * 2.外部调用switchGame()方法启动游戏
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
    protected void getCode(FragmentActivity activity, String userId, String appId, GameGetCodeListener listener) {
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailed();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
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

    /** 设置游戏的安全操作区域 */
    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        // 相对于view_size（左、上、右、下）边框偏移（单位像素）
        if (gameViewRectModel != null) {
            gameViewInfoModel.view_game_rect = gameViewRectModel;
        }
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

}
