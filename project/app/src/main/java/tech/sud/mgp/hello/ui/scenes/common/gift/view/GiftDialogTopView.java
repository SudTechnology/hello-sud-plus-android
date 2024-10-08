package tech.sud.mgp.hello.ui.scenes.common.gift.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.gift.adapter.GiftMicUserAdapter;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.SendGiftToUserListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.MicUserInfoModel;

public class GiftDialogTopView extends ConstraintLayout {

    private LinearLayout sendInMicUserLl, sendOutMicUserLl;
    private TextView cancelTv, nicknameTv;
    private ImageView roomMicRivAvatar;
    private RecyclerView micListRv;
    private List<MicUserInfoModel> micUsers = new ArrayList<>();
    private GiftMicUserAdapter userAdapter = new GiftMicUserAdapter(micUsers);
    public SendGiftToUserListener sendGiftToUserListener;
    public Map<Long, Boolean> userState = new HashMap<>();

    public GiftDialogTopView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GiftDialogTopView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftDialogTopView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_gift_send_top, this);
        sendInMicUserLl = findViewById(R.id.send_in_mic_user_ll);
        sendOutMicUserLl = findViewById(R.id.send_out_mic_user_ll);
        cancelTv = findViewById(R.id.cancel_tv);
        nicknameTv = findViewById(R.id.nickname_tv);
        roomMicRivAvatar = findViewById(R.id.room_mic_riv_avatar);
        micListRv = findViewById(R.id.mic_list_rv);
        cancelTv.setOnClickListener(v -> {
            if (cancelTv.isSelected()) {
                setChecked(false);
                this.userAdapter.setList(this.micUsers);
                cancelTv.setText(context.getString(R.string.audio_all));
                cancelTv.setSelected(false);
            } else {
                setChecked(true);
                this.userAdapter.setList(this.micUsers);
                cancelTv.setText(context.getString(R.string.audio_cancle));
                cancelTv.setSelected(true);
            }
            notifyUserState();
        });
    }

    /**
     * 设置麦上用户
     */
    public void setInMic(List<MicUserInfoModel> micUsers) {
        if (micUsers != null && micUsers.size() > 0) {
            if (micUsers.size() == 1) {
                cancelTv.setText(getContext().getString(R.string.audio_cancle));
                cancelTv.setSelected(true);
            }

            setVisibility(View.VISIBLE);
            sendInMicUserLl.setVisibility(View.VISIBLE);
            sendOutMicUserLl.setVisibility(View.GONE);
            this.micUsers.clear();
            this.micUsers.addAll(micUsers);

            micListRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            micListRv.setAdapter(this.userAdapter);
            this.userAdapter.setOnItemClickListener((adapter, view, position) -> {
                boolean isChecked = micUsers.get(position).checked;
                micUsers.get(position).checked = !isChecked;
                this.userAdapter.notifyItemChanged(position);
                userState.put(micUsers.get(position).userInfo.userId, micUsers.get(position).checked);
                notifyUserState();
            });
            initUserState();
            notifyUserState();
        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     * 更新麦位数据
     */
    public void updateInMic(List<MicUserInfoModel> micUsers) {
        if (micUsers != null && micUsers.size() > 0) {
            setVisibility(View.VISIBLE);
            sendInMicUserLl.setVisibility(View.VISIBLE);
            sendOutMicUserLl.setVisibility(View.GONE);
            this.micUsers.clear();
            this.micUsers.addAll(micUsers);
            this.userAdapter.setList(this.micUsers);
        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     * 设置麦下用户数据
     */
    public void setMicOut(UserInfo user) {
        setVisibility(View.VISIBLE);
        sendInMicUserLl.setVisibility(View.GONE);
        sendOutMicUserLl.setVisibility(View.VISIBLE);
        ImageLoader.loadAvatar(roomMicRivAvatar, user.icon);
        nicknameTv.setText(user.name);
    }

    /**
     * 批量操作
     */
    private void setChecked(boolean isSelected) {
        if (this.micUsers != null && this.micUsers.size() > 0) {
            if (isSelected) {
                for (MicUserInfoModel m : this.micUsers) {
                    m.checked = true;
                    userState.put(m.userInfo.userId, true);
                }
            } else {
                for (MicUserInfoModel m : this.micUsers) {
                    m.checked = false;
                    userState.put(m.userInfo.userId, false);
                }
            }
        }
    }

    /**
     * 查找选中
     */
    private void initUserState() {
        if (this.micUsers != null && this.micUsers.size() > 0) {
            for (MicUserInfoModel m : this.micUsers) {
                userState.put(m.userInfo.userId, m.checked);
            }
        }
    }

    /**
     * 通知用户状态变化
     */
    private void notifyUserState() {
        if (sendGiftToUserListener != null) {
            sendGiftToUserListener.onNotify(userState);
        }
    }

}