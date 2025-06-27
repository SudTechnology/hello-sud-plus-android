package tech.sud.mgp.hello.ui.main.llm.widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ToastUtils;
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
    private View mViewCannotOperate;
    private int mVoiceModel; // 0为默认，1为录制状态，但本地还未录制，2为录制状态，本地已经录制，3为分身已经创建好的情况

    public CreateLlmView(@NonNull Context context) {
        this(context, null);
    }

    public CreateLlmView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreateLlmView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initListener();
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
        mViewCannotOperate = findViewById(R.id.view_cannot_operate);
    }

    private void initListener() {
        mViewCannotOperate.setOnClickListener(v -> ToastUtils.showShort(R.string.please_start_clonse));
    }

    public void setSwitchCloned(boolean checked, boolean isEvent) {
        if (checked != mSwitchButtonCloned.isChecked()) {
            if (isEvent) {
                mSwitchButtonCloned.setChecked(checked);
            } else {
                mSwitchButtonCloned.setCheckedImmediatelyNoEvent(checked);
            }
        }
        setCannotOperateShow(!checked);
    }

    public void setSwitchEnabled(boolean enabled) {
        mSwitchButtonCloned.setEnabled(enabled);
    }

    public void setNickName(String name) {
        mEtName.setText(name);
    }

    public String getNickName() {
        Editable text = mEtName.getText();
        return text == null ? null : text.toString();
    }

    public void setPersonality(String str) {
        mTvPersonality.setText(str);
    }

    public String getPersonality() {
        CharSequence text = mTvPersonality.getText();
        return text == null ? null : text.toString();
    }

    public void setLanguage(String str) {
        mTvLanguage.setText(str);
    }

    public String getLanguage() {
        CharSequence text = mTvLanguage.getText();
        return text == null ? null : text.toString();
    }

    public void setLanguageDetail(String str) {
        mTvLanguageDetail.setText(str);
    }

    public String getLanguageDetail() {
        CharSequence text = mTvLanguageDetail.getText();
        return text == null ? null : text.toString();
    }

    public void setReadAloud(String str) {
        mTvReadAloudContent.setText(str);
    }

    public void setCannotOperateShow(boolean show) {
        mViewCannotOperate.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /** 默认状态，没有大模型声音，也没有进行添加本地声音 */
    public void showNormalStatus() {
        mVoiceModel = 0;
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
        mVoiceModel = 1;
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
        mVoiceModel = 2;
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
        mVoiceModel = 3;
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

    public void setSwitchListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        mSwitchButtonCloned.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void setPersonalityListener(OnClickListener listener) {
        mTvPersonality.setOnClickListener(listener);
    }

    public void setLanguageListener(OnClickListener listener) {
        mTvLanguage.setOnClickListener(listener);
    }

    public void setLanguageDetailListener(OnClickListener listener) {
        mTvLanguageDetail.setOnClickListener(listener);
    }

    public void setOperateListener(OnClickListener listener) {
        mIvOperateVoice.setOnClickListener(listener);
    }

    public int getVoiceModel() {
        return mVoiceModel;
    }

    public void setOnRecordListener(VoiceRecordView.OnRecordListener onRecordListener) {
        mVoiceRecordView.setOnRecordListener(onRecordListener);
    }

    public void setPlayPreVoiceListener(OnClickListener listener) {
        mContainerPlayPreVoiceContent.setOnClickListener(listener);
    }

    public void setCancelOnClickListener(OnClickListener listener) {
        mTvCancel.setOnClickListener(listener);
    }

    public void setSubmitOnClickListener(OnClickListener listener) {
        mTvSubmit.setOnClickListener(listener);
    }

    public void setLlmVoiceOnClickListener(OnClickListener listener) {
        mContainerLlmVoice.setOnClickListener(listener);
    }

}
