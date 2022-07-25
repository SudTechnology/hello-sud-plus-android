package tech.sud.mgp.hello.common.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialog;
import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * 信息弹窗
 */
public class InfoDialog extends BaseDialog {

    private TextView tvTitle;
    private TextView tvISee;

    private String mTitle;

    public InfoDialog(@NonNull Context context) {
        super(context);
    }

    public InfoDialog(@NonNull Context context, String title) {
        super(context);
        mTitle = title;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_info;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(getContext(), 296);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvTitle = mRootView.findViewById(R.id.tv_title);
        tvISee = mRootView.findViewById(R.id.tv_i_see);

        if (mTitle != null) {
            tvTitle.setText(mTitle);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvISee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
