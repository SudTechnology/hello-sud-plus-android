package tech.sud.mgp.hello.ui.login.dialog;

import android.view.View;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.main.utils.RouterUtils;

/**
 * 升级弹窗
 */
public class VersionUpdateDialog extends BaseDialogFragment {

    private UpdateInfoModel updateInfo;
    private TextView titleTv, leftTv, rightTv, singleTv;

    //更新类型 0普通更新 1强制更新
    public VersionUpdateDialog(UpdateInfoModel updateInfo) {
        this.updateInfo = updateInfo;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_version_update;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        titleTv = mRootView.findViewById(R.id.title_tv);
        leftTv = mRootView.findViewById(R.id.left_tv);
        rightTv = mRootView.findViewById(R.id.right_tv);
        singleTv = mRootView.findViewById(R.id.single_tv);

    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        if (updateInfo.updateType == 1) {
            titleTv.setText(R.string.app_version_low);
            leftTv.setVisibility(View.GONE);
            rightTv.setVisibility(View.GONE);
            singleTv.setVisibility(View.VISIBLE);
            singleTv.setOnClickListener(v -> {
                RouterUtils.openUrlPage(getContext(), updateInfo.url);
                dismiss();
            });

        } else {
            titleTv.setText(R.string.app_version_update_tip);
            leftTv.setVisibility(View.VISIBLE);
            rightTv.setVisibility(View.VISIBLE);
            singleTv.setVisibility(View.GONE);
            leftTv.setOnClickListener(v -> {
                dismiss();

            });
            rightTv.setOnClickListener(v -> {
                RouterUtils.openUrlPage(getContext(), updateInfo.url);
                dismiss();
            });
        }
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(296);
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(152);
    }

    @Override
    protected boolean cancelable() {
        return false;
    }

    @Override
    protected boolean canceledOnTouchOutside() {
        return false;
    }

    public class UpdateInfoModel {
        public int updateType;
        public String url;
    }

}
