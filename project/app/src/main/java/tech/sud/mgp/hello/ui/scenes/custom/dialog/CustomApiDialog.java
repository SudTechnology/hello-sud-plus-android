package tech.sud.mgp.hello.ui.scenes.custom.dialog;

import android.view.Gravity;
import android.widget.ImageView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.custom.view.CustomDialogView;

public class CustomApiDialog extends BaseDialogFragment {

    private ImageView helpIv;
    private CustomDialogView customDialogView;
    private CustomApiDialog.OperationListener listener;

    public void setListener(CustomApiDialog.OperationListener listener) {
        this.listener = listener;
    }

    public static CustomApiDialog getInstance() {
        return new CustomApiDialog();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_custom_api;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        helpIv = mRootView.findViewById(R.id.api_help_iv);
        customDialogView = mRootView.findViewById(R.id.custom_dialog_view);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        helpIv.setOnClickListener(v -> {
            CustomApiHelpDialog customApiHelpDialog = new CustomApiHelpDialog();
            customApiHelpDialog.show(getChildFragmentManager(), null);
        });
        customDialogView.setListener(listener);
    }

    @Override
    protected int getWidth() {
        return DensityUtils.getScreenWidth();
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    public interface OperationListener{
        void operation(int i);
    }

}
