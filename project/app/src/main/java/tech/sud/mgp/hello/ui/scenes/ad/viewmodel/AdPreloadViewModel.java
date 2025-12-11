package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.GiAdModel;

// ad广告模块预加载
public class AdPreloadViewModel {

    private AppCompatActivity mActivity;
    private List<AdPreloadTask> mTaskList = new ArrayList<>();

    public AdPreloadViewModel(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void start() {
        initAdList();
    }

    // 拿配置
    private void initAdList() {
        RoomRepository.getGiAdConfig(mActivity, new Observer<List<GiAdModel>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<GiAdModel> giAdModels) {
                refreshList(giAdModels);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                ThreadUtils.runOnUiThreadDelayed(() -> initAdList(), 5000);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void refreshList(List<GiAdModel> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        LogUtils.d("预加载ad 总数量：" + list.size());
        for (int i = 0; i < list.size(); i++) {
            GiAdModel model = list.get(i);
            AdPreloadTask task = new AdPreloadTask(mActivity, model);
            mTaskList.add(task);
            int finalI = i;
            task.start(new AdPreloadTask.PreloadListener() {
                @Override
                public void onSuccess() {
                    LogUtils.d("预加载ad成功 position:" + finalI);
                    mTaskList.remove(task);
                }
            });
        }
    }

}
