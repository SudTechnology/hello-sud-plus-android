package tech.sud.mgp.hello.ui.common.dialog;

import android.view.Window;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;

public class LoadingDialog extends BaseDialogFragment {

    private String content;

    public LoadingDialog() {
    }

    public LoadingDialog(String content) {
        this.content = content;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_loading;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(160);
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(128);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setDimAmount(0);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        TextView tvContent = findViewById(R.id.tv_content);
        tvContent.setText(content);
    }

}
