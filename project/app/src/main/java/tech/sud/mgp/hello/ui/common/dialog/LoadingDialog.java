package tech.sud.mgp.hello.ui.common.dialog;

import tech.sud.mgp.hello.common.base.BaseDialogFragment;

public class LoadingDialog extends BaseDialogFragment {

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setDimAmount(0);
        window.setWindowAnimations(R.style.TopToBottomAnim);
        if (isFullScreen) {
            ImmersionBar.with(this).init();
        } else {
            ImmersionBar.with(this).init();
        }
    }

}
