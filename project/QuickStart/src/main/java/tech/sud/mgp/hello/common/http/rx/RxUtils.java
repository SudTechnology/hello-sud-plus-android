package tech.sud.mgp.hello.common.http.rx;

import androidx.lifecycle.LifecycleOwner;

import com.trello.rxlifecycle4.android.ActivityEvent;
import com.trello.rxlifecycle4.android.FragmentEvent;
import com.trello.rxlifecycle4.components.RxFragment;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * RxJava调度器工具(线程切换)
 */
public class RxUtils {

    public static <T> ObservableTransformer<T, T> schedulers(LifecycleOwner owner) {
        return new ObservableTransformer<T, T>() {
            @Override
            public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return bindLifecycle(upstream, owner).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Observable<T> bindLifecycle(Observable<T> observable, Object owner) {
        if (owner instanceof RxAppCompatActivity) {
            return observable.compose(((RxAppCompatActivity) owner).bindUntilEvent(ActivityEvent.DESTROY));
        }
        if (owner instanceof RxFragment) {
            return observable.compose(((RxFragment) owner).bindUntilEvent(FragmentEvent.DESTROY));
        }
        return observable;
    }

}
