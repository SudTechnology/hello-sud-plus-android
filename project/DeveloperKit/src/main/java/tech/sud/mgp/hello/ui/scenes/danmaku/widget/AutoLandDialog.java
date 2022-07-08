package tech.sud.mgp.hello.ui.scenes.danmaku.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;

/**
 * 自动进入横屏提示弹窗
 */
public class AutoLandDialog extends BaseDialogFragment {

    private View viewClose;
    private TextView tvCountdown;
    private TextView tvEnter;

    private CompletedListener completedListener;
    private CustomCountdownTimer countdownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_auto_land;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(296);
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(290);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        viewClose = findViewById(R.id.view_close);
        tvCountdown = findViewById(R.id.tv_countdown);
        tvEnter = findViewById(R.id.tv_enter);
    }

    @Override
    protected void initData() {
        super.initData();
        startCountdown();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callbackEnter();
            }
        });
    }

    private void startCountdown() {
        cancelCountdown();
        countdownTimer = new CustomCountdownTimer(5) {
            @Override
            protected void onTick(int count) {
                Context context = getContext();
                if (context == null) {
                    return;
                }
                tvCountdown.setText(getString(R.string.auto_land_countdown, count + ""));
            }

            @Override
            protected void onFinish() {
                dismiss();
                callbackEnter();
            }
        };
        countdownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountdown();
    }

    private void cancelCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
    }

    private void callbackEnter() {
        if (completedListener != null) {
            completedListener.onCompleted();
        }
    }

    @Override
    protected boolean canceledOnTouchOutside() {
        return false;
    }

    public void setCompletedListener(CompletedListener completedListener) {
        this.completedListener = completedListener;
    }
}
