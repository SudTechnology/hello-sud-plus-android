package tech.sud.mgp.hello.ui.scenes.custom.viewmodel;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.ui.main.utils.GsonUtils;
import tech.sud.mgp.hello.ui.scenes.custom.model.ConfigItemModel;

public class CustomConfigViewModel extends BaseViewModel {

    public List<ConfigItemModel> items = new ArrayList<>();
    public GameConfigModel configModel;

    public void saveCustomConfig() {
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() {
                if (BuildConfig.DEBUG) {
                    String json = GsonUtils.toJson(configModel);
                    LogUtils.i("CustomConfigViewModel saveCustomConfig doInBackground=" + json);
                }
                GlobalCache.getInstance().put(GlobalCache.CUSTOM_CONFIG_KEY, configModel);
                return null;
            }

            @Override
            public void onSuccess(Object result) {
                if (BuildConfig.DEBUG) {
                    Object configModel = GlobalCache.getInstance().getSerializable(GlobalCache.CUSTOM_CONFIG_KEY);
                    if (configModel instanceof GameConfigModel) {
                        GameConfigModel newer = (GameConfigModel) configModel;
                        String json = GsonUtils.toJson(newer);
                        LogUtils.i("CustomConfigViewModel saveCustomConfig onSuccess=" + json);
                    }
                }
            }
        });
    }
}
