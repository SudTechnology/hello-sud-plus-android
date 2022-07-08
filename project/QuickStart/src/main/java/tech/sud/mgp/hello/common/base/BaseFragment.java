package tech.sud.mgp.hello.common.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trello.rxlifecycle4.components.support.RxFragment;


public abstract class BaseFragment extends RxFragment {

    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            mRootView = inflater.inflate(layoutId, container, false);
            return mRootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    public <T extends View> T findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }

}
