package tech.sud.mgp.hello.ui.scenes.custom.dialog;

import android.view.View;
import android.widget.ImageView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;

public class CustomApiDialog extends BaseDialogFragment {

    private ImageView helpIv;

    public static CustomApiDialog getInstance(){
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
        helpIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
