package tech.sud.mgp.hello.ui.scenes.custom.viewmodel;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.ui.scenes.custom.model.ConfigItemModel;

public class CustomConfigViewModel extends BaseViewModel {

    public List<ConfigItemModel> items = new ArrayList<>();
    public GameConfigModel configModel;

    public void saveCustomConfig() {
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                GlobalCache.getInstance().put(GlobalCache.CUSTOM_CONFIG_KEY, configModel);
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
    }
}
