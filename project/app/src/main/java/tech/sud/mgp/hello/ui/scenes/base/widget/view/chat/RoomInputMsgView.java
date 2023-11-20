package tech.sud.mgp.hello.ui.scenes.base.widget.view.chat;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.HSTextUtils;
import tech.sud.mgp.hello.ui.common.listener.OnVisibilityListener;

public class RoomInputMsgView extends ConstraintLayout {

    private View viewEmpty;
    private EditText editText;
    private View viewSend;
    private SendMsgListener sendMsgListener;
    private View viewMain;
    private View mContainerEmoji;
    private RecyclerView mRecyclerViewEmoji;
    private ImageView mIvEmoji;
    private OnVisibilityListener mOnVisibilityListener;

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
        inflate(getContext(), R.layout.view_room_input_msg, this);
        viewEmpty = findViewById(R.id.room_input_msg_view_empty);
        editText = findViewById(R.id.room_input_msg_edit_text);
        viewSend = findViewById(R.id.room_input_msg_tv_send);
        viewMain = findViewById(R.id.room_input_msg_cl_main);
        mContainerEmoji = findViewById(R.id.container_emoji);
        mRecyclerViewEmoji = findViewById(R.id.recycler_view_emoji);
        mIvEmoji = findViewById(R.id.view_emoji);
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
        mIvEmoji.setOnClickListener(v -> {
            clickEmoji();
        });
        mContainerEmoji.setOnClickListener(v -> {
        }); // 拦截点击事件
    }

    /** 点击emoji表情入口之后的处理 */
    public void clickEmoji() {
        if (isShowingEmoji()) {
            show();
            hideEmoji();
        } else {
            showEmoji();
        }
    }

    private void hideEmoji() {
        mContainerEmoji.setVisibility(View.GONE);
        mIvEmoji.setImageResource(R.drawable.ic_input_emoji);
    }

    public void showEmoji() {
        editText.setEnabled(false);
        setVisibility(View.VISIBLE);
        mContainerEmoji.setVisibility(View.VISIBLE);
        hideSoftInput();
        editText.setEnabled(true);
        setMainMarginBottom(getEmojiHeight());
        mIvEmoji.setImageResource(R.drawable.ic_input_keyboard);
    }

    private boolean isShowingEmoji() {
        return mContainerEmoji.getVisibility() == View.VISIBLE;
    }

    private void hideSoftInput() {
        KeyboardUtils.hideSoftInput(editText);
    }

    public void show() {
        KeyboardUtils.showSoftInput(editText);
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        hideSoftInput();
        setVisibility(View.GONE);
    }

    public void clearInput() {
        editText.setText(null);
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
        if (isShowingEmoji()) {
            if (height > 0) {
                setMainMarginBottom(height);
                hideEmoji();
            } else {
                setMainMarginBottom(getEmojiHeight());
            }
            return;
        }

        if (height == 0) {
            hide();
        } else {
            setMainMarginBottom(height);
        }
    }

    private int getEmojiHeight() {
        int measuredHeight = mContainerEmoji.getMeasuredHeight();
        if (measuredHeight == 0) {
            measuredHeight = DensityUtils.dp2px(getContext(), 219);
        }
        return measuredHeight;
    }

    private void setMainMarginBottom(int height) {
        MarginLayoutParams params = (MarginLayoutParams) viewMain.getLayoutParams();
        params.bottomMargin = height;
        viewMain.setLayoutParams(params);
    }

    public void setEmojiVisibility(int visibility) {
        mIvEmoji.setVisibility(visibility);
    }

    public void setEmojiClickListener(OnClickListener listener) {
        mIvEmoji.setOnClickListener(listener);
    }

    public interface SendMsgListener {
        void onSendMsg(CharSequence msg);
    }

    public RecyclerView getRecyclerViewEmoji() {
        return mRecyclerViewEmoji;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (mOnVisibilityListener != null) {
            mOnVisibilityListener.onVisibility(visibility);
        }
    }

    public void setOnVisibilityListener(OnVisibilityListener onVisibilityListener) {
        mOnVisibilityListener = onVisibilityListener;
    }
}
