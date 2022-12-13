package tech.sud.mgp.hello.common.http.rx;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.utils.ResponseUtils;

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
        } else {
            showToast(t);
        }
        onFinally();
    }

    /** 弹出错误提示 */
    protected void showToast(BaseResponse<T> t) {
        ToastUtils.showLong(ResponseUtils.conver(t));
    }

    /** 弹出错误提示 */
    protected void showToast(Throwable e) {
        ToastUtils.showShort(Utils.getApp().getString(R.string.net_error_hint));
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.d("onError", e);
        ThreadUtils.runOnUiThread(() -> {
            processError(e);
            onFinally();
        });
    }

    private void processError(Throwable e) {
        if (e != null) {
            LogUtils.e("okhttp", e);
        }
//        if (e instanceof SocketTimeoutException) { // 连接超时
//        } else if (e instanceof ConnectException || e instanceof UnknownHostException) { // 无网络
//        } else if (e instanceof HttpException) { // 网络异常
//        } else if (e instanceof JSONException) { // 数据异常
//        } else {// 其他错误
//        }
        showToast(e);
    }

    @Override
    public void onComplete() {
    }

    public void onStart() {
    }

    public void onSuccess(T t) {
    }

    // 正常返回或者执出异常时都会调用此方法，可用于执行网络请求完成的最终操作。
    public void onFinally() {
    }

}
