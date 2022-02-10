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
        outState.putLong("userId", HSUserInfo.userId);
        outState.putString("nickName", HSUserInfo.nickName);
        outState.putString("gender", HSUserInfo.gender);
        outState.putString("token", HSUserInfo.token);
        outState.putString("avatar", HSUserInfo.avatar);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        HSUserInfo.userId = savedInstanceState.getLong("userId");
        HSUserInfo.nickName = savedInstanceState.getString("nickName");
        HSUserInfo.gender = savedInstanceState.getString("gender");
        HSUserInfo.token = savedInstanceState.getString("token");
        HSUserInfo.avatar = savedInstanceState.getString("avatar");
    }

}
