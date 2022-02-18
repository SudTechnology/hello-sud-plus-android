package tech.sud.mgp.hello.ui.room.common.gift.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.ui.room.audio.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.room.common.gift.adapter.GiftListAdapter;
import tech.sud.mgp.hello.ui.room.common.gift.listener.GiftSendClickListener;
import tech.sud.mgp.hello.ui.room.common.gift.listener.SendGiftToUserListener;
import tech.sud.mgp.hello.ui.room.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.room.common.gift.manager.RoomGiftDialogManager;
import tech.sud.mgp.hello.ui.room.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.room.common.gift.model.MicUserInfoModel;

public class RoomGiftDialog extends BaseDialogFragment implements SendGiftToUserListener {

    private GiftDialogTopView topView;
    private GiftDialogBottomView bottomView;
    private RecyclerView giftRv;
    private GiftListAdapter giftListAdapter;
    private List<GiftModel> gifts = new ArrayList<>();
    public GiftSendClickListener giftSendClickListener;
    private RoomGiftDialogManager dialogManager = new RoomGiftDialogManager();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.audio_dialog_soft_input);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setDimAmount(0f);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_gift_send_room;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView = mRootView.findViewById(R.id.gift_top_view);
        bottomView = mRootView.findViewById(R.id.gift_bottom_view);
        giftRv = mRootView.findViewById(R.id.gift_data_rv);
        mRootView.findViewById(R.id.send_gift_empty_view).setOnClickListener(v -> dismiss());
        bottomView.presentClickListener = count -> {
            GiftModel checkedGift = GiftHelper.getInstance().getCheckedGift();
            if (giftSendClickListener != null && checkedGift != null) {
                List<UserInfo> userInfos = new ArrayList<>();
                if (GiftHelper.getInstance().inMic) {
                    if (GiftHelper.getInstance().inMics.size() > 0) {
                        for (MicUserInfoModel userInfo : GiftHelper.getInstance().inMics) {
                            if (userInfo.checked) {
                                UserInfo info = new UserInfo();
                                info.userID = userInfo.userInfo.userId;
                                info.name = userInfo.userInfo.nickName;
                                info.icon = userInfo.userInfo.avatar;
                                userInfos.add(info);
                            }
                        }
                    }
                } else {
                    userInfos.add(GiftHelper.getInstance().underMicUser);
                }
                if (userInfos.size() > 0) {
                    giftSendClickListener.onSendClick(checkedGift.giftId, 1, userInfos);
                } else {
                    ToastUtils.showShort(R.string.audio_send_gift_user_empty);
                }
            } else {
                ToastUtils.showShort(R.string.audio_send_gift_empty);
            }
        };
    }

    @Override
    protected void initData() {
        super.initData();
        gifts.addAll(GiftHelper.getInstance().creatGifts(requireContext()));
        giftListAdapter = new GiftListAdapter(gifts);
        giftRv.setAdapter(giftListAdapter);
        giftRv.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        giftListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (gifts.size() > 0) {
                for (int i = 0; i < gifts.size(); i++) {
                    if (i == position) {
                        gifts.get(i).checkState = true;
                    } else {
                        gifts.get(i).checkState = false;
                    }
                }
                giftListAdapter.setList(gifts);
            }
        });
        if (GiftHelper.getInstance().inMic) {
            topView.sendGiftToUserListener = this;
            topView.setInMic(GiftHelper.getInstance().inMics);
        } else {
            topView.setMicOut(GiftHelper.getInstance().underMicUser);
        }
    }

    /**
     * 初始化 送礼面板麦位
     */
    public void setMicUsers(List<AudioRoomMicModel> mDatas, int selectedIndex) {
        dialogManager.setMicUsers(mDatas, selectedIndex);
    }

    /**
     * 实时更新送礼面板的麦位数据
     */
    public void updateOneMicUsers(AudioRoomMicModel micModel) {
        if (GiftHelper.getInstance().inMic) {
            dialogManager.updateOneMicUsers(micModel);
            topView.updateInMic(GiftHelper.getInstance().inMics);
        }
    }

    /**
     * 实时更新送礼面板的单个麦位数据
     */
    public void updateMicUsers(List<AudioRoomMicModel> mDatas) {
        if (GiftHelper.getInstance().inMics.size() > 0 && GiftHelper.getInstance().inMic) {
            dialogManager.updateMicUsers(mDatas);
            topView.updateInMic(GiftHelper.getInstance().inMics);
        }
    }

    //设置单个收礼人数据
    public void setToUser(UserInfo user) {
        GiftHelper.getInstance().inMic = false;
        GiftHelper.getInstance().underMicUser = user;
    }

    public void setGiftEnable(AudioRoomMicModel model) {
        if (GiftHelper.getInstance().inMic && topView != null) {
            model.giftEnable = topView.userState.get(model.userId) != null && topView.userState.get(model.userId);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return ScreenUtils.getAppScreenHeight() - 1;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotify(Map<Long, Boolean> userState) {
        giftSendClickListener.onNotify(userState);
    }
}
