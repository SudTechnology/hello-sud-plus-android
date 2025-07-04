package tech.sud.mgp.hello.ui.main.llm.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.WebPUtils;
import tech.sud.mgp.hello.service.main.resp.AiInfoModel;
import tech.sud.mgp.hello.service.main.resp.GetAiCloneResp;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;

public class CreateLlmView extends ConstraintLayout {

    private SwitchButton mSwitchButtonCloned;
    private EditText mEtName;
    private TextView mTvNothingLlmVoice;
    private View mContainerLlmVoice;
    private TextView mTvReadAloud;
    private TextView mTvReadAloudContent;
    private View mContainerSubmit;
    private TextView mTvCancel;
    private TextView mTvSubmit;
    private VoiceRecordView mVoiceRecordView;
    private View mViewCannotOperate;

    private LlmTagView mTagViewPersonality;
    private LlmTagView mTagViewLanguageStyle;
    private LlmTagView mTagViewLanguageDetailStyle;
    private ImageView mIvVoice;

    private View mBottomViewCannotOperate;
    private TextView mTvNothingCloneVoice;
    private View mContainerPlayCloneVoice;
    private ImageView mIvCloneVoice;
    private TextView mTvSave;
    private TextView mTvRestartRecord;
    private int mOperateStatus;
    private TextView mTvClickPlayCloneVoice;
    private TextView mTvClickPlayMyVoice;

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
        mTvNothingLlmVoice = findViewById(R.id.tv_nothing_llm_voice);
        mContainerLlmVoice = findViewById(R.id.container_llm_voice);
        mTvReadAloud = findViewById(R.id.tv_read_aloud);
        mTvReadAloudContent = findViewById(R.id.tv_read_aloud_content);
        mContainerSubmit = findViewById(R.id.container_submit);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvSubmit = findViewById(R.id.tv_submit);
        mVoiceRecordView = findViewById(R.id.voice_record_view);
        mViewCannotOperate = findViewById(R.id.view_cannot_operate);
        mTagViewPersonality = findViewById(R.id.tag_view_personality);
        mTagViewLanguageStyle = findViewById(R.id.tag_view_language_style);
        mTagViewLanguageDetailStyle = findViewById(R.id.tag_view_language_detail_style);
        mIvVoice = findViewById(R.id.iv_voice);
        mTvSave = findViewById(R.id.tv_save);

        mBottomViewCannotOperate = findViewById(R.id.bottom_view_cannot_operate);
        mTvNothingCloneVoice = findViewById(R.id.tv_nothing_clone_voice);
        mContainerPlayCloneVoice = findViewById(R.id.container_play_clone_voice);
        mIvCloneVoice = findViewById(R.id.iv_clone_voice);
        mTvRestartRecord = findViewById(R.id.tv_restart_record);

        mTvClickPlayCloneVoice = findViewById(R.id.tv_click_play_clone_voice);
        mTvClickPlayMyVoice = findViewById(R.id.tv_click_play_my_voice);

        setTextViewShaderColor(mTvClickPlayCloneVoice);
        setTextViewShaderColor(mTvClickPlayMyVoice);
    }

    private static void setTextViewShaderColor(TextView textView) {
        textView.post(() -> {
            float width = textView.getMeasuredWidth();
            Shader shader = new LinearGradient(
                    0, 0, width, textView.getTextSize(),
                    new int[]{Color.parseColor("#FF008CAF"), Color.parseColor("#FF002FB0")},
                    null,
                    Shader.TileMode.CLAMP
            );
            textView.getPaint().setShader(shader);
            textView.invalidate();
        });
    }

    private void initListener() {
        mViewCannotOperate.setOnClickListener(v -> ToastUtils.showShort(R.string.please_start_clonse));
        mBottomViewCannotOperate.setOnClickListener(v -> ToastUtils.showShort(R.string.please_start_clonse));
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

    public void setReadAloud(String str) {
        mTvReadAloudContent.setText(str);
    }

    public void setCannotOperateShow(boolean show) {
        mViewCannotOperate.setVisibility(show ? View.VISIBLE : View.GONE);
        mBottomViewCannotOperate.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /** 设置分身的声音是否存在 */
    public void setExistsCloneVoice(boolean exists) {
        if (exists) {
            mTvNothingCloneVoice.setVisibility(View.GONE);
            mContainerPlayCloneVoice.setVisibility(View.VISIBLE);
        } else {
            mTvNothingCloneVoice.setVisibility(View.VISIBLE);
            mContainerPlayCloneVoice.setVisibility(View.GONE);
        }
        setTextViewShaderColor(mTvClickPlayCloneVoice);
    }

    /** 设置我的声音是否存在 */
    public void setExistsMyVoice(boolean exists) {
        if (exists) {
            mTvNothingLlmVoice.setVisibility(View.GONE);
            mContainerLlmVoice.setVisibility(View.VISIBLE);
        } else {
            mTvNothingLlmVoice.setVisibility(View.VISIBLE);
            mContainerLlmVoice.setVisibility(View.GONE);
        }
        setTextViewShaderColor(mTvClickPlayMyVoice);
    }

    /**
     * 设置操作按钮的显示状态
     *
     * @param status 0显示按住录制 1显示取消和提交 2显示重新开始录制
     */
    public void setOperateStatus(int status) {
        mOperateStatus = status;
        switch (status) {
            case 0:
                mContainerSubmit.setVisibility(View.GONE);
                mVoiceRecordView.setVisibility(View.VISIBLE);
                mTvRestartRecord.setVisibility(View.GONE);
                break;
            case 1:
                mContainerSubmit.setVisibility(View.VISIBLE);
                mVoiceRecordView.setVisibility(View.GONE);
                mTvRestartRecord.setVisibility(View.GONE);
                break;
            case 2:
                mContainerSubmit.setVisibility(View.GONE);
                mVoiceRecordView.setVisibility(View.GONE);
                mTvRestartRecord.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setSwitchListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        mSwitchButtonCloned.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public int getOperateStatus() {
        return mOperateStatus;
    }

    public void setOnRecordListener(VoiceRecordView.OnRecordListener onRecordListener) {
        mVoiceRecordView.setOnRecordListener(onRecordListener);
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

    public void setSaveOnClickListener(OnClickListener listener) {
        mTvSave.setOnClickListener(listener);
    }

    public void setRestartRecordOnClickListener(OnClickListener listener) {
        mTvRestartRecord.setOnClickListener(listener);
    }

    public void setPlayCloneVoiceOnClickListener(OnClickListener listener) {
        mContainerPlayCloneVoice.setOnClickListener(listener);
    }

    public void setDatas(GetAiCloneResp resp) {
        if (resp == null) {
            return;
        }
        AiInfoModel aiInfo = resp.aiInfo;
        if (aiInfo != null) {
            mTagViewPersonality.initSelectedTags(aiInfo.personalities);
            mTagViewLanguageStyle.initSelectedTags(aiInfo.languageStyles);
            mTagViewLanguageDetailStyle.initSelectedTags(aiInfo.languageDetailStyles);
        }

        mTagViewPersonality.setDatas(resp.personalityOptions);
        mTagViewLanguageStyle.setDatas(resp.languageStyleOptions);
        mTagViewLanguageDetailStyle.setDatas(resp.languageDetailStyleOptions);
    }

    public List<String> getPersonality() {
        return mTagViewPersonality.getSelectedTags();
    }

    public List<String> getLanguageStyle() {
        return mTagViewLanguageStyle.getSelectedTags();
    }

    public List<String> getLanguageDetailStyle() {
        return mTagViewLanguageDetailStyle.getSelectedTags();
    }

    public void startVoiceAnim() {
        Drawable drawable = mIvVoice.getDrawable();
        if (drawable instanceof WebpDrawable) {

        } else {
            WebPUtils.loadWebP(mIvVoice, R.raw.llm_bot_voice, -1, new PlayResultListener() {
                @Override
                public void onResult(PlayResult result) {
                }
            });
        }
    }

    public void stopVoiceAnim() {
        Drawable drawable = mIvVoice.getDrawable();
        if (drawable instanceof WebpDrawable) {
            WebpDrawable webpDrawable = (WebpDrawable) drawable;
            webpDrawable.stop();
            ImageLoader.loadDrawable(mIvVoice, R.drawable.ic_voice);
        }
    }

    public boolean isRecording() {
        return mVoiceRecordView.isRecording();
    }

    public void startCloneVoiceAnim() {
        Drawable drawable = mIvCloneVoice.getDrawable();
        if (drawable instanceof WebpDrawable) {

        } else {
            WebPUtils.loadWebP(mIvCloneVoice, R.raw.llm_bot_voice, -1, new PlayResultListener() {
                @Override
                public void onResult(PlayResult result) {
                }
            });
        }
    }

    public void stopCloneVoiceAnim() {
        Drawable drawable = mIvCloneVoice.getDrawable();
        if (drawable instanceof WebpDrawable) {
            WebpDrawable webpDrawable = (WebpDrawable) drawable;
            webpDrawable.stop();
            ImageLoader.loadDrawable(mIvCloneVoice, R.drawable.ic_voice);
        }
    }

}
