package tech.sud.mgp.hello.ui.login.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.IntentUtils;
import tech.sud.mgp.hello.service.main.resp.CheckUpgradeResp;

/**
 * 升级弹窗
 */
public class VersionUpgradeDialog extends BaseDialogFragment {

    private CheckUpgradeResp upgradeResp;
    private TextView titleTv, leftTv, rightTv, singleTv;
    private boolean isDelayDismiss = false; // 延迟消失

    public static VersionUpgradeDialog getInstance(CheckUpgradeResp resp) {
        VersionUpgradeDialog dialog = new VersionUpgradeDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("CheckUpgradeResp", resp);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Serializable resp = arguments.getSerializable("CheckUpgradeResp");
            if (resp instanceof CheckUpgradeResp) {
                upgradeResp = (CheckUpgradeResp) resp;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_version_upgrade;
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
        if (isForceUpgrade()) {
            titleTv.setText(R.string.app_version_low);
            leftTv.setVisibility(View.GONE);
            rightTv.setVisibility(View.GONE);
            singleTv.setVisibility(View.VISIBLE);
        } else {
            titleTv.setText(R.string.app_version_update_tip);
            leftTv.setVisibility(View.VISIBLE);
            rightTv.setVisibility(View.VISIBLE);
            singleTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        if (upgradeResp == null) {
            dismiss();
            return;
        }
        if (isForceUpgrade()) {
            singleTv.setOnClickListener(v -> {
                IntentUtils.openUrl(getContext(), upgradeResp.packageUrl);
            });
        } else {
            leftTv.setOnClickListener(v -> dismiss());
            rightTv.setOnClickListener(v -> {
                boolean success = IntentUtils.openUrl(getContext(), upgradeResp.packageUrl);
                if (success) {
                    // delay的逻辑是为了给打开系统浏览器足够的时间，否则会直接执行下一步的逻辑，跟体验不符。
                    isDelayDismiss = true;
                    startDelayDismissTask();
                } else {
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isDelayDismiss) {
            removeDelayDismissTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDelayDismiss) {
            startDelayDismissTask();
        }
    }

    public void startDelayDismissTask() {
        removeDelayDismissTask();
        titleTv.postDelayed(delayDismissTask, 1000);
    }

    public void removeDelayDismissTask() {
        titleTv.removeCallbacks(delayDismissTask);
    }

    private boolean isForceUpgrade() {
        return upgradeResp != null && upgradeResp.upgradeType == CheckUpgradeResp.FORCE_UPGRADE;
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

    private Runnable delayDismissTask = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

}
