package tech.sud.mgp.hello.common.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;

public abstract class BaseActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (beforeSetContentView()) {
            finish();
            return;
        }
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            setContentView(layoutId);
        }
        setStatusBar();
        initWidget();
        initData();
        setListeners();
    }

    /**
     * 在SetContentView之前会调用
     *
     * @return 返回true则直接销毁页面，不进行后续初始化
     */
    protected boolean beforeSetContentView() {
        return false;
    }

    /**
     * 设置沉浸式状态栏
     */
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(getStatusBarColorResId()).statusBarDarkFont(true)
                .fitsSystemWindows(true).init();
    }

    /**
     * 获取状态栏的颜色
     *
     * @return 返回资源id
     */
    protected int getStatusBarColorResId() {
        return R.color.white;
    }

    protected abstract int getLayoutId();

    protected void initWidget() {
    }

    protected void initData() {
    }

    protected void setListeners() {
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        HSUserInfo.onSaveInstanceState(outState);
        AppData.getInstance().onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        HSUserInfo.onRestoreInstanceState(savedInstanceState);
        AppData.getInstance().onRestoreInstanceState(savedInstanceState);
    }

}
