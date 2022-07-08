package tech.sud.mgp.hello.ui.scenes.quiz.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;

/**
 * 更多竞猜活动的业务
 */
public class MoreQuizViewModel extends BaseViewModel {

    public MutableLiveData<QuizGameListResp> dataSuccessLiveData = new MutableLiveData<>();
    public MutableLiveData<QuizGameListResp> dataFailedLiveData = new MutableLiveData<>();

    /** 获取数据 */
    public void getDatas(LifecycleOwner owner, int pageNumber, int pageSize) {
        HomeRepository.quizGameList(owner, new RxCallback<QuizGameListResp>() {

            @Override
            public void onNext(BaseResponse<QuizGameListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == t.getRetCode()) {
                    dataSuccessLiveData.setValue(t.getData());
                } else {
                    dataFailedLiveData.setValue(null);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dataFailedLiveData.setValue(null);
            }
        });
    }

}
