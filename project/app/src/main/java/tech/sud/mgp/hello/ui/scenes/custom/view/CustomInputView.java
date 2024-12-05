package tech.sud.mgp.hello.ui.scenes.custom.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.common.listener.SimpleTextWatcher;

/**
 * 输入的类型
 */
public class CustomInputView extends ConstraintLayout {

    private TextView titleTv, subTitleTv;
    private EditText etInput;
    private ModifyDataListener listener;

    public CustomInputView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomInputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_custom_input, this);
        titleTv = findViewById(R.id.title_tv);
        subTitleTv = findViewById(R.id.subtitle_tv);
        etInput = findViewById(R.id.et_input);
        etInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                int gameMode;
                try {
                    gameMode = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (listener != null) {
                    listener.onChange(gameMode);
                }
            }
        });
    }

    public void setData(String title, String subTitle, int gameMode) {
        titleTv.setText(title);
        subTitleTv.setText(subTitle);
        etInput.setText(gameMode + "");
    }

    public interface ModifyDataListener {
        void onChange(int gameMode);
    }

    public void setListener(ModifyDataListener listener) {
        this.listener = listener;
    }

}
