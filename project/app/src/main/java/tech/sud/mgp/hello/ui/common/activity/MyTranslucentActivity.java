package tech.sud.mgp.hello.ui.common.activity;

import tech.sud.mgp.hello.common.base.BaseActivity;

/**
 * 一个透明的Activity，只用于唤醒App，此activity启动后即销毁
 */
public class MyTranslucentActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected boolean beforeSetContentView() {
        return true;
    }
}
