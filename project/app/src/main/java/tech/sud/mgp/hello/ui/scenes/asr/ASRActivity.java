package tech.sud.mgp.hello.ui.scenes.asr;

import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.AnimUtils;
import tech.sud.mgp.hello.ui.main.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AudioRoomActivity;

/**
 * asr类场景
 */
public class ASRActivity extends AudioRoomActivity {

    private View clOpenMic;
    private TextView tvOpenMic;
    private TextView tvASRHint;
    private long delayDismissDuration = 3000;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_asr;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        clOpenMic = findViewById(R.id.cl_open_mic);
        tvOpenMic = findViewById(R.id.tv_open_mic);
        tvASRHint = findViewById(R.id.tv_asr_hint);
        clOpenMic.setVisibility(View.GONE);
    }

    @Override
    protected void onASRChanged(boolean open) {
        super.onASRChanged(open);
        if (open && binder != null && !binder.isOpenedMic()) {
            clOpenMic.setVisibility(View.VISIBLE);
            clOpenMic.removeCallbacks(delayDismissTask);
            clOpenMic.postDelayed(delayDismissTask, delayDismissDuration);
            AnimUtils.shakeVertical(clOpenMic);
            setOpenMicText();
        }
    }

    private void setOpenMicText() {
        if (playingGameId == GameIdCons.I_GUESS_YOU_SAID) { //  你说我猜
            tvOpenMic.setText(R.string.asr_guide_draw);
        } else if (playingGameId == GameIdCons.DIGITAL_BOMB) { // 数字炸弹
            tvOpenMic.setText(R.string.asr_guide_number_boom);
        } else if (playingGameId == GameIdCons.YOU_DRAW_AND_I_GUESS) { // 你画我猜
            tvOpenMic.setText(R.string.asr_guide_draw);
        }
    }

    // 是否支持ASR
    private boolean isSupportASR(long gameId) {
        if (gameId == GameIdCons.I_GUESS_YOU_SAID) return true;
        if (gameId == GameIdCons.DIGITAL_BOMB) return true;
        if (gameId == GameIdCons.YOU_DRAW_AND_I_GUESS) return true;
        return false;
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (isSupportASR(playingGameId)) {
            tvASRHint.setVisibility(View.VISIBLE);
        } else {
            tvASRHint.setVisibility(View.GONE);
        }
    }

    private final Runnable delayDismissTask = new Runnable() {
        @Override
        public void run() {
            Animation animation = clOpenMic.getAnimation();
            if (animation != null) {
                animation.cancel();
            }
            clOpenMic.setVisibility(View.GONE);
        }
    };

}