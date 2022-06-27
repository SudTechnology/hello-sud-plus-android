package tech.sud.mgp.hello.ui.scenes.quiz.widget;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;

/**
 * 猜自己赢的提示弹窗
 */
public class GuessIWinDialog extends BaseDialogFragment {

    private View.OnClickListener startNowOnClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_guess_i_win;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        TextView tvHint = findViewById(R.id.tv_hint_auto_hint);
        tvHint.setText(getString(R.string.each_round_coin, APPConfig.QUIZ_SINGLE_BET_COUNT + ""));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.tv_confirm).setOnClickListener(startNowOnClickListener);
    }

    public void setStartNowOnClickListener(View.OnClickListener listener) {
        startNowOnClickListener = listener;
    }


}
