package tech.sud.mgp.audio.gift.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.UserInfo;
import tech.sud.mgp.audio.gift.adapter.GiftListAdapter;
import tech.sud.mgp.audio.gift.callback.GiftSendClickCallback;
import tech.sud.mgp.audio.gift.manager.GiftHelper;
import tech.sud.mgp.audio.gift.model.GiftModel;
import tech.sud.mgp.audio.gift.model.MicUserInfoModel;
import tech.sud.mgp.common.base.BaseDialogFragment;
import tech.sud.mgp.common.model.HSUserInfo;

public class RoomGiftDialog extends BaseDialogFragment {

    private GiftDialogTopView topView;
    private GiftDialogBottomView bottomView;
    private RecyclerView giftRv;
    private GiftListAdapter giftListAdapter;
    private List<GiftModel> gifts = new ArrayList<>();
    public GiftSendClickCallback clickCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.audio_dialog_soft_input);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setDimAmount(0f);
        window.setWindowAnimations(R.style.DtBottomToTopAnim);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.audio_dialog_gift_send_room;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView = mRootView.findViewById(R.id.gift_top_view);
        bottomView = mRootView.findViewById(R.id.gift_bottom_view);
        giftRv = mRootView.findViewById(R.id.gift_data_rv);
        mRootView.findViewById(R.id.send_gift_empty_view).setOnClickListener(v -> dismiss());
        bottomView.callback = count -> {
            GiftModel checkedGift = GiftHelper.getInstance().getCheckedGift();
            if (clickCallback != null && checkedGift != null) {
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
                    clickCallback.sendClick(checkedGift.giftId, 1, userInfos);
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
            topView.setInMic(GiftHelper.getInstance().inMics);
        } else {
            topView.setMicOut(GiftHelper.getInstance().underMicUser);
        }
    }

    //设置收礼人数据
    public void setMicUsers(List<AudioRoomMicModel> mDatas, int selectedIndex) {
        if (mDatas != null && mDatas.size() > 0) {
            GiftHelper.getInstance().inMic = true;
            GiftHelper.getInstance().inMics.clear();
            if (selectedIndex < 0 || selectedIndex >= mDatas.size()) {
                selectedIndex = 0;
            }
            for (AudioRoomMicModel model : mDatas) {
                if (model.userId != 0 && model.userId != HSUserInfo.userId) {
                    MicUserInfoModel user = new MicUserInfoModel();
                    user.userInfo = model;
                    user.indexMic = model.micIndex;
                    user.checked = model.micIndex == selectedIndex;
                    GiftHelper.getInstance().inMics.add(user);
                }
            }
        }
    }

    //设置单个收礼人数据
    public void setToUser(UserInfo user) {
        GiftHelper.getInstance().inMic = false;
        GiftHelper.getInstance().underMicUser = user;
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
}
