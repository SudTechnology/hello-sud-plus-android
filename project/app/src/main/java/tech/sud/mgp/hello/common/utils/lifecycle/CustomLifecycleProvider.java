package tech.sud.mgp.hello.common.utils.lifecycle;

import com.trello.rxlifecycle4.LifecycleTransformer;

/**
 * 自定义的lifecycle提供者
 */
public interface CustomLifecycleProvider {

    <T> LifecycleTransformer<T> bindToLifecycle();

}
