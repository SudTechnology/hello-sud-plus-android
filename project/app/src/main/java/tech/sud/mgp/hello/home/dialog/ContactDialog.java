package tech.sud.mgp.hello.home.dialog;

import android.view.View;

import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.R;

public class ContactDialog extends BaseDialogFragment {

    @Override
    protected void setListeners() {
        super.setListeners();
        mRootView.findViewById(R.id.ok_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(requireContext(), 296f);
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(requireContext(), 160f);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_contact;
    }
}
