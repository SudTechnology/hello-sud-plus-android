package tech.sud.mgp.hello.common.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialog;
import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * 信息弹窗，带title以及info
 */
public class TitleInfoDialog extends BaseDialog {

    private TextView tvTitle;
    private TextView tvInfo;
    private TextView tvConfirm;

    private String title;
    private String info;
    private String btn;

    public TitleInfoDialog(@NonNull Context context) {
        super(context);
    }

    public TitleInfoDialog(@NonNull Context context, String title, String info, String btn) {
        super(context);
        this.title = title;
        this.info = info;
        this.btn = btn;
    }

    public TitleInfoDialog(@NonNull Context context, String title, String info) {
        super(context);
        this.title = title;
        this.info = info;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_title_info;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(getContext(), 296);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvTitle = mRootView.findViewById(R.id.tv_title);
        tvInfo = mRootView.findViewById(R.id.tv_info);
        tvConfirm = mRootView.findViewById(R.id.tv_confirm);

        if (title != null) {
            tvTitle.setText(title);
        }
        if (info != null) {
            tvInfo.setText(info);
        }
        if (btn != null) {
            tvConfirm.setText(btn);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
