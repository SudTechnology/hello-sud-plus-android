package tech.sud.mgp.hello.ui.main.llm.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
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
    private int mVoiceModel; // 0为默认，1为录制状态，本地已经录制，2为分身已经创建好的情况

    private LlmTagView mTagViewPersonality;
    private LlmTagView mTagViewLanguageStyle;
    private LlmTagView mTagViewLanguageDetailStyle;
    private ImageView mIvVoice;
    private TextView mTvTrialListening;

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
        mTvTrialListening = findViewById(R.id.tv_trial_listening);
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
        mTvReadAloud.setVisibility(View.VISIBLE);
        mTvReadAloudContent.setVisibility(View.VISIBLE);
        mContainerSubmit.setVisibility(View.GONE);
        mVoiceRecordView.setVisibility(View.VISIBLE);
    }

    /** 录制状态，本地已经录制 */
    public void showReadyRecordStatus() {
        mVoiceModel = 1;
        mTvNothingLlmVoice.setVisibility(View.GONE);
        mContainerLlmVoice.setVisibility(View.VISIBLE);
        mTvReadAloud.setVisibility(View.VISIBLE);
        mTvReadAloudContent.setVisibility(View.VISIBLE);
        mContainerSubmit.setVisibility(View.VISIBLE);
        mVoiceRecordView.setVisibility(View.GONE);
    }

    /** 分身已经创建好的情况 */
    public void showReadyClonedStatus() {
        mVoiceModel = 2;
        mTvNothingLlmVoice.setVisibility(View.GONE);
        mContainerLlmVoice.setVisibility(View.VISIBLE);
        mTvReadAloud.setVisibility(View.VISIBLE);
        mTvReadAloudContent.setVisibility(View.VISIBLE);
        mContainerSubmit.setVisibility(View.GONE);
        mVoiceRecordView.setVisibility(View.VISIBLE);
    }

    public void setSwitchListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        mSwitchButtonCloned.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public int getVoiceModel() {
        return mVoiceModel;
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

    public void setTrialListeningOnClickListener(OnClickListener listener) {
        mTvTrialListening.setOnClickListener(listener);
    }

    public void setDatas(GetAiCloneResp resp) {
        if (resp == null) {
            return;
        }
        mTagViewPersonality.setDatas(resp.personalityOptions);
        mTagViewLanguageStyle.setDatas(resp.languageStyleOptions);
        mTagViewLanguageDetailStyle.setDatas(resp.languageDetailStyleOptions);
    }

    public String getPersonality() {
        List<String> selectedTags = mTagViewPersonality.getSelectedTags();
        if (selectedTags == null || selectedTags.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedTags.size(); i++) {
            String tag = selectedTags.get(i);
            if (TextUtils.isEmpty(tag)) {
                continue;
            }
            if (i > 0) {
                sb.append(",");
            }
            sb.append(tag);
        }
        return sb.toString();
    }

    public String getLanguageStyle() {
        List<String> selectedTags = mTagViewLanguageStyle.getSelectedTags();
        if (selectedTags == null || selectedTags.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedTags.size(); i++) {
            String tag = selectedTags.get(i);
            if (TextUtils.isEmpty(tag)) {
                continue;
            }
            if (i > 0) {
                sb.append(",");
            }
            sb.append(tag);
        }
        return sb.toString();
    }

    public String getLanguageDetailStyle() {
        List<String> selectedTags = mTagViewLanguageDetailStyle.getSelectedTags();
        if (selectedTags == null || selectedTags.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedTags.size(); i++) {
            String tag = selectedTags.get(i);
            if (TextUtils.isEmpty(tag)) {
                continue;
            }
            if (i > 0) {
                sb.append(",");
            }
            sb.append(tag);
        }
        return sb.toString();
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

}
