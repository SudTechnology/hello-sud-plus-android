package tech.sud.mgp.hello.ui.main.llm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kyleduo.switchbutton.SwitchButton;

import tech.sud.mgp.hello.R;

public class CreateLlmView extends ConstraintLayout {

    private SwitchButton mSwitchButtonCloned;
    private EditText mEtName;
    private TextView mTvPersonality;
    private TextView mTvLanguage;
    private TextView mTvLanguageDetail;
    private TextView mTvNothingLlmVoice;
    private View mContainerLlmVoice;
    private ImageView mIvOperateVoice;
    private TextView mTvReadAloud;
    private TextView mTvReadAloudContent;
    private View mContainerPlayPreVoice;
    private View mContainerPlayPreVoiceContent;
    private View mContainerSubmit;
    private TextView mTvCancel;
    private TextView mTvSubmit;
    private VoiceRecordView mVoiceRecordView;

    public CreateLlmView(@NonNull Context context) {
        this(context, null);
    }

    public CreateLlmView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreateLlmView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_create_llm, this);
        mSwitchButtonCloned = findViewById(R.id.switch_button_cloned);
        mEtName = findViewById(R.id.et_name);
        mTvPersonality = findViewById(R.id.tv_personality);
        mTvLanguage = findViewById(R.id.tv_language);
        mTvLanguageDetail = findViewById(R.id.tv_language_detail);
        mTvNothingLlmVoice = findViewById(R.id.tv_nothing_llm_voice);
        mContainerLlmVoice = findViewById(R.id.container_llm_voice);
        mIvOperateVoice = findViewById(R.id.iv_operate_voice);
        mTvReadAloud = findViewById(R.id.tv_read_aloud);
        mTvReadAloudContent = findViewById(R.id.tv_read_aloud_content);
        mContainerPlayPreVoice = findViewById(R.id.container_play_pre_voice);
        mContainerPlayPreVoiceContent = findViewById(R.id.container_play_pre_voice_content);
        mContainerSubmit = findViewById(R.id.container_submit);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvSubmit = findViewById(R.id.tv_submit);
        mVoiceRecordView = findViewById(R.id.voice_record_view);
    }

    public void setSwitchCloned(boolean checked, boolean isEvent) {
        if (isEvent) {
            mSwitchButtonCloned.setChecked(checked);
        } else {
            mSwitchButtonCloned.setCheckedNoEvent(checked);
        }
    }

    public void setName(String name) {
        mEtName.setText(name);
    }

    public void setPersonality(String str) {
        mTvPersonality.setText(str);
    }

    public void setLanguage(String str) {
        mTvLanguage.setText(str);
    }

    public void setLanguageDetail(String str) {
        mTvLanguageDetail.setText(str);
    }

    public void setReadAloud(String str) {
        mTvReadAloudContent.setText(str);
    }

    /** 默认状态，没有大模型声音，也没有进行添加本地声音 */
    public void showNormalStatus() {
        mTvNothingLlmVoice.setVisibility(View.VISIBLE);
        mContainerLlmVoice.setVisibility(View.GONE);
        mIvOperateVoice.setImageResource(R.drawable.ic_white_add);
        mTvReadAloud.setVisibility(View.GONE);
        mTvReadAloudContent.setVisibility(View.GONE);
        mContainerPlayPreVoice.setVisibility(View.GONE);
        mContainerSubmit.setVisibility(View.GONE);
        mVoiceRecordView.setVisibility(View.GONE);
    }

    /** 录制状态，但本地还未录制 */
    public void showNormalRecordStatus() {
        mTvNothingLlmVoice.setVisibility(View.VISIBLE);
        mContainerLlmVoice.setVisibility(View.GONE);
        mIvOperateVoice.setImageResource(R.drawable.ic_white_add);
        mTvReadAloud.setVisibility(View.VISIBLE);
        mTvReadAloudContent.setVisibility(View.VISIBLE);
        mContainerPlayPreVoice.setVisibility(View.VISIBLE);
        mContainerPlayPreVoiceContent.setVisibility(View.GONE);
        mContainerSubmit.setVisibility(View.GONE);
        mVoiceRecordView.setVisibility(View.VISIBLE);
    }

    /** 录制状态，本地已经录制 */
    public void showReadyRecordStatus() {
        mTvNothingLlmVoice.setVisibility(View.VISIBLE);
        mContainerLlmVoice.setVisibility(View.GONE);
        mIvOperateVoice.setImageResource(R.drawable.ic_white_add);
        mTvReadAloud.setVisibility(View.VISIBLE);
        mTvReadAloudContent.setVisibility(View.VISIBLE);
        mContainerPlayPreVoice.setVisibility(View.VISIBLE);
        mContainerPlayPreVoiceContent.setVisibility(View.VISIBLE);
        mContainerSubmit.setVisibility(View.VISIBLE);
        mVoiceRecordView.setVisibility(View.GONE);
    }

    /** 分身已经创建好的情况 */
    public void showReadyClonedStatus() {
        mTvNothingLlmVoice.setVisibility(View.GONE);
        mContainerLlmVoice.setVisibility(View.VISIBLE);
        mIvOperateVoice.setImageResource(R.drawable.ic_white_edit);
        mTvReadAloud.setVisibility(View.GONE);
        mTvReadAloudContent.setVisibility(View.GONE);
        mContainerPlayPreVoice.setVisibility(View.GONE);
        mContainerPlayPreVoiceContent.setVisibility(View.GONE);
        mContainerSubmit.setVisibility(View.GONE);
        mVoiceRecordView.setVisibility(View.GONE);
    }

    private void setSwitchListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        mSwitchButtonCloned.setOnCheckedChangeListener(onCheckedChangeListener);
    }

}
