package tech.sud.mgp.hello.ui.scenes.common.gift.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.GiftListResp;
import tech.sud.mgp.hello.ui.common.utils.FormatUtils;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppRocketGameViewModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.adapter.GiftListAdapter;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.GiftSendClickListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PresentClickListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.listener.SendGiftToUserListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftId;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.RoomGiftDialogManager;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.MicUserInfoModel;

/**
 * 房间礼物弹窗
 */
public class RoomGiftDialog extends BaseDialogFragment implements SendGiftToUserListener {

    private GiftDialogTopView topView;
    private GiftDialogBottomView bottomView;
    private RecyclerView giftRv;
    private GiftListAdapter giftListAdapter;
    public GiftSendClickListener giftSendClickListener;
    private final RoomGiftDialogManager dialogManager = new RoomGiftDialogManager();
    private int sceneType; // 场景类型
    private long gameId; // 游戏id
    private GridLayoutManager layoutManager;

    private View containerCustomRocket;
    private ImageView ivRocketIcon;
    private View containerGoCustom;

    private static long oldSelectedGiftId;
    private static int oldSelectedGiftType;

    private OnShowCustomRocketClickListener onShowCustomRocketClickListener;

    public static RoomGiftDialog newInstance(int sceneType, long gameId) {
        Bundle args = new Bundle();
        args.putInt("sceneType", sceneType);
        args.putLong("gameId", gameId);
        RoomGiftDialog fragment = new RoomGiftDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.audio_dialog_soft_input);
        Bundle arguments = getArguments();
        if (arguments != null) {
            sceneType = arguments.getInt("sceneType");
            gameId = arguments.getLong("gameId");
        }
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
        topView = findViewById(R.id.gift_top_view);
        bottomView = findViewById(R.id.gift_bottom_view);
        giftRv = findViewById(R.id.gift_data_rv);
        containerCustomRocket = findViewById(R.id.container_custom_rocket);
        ivRocketIcon = findViewById(R.id.iv_rocket_icon);
        containerGoCustom = findViewById(R.id.container_go_custom);

        layoutManager = new GridLayoutManager(requireContext(), 4);
        giftRv.setLayoutManager(layoutManager);
        giftListAdapter = new GiftListAdapter();
        giftRv.setAdapter(giftListAdapter);
        giftRv.setItemAnimator(null);
    }

    public GiftModel getCheckedGift() {
        for (GiftModel item : giftListAdapter.getData()) {
            if (item.checkState) {
                return item;
            }
        }
        return null;
    }

    @Override
    protected void initData() {
        super.initData();
        // 礼物列表是否显示标签
        if (sceneType == SceneType.DISCO) {
            giftListAdapter.isShowFlag = true;
        }

        List<GiftModel> list = new ArrayList<>();
        List<GiftModel> gifts = GiftHelper.getInstance().creatGifts(requireContext());
        if (gifts != null) {
            for (GiftModel gift : gifts) {
                if (gift.giftId == GiftId.ROCKET && gameId > 0) {
                    continue;
                }
                list.add(gift);
            }
        }
        giftListAdapter.setList(list);

        // 添加不同场景不同的数据
        if (sceneType == SceneType.DISCO) {
            addDiscoData();
        }

        if (GiftHelper.getInstance().inMic) {
            topView.sendGiftToUserListener = this;
            topView.setInMic(GiftHelper.getInstance().inMics);
        } else {
            topView.setMicOut(GiftHelper.getInstance().underMicUser);
        }
        addServerGifts();
        initBalance();
        checkSelectedItem();

        updateCustomRocketShow();
    }

    private void addDiscoData() {
        giftListAdapter.addData(GiftHelper.getInstance().createGiftModel(5));
        giftListAdapter.addData(GiftHelper.getInstance().createGiftModel(6));
        giftListAdapter.addData(GiftHelper.getInstance().createGiftModel(7));
    }

    private void initBalance() {
        HomeRepository.getAccount(this, new RxCallback<GetAccountResp>() {
            @Override
            public void onSuccess(GetAccountResp resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    bottomView.setBalance(FormatUtils.formatMoney(resp.coin));
                }
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.send_gift_empty_view).setOnClickListener(v -> dismiss());
        bottomView.presentClickListener = new PresentClickListener() {
            @Override
            public void onPresent(int count) {
                onClickPresent();
            }
        };
        giftListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                onClickGift(position);
            }
        });
        containerGoCustom.setOnClickListener((v) -> {
            if (onShowCustomRocketClickListener != null) {
                onShowCustomRocketClickListener.onClick(v);
            }
            dismiss();
        });
    }

    public void setOnShowCustomRocketClickListener(OnShowCustomRocketClickListener onShowCustomRocketClickListener) {
        this.onShowCustomRocketClickListener = onShowCustomRocketClickListener;
    }

    public interface OnShowCustomRocketClickListener {
        void onClick(View view);
    }

    private void onClickGift(int position) {
        GiftModel item = giftListAdapter.getItem(position);
        if (item.checkState) {
            return;
        }
        item.checkState = true;
        giftListAdapter.notifyItemChanged(position);
        for (int i = 0; i < giftListAdapter.getData().size(); i++) {
            GiftModel model = giftListAdapter.getItem(i);
            if (i != position && model.checkState) {
                model.checkState = false;
                giftListAdapter.notifyItemChanged(i);
            }
        }

        oldSelectedGiftId = item.giftId;
        oldSelectedGiftType = item.type;

        updateCustomRocketShow();
    }

    private void updateCustomRocketShow() {
        GiftModel checkedGift = getCheckedGift();
        if (checkedGift != null && checkedGift.giftId == GiftId.ROCKET) {
            containerCustomRocket.setVisibility(View.VISIBLE);
            ImageLoader.loadRocketImage(ivRocketIcon, AppRocketGameViewModel.getExistsRocketThumbPath());
        } else {
            containerCustomRocket.setVisibility(View.GONE);
        }
    }

    private void onClickPresent() {
        GiftModel checkedGift = getCheckedGift();
        if (giftSendClickListener != null && checkedGift != null) {
            List<UserInfo> userInfos = new ArrayList<>();
            if (GiftHelper.getInstance().inMic) {
                if (GiftHelper.getInstance().inMics.size() > 0) {
                    for (MicUserInfoModel userInfo : GiftHelper.getInstance().inMics) {
                        if (userInfo.checked) {
                            UserInfo info = new UserInfo();
                            info.userID = userInfo.userInfo.userId + "";
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
                giftSendClickListener.onSendClick(checkedGift, 1, userInfos);
            } else {
                ToastUtils.showShort(R.string.audio_send_gift_user_empty);
            }
        } else {
            ToastUtils.showShort(R.string.audio_send_gift_empty);
        }
    }

    /** 添加后端配置的礼物 */
    private void addServerGifts() {
        if (sceneType != SceneType.DANMAKU) {
            return;
        }
        RoomRepository.giftList(this, sceneType, gameId, new RxCallback<GiftListResp>() {
            @Override
            public void onSuccess(GiftListResp giftListResp) {
                super.onSuccess(giftListResp);
                giftListAdapter.addData(0, conver(giftListResp));
                if (layoutManager != null) {
                    layoutManager.scrollToPosition(0);
                }
                checkSelectedItem();
            }
        });
    }

    /** 如果没有选中的礼物，进行默认选中 */
    private void checkSelectedItem() {
        // 判断是否有选中的
        for (GiftModel item : giftListAdapter.getData()) {
            if (item.checkState) {
                return;
            }
        }
        // 没有选中的，那就匹配上一次选中的
        for (int i = 0; i < giftListAdapter.getData().size(); i++) {
            GiftModel item = giftListAdapter.getData().get(i);
            if (item.giftId == oldSelectedGiftId && item.type == oldSelectedGiftType) {
                if (!item.checkState) {
                    item.checkState = true;
                    giftListAdapter.notifyItemChanged(i);
                }
                return;
            }
        }
        // 默认选中第一条
        if (giftListAdapter.getData().size() > 0) {
            giftListAdapter.getData().get(0).checkState = true;
            giftListAdapter.notifyItemChanged(0);
        }
    }

    private List<GiftModel> conver(GiftListResp resp) {
        List<GiftModel> list = new ArrayList<>();
        if (resp != null && resp.giftList != null) {
            for (GiftListResp.BackGiftModel backGiftModel : resp.giftList) {
                GiftModel giftModel = new GiftModel();
                giftModel.type = 1;
                giftModel.giftId = backGiftModel.giftId;
                giftModel.giftName = backGiftModel.name;
                giftModel.giftPrice = backGiftModel.giftPrice;
                giftModel.giftUrl = backGiftModel.giftUrl;
                giftModel.animationUrl = backGiftModel.animationUrl;
                list.add(giftModel);
            }
        }
        return list;
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

    // 设置单个收礼人数据
    public void setToUser(UserInfo user) {
        GiftHelper.getInstance().inMic = false;
        GiftHelper.getInstance().underMicUser = user;
    }

    /** 判断是否展示礼物icon */
    public void setGiftEnable(AudioRoomMicModel model) {
        if (GiftHelper.getInstance().inMic && topView != null) {
            model.giftEnable = Boolean.TRUE.equals(topView.userState.get(model.userId));
        }
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
