package tech.sud.mgp.hello.common.http.rx;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.HttpException;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.ErrorStatus;
import tech.sud.mgp.hello.common.http.param.RetCode;

/**
 * 请求回调
 */
public class RxCallback<T> implements Observer<BaseResponse<T>> {

    @Override
    public void onSubscribe(Disposable d) {
        ThreadUtils.runOnUiThread(() -> {
            onStart();
        });
    }

    @Override
    public void onNext(BaseResponse<T> t) {
        if (t.getRetCode() == RetCode.SUCCESS) {
            onSuccess(t.getData());
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.d("onError", e);
        ThreadUtils.runOnUiThread(() -> {
            if (e instanceof SocketTimeoutException) { // 连接超时
                onError(e, ErrorStatus.HTTP_TIMEOUT);
            } else if (e instanceof ConnectException || e instanceof UnknownHostException) { // 无网络
                onError(e, ErrorStatus.HTTP_UNCONNECTED);
            } else if (e instanceof HttpException) { // 网络异常
                onError(e, ErrorStatus.HTTP_EXCEPTION);
            } else if (e instanceof JSONException) { // 数据异常
                onError(e, ErrorStatus.DATA_EXCEPTION);
            } else {// 其他错误
                onError(e, ErrorStatus.OTHER_EXCEPTION);
            }
        });
    }

    private void onError(Throwable e, final ErrorStatus otherException) {
        if (e != null) {
            e.printStackTrace();
        }
        ToastUtils.showShort(otherException.getErrorMessage());
    }

    @Override
    public void onComplete() {
    }

    public void onStart() {
    }

    public void onSuccess(T t) {
    }

}
