package tech.sud.mgp.audio.example.widget.view.chat;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.KeyboardUtils;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.common.utils.HSTextUtils;

public class RoomInputMsgView extends ConstraintLayout {

    private View viewEmpty;
    private EditText editText;
    private View viewSend;
    private SendMsgListener sendMsgListener;
    private View viewMain;

    public RoomInputMsgView(@NonNull Context context) {
        this(context, null);
    }

    public RoomInputMsgView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomInputMsgView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initListener();
    }

    private void initView() {
        inflate(getContext(), R.layout.audio_view_room_input_msg, this);
        viewEmpty = findViewById(R.id.room_input_msg_view_empty);
        editText = findViewById(R.id.room_input_msg_edit_text);
        viewSend = findViewById(R.id.room_input_msg_tv_send);
        viewMain = findViewById(R.id.room_input_msg_cl_main);
    }

    private void initListener() {
        viewSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    return !send();
                }
                return false;
            }
        });
        viewEmpty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    public void show() {
        KeyboardUtils.showSoftInput(editText);
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        KeyboardUtils.hideSoftInput(editText);
        setVisibility(View.GONE);
    }

    public void setSendMsgListener(SendMsgListener listener) {
        sendMsgListener = listener;
    }

    private boolean send() {
        String text = HSTextUtils.filterEmptyInputChatContent(getInputString());
        if (text == null || text.isEmpty()) {
            return false;
        }
        if (sendMsgListener != null) {
            sendMsgListener.onSendMsg(text);
        }
        return true;
    }

    private String getInputString() {
        Editable text = editText.getText();
        if (text != null) {
            return text.toString();
        }
        return null;
    }

    public void onSoftInputChanged(int height) {
        if (height == 0) {
            hide();
        } else {
            MarginLayoutParams params = (MarginLayoutParams) viewMain.getLayoutParams();
            params.bottomMargin = height;
            viewMain.setLayoutParams(params);
        }
    }

    public interface SendMsgListener {
        void onSendMsg(CharSequence msg);
    }

}
