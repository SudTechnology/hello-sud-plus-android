package tech.sud.mgp.hello.ui.main.settings.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.service.main.config.SudConfig;

/**
 * 切换appId服务业务逻辑
 */
public class ChangeAppIdViewModel extends BaseViewModel {

    private final Executor executor = Executors.newSingleThreadExecutor();

    // 数据返回
    public final MutableLiveData<List<SudConfig>> datasLiveData = new MutableLiveData<>();

    // 获取数据
    public void getDatas() {
        List<SudConfig> list = getSudConfigs();
        datasLiveData.postValue(list);
    }

    @NonNull
    public static List<SudConfig> getSudConfigs() {
        Application context = Utils.getApp();
        List<SudConfig> list = new ArrayList<>();
        list.add(buildSudConfig(context.getString(R.string.appid_sz), "1461564080052506636", "03pNxK2lEXsKiiwrBQ9GbH541Fk2Sfnc"));
        list.add(buildSudConfig(context.getString(R.string.appid_sz001), "1473625878200025090", "3P43gq63ekk0tEFjDCSuxHQhEuu23VtQ"));
        list.add(buildSudConfig(context.getString(R.string.appid_sg), "1473207032642564097", "KzWN6fGccA9QYDZnBY0foK3hpejfrAxo"));
        list.add(buildSudConfig(context.getString(R.string.appid_jp001), "1484434624192245762", "wgsr1Gz8oPMgOc7vRcotkMlE9b6QfDL2"));
        list.add(buildSudConfig(context.getString(R.string.appid_me001), "1484434902689837058", "76OidDxjiAumcWK3d3m3obWHicUTjNut"));
        list.add(buildSudConfig(context.getString(R.string.appid_sa001), "1494278107449683969", "plCg0dlpnckfZ2iJhyEPjq58nNvTETgP"));
        list.add(buildSudConfig(context.getString(R.string.appid_mumbai), "1518141831860158465", "WNQeoU5TX7GRvey8ZtZWNa9SAZf0cXs5"));
        list.add(buildSudConfig(context.getString(R.string.appid_europe), "1524289012192604162", "33PKQhJEwCHL4lGgATTCX2j5pL6o3WHV"));
        return list;
    }

    private static SudConfig buildSudConfig(String area, String appId, String appKey) {
        SudConfig config = new SudConfig();
        config.area = area;
        config.appId = appId;
        config.appKey = appKey;
        return config;
    }


}
