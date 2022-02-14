package tech.sud.mgp.hello.common.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import tech.sud.mgp.hello.common.model.AppConfig;
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
        initWidget();
        initData();
        setListeners();
    }

    protected boolean beforeSetContentView() {
        return false;
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
        AppConfig.getInstance().onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        HSUserInfo.onRestoreInstanceState(savedInstanceState);
        AppConfig.getInstance().onRestoreInstanceState(savedInstanceState);
    }

}
