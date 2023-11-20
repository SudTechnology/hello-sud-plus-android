package tech.sud.mgp.hello.ui.scenes.audio3d.widget.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;

public class RoomUserInfoDialog extends BaseDialogFragment {

    private ImageView mIvIcon;
    private TextView mTvName;
    private TextView mTvSendGift;
    private long mUserId;
    private OnClickSendGiftListener mOnClickSendGiftListener;
    private UserInfoResp mUserInfoResp;

    public static RoomUserInfoDialog newInstance(long userId) {
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        RoomUserInfoDialog fragment = new RoomUserInfoDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean beforeSetContentView() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUserId = arguments.getLong("userId");
        }
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_room_user_info;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mIvIcon = findViewById(R.id.iv_icon);
        mTvName = findViewById(R.id.tv_name);
        mTvSendGift = findViewById(R.id.tv_send_gift);
    }

    @Override
    protected void initData() {
        super.initData();
        getUserInfo();
    }

    private void getUserInfo() {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(mUserId);
        UserInfoRepository.getUserInfoList(this, userIdList, new UserInfoRepository.UserInfoResultListener() {
            @Override
            public void userInfoList(List<UserInfoResp> userInfos) {
                if (userInfos != null && userInfos.size() > 0) {
                    setUserInfo(userInfos.get(0));
                }
            }
        });
    }

    private void setUserInfo(UserInfoResp userInfoResp) {
        mUserInfoResp = userInfoResp;
        ImageLoader.loadAvatar(mIvIcon, userInfoResp.getUseAvatar());
        mTvName.setText(userInfoResp.nickname);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mTvSendGift.setOnClickListener(v -> {
            if (mOnClickSendGiftListener != null) {
                mOnClickSendGiftListener.onClickSendGift(mUserInfoResp);
            }
            dismiss();
        });
    }

    public void setOnClickSendGiftListener(OnClickSendGiftListener onClickSendGiftListener) {
        mOnClickSendGiftListener = onClickSendGiftListener;
    }

    public interface OnClickSendGiftListener {
        void onClickSendGift(UserInfoResp userInfoResp);
    }

}
