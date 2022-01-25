package tech.sud.mgp.common.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import tech.sud.mgp.common.model.HSUserInfo;

public abstract class BaseActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            setContentView(layoutId);
        }
        initWidget();
        initData();
        setListeners();
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
        outState.putLong("userId", HSUserInfo.getInstance().userId);
        outState.putString("nickName", HSUserInfo.getInstance().nickName);
        outState.putString("gender", HSUserInfo.getInstance().gender);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        HSUserInfo.getInstance().userId = savedInstanceState.getLong("userId");
        HSUserInfo.getInstance().nickName = savedInstanceState.getString("nickName");
        HSUserInfo.getInstance().gender = savedInstanceState.getString("gender");
    }

}
