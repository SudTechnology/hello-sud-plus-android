package tech.sud.mgp.hello.ui.scenes.custom.dialog;

import android.view.Gravity;
import android.widget.ImageView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;

public class CustomApiDialog extends BaseDialogFragment {

    private ImageView helpIv;

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
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        helpIv.setOnClickListener(v -> {
            CustomApiHelpDialog customApiHelpDialog = new CustomApiHelpDialog();
            customApiHelpDialog.show(getChildFragmentManager(), null);
        });
    }

    @Override
    protected int getWidth() {
        return DensityUtils.getScreenWidth();
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }
}
