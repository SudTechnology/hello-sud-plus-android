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
public class RxUtil {

    public static <T> ObservableTransformer<T, T> schedulers(LifecycleOwner owner) {
        return new ObservableTransformer<T, T>() {
            @Override
            public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                if (owner instanceof RxAppCompatActivity) {
                    return upstream.compose(((RxAppCompatActivity) owner).bindUntilEvent(ActivityEvent.DESTROY))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }
                if (owner instanceof RxFragment) {
                    return upstream.compose(((RxFragment) owner).bindUntilEvent(FragmentEvent.DESTROY))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
