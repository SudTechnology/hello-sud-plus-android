package tech.sud.mgp.hello.ui.scenes.ai.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.logger.LogUtils;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.permission.PermissionFragment;
import tech.sud.mgp.hello.common.utils.permission.SudPermissionUtils;
import tech.sud.mgp.hello.ui.scenes.ai.manager.AudioRecordManager;
import tech.sud.mgp.hello.ui.scenes.ai.viewmodel.AiAudioRoomGameViewModel;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdChatMediaModel;

public class AiAudioRoomActivity extends AbsAudioRoomActivity<AiAudioRoomGameViewModel> {

    private TextView tvInput;
    private TextView tvAudioRecord;
    private View viewInputMode;
    private boolean isAudioMode;
    private AudioRecordManager audioRecordManager;
    private Handler handler = new Handler();

    @Override
    protected AiAudioRoomGameViewModel initGameViewModel() {
        return new AiAudioRoomGameViewModel();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        customBottomView();
    }

    private void customBottomView() {
        bottomView.removeMicStateView();
        bottomView.removeGotMicView();

        ConstraintLayout viewRoot = bottomView.getViewRoot();
        tvInput = bottomView.getTvInput();
        int tvInputId = tvInput.getId();
        tvAudioRecord = new TextView(this);
        tvAudioRecord.setTextSize(12);
        tvAudioRecord.setTextColor(Color.WHITE);
        tvAudioRecord.setGravity(Gravity.CENTER);
        tvAudioRecord.setBackgroundResource(R.drawable.shape_content_bg);
        tvAudioRecord.setText(R.string.hold_to_speak);
        ConstraintLayout.LayoutParams audioRecordParams = new ConstraintLayout.LayoutParams(0, 0);
        audioRecordParams.leftToLeft = tvInputId;
        audioRecordParams.rightToRight = tvInputId;
        audioRecordParams.topToTop = tvInputId;
        audioRecordParams.bottomToBottom = tvInputId;
        viewRoot.addView(tvAudioRecord, audioRecordParams);
        tvAudioRecord.setVisibility(View.GONE);

        viewInputMode = new View(this);
        LinearLayout.LayoutParams foldParams = new LinearLayout.LayoutParams(DensityUtils.dp2px(32), DensityUtils.dp2px(32));
        foldParams.setMarginStart(DensityUtils.dp2px(15));
        bottomView.addViewToLeft(viewInputMode, 0, foldParams);

        setInputModeStyle();
    }

    private void setInputModeStyle() {
        if (isAudioMode) {
            viewInputMode.setBackgroundResource(R.drawable.ic_ai_audio);
        } else {
            viewInputMode.setBackgroundResource(R.drawable.ic_ai_keyboard);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setListeners() {
        super.setListeners();
        viewInputMode.setOnClickListener(v -> onClickInputMode());
        tvAudioRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchAudioRecord(event);
            }
        });
        gameViewModel.happyGoatChatLiveData.observe(this, this::onHappyGoatChat);
    }

    private void onHappyGoatChat(SudMGPMGState.MGHappyGoatChat model) {
        if (model == null || model.data == null || model.data.length == 0) {
            return;
        }
        for (SudMGPMGState.MGHappyGoatChat.ChatAudioTextModel audioTextModel : model.data) {
            SudMGPMGState.MGHappyGoatChat.TextModel textModel = audioTextModel.text;
            if (textModel == null || TextUtils.isEmpty(textModel.text)) {
                continue;
            }
            if (binder != null) {
                String userId = gameViewModel.getPlayingGameId() + "";
                binder.assignUserSendPublicMsg(userId, model.icon, model.nickname, textModel.text);
            }
        }
    }

    private boolean onTouchAudioRecord(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.postDelayed(abortAudioRecordTask, 15000); // 15秒，要自动结束
                tvAudioRecord.setText(R.string.end_of_release);
                intentStartAudioRecord();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(abortAudioRecordTask);
                tvAudioRecord.setText(R.string.hold_to_speak);
                stopAudioRecord();
                break;
        }
        return true;
    }

    private Runnable abortAudioRecordTask = new Runnable() {
        @Override
        public void run() {
            tvAudioRecord.dispatchTouchEvent(MotionEvent.obtain(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_CANCEL, // 触发 ACTION_CANCEL
                    0, 0, 0
            ));
//            tvAudioRecord.setEnabled(false);
        }
    };

    private void intentStartAudioRecord() {
        // 检查权限
        SudPermissionUtils.requirePermission(this, getSupportFragmentManager(), new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionFragment.OnPermissionListener() {
            @Override
            public void onPermission(boolean success) {
                if (success) {
                    startAudioRecord();
                }
            }
        });
    }

    private void startAudioRecord() {
        if (audioRecordManager == null) {
            audioRecordManager = new AudioRecordManager();
            audioRecordManager.setOnAudioRecordListener(this::onAudioData);
        }
        LogUtils.d("startRecording");
        audioRecordManager.startRecording();
    }

    private void stopAudioRecord() {
        if (audioRecordManager == null) {
            return;
        }
        LogUtils.d("stopAudioRecord");
        audioRecordManager.stopRecording();
    }

    private void onAudioData(byte[] audioData) {
        LogUtils.d("onAudioData");
        if (audioData == null || audioData.length == 0) {
            return;
        }
        String audioBase64 = Base64.encodeToString(audioData, Base64.NO_WRAP);
        SudMGPAPPState.AppHappyGoatChat model = new SudMGPAPPState.AppHappyGoatChat();
        model.type = 1;
        SudMGPAPPState.AppHappyGoatChat.ChatAudioModel audioModel = new SudMGPAPPState.AppHappyGoatChat.ChatAudioModel();
        audioModel.sample_rate = "16k";
        audioModel.audio_format = "PCM";
        audioModel.audio_base64 = audioBase64;
        model.audio = audioModel;
        gameViewModel.notifyStateChange(SudMGPAPPState.APP_HAPPY_GOAT_CHAT, model);

        if (binder != null) {
            binder.sendMediaMsg(RoomCmdChatMediaModel.MSG_TYPE_INACTIVE_TYPE, null);
        }
    }

    @Override
    public void onSelfSendMsg(String msg) {
        super.onSelfSendMsg(msg);
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        SudMGPAPPState.AppHappyGoatChat model = new SudMGPAPPState.AppHappyGoatChat();
        model.type = 0;
        SudMGPAPPState.AppHappyGoatChat.ChatTextModel textModel = new SudMGPAPPState.AppHappyGoatChat.ChatTextModel();
        textModel.text = msg;
        model.text = textModel;
        gameViewModel.notifyStateChange(SudMGPAPPState.APP_HAPPY_GOAT_CHAT, model);
    }

    // 切换键盘或语音
    private void onClickInputMode() {
        isAudioMode = !isAudioMode;
        setInputModeStyle();
        if (isAudioMode) {
            tvInput.setVisibility(View.INVISIBLE);
            tvAudioRecord.setVisibility(View.VISIBLE);
        } else {
            tvInput.setVisibility(View.VISIBLE);
            tvAudioRecord.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecordManager != null) {
            audioRecordManager.stopRecording();
        }
        handler.removeCallbacks(abortAudioRecordTask);
    }

}
